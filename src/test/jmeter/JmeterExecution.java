import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.save.SaveService;
import org.apache.jorphan.collections.ListedHashTree;
import org.base.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class JmeterExecution {
    private static final Logger log = LoggerFactory.getLogger(JmeterExecution.class);

    public static void main(String[] args) throws Exception {
        Utils utils = new Utils();
        StandardJMeterEngine jMeterEngine= utils.initJmeter();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode configRootNode = mapper.readTree(new File("configuration/config.json"));
        String payload = configRootNode.get("payloadFile").asText();
        log.info("Payload filepath : "+payload);
        JsonNode payloadrootNode = mapper.readTree(new File(payload));
        String testName = payloadrootNode.get("info").get("name").asText();
        log.info("TestPlan Name :"+testName);
        ListedHashTree hashTree = new ListedHashTree();
        ListedHashTree testPlan = utils.testPlan(testName, hashTree);
        utils.addCookieManager(testPlan);
        utils.addCacheManager(testPlan);
        JsonNode scenarios = configRootNode.get("scenario");
        for (JsonNode scenario : scenarios) {
            int tps = scenario.get("tps").asInt();
            log.info("TPS value: "+tps);
            int duration = scenario.get("duration").asInt();
            log.info("Duration value: "+duration);
            int rampUp = scenario.get("rampUp").asInt();
            log.info("RampUp value: "+rampUp);
            String tGName = scenario.get("name").asText();
            log.info("ThreadGroup Name: "+tGName);
           // Double respTime = scenario.get("responseTime").asDouble();
            ListedHashTree thread = utils.threadGroup(tGName, testPlan, tps, rampUp, duration, 1);
            JsonNode csvVariables = scenario.get("csv_variable");
            JsonNode controllers = scenario.get("controller");
            JsonNode items = payloadrootNode.get("item");
            if (!csvVariables.isNull()) {
                for(JsonNode csvVariable : csvVariables){
                    JsonNode apis = csvVariable.get("apis");
                    String varName = csvVariable.get("var_name").asText();
                    for(JsonNode api: apis){
                        String apiName = api.get("apiName").asText();
                        String attribute = api.get("attribute").asText();
                        for(JsonNode item:items){
                            System.out.println(apiName+" : "+item.get("name").asText());
                            if(apiName.equalsIgnoreCase(item.get("name").asText())){
                                String text = item.get("request").get("body").get("raw").asText();
                                System.out.println(text);
                                JsonNode jsonNode = mapper.readTree(text);
                                ObjectNode objectNode = (ObjectNode) jsonNode;
                                String variable = "${"+varName+"}";
                                objectNode.put(attribute,variable);
                                System.out.println("ObjectNode direct "+objectNode);
                                System.out.println(mapper.writeValueAsString(objectNode));
                                ObjectNode object = (ObjectNode) item.get("request").get("body");
                                object.put("raw",mapper.writeValueAsString(objectNode));

                            }
                        }

                    }
                }
                utils.csvDataConfig(thread, "csvFiles/" + testName+scenario.get("name").asText() + ".csv", csvVariables);
            }
            for (JsonNode controller : controllers) {
                if (!controller.get("name").isNull()) {
                    String controlName = controller.get("name").asText();
                    log.info("Controller Name: "+controlName);
                    JsonNode apis = controller.get("apiName");
                    if (controlName.equalsIgnoreCase("only-once")) {
                        ListedHashTree onlyOnce = utils.onceOnlyController(thread);
                        for (JsonNode api : apis) {
                            for (JsonNode item : items) {
                                String apiMethod = item.get("request").get("method").asText();
                                if (api.asText().equalsIgnoreCase(item.get("name").asText())) {
                                    ListedHashTree sampler = utils.httpSampler(item, onlyOnce);
                                    if (!item.get("request").get("header").isNull()) {
                                        JsonNode header = item.get("request").get("header");
                                        utils.headerManager(sampler, header);
                                    }
                                    if (apiMethod.equalsIgnoreCase("POST")) {
                                        utils.responseAssertion("201", sampler);
                                    } else {
                                        utils.responseAssertion("200", sampler);
                                    }
                                    utils.jsonExtractor(sampler, controller.get("jsonPath").asText(), controller.get("variable").asText());
                                }
                            }
                        }
                    } else if (controlName.equalsIgnoreCase("transaction")) {
                        ListedHashTree transaction = utils.transactionController(thread);
                        for (JsonNode api : apis) {
                            for (JsonNode item : items) {
                                String apiMethod = item.get("request").get("method").asText();
                                if (api.asText().equalsIgnoreCase(item.get("name").asText())) {
                                    ListedHashTree sampler = utils.httpSampler(item, transaction);
                                    if (!item.get("request").get("header").isNull()) {
                                        JsonNode header = item.get("request").get("header");
                                        utils.headerManager(sampler, header);
                                    }
                                    if (apiMethod.equalsIgnoreCase("POST")) {
                                        utils.responseAssertion("201", sampler);
                                    } else {
                                        utils.responseAssertion("200", sampler);
                                    }
                                    //utils.jsonExtractor(sampler,controller.get("jsonPath").asText(),controller.get("variable").asText());
                                }
                            }
                        }
                    } else if (controlName.equalsIgnoreCase("critical-section")) {
                        ListedHashTree criticalSection = utils.criticalSectionController(thread);
                        for (JsonNode api : apis) {
                            for (JsonNode item : items) {
                                String apiMethod = item.get("request").get("method").asText();
                                if (api.asText().equalsIgnoreCase(item.get("name").asText())) {
                                    ListedHashTree sampler = utils.httpSampler(item, criticalSection);
                                    if (!item.get("request").get("header").isNull()) {
                                        JsonNode header = item.get("request").get("header");
                                        utils.headerManager(sampler, header);
                                    }
                                    if (apiMethod.equalsIgnoreCase("POST")) {
                                        utils.responseAssertion("201", sampler);
                                    } else {
                                        utils.responseAssertion("200", sampler);
                                    }
                                    //utils.jsonExtractor(sampler,controller.get("jsonPath").asText(),controller.get("variable").asText());
                                }
                            }
                        }
                    }
                }
            }
            log.info("Controllers are added successfully");
            JsonNode apis = scenario.get("api_order");
            for (JsonNode api : apis) {
                for (JsonNode item : items) {
                    if (api.asText().equalsIgnoreCase(item.get("name").asText())) {
                        String apiMethod = item.get("request").get("method").asText();
                        ListedHashTree sampler = utils.httpSampler(item, thread);
                        if (!item.get("request").get("header").isNull()) {
                            JsonNode header = item.get("request").get("header");
                            utils.headerManager(sampler, header);
                        }
                        if (apiMethod.equalsIgnoreCase("POST")) {
                            utils.responseAssertion("201", sampler);
                        } else {
                            utils.responseAssertion("200", sampler);
                        }
                    }
                }
            }
            log.info("apis added Successfully" );
        }
        // Save Test Plan to JMX File
        String jmxFile = utils.JMXFileCreator(testName);
        try (FileOutputStream fos = new FileOutputStream(jmxFile)) {
            SaveService.saveTree(hashTree, fos);
        }
//        Summariser summariser = new Summariser("summary");
//
//        // Create JTL result collector to store results
//        String jtlFile = "results.jtl";
//        ResultCollector resultCollector = new ResultCollector(summariser);
//        resultCollector.setFilename(jtlFile);
//        hashTree.add(hashTree.getArray()[0], resultCollector);
//        jMeterEngine.configure(hashTree);
//        jMeterEngine.run();
//        System.out.println("JMeter execution completed. Results saved in: " + jtlFile);
        //utils.runJmxFile(jmxFile);
    }
}
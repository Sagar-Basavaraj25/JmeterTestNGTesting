import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        ObjectMapper mapper1 = new ObjectMapper();
        ObjectMapper mapper2 = new ObjectMapper();
        JsonNode configRootNode = mapper1.readTree(new File("configuration/config.json"));
        String payload = configRootNode.get("payloadFile").asText();
        log.info("Payload filepath : "+payload);
        JsonNode rootNode = mapper2.readTree(new File(payload));
        String testName = rootNode.get("info").get("name").asText();
        log.info("TestPlan Name :"+testName);
        ListedHashTree hashTree = new ListedHashTree();
        ListedHashTree testPlan = utils.testPlan(testName, hashTree);
        utils.addCookieManager(testPlan);
        utils.addCacheManager(testPlan);
        JsonNode scenarios = configRootNode.get("scenario");
        for (JsonNode scenario : scenarios) {
            int tps = scenario.get("tps").asInt();
            int duration = scenario.get("duration").asInt();
            int rampUp = scenario.get("rampUp").asInt();
            String tGName = scenario.get("name").asText();
            ListedHashTree thread = utils.threadGroup(tGName, testPlan, (duration * tps), rampUp, duration, 1);
            JsonNode csvVariables = scenario.get("csv_variable");
            JsonNode controllers = scenario.get("controller");
            JsonNode items = rootNode.get("item");
            if (!csvVariables.isNull()) {
                utils.csvDataConfig(thread, "csvFiles/" + testName + ".csv", csvVariables);
            }
            for (JsonNode controller : controllers) {
                if (!controller.get("name").isNull()) {
                    String controlName = controller.get("name").asText();
                    JsonNode apis = controller.get("apiName");
                    if (controlName.equalsIgnoreCase("only-once")) {
                        ListedHashTree onlyOnce = utils.onceOnlyController(thread);
                        for (JsonNode api : apis) {
                            for (JsonNode item : items) {
                                String apiMethod = item.get("request").get("method").asText();
                                if (api.asText().equalsIgnoreCase(item.get("name").asText())) {
                                    ListedHashTree sampler = utils.httpSampler(item, onlyOnce);
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
                                    if (apiMethod.equalsIgnoreCase("POST")) {
                                        utils.responseAssertion("201", sampler);
                                    } else {
                                        utils.responseAssertion("200", sampler);
                                    }
                                    utils.responseAssertion("200", sampler);
                                    //utils.jsonExtractor(sampler,controller.get("jsonPath").asText(),controller.get("variable").asText());
                                }
                            }
                        }
                    } else if (controlName.equalsIgnoreCase("critical-section")) {
                        ListedHashTree crticialSection = utils.criticalSectionController(thread);
                        for (JsonNode api : apis) {
                            for (JsonNode item : items) {
                                String apiMethod = item.get("request").get("method").asText();
                                if (api.asText().equalsIgnoreCase(item.get("name").asText())) {
                                    ListedHashTree sampler = utils.httpSampler(item, crticialSection);
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
            JsonNode apis = scenario.get("api_order");
            for (JsonNode api : apis) {
                for (JsonNode item : items) {
                    if (api.asText().equalsIgnoreCase(item.get("name").asText())) {
                        String apiMethod = item.get("request").get("method").asText();
                        ListedHashTree sampler = utils.httpSampler(item, thread);
                        if (apiMethod.equalsIgnoreCase("POST")) {
                            utils.responseAssertion("201", sampler);
                        } else {
                            utils.responseAssertion("200", sampler);
                        }
                    }
                }
            }
        }
        // Save Test Plan to JMX File
        String jmxFile = utils.JMXFileCreator(testName);
        try (FileOutputStream fos = new FileOutputStream(jmxFile)) {
            SaveService.saveTree(hashTree, fos);
        }
    }
}
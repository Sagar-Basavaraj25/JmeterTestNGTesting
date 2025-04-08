package org.base;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.ListedHashTree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Utils {
    private static final Logger log = LogManager.getLogger(Utils.class);
    ConfigUtils configUtils = new ConfigUtils();
    ControllerUtils controllerUtils = new ControllerUtils();
    SamplerUtils samplerUtils = new SamplerUtils();
    AssertionUtils assertionUtils = new AssertionUtils();
    ProcessorUtils processorUtils = new ProcessorUtils();
    TimerUtils timerUtils = new TimerUtils();
    public void initJmeter(){
        String jmeterHome = System.getenv("JMETER_HOME");
        System.out.println(jmeterHome);
        if (jmeterHome == null) {
            throw new IllegalStateException("Please set JMETER_HOME environment variable.");
        }

        // Initialize JMeter Engine
        StandardJMeterEngine jmeterEngine = new StandardJMeterEngine();
        // Initialize JMeter Properties
        JMeterUtils.setJMeterHome(jmeterHome);
        JMeterUtils.loadJMeterProperties(jmeterHome+"/bin/jmeter.properties");
        JMeterUtils.setProperty("jmeter.save.saveservice.autoflush","true");
        JMeterUtils.initLocale();
        log.info("Jmeter is initialised ");
    }

    public ListedHashTree testPlan(String testPlanName, ListedHashTree tree){
        TestPlan testPlan = new TestPlan(testPlanName);
        testPlan.setProperty("TestElement.gui_class", TestPlanGui.class.getName());
        testPlan.setProperty("TestElement.test_class", TestPlan.class.getName());
        Arguments userDefinedVars = new Arguments();
        testPlan.setUserDefinedVariables(userDefinedVars);
        log.info("Test Plan added to HashTree : ");
        return tree.add(testPlan);
    }

    public String JMXFileCreator(String threadName){
        LocalDateTime today = LocalDateTime.now();
        String dateString = today.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));// Format the date to a string (e.g., "20250130" for January 30, 2025)
        String fileName = "target/jmxFiles/" + threadName + "_" + dateString + ".jmx";
        // Create the File object
        File jmxFile = new File(fileName);
        System.out.println("JMX file will be created at: " + jmxFile.getAbsolutePath());
        return fileName;
    }

    public void runJmxFile(String filename) {
        try {
            LocalDateTime today = LocalDateTime.now();
            String dateString = today.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String logs = "target/jmeterLogs/" + dateString + ".jtl";
            String report = "target/jmeterReports/"+dateString;
            String command = "jmeter -n -t "+filename+" -l "+logs+" -e -o "+report;

            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            Process process = processBuilder.start();
            processBuilder.redirectErrorStream(true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("Jmeter Execution Ended");
            // Read the output of the command
            int exitCode = process.waitFor();
            System.out.println("Command exited with code: " + exitCode);
            aggregateReport(logs);
        } catch (Exception e) {
            log.error("Error Occured while running the jmx : "+e.getMessage());
            throw new RuntimeException("Error : "+e.getMessage());
        }
    }
    public void aggregateReport(String jtlFileName) {
        try {
            LocalDateTime today = LocalDateTime.now();
            String dateString = today.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String report = "target/JmeterAggreagateReport/"+dateString+".csv";
            String command = "JMeterPluginsCMD.bat --generate-csv "+report+" --input-jtl "+jtlFileName+" --plugin-type AggregateReport";

            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            // Read the output of the command
            int exitCode = process.waitFor();
            System.out.println("Command exited with code: " + exitCode);
        } catch (Exception e) {
            log.error("Error Occured while running the jmx : "+e.getMessage());
            throw new RuntimeException("Error : "+e.getMessage());
        }
    }

    public static ListedHashTree addThreadGroup(ListedHashTree testplan, JsonNode scenrio){
        ListedHashTree thread;
        ThreadGroupUtils threadGroupUtils = new ThreadGroupUtils();
        String testType = scenrio.get("thread_type").asText();
        switch (testType.toLowerCase()){
            case "normal":
                thread=threadGroupUtils.threadGroup(testplan,scenrio);
                break;
            case "concurrent":
                thread=threadGroupUtils.concurrentThreadGroup(testplan,scenrio);
                break;
            case "ultimate":
                thread = threadGroupUtils.ulimateThreadGroup(testplan,scenrio);
                break;
            default:
                log.error("Please Enter Valid test Type");
                throw new RuntimeException("Enter Valid test type");
        }
        return thread;
    }

    public void processScenario(JsonNode scenario, JsonNode payloadRootNode, ListedHashTree testPlan, Utils utils, ObjectMapper mapper, String testName) throws Exception {
        String scenarioName = scenario.get("name").asText();
        int tps = scenario.get("tps").asInt();
        int duration = scenario.get("duration").asInt();
        int rampUp = scenario.get("rampUp").asInt();

        log.info("Processing Scenario: {}, TPS: {}, Duration: {}, RampUp: {}", scenarioName, tps, duration, rampUp);

        ListedHashTree threadGroup = addThreadGroup(testPlan,scenario);
        JsonNode csvVariables = scenario.get("csv_variable");
        JsonNode controllers = scenario.get("controller");
        JsonNode jsonExtractors = scenario.get("JsonExtractor");
        JsonNode apiItems = payloadRootNode.get("item");
        Map<String, JsonNode> apiMap = new TreeMap<String,JsonNode>();
        for(JsonNode item: apiItems){
            if (item.has("name")) {
                apiMap.put(item.get("name").asText(), item);
            }
        }
        // Process CSV Variables
        if (!csvVariables.isNull()) {
            processCsvVariables(csvVariables, apiMap, mapper);

            configUtils.csvDataConfig(threadGroup, "csvFiles/" + testName + scenarioName + ".csv", csvVariables);
        }

        // Process Controllers
        for (JsonNode controller : controllers) {
            processController(controller, apiMap, threadGroup, utils, mapper,jsonExtractors);
        }

        // Process API Order
        processApiOrder(scenario.get("api_order"), apiMap, threadGroup, utils, mapper,jsonExtractors);
        log.info("Scenario processed successfully" + scenarioName);

    }

    public void processCsvVariables(JsonNode csvVariables, Map<String,JsonNode> apiMap, ObjectMapper mapper) throws Exception {
        for (JsonNode csvVariable : csvVariables) {
            String varName = csvVariable.get("var_name").asText();
            JsonNode apis = csvVariable.get("apis");

            for (JsonNode api : apis) {
                String apiName = api.get("apiName").asText();
                String attribute = api.get("attribute").asText();
                JsonNode item = apiMap.get(apiName);
                if (apiName.equalsIgnoreCase(item.get("name").asText())) {
                    String requestBody = item.get("request").get("body").get("raw").asText();
                    configUtils.changeBodyVariables(requestBody, attribute, varName, item);
                }
            }
        }
    }
    public void processController(JsonNode controller, Map<String,JsonNode> apiItems, ListedHashTree threadGroup, Utils utils, ObjectMapper mapper,JsonNode jsonExtractors) {
        String controlName = controller.get("name").asText();
        int loop = controller.has("loopCount")?controller.get("loopCount").asInt():-1;
        Long runTime = controller.has("runTime")?controller.get("runTime").asLong():1;
        log.info("Processing Controller: {}", controlName);

        ListedHashTree controllerTree;
        switch (controlName.toLowerCase()) {
            case "only-once":
                controllerTree = controllerUtils.onceOnlyController(threadGroup);
                break;
            case "transaction":
                controllerTree = controllerUtils.transactionController(threadGroup);
                break;
            case "critical-section":
                controllerTree = controllerUtils.criticalSectionController(threadGroup);
                break;
            case "loop":
                controllerTree = controllerUtils.loopController(threadGroup,loop);
                break;
            case "runtime":
                controllerTree = controllerUtils.runTimeController(threadGroup,runTime);
                break;
            case "simple":
                controllerTree = controllerUtils.simpleController(threadGroup);
                break;
            case "random":
                controllerTree = controllerUtils.randomController(threadGroup);
                break;
            case "throughput":
                controllerTree = controllerUtils.throughputController(threadGroup,2f);
                break;
            case "randomOrder":
                controllerTree = controllerUtils.randomOrderController(threadGroup);
                break;
            default:
                log.error("Unknown Controller: " + controlName);
                return;
        }

        for (JsonNode api : controller.get("apiName")) {
            addHttpSampler(api.asText(), apiItems, controllerTree, utils, mapper, jsonExtractors);
        }
    }

    public void processApiOrder(JsonNode apiOrder, Map<String,JsonNode> apiItems, ListedHashTree threadGroup, Utils utils, ObjectMapper mapper,JsonNode jsonExtractors) {
        for (JsonNode api : apiOrder) {
            addHttpSampler(api.asText(), apiItems, threadGroup, utils, mapper,jsonExtractors);
        }
    }

    public void addHttpSampler(String apiName, Map<String,JsonNode> apiItems, ListedHashTree parentTree, Utils utils, ObjectMapper mapper,JsonNode jsonExtractors) {
        JsonNode item = apiItems.get(apiName);
        if (apiName.equalsIgnoreCase(item.get("name").asText())) {
            String apiMethod = item.get("request").get("method").asText();
            ListedHashTree samplerTree = samplerUtils.httpSampler(item, parentTree);

            if (!item.get("request").get("header").isNull()) {
                configUtils.headerManager(samplerTree, item.get("request").get("header"));
            }
            assertionUtils.responseAssertion(apiMethod.equalsIgnoreCase("POST") ? "201" : "200", samplerTree);
            addJsonExtractors(apiName, jsonExtractors, apiItems, samplerTree, utils,mapper);
        }
    }
    public void addJsonExtractors(String apiName, JsonNode jsonExtractors, Map<String,JsonNode> apiItems,ListedHashTree samplerTree, Utils utils, ObjectMapper mapper)
    {
        for (JsonNode jsonExtractor : jsonExtractors) {
            if(jsonExtractor.has("apiName") && !jsonExtractor.get("apiName").isNull()) {
                if (apiName.equalsIgnoreCase(jsonExtractor.get("apiName").asText())) {
                    String variableName = jsonExtractor.get("variableName").asText();
                    processorUtils.jsonExtractor(samplerTree, jsonExtractor.get("JsonPath").asText(), jsonExtractor.get("variableName").asText());
                    log.info("Added JSON Extractor to API:" + apiName);
                    JsonNode targetApisExtractors = jsonExtractor.get("targets");
                    for(JsonNode targetApisExtractor : targetApisExtractors){
                        JsonNode targetApi = apiItems.get(targetApisExtractor.get("apiName").asText());
                        String target_value = targetApisExtractor.get("target_value").asText();
                        switch (targetApisExtractor.get("target_type").asText().toLowerCase()) {
                            case "header":
                                JsonNode headerNode = targetApi.get("request").get("header");
                                try {
                                    ArrayNode dataArray = (ArrayNode) headerNode;
                                    for(JsonNode dataArr :dataArray ){
                                        if(target_value.equalsIgnoreCase(dataArr.get("key").asText())){
                                            ObjectNode secondEntry = (ObjectNode) dataArr;
                                            secondEntry.put("value", "Bearer "+"${" + variableName + "}");
                                        }
                                    }
                                    System.out.println("Json Extractor variable added inside Header:");
                                } catch (Exception e) {
                                    System.out.println("Exception occurs while adding JsonExtractor in Header: " + e.getMessage());
                                }
                                break;
                            case "body":
                                String requestBody = targetApi.get("request").get("body").get("raw").asText();
//                                try {
//                                    ObjectNode jsonNode = (ObjectNode)mapper.readTree(requestBody);
//                                    jsonNode.put(target_value, "${" + variableName + "}");
////                                    ((ObjectNode) targetApi.get("request").get("body")).put("raw", mapper.writeValueAsString(jsonNode));
//                                    System.out.println("Json Extractor variable added inside Body:");
//                                } catch (Exception e) {
//                                    System.out.println("Exception occurs while adding JsonExtractor in body: " + e.getMessage());
//                                }
                                configUtils.changeBodyVariables(requestBody, target_value, variableName, targetApi);
                                break;
                            case "path":
                                JsonNode path = targetApi.get("request").get("url").get("path");
                                try {
                                    ArrayNode pathNode = (ArrayNode) path;
                                    pathNode.add("${" + variableName + "}");
                                    System.out.println("Json Extractor variable added to path:");
                                } catch (Exception e) {
                                    System.out.println("Exception occurs while adding path: " + e.getMessage());
                                }
                                break;
                            default:
                                log.warn("Unknown Target Type: ");
                                return;
                        }
                    }
                }
            }
        }
    }
    public int numberUsers(int tps, double responseTime) {
        double noUsers = Math.ceil((tps * responseTime) );
        System.out.println(noUsers + "Number of users");
        return (int) noUsers;
    }
}


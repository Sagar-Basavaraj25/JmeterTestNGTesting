package org.base;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kg.apc.jmeter.reporters.ConsoleStatusLogger;
import kg.apc.jmeter.reporters.ConsoleStatusLoggerGui;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.testbeans.gui.TestBeanGUI;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.JSR223Listener;
import org.apache.jorphan.collections.ListedHashTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Utils {
    private static final Logger log = LoggerFactory.getLogger(Utils.class);
    ConfigUtils configUtils = new ConfigUtils();
    ControllerUtils controllerUtils = new ControllerUtils();
    SamplerUtils samplerUtils = new SamplerUtils();
    AssertionUtils assertionUtils = new AssertionUtils();
    ProcessorUtils processorUtils = new ProcessorUtils();
    public StandardJMeterEngine initJmeter(){
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
        JMeterUtils.initLocale();
        log.info("Jmeter is initialised ");
        return jmeterEngine;
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
            //System.out.println("command===>" + command);

            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("cmd.exe", "/c", command);
            Process process = processBuilder.start();
            // Read the output of the command
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            int exitCode = process.waitFor();
            System.out.println("Command exited with code: " + exitCode);
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions
        }
    }

    public void reportCreation(String filename) {
        try {
            LocalDateTime today = LocalDateTime.now();
            String dateString = today.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String report = "target/jmeterReports/"+dateString;
            String command = "jmeter -g "+filename+" -o "+report;
            //System.out.println("command===>" + command);

            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("cmd.exe", "/c", command);
            Process process = processBuilder.start();
            // Read the output of the command
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            int exitCode = process.waitFor();
            System.out.println("Command exited with code: " + exitCode);
        } catch (Exception e) {
            System.out.println(e.getMessage());; // Handle exceptions
        }
    }
    public void consoleLogger(ListedHashTree testplan){
        ConsoleStatusLogger consoleLog = new ConsoleStatusLogger();
        consoleLog.setProperty("TestElement.gui_class", ConsoleStatusLoggerGui.class.getName());
        consoleLog.setProperty("TestElement.test_class", ConsoleStatusLogger.class.getName());
        consoleLog.setName("jp@gc - Console Status Logger");
        testplan.add(consoleLog);
    }
    public void addJsr223Listener(ListedHashTree testplan){
        JSR223Listener listener = new JSR223Listener();
        listener.setName("JSR223 Listener");
        listener.setProperty("TestElement.test_class",JSR223Listener.class.getName());
        listener.setProperty("TestElement.gui_class", TestBeanGUI.class.getName());
        listener.setProperty("scriptLanguage", "groovy");
        listener.setProperty("cacheKey", "true");
        String groovyScript = """
                // Extract basic details
                def samplerName = prev.getSampleLabel()
                def threadName = prev.getThreadName()
                def responseCode = prev.getResponseCode()
                def responseMessage = prev.getResponseMessage()
                def responseTime = prev.getTime()
                def status = prev.isSuccessful() ? "PASS" : "FAIL"
                def requestHeaders = prev.getRequestHeaders()
                def responseHeaders = prev.getResponseHeaders()
                def responseData = prev.getResponseDataAsString()
                def requestData = sampler.getArguments().getArgument(0).getValue()
        // Logging data
        println " ThreadName: ${threadName}, Sampler: ${samplerName}, Response Time: ${responseTime}ms, Status: ${status}, Message: ${responseMessage}"
        """;
        listener.setProperty("script", groovyScript);
        //println "Request Body: ${requestData}"
        testplan.add(listener);
    }
    public static ListedHashTree addThreadGroup(ListedHashTree testplan, JsonNode scenrio){
        ListedHashTree thread;
        ThreadGroupUtils threadGroupUtils = new ThreadGroupUtils();
        String testType = scenrio.get("test_type").asText();
        switch (testType.toLowerCase()){
            case "load":
                thread=threadGroupUtils.threadGroup(testplan,scenrio);
                break;
            case "concurrent_load":
                thread=threadGroupUtils.concurrentThreadGroup(testplan,scenrio);
                break;
            case "spike":
            case "stress":
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
        Map<String, JsonNode> apiMap = new HashMap<String,JsonNode>();
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
            default:
                log.warn("Unknown Controller: " + controlName);
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
}


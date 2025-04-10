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
    static final Logger log = LogManager.getLogger(Utils.class);
    ConfigUtils configUtils = new ConfigUtils();
    ControllerUtils controllerUtils = new ControllerUtils();
    SamplerUtils samplerUtils = new SamplerUtils();
    AssertionUtils assertionUtils = new AssertionUtils();
    ProcessorUtils processorUtils = new ProcessorUtils();
    TimerUtils timerUtils = new TimerUtils();
    ObjectMapper mapper = new ObjectMapper();
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

    public static ListedHashTree addThreadGroup(ListedHashTree testplan, JsonNode scenario){
        ListedHashTree thread;
        ThreadGroupUtils threadGroupUtils = new ThreadGroupUtils();
        double tps = scenario.get("tps").asDouble();
        JsonNode threadGroup = scenario.get("thread");
        String testType = threadGroup.get("thread_name").asText();
        switch (testType.toLowerCase()){
            case "normal":
                thread=threadGroupUtils.threadGroup(testplan,scenario);
                break;
            case "concurrent":
                thread=threadGroupUtils.concurrentThreadGroup(testplan,scenario);
                break;
            case "ultimate":
                thread = threadGroupUtils.ulimateThreadGroup(testplan,scenario);
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
        JsonNode assertions = scenario.get("assertions");
        JsonNode apiItems = payloadRootNode.get("item");
        Map<String, JsonNode> apiMap = new TreeMap<String,JsonNode>();
        for(JsonNode item: apiItems){
            if (item.has("name")) {
                apiMap.put(item.get("name").asText(), item);
            }
        }
        // Process CSV Variables
        if (!csvVariables.isNull()) {
            processCsvVariables(csvVariables, apiMap);

            configUtils.csvDataConfig(threadGroup, "csvFiles/" + testName + scenarioName + ".csv", csvVariables);
        }

        // Process Controllers
        for (JsonNode controller : controllers) {
            // processController(controller, apiMap, threadGroup,jsonExtractors);
        }

        // Process API Order
        //processApiOrder(scenario.get("api_order"), apiMap, threadGroup, utils, mapper,jsonExtractors);
        log.info("Scenario processed successfully" + scenarioName);

    }

    public void processCsvVariables(JsonNode csvVariables, Map<String,JsonNode> apiMap){
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
    public void processController(JsonNode controller, Map<String,JsonNode> apiItems, ListedHashTree threadGroup,JsonNode processors,JsonNode timers,JsonNode assertions) {
        String controlName = controller.get("controller-type").asText();
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
                int loop = controller.get("runTime/loop/throughput").asInt();
                controllerTree = controllerUtils.loopController(threadGroup,loop);
                break;
            case "runtime":
                Long runTime = controller.get("runTime/loop/throughput").asLong();
                controllerTree = controllerUtils.runTimeController(threadGroup,runTime);
                break;
            case "simple":
                controllerTree = controllerUtils.simpleController(threadGroup);
                break;
            case "random":
                controllerTree = controllerUtils.randomController(threadGroup);
                break;
            case "throughput":
                double percentThroughput = controller.get("runTime/loop/throughput").asDouble();
                controllerTree = controllerUtils.throughputController(threadGroup,(float)percentThroughput);
                break;
            case "random-order":
                controllerTree = controllerUtils.randomOrderController(threadGroup);
                break;
            default:
                log.error("Unknown Controller: " + controlName);
                return;
        }

        for (JsonNode api : controller.get("apiName")) {
            addHttpSampler(api.asText(), apiItems, controllerTree,processors,timers,assertions);
        }
    }

    public void addHttpSampler(String apiName, Map<String,JsonNode> apiItems, ListedHashTree parentTree,JsonNode processors,JsonNode timers,JsonNode assertions) {
        JsonNode item = apiItems.get(apiName);
        if (apiName.equalsIgnoreCase(item.get("name").asText())) {
            ListedHashTree samplerTree = samplerUtils.httpSampler(item, parentTree);
            if (!item.get("request").get("header").isNull()) {
                configUtils.headerManager(samplerTree, item.get("request").get("header"));
            }
            for (JsonNode assertion:assertions){
                List apiNames = mapper.convertValue(assertion.get("apiName"),List.class);
                log.info("Assertion : "+apiNames.isEmpty());
                if(apiNames.contains(apiName)){
                    assertions(samplerTree,assertion);
                }
            }
            for (JsonNode timer:timers){
                List apiNames = mapper.convertValue(timer.get("apiName"),List.class);
                log.info("timer : "+apiNames.isEmpty());
                if(apiNames.contains(apiName)){
                    addTimers(samplerTree,timer);
                }
            }
            for (JsonNode processor:processors){
                String api = processor.get("apiName").asText();
                if(api.equalsIgnoreCase(apiName)){
                    addProcessor(apiName,samplerTree,processor,apiItems);
                }
            }
        }
    }
    public int numberUsers(double tps, double responseTime) {
        double noUsers = Math.ceil((tps * responseTime) );
        System.out.println(noUsers + "Number of users");
        return (int) noUsers;
    }
    public void addProcessor(String apiName,ListedHashTree sampler,JsonNode processor,Map<String,JsonNode> apiItems){
        String processorName = processor.get("processor_name").asText();
        switch (processorName.toLowerCase()) {
            case "jsonextractor":
                processorUtils.jsonExtractor(sampler, processor);
                log.info("Added JSON Extractor to API: {}", apiName);
                //processorUtils.beanShellPostProcessor(sampler, processor);
                break;
            case "regexextractor":
                processorUtils.regexPostProcessor(sampler, processor);
                log.info("Added Regex Extractor to API: {}", apiName);
                //processorUtils.beanShellPostProcessor(sampler, processor);
                break;
            case "beanshellpreprocessor":
                processorUtils.beanShellPreProcessor(sampler, processor);
                log.info("Added BeanshellPreProcessor to API: {}", apiName);
                break;
            case "jdbcpreprocessor":
                processorUtils.jdbcPreProcessor(sampler);
                log.info("Added JDBC preprocessor");
                break;
            case "jdbcpostprocessor":
                processorUtils.jdbcPostProcessor(sampler);
                processorUtils.beanShellPostProcessor(sampler, processor);
                break;
            default:
                log.error("Please enter valid processor");
                throw new RuntimeException("Invalid Processor");
        }
        JsonNode targets = processor.get("targets");
        String variableName = processor.get("proc_variableName").asText();
        for (JsonNode target : targets) {
            String api = target.get("apiName").asText();
            JsonNode targetApi = apiItems.get(api);
            String targetValue = target.get("target_value").asText();
            String targetType = target.get("target_type").asText();
            switch (targetType.toLowerCase()) {
                case "body":
                    String requestBody = targetApi.get("request").get("body").get("raw").asText();
                    configUtils.changeBodyVariables(requestBody, targetValue, variableName, targetApi);
                    break;
                case "header":
                    JsonNode headerNode = targetApi.get("request").get("header");
                    try {
                        ArrayNode dataArray = (ArrayNode) headerNode;
                        for (JsonNode dataArr : dataArray) {
                            if (targetValue.equalsIgnoreCase(dataArr.get("key").asText())) {
                                ObjectNode secondEntry = (ObjectNode) dataArr;
                                secondEntry.put("value", "Bearer " + "${" + variableName + "}");
                            }
                        }
                        System.out.println("Json Extractor variable added inside Header:");
                    } catch (Exception e) {
                        System.out.println("Exception occurs while adding JsonExtractor in Header: " + e.getMessage());
                    }
                    break;
                case "path":
                    JsonNode path = targetApi.get("request").get("url").get("path");
                    try {
                        ArrayNode pathNode = (ArrayNode) path;
                        pathNode.remove(pathNode.size()-1);
                        pathNode.add("${" + variableName + "}");
                        System.out.println("Json Extractor variable added to path:");
                    } catch (Exception e) {
                        System.out.println("Exception occurs while adding path: " + e.getMessage());
                    }
                    break;
                default:
                    log.error("Invalid Target Type");
                    throw new RuntimeException("Invalid Target Type for processor");
            }
        }
    }
    public void addTimers(ListedHashTree sampler,JsonNode timer){
        String timerName = timer.get("timer_name").asText();
        switch(timerName.toLowerCase()){
            case "constant":
                timerUtils.constantTimer(sampler,timer);
                break;
            case "sync":
                timerUtils.syncTimer(sampler,timer);
                break;
            case "constantthrougput":
                timerUtils.constantThroughputTimer(sampler,timer);
                break;
            default:
                log.error("Invalid Timer is added");
                throw new RuntimeException("Please Enter valid timer");
        }
    }
    public void assertions(ListedHashTree tree,JsonNode assertion){
        String assertName = assertion.get("assert_name").asText();
        log.info(assertName);
        switch(assertName.toLowerCase()){
            case "response":
                assertionUtils.responseAssertion(tree,assertion);
            break;
            case "size":
                assertionUtils.sizeAssertion(tree,assertion);
                break;
            case "json":
                assertionUtils.jsonAssertion(tree,assertion);
                break;
            case "duration":
                assertionUtils.durationAssertion(tree,assertion);
                break;
            default:
                log.error("Enter Valid response assertion");
                throw new RuntimeException("Enter valid response assertion");
        }
    }
    public void processScenario(ListedHashTree testPlan, JsonNode scenario, JsonNode payload, String testName){
        String scenarioName = scenario.get("name").asText();
        JsonNode apiItems = payload.get("item");
        int tps = scenario.get("tps").asInt();
        int duration=0,rampUp=0;
        for(JsonNode property:scenario.get("thread").get("property")){
            duration = property.get("duration").asInt();
            rampUp = property.get("rampUp").asInt();
        }
        log.info("Processing Scenario: {}, TPS: {}, Duration: {}, RampUp: {}", scenarioName, tps, duration, rampUp);

        ListedHashTree threadGroup = addThreadGroup(testPlan,scenario);
        JsonNode csvVariables = scenario.get("csv_variable");
        JsonNode controllers = scenario.get("controller");
        JsonNode timers = scenario.get("timers");
        JsonNode assertions = scenario.get("assertions");
        JsonNode processors = scenario.get("processors");
        Map<String,JsonNode> apiMap = new TreeMap<>();
        Map<String,JsonNode>controllerMap = new TreeMap<>();
        if(controllers.isArray() && !controllers.isEmpty()){
            for(JsonNode controller: controllers){
                if(controller.has("name")){
                    controllerMap.put(controller.get("name").asText(),controller);
                }
            }
        }
        for(JsonNode item: apiItems){
            if (item.has("name")) {
                apiMap.put(item.get("name").asText(), item);
            }
        }
        // Process CSV Variables
        if(!csvVariables.isEmpty() && !csvVariables.isNull()){
            processCsvVariables(csvVariables, apiMap);
            configUtils.csvDataConfig(threadGroup, "csvFiles/" + testName + scenarioName + ".csv", csvVariables);
        }

        JsonNode componentOrder = scenario.get("component_order");

        for(JsonNode component:componentOrder){
            String name = component.asText();
            if(apiMap.containsKey(name)){
                addHttpSampler(name,apiMap,threadGroup,processors,timers,assertions);
            } else if (controllerMap.containsKey(name)) {
                processController(controllerMap.get(name),apiMap,threadGroup,processors,timers,assertions);
            } else if (name.equalsIgnoreCase("debug")){
                samplerUtils.debugSampler(threadGroup);
            }else if(name.equalsIgnoreCase("flow-control")){
                samplerUtils.flowControlActionSampler(threadGroup);
            }
            log.info("Component added successfully : {}",name);
        }

    }
}


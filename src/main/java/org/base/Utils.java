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
import java.time.Duration;
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
    TimerUtils timerUtils = new TimerUtils();
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
        JMeterUtils.setProperty("jmeter.save.saveservice.autoflush","true");
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

            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            // Read the output of the command
            int exitCode = process.waitFor();
            System.out.println("Command exited with code: " + exitCode);
            aggreagateReport(logs);
        } catch (Exception e) {
            log.error("Error Occured while running the jmx : "+e.getMessage());
            throw new RuntimeException("Error : "+e.getMessage());
        }
    }
    public void aggreagateReport(String jtlFileName) {
        try {
            LocalDateTime today = LocalDateTime.now();
            String dateString = today.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String report = "target/JmeterAggreagateReport/"+dateString+".csv";
            String command = "JMeterPluginsCMD.bat --generate-csv "+report+" --input-jtl "+jtlFileName+" --plugin-type AggregateReport";

            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            processBuilder.redirectErrorStream(true);
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
//        String groovyScript = """
//                // Extract basic details
//                def samplerName = prev.getSampleLabel()
//                def threadName = prev.getThreadName()
//                def responseCode = prev.getResponseCode()
//                def responseMessage = prev.getResponseMessage()
//                def responseTime = prev.getTime()
//                def status = prev.isSuccessful() ? "PASS" : "FAIL"
//                def requestHeaders = prev.getRequestHeaders()
//                def responseHeaders = prev.getResponseHeaders()
//                def responseData = prev.getResponseDataAsString()
//                def requestData = sampler.getArguments().getArgument(0).getValue()
//        // Logging data
//        println " ThreadName: ${threadName}, Sampler: ${samplerName}, Response Time: ${responseTime}ms, Status: ${status}, Message: ${responseMessage}"
//        """;
        String groovyScript = """
                import org.apache.jmeter.samplers.SampleResult
                
                // Fetch current sample details
                SampleResult result = prev
                if (result != null) {
                    String samplerName = result.getSampleLabel()
                    long avgTime = result.getTime()  // Average Response Time
                    long minTime = result.getStartTime()  // Min Time
                    long maxTime = result.getEndTime()  // Max Time
                    long medianTime = result.getTime() // Approximate median (for demo)
                    long latency = result.getLatency()
                   \s
                    // Print dynamic aggregate report in a single line using \r (carriage return)
                    print "\rSampler: ${samplerName} | Avg: ${avgTime} ms | Median: ${medianTime} ms | Min: ${minTime} | Max: ${maxTime} | Latency: ${latency} ms      "
                   \s
                    // Flush the output to ensure it's visible in real-time
                    System.out.flush()
                }
                
                """;
        listener.setProperty("script", groovyScript);
        //println "Request Body: ${requestData}"
        testplan.add(listener);
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
//        controllerUtils.loopController(threadGroup,1);
//        controllerUtils.randomController(threadGroup);
//        controllerUtils.simpleController(threadGroup);
//        controllerUtils.runTimeController(threadGroup,10L);
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
        samplerUtils.JDBCSampler(threadGroup);
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
                controllerTree = controllerUtils.throughputController(threadGroup,2.0f);
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
//            assertionUtils.jsonAssertion(samplerTree);
//            assertionUtils.sizeAssertion(samplerTree);
//            assertionUtils.durationAssertion(samplerTree);
//            timerUtils.constantThroughputTimer(samplerTree);
//            timerUtils.syncTimer(samplerTree);
//            processorUtils.jdbcPostProcessor(samplerTree);
//            processorUtils.jdbcPreProcessor(samplerTree);
//            processorUtils.beanShellPostProcessor(samplerTree);
//            processorUtils.beanShellPreProcessor(samplerTree);
              timerUtils.constantTimer(samplerTree);
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
    public void runJmxFile1(String jmxFile){
        try {
            LocalDateTime today = LocalDateTime.now();
            String dateString = today.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String logs = "target/jmeterLogs/" + dateString + ".jtl";
            //File jtlFile = new File(logs);
            //jtlFile.createNewFile();
            String report = "target/jmeterReports/"+dateString;
//            File reportDir = new File(report);
//            reportDir.mkdir();
            String command = "jmeter -n -t "+jmxFile+" -l "+logs+" -e -o "+report;
            //System.out.println("command===>" + command);

            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);

            Process process = processBuilder.start();

            // Read JMeter real-time output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // Data structure to store sampler results
            Map<String, List<Long>> samplerData = new HashMap<>();
            Map<String, Integer> samplerErrors = new HashMap<>();

            // Read the JTL file in real-time
            //System.out.println("Hello");
            File jtlFile = new File(logs);
            while (!jtlFile.exists()) {
                Thread.sleep(Duration.ofSeconds(10)); // Wait for JTL file to be created
            }
            System.out.println("Jtl file status : "+jtlFile.exists());
            // Read the JTL file
            BufferedReader jtlReader = new BufferedReader(new FileReader(jtlFile));
            String jtlLine;
            int count=0;
            int num = 50;
            System.out.println((jtlLine = jtlReader.readLine()) != null);
            System.out.println("Sampler Name      | Avg Response Time | Status");
            System.out.println("-------------------------------------------------");
            while ((jtlLine = jtlReader.readLine()) != null || process.isAlive()) {
                //System.out.println(jtlLine);
                String[] fields = jtlLine.split(",");
                if (fields.length > 5) {
                    if(jtlLine.contains("timeStamp")){
                        //System.out.println(jtlLine);
                    }else{
                        String samplerName = fields[2];// Get Sampler Name
                        long responseTime = Long.parseLong(fields[1]); // Get Response Time
                        boolean success = fields[7].equalsIgnoreCase("true"); // Check if request passed

                        samplerData.putIfAbsent(samplerName, new ArrayList<>());
                        samplerData.get(samplerName).add(responseTime);

                        samplerErrors.putIfAbsent(samplerName, 0);
                        if (!success) {
                            samplerErrors.put(samplerName, samplerErrors.get(samplerName) + 1);
                        }
                        if (count == num){
                            for (Map.Entry<String, List<Long>> entry : samplerData.entrySet()) {
                                String samplerName1 = entry.getKey();
                                List<Long> responseTimes = entry.getValue();

                                long avgResponseTime = responseTimes.stream().mapToLong(Long::longValue).sum() / responseTimes.size();
                                int errorCount = samplerErrors.get(samplerName1);
                                String status = (errorCount == 0) ? "Pass" : "Fail";

                                System.out.printf("%-18s | %-18d | %s\n", samplerName, avgResponseTime, status);
                                num = num + 50;
                            }}
                            count++;
                            System.out.println("Count : "+count+"Num : "+num);
                        }
                    }

                }
            jtlReader.close();
            System.out.println();
            // Print custom summary header
            System.out.println("Sampler Name      | Avg Response Time | Status");
            System.out.println("-------------------------------------------------");
            // Print the custom summary report
            for (Map.Entry<String, List<Long>> entry : samplerData.entrySet()) {
                String samplerName = entry.getKey();
                List<Long> responseTimes = entry.getValue();

                long avgResponseTime = responseTimes.stream().mapToLong(Long::longValue).sum() / responseTimes.size();
                int errorCount = samplerErrors.get(samplerName);
                String status = (errorCount == 0) ? "Pass" : "Fail";

                System.out.printf("%-18s | %-18d | %s\n", samplerName, avgResponseTime, status);
            }

            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());;
        }
    }

}


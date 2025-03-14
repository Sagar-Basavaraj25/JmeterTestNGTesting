package org.base;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kg.apc.jmeter.reporters.ConsoleStatusLogger;
import kg.apc.jmeter.reporters.ConsoleStatusLoggerGui;
import org.apache.jmeter.assertions.ResponseAssertion;
import org.apache.jmeter.assertions.gui.AssertionGui;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.CSVDataSet;
import org.apache.jmeter.control.CriticalSectionController;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.OnceOnlyController;
import org.apache.jmeter.control.TransactionController;
import org.apache.jmeter.control.gui.CriticalSectionControllerGui;
import org.apache.jmeter.control.gui.OnceOnlyControllerGui;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.control.gui.TransactionControllerGui;
import org.apache.jmeter.engine.JMeterEngine;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.extractor.json.jsonpath.JSONPostProcessor;
import org.apache.jmeter.extractor.json.jsonpath.gui.JSONPostProcessorGui;
import org.apache.jmeter.protocol.http.control.CacheManager;
import org.apache.jmeter.protocol.http.control.CookieManager;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.gui.CacheManagerGui;
import org.apache.jmeter.protocol.http.gui.CookiePanel;
import org.apache.jmeter.protocol.http.gui.HeaderPanel;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.testbeans.gui.TestBeanGUI;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.ListedHashTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Utils {
    private static final Logger log = LoggerFactory.getLogger(Utils.class);
    public static Properties properties = new Properties();
     public static ObjectMapper globalMapper;
    public static String loadProperties(String key) {
        try (InputStream input = new FileInputStream("configuration/config.properties")) {
            // Load the properties from the file
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception as needed, e.g., log it or throw a runtime exception
        }
        return properties.getProperty(key);
    }
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
        log.info("Jmeter Engine Started : ");
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

    public void headerManager(ListedHashTree tree, JsonNode header){
        HeaderManager headerManager = new HeaderManager();
        headerManager.setName("HTTP Header Manager");
        headerManager.setProperty("TestElement.gui_class", HeaderPanel.class.getName());
        headerManager.setProperty("TestElement.test_class", HeaderManager.class.getName());
        for(JsonNode head: header){
            headerManager.add(new Header(head.get("key").asText(),head.get("value").asText()));
        }
        tree.add(headerManager);
        log.info("Header Manager added : ");
    }
    public ListedHashTree threadGroup(String ThreadGroupName,ListedHashTree testplan,int NumThreads,int rampUpCount, int durationSec, int loops){
        ThreadGroup threadGroup= new ThreadGroup();
        threadGroup.setProperty("TestElement.test_class", ThreadGroup.class.getName());
        threadGroup.setProperty("TestElement.gui_class", ThreadGroupGui.class.getName());
        threadGroup.setName(ThreadGroupName);
        log.info("Thread Group name : " + ThreadGroupName);
        threadGroup.setNumThreads(NumThreads);
        log.info("Number of the Threads : " + NumThreads);
        threadGroup.setRampUp(rampUpCount);
        log.info("Ramp Up count : " + rampUpCount);
        threadGroup.setScheduler(true);
        threadGroup.setProperty("ThreadGroup.on_sample_error", "continue");
        threadGroup.setDuration(durationSec);
        threadGroup.setSamplerController(loopController(loops));
        log.info("Thread Group added to TestPlan : ");
        return testplan.add(threadGroup);
    }

    public ListedHashTree httpSampler(JsonNode item, ListedHashTree threadGroup){
        HTTPSamplerProxy httpSampler = new HTTPSamplerProxy();
        StringBuilder path = new StringBuilder();
        String method;
        String protocol;
        String body=null;
        int port;
        if(item.get("request").get("url").has("port")){
            port= item.get("request").get("url").get("port").asInt();
            httpSampler.setPort(port);
        }
        StringBuilder domain = new StringBuilder();
        method = item.get("request").get("method").asText();
        protocol = item.get("request").get("url").get("protocol").asText();
        JsonNode pathNode = item.get("request").get("url").get("path");
        JsonNode domainNode = item.get("request").get("url").get("host");
        if(method.equalsIgnoreCase("POST")||method.equalsIgnoreCase("PUT")||method.equalsIgnoreCase("PATCH")){
            body = item.get("request").get("body").get("raw").asText();
        }
        for(JsonNode subpath : pathNode){
            path.append("/").append(subpath.asText());
        }
        for (int i = 0; i < domainNode.size(); i++) {
            domain.append(domainNode.get(i).asText());
            if (i < domainNode.size() - 1) {
                domain.append(".");
            }
        }
        httpSampler.setProperty("TestElement.gui_class", HttpTestSampleGui.class.getName());
        httpSampler.setProperty("TestElement.test_class",HTTPSamplerProxy.class.getName());
        httpSampler.setName(item.get("name").asText());
        httpSampler.setMethod(method);
        httpSampler.setDomain(String.valueOf(domain));
        httpSampler.setPath(path.toString());
        httpSampler.setProtocol(protocol);
        httpSampler.setFollowRedirects(true);
        httpSampler.setUseKeepAlive(true);
        if (method.equalsIgnoreCase("GET")){
            httpSampler.setPostBodyRaw(false);
            httpSampler.setArguments(new Arguments());
        }
        else {
            httpSampler.setPostBodyRaw(true);
            httpSampler.addNonEncodedArgument("",body,"");
        }
        return threadGroup.add(httpSampler);
    }

    public void responseAssertion(String statusCode, ListedHashTree tree){
        ResponseAssertion responseAssertion = new ResponseAssertion();
        responseAssertion.setProperty("TestElement.gui_class", AssertionGui.class.getName());
        responseAssertion.setProperty("TestElement.test_class", ResponseAssertion.class.getName());
        responseAssertion.setName("Response Assertion");
        responseAssertion.setTestFieldResponseCode();
        responseAssertion.setToEqualsType();// This sets the assertion type to 'equals' for response code
        responseAssertion.addTestString(statusCode);
//        return responseAssertion;
        tree.add(responseAssertion);
    }
    // Create CSV Data Set Config
    public void csvDataConfig(ListedHashTree tree,String filename,JsonNode csvVariables){
        //System.out.println(filename);
        String variableName = generateCsvFile1(filename,csvVariables);
        CSVDataSet csvDataSet = new CSVDataSet();
        csvDataSet.setName("CSV Data Set Config");
        csvDataSet.setProperty("TestElement.gui_class", TestBeanGUI.class.getName());
        csvDataSet.setProperty("TestElement.test_class", CSVDataSet.class.getName());
        csvDataSet.setDelimiter(",");
        csvDataSet.setProperty("filename",filename);
        csvDataSet.setProperty("variableNames",variableName);
        csvDataSet.setProperty("fileEncoding","UTF-8");
        csvDataSet.setProperty("ignoreFirstLine",true);
        csvDataSet.setProperty("quotedData",false);
        csvDataSet.setProperty("recycle", true);
        csvDataSet.setProperty("stopThread",false);
        csvDataSet.setProperty("shareMode", "shareMode.all");
        tree.add(csvDataSet);
    }

    public LoopController loopController(int loops){
        LoopController loopController = new LoopController();
        loopController.setLoops(loops);
        loopController.setContinueForever(true);
        loopController.initialize();
        return loopController;
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

    private static String generateRandomName(Random random, int minLen, int maxLen) {
        int nameLength = minLen + random.nextInt(maxLen - minLen + 1);
        StringBuilder name = new StringBuilder();

        // Generate first letter uppercase
        name.append((char) ('A' + random.nextInt(26)));

        // Generate rest of the letters as lowercase
        for (int i = 1; i < nameLength; i++) {
            name.append((char) ('a' + random.nextInt(26)));
        }

        return name.toString();
    }
    public void addCookieManager(ListedHashTree tree){
        CookieManager cookieManager = new CookieManager();
        cookieManager.setProperty(TestElement.GUI_CLASS, CookiePanel.class.getName());
        cookieManager.setProperty(TestElement.TEST_CLASS, CookieManager.class.getName());
        cookieManager.setName("HTTP Cookie Manager");
        cookieManager.setEnabled(true);
        cookieManager.setClearEachIteration(true);
        tree.add(cookieManager);
    }
    public void addCacheManager(ListedHashTree tree){
        CacheManager cacheManager = new CacheManager();
        cacheManager.setProperty(TestElement.GUI_CLASS, CacheManagerGui.class.getName());
        cacheManager.setProperty(TestElement.TEST_CLASS,CacheManager.class.getName());
        cacheManager.setName("HTTP Cache Manager");
        cacheManager.setClearEachIteration(true);
        cacheManager.setEnabled(true);
        cacheManager.setUseExpires(true);
        tree.add(cacheManager);
    }
    public void jsonExtractor(ListedHashTree tree,String JsonPath,String JsonVariable){
        JSONPostProcessor jsonExtractor = new JSONPostProcessor();
        jsonExtractor.setProperty(TestElement.GUI_CLASS, JSONPostProcessorGui.class.getName());
        jsonExtractor.setProperty(TestElement.TEST_CLASS,JSONPostProcessor.class.getName());
        jsonExtractor.setName("JSON Extractor");
        jsonExtractor.setRefNames(JsonVariable);  // Variable Name to Store Extracted Data
        jsonExtractor.setJsonPathExpressions(JsonPath); // JSON Path Expression
        jsonExtractor.setMatchNumbers("1"); // First occurrence
        jsonExtractor.setDefaultValues("Not Found");
        tree.add(jsonExtractor);
    }
    public ListedHashTree onceOnlyController(ListedHashTree threadGroup){
        OnceOnlyController onceOnlyController = new OnceOnlyController();
        onceOnlyController.setProperty(TestElement.GUI_CLASS, OnceOnlyControllerGui.class.getName());
        onceOnlyController.setName("Only Once Controller");
        onceOnlyController.setEnabled(true);
        return threadGroup.add(onceOnlyController);
    }
    public ListedHashTree transactionController(ListedHashTree threadGroup){
        TransactionController transactionController = new TransactionController();
        transactionController.setProperty(TestElement.GUI_CLASS, TransactionControllerGui.class.getName());
        transactionController.setProperty(TestElement.TEST_CLASS, TransactionController.class.getName());
        transactionController.setName("Transaction Controller");
        transactionController.setEnabled(true);
        transactionController.setIncludeTimers(false);
        transactionController.setGenerateParentSample(true);
        transactionController.setComment("Transaction Controller Testing");
        return threadGroup.add(transactionController);
    }
    public ListedHashTree criticalSectionController(ListedHashTree threadGroup){
        CriticalSectionController criticalSectionController = new CriticalSectionController();
        criticalSectionController.setProperty(TestElement.GUI_CLASS, CriticalSectionControllerGui.class.getName());
        criticalSectionController.setProperty(TestElement.TEST_CLASS, CriticalSectionController.class.getName());
        criticalSectionController.setName("Critical Section Controller");
        criticalSectionController.setLockName("global_lock");
        return threadGroup.add(criticalSectionController);
    }
    public String generateCsvFile1(String fileName,JsonNode csvVariables){
        String headerValue="";
        Map<String,Set<String>> variables = new LinkedHashMap<String,Set<String>>();
        Random random = new Random();
        for(JsonNode csvVariable : csvVariables){
            String variableName = csvVariable.get("var_name").asText();
            Set<String> uniqueNames = new LinkedHashSet<String>();
            Set<Integer> uniqueNumbers = new LinkedHashSet<Integer>();
            String prefix="";
            int min_len=csvVariable.get("min_len").asInt();
            int max_len=csvVariable.get("max_len").asInt();
            if(!csvVariable.get("var_constant").isNull()){
                prefix = csvVariable.get("var_constant").asText();
                int prefix_len = prefix.length();
                min_len = min_len-prefix_len;
                max_len = max_len-prefix_len;
            }
            String varType = csvVariable.get("dynamic_type").asText();
            while (uniqueNames.size() < 500) {
                if(varType.equalsIgnoreCase("String")){
                    String name = prefix+generateRandomName(random,min_len,max_len);
                    uniqueNames.add(name);
                } else if (varType.equalsIgnoreCase("Number")) {
                    String num = prefix + getRandomNumber(min_len,max_len); // Range: 1 to 100,000
                    uniqueNames.add(num);
                }
            }
            variables.put(variableName,uniqueNames);
        }
        for(Map.Entry<String,Set<String>> entryset : variables.entrySet()){
                if(headerValue.equals("")){
                    headerValue=headerValue+entryset.getKey();
                }else{
                    headerValue=headerValue+","+entryset.getKey();
                }
        }
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.append(headerValue+"\n");
            List<List<String>> listOfValues = new ArrayList<>();
            for (Set<String> valueSet : variables.values()) {
                listOfValues.add(new ArrayList<>(valueSet)); // Convert each Set to a List
            }
            for (int i = 0; i < 1000; i++) {
                List<String> currentRow = new ArrayList<>();
                // Iterate over all lists and fetch the i-th element if available
                for (List<String> valueList : listOfValues) {
                    if (i < valueList.size()) {
                        currentRow.add(valueList.get(i));
                    } else {
                        currentRow.add("N/A"); // Placeholder if a list is shorter
                    }
                }
                // Print output in required format
                for (int j=0;j<currentRow.size();j++) {
                    if(j==currentRow.size()-1){
                        writer.append(currentRow.get(j));
                    }else{
                        writer.append(currentRow.get(j)+",");
                    }
                }
                writer.append("\n");
            }
            writer.close();
            System.out.println("CSV file generated successfully: "+fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return headerValue;
    }
    public static int getRandomNumber(int minDigits, int maxDigits) {
        Random random = new Random();

        // Compute min and max values based on the number of digits
        int min = (int) Math.pow(10, minDigits - 1);
        int max = (int) Math.pow(10, maxDigits) - 1;

        return random.nextInt(max - min + 1) + min;
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
            e.printStackTrace(); // Handle exceptions
        }
    }
    public void consoleLogger(ListedHashTree testplan){
        ConsoleStatusLogger consoleLog = new ConsoleStatusLogger();
        consoleLog.setProperty("TestElement.gui_class", ConsoleStatusLoggerGui.class.getName());
        consoleLog.setProperty("TestElement.test_class", ConsoleStatusLogger.class.getName());
        consoleLog.setName("jp@gc - Console Status Logger");
       // CustomConsoleLogger consoleLog = new CustomConsoleLogger();
        testplan.add(consoleLog);
    }
    public static boolean isValidJSON(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.readTree(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public static void processScenario(JsonNode scenario, JsonNode payloadRootNode, ListedHashTree testPlan, Utils utils, ObjectMapper mapper, String testName) throws Exception {
        String scenarioName = scenario.get("name").asText();
        int tps = scenario.get("tps").asInt();
        int duration = scenario.get("duration").asInt();
        int rampUp = scenario.get("rampUp").asInt();
        globalMapper = mapper;
        
        log.info("Processing Scenario: {}, TPS: {}, Duration: {}, RampUp: {}", scenarioName, tps, duration, rampUp);

        ListedHashTree threadGroup = utils.threadGroup(scenarioName, testPlan, tps, rampUp, duration, 1);
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
            utils.csvDataConfig(threadGroup, "csvFiles/" + testName + scenarioName + ".csv", csvVariables);
        }

        // Process Controllers
        for (JsonNode controller : controllers) {
            processController(controller, apiMap, threadGroup, utils, mapper,jsonExtractors);
        }

        // Process API Order
        processApiOrder(scenario.get("api_order"), apiMap, threadGroup, utils, mapper,jsonExtractors);

        log.info("Scenario processed successfully" + scenarioName);
    }

    public static void processCsvVariables(JsonNode csvVariables, Map<String,JsonNode> apiMap, ObjectMapper mapper) throws Exception {
        for (JsonNode csvVariable : csvVariables) {
            String varName = csvVariable.get("var_name").asText();
            JsonNode apis = csvVariable.get("apis");

            for (JsonNode api : apis) {
                String apiName = api.get("apiName").asText();
                String attribute = api.get("attribute").asText();
                JsonNode item = apiMap.get(apiName);
                if (apiName.equalsIgnoreCase(item.get("name").asText())) {
                        JsonNode requestBody = item.get("request").get("body").get("raw");
                        ObjectNode jsonNode = (ObjectNode) mapper.readTree(requestBody.asText());

                        jsonNode.put(attribute, "${" + varName + "}");
                        ((ObjectNode) item.get("request").get("body")).put("raw", mapper.writeValueAsString(jsonNode));
                    }
            }
        }
    }
        public static void processController(JsonNode controller, Map<String,JsonNode> apiItems, ListedHashTree threadGroup, Utils utils, ObjectMapper mapper,JsonNode jsonExtractors) {
            String controlName = controller.get("name").asText();
            log.info("Processing Controller: {}", controlName);

            ListedHashTree controllerTree;
            switch (controlName.toLowerCase()) {
                case "only-once":
                    controllerTree = utils.onceOnlyController(threadGroup);
                    break;
                case "transaction":
                    controllerTree = utils.transactionController(threadGroup);
                    break;
                case "critical-section":
                    controllerTree = utils.criticalSectionController(threadGroup);
                    break;
                default:
                    log.warn("Unknown Controller: " + controlName);
                    return;
            }

            for (JsonNode api : controller.get("apiName")) {
                addHttpSampler(api.asText(), apiItems, controllerTree, utils, mapper, jsonExtractors);
            }
        }

        public static void processApiOrder(JsonNode apiOrder, Map<String,JsonNode> apiItems, ListedHashTree threadGroup, Utils utils, ObjectMapper mapper,JsonNode jsonExtractors) {
            for (JsonNode api : apiOrder) {
                addHttpSampler(api.asText(), apiItems, threadGroup, utils, mapper,jsonExtractors);
            }
        }

        public static void addHttpSampler(String apiName, Map<String,JsonNode> apiItems, ListedHashTree parentTree, Utils utils, ObjectMapper mapper,JsonNode jsonExtractors) {
            JsonNode item = apiItems.get(apiName);
            if (apiName.equalsIgnoreCase(item.get("name").asText())) {
                    String apiMethod = item.get("request").get("method").asText();
                    ListedHashTree samplerTree = utils.httpSampler(item, parentTree);

                    if (!item.get("request").get("header").isNull()) {
                        utils.headerManager(samplerTree, item.get("request").get("header"));
                    }
                    utils.responseAssertion(apiMethod.equalsIgnoreCase("POST") ? "201" : "200", samplerTree);
                    addJsonExtractors(apiName, jsonExtractors, apiItems, samplerTree, utils,mapper);
                }
            }
        public static void addJsonExtractors(String apiName, JsonNode jsonExtractors, Map<String,JsonNode> apiItems,ListedHashTree samplerTree, Utils utils, ObjectMapper mapper)
        {
            for (JsonNode jsonExtractor : jsonExtractors) {
                if(jsonExtractor.has("apiName") && !jsonExtractor.get("apiName").isNull()) {
                    if (apiName.equalsIgnoreCase(jsonExtractor.get("apiName").asText())) {
                        String variableName = jsonExtractor.get("variableName").asText();
                        utils.jsonExtractor(samplerTree, jsonExtractor.get("JsonPath").asText(), jsonExtractor.get("variableName").asText());
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
                                    JsonNode requestBody = targetApi.get("request").get("body").get("raw");
                                    try {
                                        ObjectNode jsonNode = (ObjectNode)mapper.readTree(requestBody.asText());
                                        jsonNode.put(target_value, "${" + variableName + "}");
                                        ((ObjectNode) targetApi.get("request").get("body")).put("raw", mapper.writeValueAsString(jsonNode));
                                        System.out.println("Json Extractor variable added inside Body:");
                                    } catch (Exception e) {
                                        System.out.println("Exception occurs while adding JsonExtractor in body: " + e.getMessage());
                                    }
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


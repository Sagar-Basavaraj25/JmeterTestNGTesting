package org.base;

import com.fasterxml.jackson.databind.JsonNode;
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
import org.apache.jmeter.testbeans.gui.TestBeanGUI;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Utils {
    public static Properties properties = new Properties();
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
        JMeterUtils.loadJMeterProperties(jmeterHome + "/bin/jmeter.properties");
        JMeterUtils.initLocale();
    }

    public ListedHashTree testPlan(String testPlanName, ListedHashTree tree){
        TestPlan testPlan = new TestPlan(testPlanName);
        testPlan.setProperty("TestElement.gui_class", TestPlanGui.class.getName());
        testPlan.setProperty("TestElement.test_class", TestPlan.class.getName());
        Arguments userDefinedVars = new Arguments();
        testPlan.setUserDefinedVariables(userDefinedVars);
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
    }
    public ListedHashTree threadGroup(String ThreadGroupName,ListedHashTree testplan,int NumThreads,int rampUpCount, int durationSec, int loops){
        ThreadGroup threadGroup= new ThreadGroup();
        threadGroup.setProperty("TestElement.test_class", ThreadGroup.class.getName());
        threadGroup.setProperty("TestElement.gui_class", ThreadGroupGui.class.getName());
        threadGroup.setName(ThreadGroupName);
        threadGroup.setNumThreads(NumThreads);
        threadGroup.setRampUp(rampUpCount);
        threadGroup.setScheduler(true);
        threadGroup.setProperty("ThreadGroup.on_sample_error", "continue");
        threadGroup.setDuration(durationSec);
        threadGroup.setSamplerController(loopController(loops));
        return testplan.add(threadGroup);
    }

    public ListedHashTree httpSampler(JsonNode item, ListedHashTree threadGroup){
        String path = "",method,protocol,body = "";
        int port= item.get("request").get("url").get("port").asInt();
        StringBuilder domain = new StringBuilder();
        method = item.get("request").get("method").asText();
        protocol = item.get("request").get("url").get("protocol").asText();
        JsonNode pathNode = item.get("request").get("url").get("path");
        JsonNode domainNode = item.get("request").get("url").get("host");
        if(method.equalsIgnoreCase("POST")||method.equalsIgnoreCase("PUT")||method.equalsIgnoreCase("PATCH")){
            body = item.get("request").get("body").get("raw").asText();
        }
        for(JsonNode subpath : pathNode){
            path = path+"/"+subpath.asText();
        }
        for (int i = 0; i < domainNode.size(); i++) {
            domain.append(domainNode.get(i).asText());
            if (i < domainNode.size() - 1) {
                domain.append(".");
            }
        }
        HTTPSamplerProxy httpSampler = new HTTPSamplerProxy();
        httpSampler.setProperty("TestElement.gui_class", HttpTestSampleGui.class.getName());
        httpSampler.setProperty("TestElement.test_class",HTTPSamplerProxy.class.getName());
        httpSampler.setName(item.get("name").asText());
        httpSampler.setMethod(method);
        httpSampler.setDomain(String.valueOf(domain));
        httpSampler.setPath(path);
        httpSampler.setPort(port);
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

    public String readPayload( String fileName ) {
        String content = null;
        try {
            String projPath = System.getProperty("user.dir");
            content = Files.readString(Path.of(projPath+"/payload/"+ fileName + ".json"));
            System.out.println(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }
    public void responseAssertion(String statusCode, HashTree tree){
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
    public void csvDataConfig(ListedHashTree tree,String filename,String[] name){
        System.out.println(filename);
        generateCsvFile(filename,name);
        CSVDataSet csvDataSet = new CSVDataSet();
        csvDataSet.setName("CSV Data Set Config");
        csvDataSet.setProperty("TestElement.gui_class", TestBeanGUI.class.getName());
        csvDataSet.setProperty("TestElement.test_class", CSVDataSet.class.getName());
        csvDataSet.setDelimiter(",");
        csvDataSet.setProperty("filename",filename);
        String variableName="";
        for(int i=0;i< name.length;i++) {
            if(i!=name.length-1){
                variableName=variableName+name[i]+",";
            }else{
                variableName=variableName+name[i];
            }
        }
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
        loopController.setContinueForever(false);
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
            System.out.println("command===>" + command);

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
    public void generateCsvFile(String fileName,String[] headerValue){
        Map<String,Set<String>> variables = new LinkedHashMap<String,Set<String>>();
        Random random = new Random();
        for(int i=0;i< headerValue.length;i++){
            if(!headerValue[i].equalsIgnoreCase("ID")){
                Set<String> uniqueNames = new HashSet<>();
                while (uniqueNames.size() < 10) {
                    String name = generateRandomName(random, 6, 10); // Generates a name of length 6-10
                    uniqueNames.add(name);
                }
                variables.put(headerValue[i],uniqueNames);
            }
        }
        try (FileWriter writer = new FileWriter(fileName)) {
            String s="";
            for (int i=0;i<headerValue.length;i++){
                if(s.equalsIgnoreCase("")){
                    s=s+headerValue[i];
                }else{
                    s= s+","+headerValue[i];
                }
            }
            writer.append(s+"\n");
            List<List<String>> listOfValues = new ArrayList<>();
            for (Set<String> valueSet : variables.values()) {
                listOfValues.add(new ArrayList<>(valueSet)); // Convert each Set to a List
            }
            int id = 1;
            for (int i = 0; i < 10; i++) {
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
                writer.append(id + ",");
                for (int j=0;j<currentRow.size();j++) {
                    if(j==currentRow.size()-1){
                        writer.append(currentRow.get(j));
                    }else{
                        writer.append(currentRow.get(j)+",");
                    }
                }
                writer.append("\n");
                id++;
            }
            System.out.println("CSV file with unique random names generated successfully!");
        } catch (IOException e) {
            e.printStackTrace();
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
        cookieManager.setName("Cookie Manager");
        cookieManager.setEnabled(true);
        cookieManager.setClearEachIteration(true);
        tree.add(cookieManager);
    }
    public void addCacheManager(ListedHashTree tree){
        CacheManager cacheManager = new CacheManager();
        cacheManager.setProperty(TestElement.GUI_CLASS,CacheManagerGui.class.getName());
        cacheManager.setProperty(TestElement.TEST_CLASS,CacheManager.class.getName());
        cacheManager.setName("Cache Manager");
        cacheManager.setClearEachIteration(true);
        cacheManager.setEnabled(true);
        tree.add(cacheManager);
    }
    public void JsonExtractor(ListedHashTree tree,String JsonPath,String JsonVariable){
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
}


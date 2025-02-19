package org.base;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.jmeter.assertions.ResponseAssertion;
import org.apache.jmeter.assertions.gui.AssertionGui;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.CSVDataSet;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.gui.HeaderPanel;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.testbeans.gui.TestBeanGUI;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.ListedHashTree;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

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
    public int deriveUsers(int tps, int durationSec, double apiResponseTime){
        //To calculate the actual number of threads required, we need to estimate the "throughput" of a single thread in terms of how many transactions it can complete in 1 second.
        //Transaction per thread per sec = (1/API response time)
//        double transPerThreadsPerSec = (1/apiResponseTime);
        //Thread = (Desired tps / Transaction per thread per sec )
        int numThreads = (int)(tps*apiResponseTime);
        System.out.println("Numbers of Users :" + numThreads);
        return numThreads;
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
    public ListedHashTree threadGroup(String ThreadGroupName,ListedHashTree testplan,int tps,int rampUpCount, int durationSec, double apiResponseTime, int loops){
        ThreadGroup threadGroup= new ThreadGroup();
        threadGroup.setProperty("TestElement.test_class", ThreadGroup.class.getName());
        threadGroup.setProperty("TestElement.gui_class", ThreadGroupGui.class.getName());
        threadGroup.setName(ThreadGroupName);
        int numThread = deriveUsers( tps, durationSec, apiResponseTime);
        threadGroup.setNumThreads(numThread);
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
    public void csvDataSet(ListedHashTree tree) {
        CSVDataSet csvDataSet = new CSVDataSet();
        csvDataSet.setName("CSV Data Set Config");
        csvDataSet.setProperty("TestElement.gui_class", TestBeanGUI.class.getName());
        csvDataSet.setProperty("TestElement.test_class", CSVDataSet.class.getName());
        csvDataSet.setProperty("filename", "unique_random_numbers.csv"); // Set CSV File path if needed
        csvDataSet.setProperty("fileEncoding","UTF-8");
        csvDataSet.setProperty("variableNames","ID");
        csvDataSet.setProperty("delimiter", ",");
        csvDataSet.setProperty("quotedData", "false");
        csvDataSet.setProperty("ignoreFirstLine", "true");
        csvDataSet.setProperty("recycle", "true");
        csvDataSet.setProperty("stopThread", "false");
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
             }}
}

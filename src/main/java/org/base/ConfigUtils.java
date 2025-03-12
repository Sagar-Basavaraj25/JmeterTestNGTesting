package org.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.javafaker.Faker;
import kg.apc.jmeter.reporters.ConsoleStatusLogger;
import kg.apc.jmeter.reporters.ConsoleStatusLoggerGui;
import org.apache.jmeter.config.CSVDataSet;
import org.apache.jmeter.protocol.http.control.CacheManager;
import org.apache.jmeter.protocol.http.control.CookieManager;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.gui.CacheManagerGui;
import org.apache.jmeter.protocol.http.gui.CookiePanel;
import org.apache.jmeter.protocol.http.gui.HeaderPanel;
import org.apache.jmeter.testbeans.gui.TestBeanGUI;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.collections.ListedHashTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ConfigUtils {
    private static final Logger log = LoggerFactory.getLogger(ConfigUtils.class);
    ObjectMapper mapper = new ObjectMapper();
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
    public void csvDataConfig(ListedHashTree tree,String filename,JsonNode csvVariables){
        String variableName = generateCsvFile(filename,csvVariables);
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
    public void changePayloadData(JsonNode csvVariables,JsonNode items) throws JsonProcessingException {
        try {
            for (JsonNode csvVariable : csvVariables) {
                JsonNode apis = csvVariable.get("apis");
                String varName = csvVariable.get("var_name").asText();
                for (JsonNode api : apis) {
                    String apiName = api.get("apiName").asText();
                    String attribute = api.get("attribute").asText();
                    for (JsonNode item : items) {
                        System.out.println(apiName + " : " + item.get("name").asText());
                        if (apiName.equalsIgnoreCase(item.get("name").asText())) {
                            String text = item.get("request").get("body").get("raw").asText();
                            System.out.println(text);
                            JsonNode jsonNode = mapper.readTree(text);
                            ObjectNode objectNode = (ObjectNode) jsonNode;
                            String variable = "${" + varName + "}";
                            objectNode.put(attribute, variable);
                            System.out.println("ObjectNode direct " + objectNode);
                            System.out.println(mapper.writeValueAsString(objectNode));
                            ObjectNode object = (ObjectNode) item.get("request").get("body");
                            object.put("raw", mapper.writeValueAsString(objectNode));
                            log.info("Successfully changed the "+attribute+" value in "+apiName+" to "+variable);
                        }
                    }
                }
            }
        }catch (Exception e){
            log.error("Error in changing the Payload Data"+e.getMessage());
            throw new RuntimeException("Cannot change the Payoad Data");
        }
    }
    public String generateCsvFile(String fileName,JsonNode csvVariables){
        Faker faker = new Faker();
        String headerValue="";
        Map<String, Set<String>> variables = new LinkedHashMap<String,Set<String>>();
        Random random = new Random();
        for(JsonNode csvVariable : csvVariables){
            String variableName = csvVariable.get("var_name").asText();
            Set<String> uniqueNames = new LinkedHashSet<String>();
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
            String randomValue="";
            while (uniqueNames.size() < 1000) {
                switch (varType.toLowerCase()){
                    case "string":
                        randomValue=prefix+generateRandomName(random,min_len,max_len);
                        break;
                    case "number":
                        randomValue=prefix + getRandomNumber(min_len,max_len);
                        break;
                    case "email":
                        randomValue=faker.internet().emailAddress();
                        break;
                }
                uniqueNames.add(randomValue);
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
    public void consoleLogger(ListedHashTree testplan){
        ConsoleStatusLogger consoleLog = new ConsoleStatusLogger();
        consoleLog.setProperty("TestElement.gui_class", ConsoleStatusLoggerGui.class.getName());
        consoleLog.setProperty("TestElement.test_class", ConsoleStatusLogger.class.getName());
        consoleLog.setName("jp@gc - Console Status Logger");
        testplan.add(consoleLog);
    }
}

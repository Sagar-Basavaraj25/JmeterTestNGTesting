import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.jmeter.save.SaveService;
import org.apache.jorphan.collections.ListedHashTree;
import org.base.Utils;
import org.testng.annotations.Test;

import java.io.*;
public class ExecutionFile {

    public static void main(String[] argv) throws Exception {

        //load properties
        Utils utils = new Utils();
        String payloadPath = Utils.loadProperties("payloadFile");
        int tps = Integer.parseInt(Utils.loadProperties("tps"));
        int rampUpTimeSec = Integer.parseInt(Utils.loadProperties("rampUp"));
        int duration = Integer.parseInt(Utils.loadProperties("duration"));
        double apiResponseTime = Double.parseDouble(Utils.loadProperties("apiResponseTime"));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(new File(payloadPath));
        //Start Jmeter Engine
        utils.initJmeter();
        String testName = rootNode.get("info").get("name").asText();

        //Creating hash tree
        ListedHashTree ListedHashTree= new ListedHashTree();
        ListedHashTree testPlan = utils.testPlan(testName, ListedHashTree);
        ListedHashTree threadGroup = utils.threadGroup("ThreadGroup",testPlan,tps,rampUpTimeSec,duration, apiResponseTime,-1);

            // sampler
        JsonNode items = rootNode.get("item");
        for (JsonNode item : items) {
            String apiMethod = item.get("request").get("method").asText();
            ListedHashTree sampler = utils.httpSampler(item,threadGroup);
            if(!item.get("request").get("header").isNull()){
                JsonNode header = item.get("request").get("header");
                utils.headerManager(sampler,header);
            }
            if(apiMethod.equalsIgnoreCase("POST")){
                utils.responseAssertion("200",sampler);
//                utils.csvDataSet(sampler);
            }else {
                utils.responseAssertion("200",sampler);
            }

        }

        // Save Test Plan to JMX File
        String jmxFile = utils.JMXFileCreator(testName);
        try (FileOutputStream fos = new FileOutputStream(jmxFile)) {
            SaveService.saveTree(ListedHashTree, fos);
        }
        //Command-Line execution
        utils.runJmxFile(jmxFile);
    }
}

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.jmeter.save.SaveService;
import org.apache.jorphan.collections.ListedHashTree;
import org.base.Utils;
import java.io.*;

public class ExecutionFile {
    public static void main(String[] argv) throws Exception {

        //load properties
        Utils utils = new Utils();
        String payload = Utils.loadProperties("payloadFile");
        String api = Utils.loadProperties("apiName");
        System.out.println("API " +api);
        String variableName = Utils.loadProperties("dynamic_value_variables");
        System.out.println(variableName);
        String[] variables = variableName.split(",");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(new File(payload));
        //Start Jmeter Engine
        utils.initJmeter();
        String testName = rootNode.get("info").get("name").asText();

        ListedHashTree hashTree= new ListedHashTree();
        ListedHashTree testPlan = utils.testPlan(testName, hashTree);
        utils.addCacheManager(testPlan);
        utils.addCookieManager(testPlan);
        String filePath = "csvFiles/Global_config.csv";
        utils.csvDataConfig(testPlan,filePath,variables);
        ListedHashTree threadGroup = utils.threadGroup("ThreadGroup",testPlan,20,1,30,1);
        JsonNode items = rootNode.get("item");
        for (JsonNode item : items) {
            String apiMethod = item.get("request").get("method").asText();
            String apiName = item.get("name").asText();
            ListedHashTree sampler = utils.httpSampler(item, threadGroup);
            if (!item.get("request").get("header").isNull()) {
                JsonNode header = item.get("request").get("header");
                utils.headerManager(sampler, header);
            }
            if (apiMethod.equalsIgnoreCase("POST")) {
                utils.responseAssertion("201", sampler);
            } else {
                utils.responseAssertion("200", sampler);
            }
            if(apiName.equalsIgnoreCase("signin")){
                utils.JsonExtractor(sampler,".*JSON","ID");
            }
        }
        // Save Test Plan to JMX File
        String jmxFile = utils.JMXFileCreator(testName);
        try (FileOutputStream fos = new FileOutputStream(jmxFile)) {
            SaveService.saveTree(hashTree, fos);
        }
        //Command-Line execution
        //utils.runJmxFile(jmxFile);
    }
}

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.jmeter.save.SaveService;
import org.apache.jorphan.collections.ListedHashTree;
import org.base.ConfigUtils;
import org.base.ListenerUtils;
import org.base.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;

public class JmeterAutomation {

    private static final Logger log = LoggerFactory.getLogger(JmeterAutomation.class);

    public static void main(String[] args) throws Exception {
        Utils utils = new Utils();
        ListenerUtils listenerUtils = new ListenerUtils();
        ConfigUtils configUtils = new ConfigUtils();
        utils.initJmeter();
        ObjectMapper mapper = new ObjectMapper();
        // Read Configuration JSON
        JsonNode configRootNode = mapper.readTree(new File("configuration/jsonConfig.json"));
        String payloadFile = configRootNode.get("payloadFile").asText();
        log.info("Payload File: {}", payloadFile);

        // Read Payload JSON
        JsonNode payloadRootNode = mapper.readTree(new File(payloadFile));
        String testName = payloadRootNode.get("info").get("name").asText();
        log.info("Test Plan Name: {}", testName);

        // Initialize Test Plan
        ListedHashTree hashTree = new ListedHashTree();
        ListedHashTree testPlan = utils.testPlan(testName, hashTree);
        configUtils.addCookieManager(testPlan);
        configUtils.addCacheManager(testPlan);

        // Process Scenarios
        JsonNode scenarios = configRootNode.get("scenario");
        for (JsonNode scenario : scenarios) {
            JsonNode payloadRootNode1 = mapper.readTree(new File(payloadFile));
            utils.processScenario(scenario, payloadRootNode1, testPlan, utils, mapper, testName);
        }
        listenerUtils.backendListener(testPlan);
        // Save Test Plan to JMX File
        String jmxFile = utils.JMXFileCreator(testName);
        try (FileOutputStream fos = new FileOutputStream(jmxFile)) {
            SaveService.saveTree(hashTree, fos);
        }
        log.info("JMX File created: {}", jmxFile);
        utils.runJmxFile(jmxFile);
    }

}

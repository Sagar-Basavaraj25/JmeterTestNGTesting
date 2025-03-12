import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.save.SaveService;
import org.apache.jorphan.collections.ListedHashTree;
import org.base.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileOutputStream;

    public class CodeOpti {
        private static final Logger log = LoggerFactory.getLogger(CodeOpti.class);

        public static void main(String[] args) throws Exception {
            Utils utils = new Utils();
            StandardJMeterEngine jMeterEngine = utils.initJmeter();
            ObjectMapper mapper = new ObjectMapper();

            // Read Configuration JSON
            JsonNode configRootNode = mapper.readTree(new File("configuration/config.json"));
            String payloadFile = configRootNode.get("payloadFile").asText();
            log.info("Payload File: {}", payloadFile);

            // Read Payload JSON
            JsonNode payloadRootNode = mapper.readTree(new File(payloadFile));
            String testName = payloadRootNode.get("info").get("name").asText();
            log.info("Test Plan Name: {}", testName);

            // Initialize Test Plan
            ListedHashTree hashTree = new ListedHashTree();
            ListedHashTree testPlan = utils.testPlan(testName, hashTree);
            utils.addCookieManager(testPlan);
            utils.addCacheManager(testPlan);

            // Process Scenarios
            JsonNode scenarios = configRootNode.get("scenario");
            for (JsonNode scenario : scenarios) {
                Utils.processScenario(scenario, payloadRootNode, testPlan, utils, mapper, testName);
            }

            // Save Test Plan to JMX File
            String jmxFile = utils.JMXFileCreator(testName);
            try (FileOutputStream fos = new FileOutputStream(jmxFile)) {
                SaveService.saveTree(hashTree, fos);
            }
            log.info("JMX File created: {}", jmxFile);
        }

        //utils.runJmxFile(jmxFile);
    }


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.jmeter.save.SaveService;
import org.apache.jorphan.collections.ListedHashTree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.base.ConfigUtils;
import org.base.ListenerUtils;
import org.base.ThreadGroupUtils;
import org.base.Utils;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.base.Utils.addThreadGroup;

public class SmokeTest {
    private static final Logger log = LogManager.getLogger(SmokeTest.class);
    public static String errorTolerance =null,jmxFile=null;
    @Test(groups = {"smoke"})
    public void jmxSmokeFileCreation(){
        try{
            Utils utils = new Utils();
            ThreadGroupUtils threadGroupUtils = new ThreadGroupUtils();
            ListenerUtils listenerUtils = new ListenerUtils();
            ConfigUtils configUtils = new ConfigUtils();
            utils.initJmeter();
            ObjectMapper mapper = new ObjectMapper();
            // Read Configuration JSON
            JsonNode configRootNode = mapper.readTree(new File("configuration/CustomConfig.json"));
            String payloadFile = configRootNode.get("payloadFile").asText();
            log.info("Payload File: {}", payloadFile);

            // Read Payload JSON
            JsonNode payloadRootNode = mapper.readTree(new File(payloadFile));
            String testName = payloadRootNode.get("info").get("name").asText();
            log.info("Test Plan Name: {}", testName);

            // Initialize Test Plan
            errorTolerance = configRootNode.get("errorTolerance").asText();
            ListedHashTree hashTree = new ListedHashTree();
            ListedHashTree testPlan = utils.testPlan(testName, hashTree);
            log.info("Test plan is added to the tree");
            configUtils.addCookieManager(testPlan);
            configUtils.addCacheManager(testPlan);

            // Process Scenarios
            JsonNode scenarios = configRootNode.get("scenario");
            for (JsonNode scenario : scenarios) {
                JsonNode payloadRootNode1 = mapper.readTree(new File(payloadFile));
                ListedHashTree threadGroup = threadGroupUtils.smokeThreadGroup(testPlan);
                utils.processScenario(threadGroup,scenario,payloadRootNode1,testName);
            }
            //listenerUtils.backendListener(testPlan);
            listenerUtils.addJsr223Listener(testPlan);
            // Save Test Plan to JMX File
            jmxFile = utils.JMXFileCreator(testName);
            FileOutputStream fos = new FileOutputStream(jmxFile);
            SaveService.saveTree(hashTree, fos);
        } catch (Exception e) {
            log.info("Error Message : {}",e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Test(groups = {"smoke"}, dependsOnMethods = {"jmxSmokeFileCreation"})
    public void runSmoke(){
        try {
            Utils utils = new Utils();
            LocalDateTime today = LocalDateTime.now();
            String dateString = today.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String logs = "target/jmeterLogs/" + dateString + ".jtl";
            String report = "target/jmeterReports/"+dateString;
            String command = "jmeter -n -t "+jmxFile+" -l "+logs+" -e -o "+report;

            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            Process process = processBuilder.start();
            processBuilder.redirectErrorStream(true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            Pattern pattern = Pattern.compile("Err:\\s+\\d+\\s+\\((\\d+\\.\\d+)%\\)");
            Matcher matcher;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String errorPercentage = matcher.group(1);
                    if(((int)Double.parseDouble(errorPercentage))>0){
                        process.destroy();
                        utils.failureReport(logs);
                        break;
                    }
                }
            }
            log.info("Jmeter Smoke Ended Successfully");
            SharedStatus.jmxCompletedLatch.countDown();
            // Read the output of the command
            int exitCode = process.waitFor();
            String fileName = utils.aggregateReport(logs);
            log.info("Command exited with code: " + exitCode);
        } catch (Exception e) {
            log.error("Error Occured while running the jmx : "+e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error : "+e);

        }
    }
}

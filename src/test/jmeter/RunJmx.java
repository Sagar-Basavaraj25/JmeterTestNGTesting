

import java.io.File;
import java.io.IOException;

import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.report.config.ConfigurationException;
import org.apache.jmeter.report.dashboard.GenerationException;
import org.apache.jmeter.report.dashboard.ReportGenerator;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jorphan.collections.HashTree;
import org.base.Utils;
public class RunJmx {
    public static void main(String[] args) throws IOException{
        Utils utils = new Utils();
        StandardJMeterEngine engine = utils.initJmeter();
        File testPlan = new File("target/jmxfiles/Studentsapi_20250303_171424.jmx");
        HashTree testTree = SaveService.loadTree(testPlan);
        Summariser summariser = new Summariser("summary");


        // Create JTL result collector to store results
        String jtlFile = "target/jmeterLogs/results1.jtl";
        ResultCollector resultCollector = new ResultCollector(summariser);
        resultCollector.setFilename(jtlFile);
        testTree.add(testTree.getArray()[0], resultCollector);
        //HtmlReportGenerator generator = new HtmlReportGenerator(jtlFile,"src/test/resources/user.properties","target/jmeterReports");
        /*ResultCollector resultCollector1 = new ResultCollector() {
            @Override
            public void sampleOccurred(SampleEvent e) {
                SampleResult result = e.getResult();
                System.out.println("Sample: " + result.getSampleLabel() +
                        " | Status: " + (result.isSuccessful() ? "PASS" : "FAIL") +
                        " | Response Time: " + result.getTime() + "ms");
            }
        };
        testTree.add(testTree.getArray()[0], resultCollector1);
*/
        engine.configure(testTree);
        engine.run();
        try {
            ReportGenerator reportGenerator = new ReportGenerator(jtlFile, null);
            reportGenerator.generate();
            System.out.println("HTML Report generated at: ");
        } catch (GenerationException e) {
            System.err.println("Error generating HTML report: " + e.getMessage());
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}

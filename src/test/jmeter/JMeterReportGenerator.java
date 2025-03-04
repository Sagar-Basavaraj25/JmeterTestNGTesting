import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.report.config.ConfigurationException;
import org.apache.jmeter.report.dashboard.ReportGenerator;
import org.apache.jmeter.report.dashboard.GenerationException;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

import java.io.File;
import java.io.IOException;

public class JMeterReportGenerator {
    public static void main(String[] args) throws IOException {
            // Define paths
            String jmxFilePath = "target/jmxfiles/Studentsapi_20250227_162120.jmx"; // Your JMX file
            String resultsFile = "target/jmeterLogs/results1.jtl"; // JTL results file
            String reportOutputDir = "target/jmeterReports"; // Destination HTML report directory
            String customProperties = "src/test/resources/jmeter.properties"; // Custom JMeter properties file

            // Load custom JMeter properties
            JMeterUtils.loadJMeterProperties(customProperties);
            JMeterUtils.setProperty("jmeter.reportgenerator.exporter.html.property.output_dir", reportOutputDir); // Override report directory
            JMeterUtils.initLocale();
            SaveService.loadProperties();

            // Load and run JMeter Test Plan
            StandardJMeterEngine jmeter = new StandardJMeterEngine();
            HashTree testPlanTree;
            try {
                File jmxFile = new File(jmxFilePath);
                if (!jmxFile.exists()) {
                    System.err.println("JMX file not found at: " + jmxFilePath);
                    return;
                }
                testPlanTree = SaveService.loadTree(jmxFile);
            } catch (Exception e) {
                System.err.println("Error loading JMX file: " + e.getMessage());
                return;
            }

            jmeter.configure(testPlanTree);
            jmeter.run(); // Run the test

            // Generate HTML Report
            try {
                ReportGenerator reportGenerator = new ReportGenerator(resultsFile, null);
                reportGenerator.generate();
                System.out.println("HTML Report successfully generated at: " + reportOutputDir);
            } catch (GenerationException | ConfigurationException e) {
                System.err.println("Error generating HTML report: " + e.getMessage());
            }
        }
}


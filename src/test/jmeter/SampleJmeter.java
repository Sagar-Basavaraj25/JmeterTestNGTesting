

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.gui.HeaderPanel;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.ListedHashTree;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;

public class SampleJmeter {
    @Test
    public static void jmxCreation() throws Exception {
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
        TestPlan testPlan = new TestPlan("Test Plan");
        testPlan.setProperty("TestElement.gui_class", TestPlanGui.class.getName());
        testPlan.setProperty("TestElement.test_class", TestPlan.class.getName());

        // Add User Defined Variables (Empty)
        Arguments userDefinedVars = new Arguments();
        testPlan.setUserDefinedVariables(userDefinedVars);

        // Create Header Manager (Empty)
        HeaderManager headerManager = new HeaderManager();
        headerManager.setName("HTTP Header Manager");
        headerManager.setProperty("TestElement.gui_class", HeaderPanel.class.getName());
        headerManager.setProperty("TestElement.test_class", HeaderManager.class.getName());

        // Create Thread Group
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setProperty("TestElement.test_class", ThreadGroup.class.getName());
        threadGroup.setProperty("TestElement.gui_class", ThreadGroupGui.class.getName());
        threadGroup.setName("Thread Group");
        threadGroup.setNumThreads(20);
        threadGroup.setRampUp(1);
        threadGroup.setScheduler(false);
        threadGroup.setProperty("ThreadGroup.on_sample_error", "continue");
        threadGroup.setDuration(30);

        // Create Loop Controller
        LoopController loopController = new LoopController();
        loopController.setLoops(1);
        loopController.setContinueForever(false);
        loopController.initialize();
        threadGroup.setSamplerController(loopController);

        // Create HTTP Sampler
        HTTPSamplerProxy httpSampler = new HTTPSamplerProxy();
        httpSampler.setProperty("TestElement.gui_class", HttpTestSampleGui.class.getName());
        httpSampler.setProperty("TestElement.test_class",HTTPSamplerProxy.class.getName());
        httpSampler.setName("HTTP Request");
        httpSampler.setMethod("GET");
        httpSampler.setProtocol("Https");
        httpSampler.setDomain("www.google.com");
        httpSampler.setPath("/");
        httpSampler.setFollowRedirects(true);
        httpSampler.setUseKeepAlive(true);
        httpSampler.setPostBodyRaw(true);
        httpSampler.setArguments(new Arguments());// Empty Arguments

        // Create CSV Data Set Config
//        CSVDataSet csvDataSet = new CSVDataSet();
//        csvDataSet.setName("CSV Data Set Config");
//        csvDataSet.setProperty("TestElement.gui_class", TestBeanGUI.class.getName());
//        csvDataSet.setProperty("TestElement.test_class", CSVDataSet.class.getName());
//        csvDataSet.setProperty("filename", ""); // Set CSV File path if needed
//        csvDataSet.setProperty("delimiter", ",");
//        csvDataSet.setProperty("quotedData", "false");
//        csvDataSet.setProperty("ignoreFirstLine", "false");
//        csvDataSet.setProperty("recycle", "true");
//        csvDataSet.setProperty("stopThread", "false");
//        csvDataSet.setProperty("shareMode", "shareMode.all");

        // Create ListedHashTree (Test Plan Structure)
        ListedHashTree testPlanTree = new ListedHashTree();
        ListedHashTree threadGroupTree = testPlanTree.add(testPlan);
        ListedHashTree samplerTree = threadGroupTree.add(threadGroup);

        samplerTree.add(httpSampler);
        //samplerTree.add(csvDataSet);
        threadGroupTree.add(headerManager);


        // Save Test Plan to JMX File
        File jmxFile = new File("test-plan.jmx");
        try (FileOutputStream fos = new FileOutputStream(jmxFile)) {
            SaveService.saveTree(testPlanTree, fos);
        }

        System.out.println("JMeter Test Plan created successfully: " + jmxFile.getAbsolutePath());
    }
}


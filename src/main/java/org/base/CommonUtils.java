package org.base;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.gui.HeaderPanel;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.util.JMeterUtils;

public class CommonUtils {
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
    public TestPlan testPlan(){
        TestPlan testPlan = new TestPlan("Test Plan");
        testPlan.setProperty("TestElement.gui_class", TestPlanGui.class.getName());
        testPlan.setProperty("TestElement.test_class", TestPlan.class.getName());

        // Add User Defined Variables (Empty)
        Arguments userDefinedVars = new Arguments();
        testPlan.setUserDefinedVariables(userDefinedVars);
        return testPlan;
    }
    public HeaderManager headerManager(){
        HeaderManager headerManager = new HeaderManager();
        headerManager.setName("HTTP Header Manager");
        headerManager.setProperty("TestElement.gui_class", HeaderPanel.class.getName());
        headerManager.setProperty("TestElement.test_class", HeaderManager.class.getName());
        return headerManager;
    }
    public ThreadGroup threadGroup(){
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setProperty("TestElement.test_class", ThreadGroup.class.getName());
        threadGroup.setProperty("TestElement.gui_class", ThreadGroupGui.class.getName());
        threadGroup.setName("Thread Group");
        threadGroup.setNumThreads(20);
        threadGroup.setRampUp(1);
        threadGroup.setScheduler(false);
        threadGroup.setProperty("ThreadGroup.on_sample_error", "continue");
        threadGroup.setDuration(30);
        threadGroup.setSamplerController(loopController());
        return threadGroup;
    }
    public LoopController loopController(){
        LoopController loopController = new LoopController();
        loopController.setLoops(1);
        loopController.setContinueForever(false);
        loopController.initialize();
        return loopController;
    }
    public HTTPSamplerProxy httpSampler(){
        HTTPSamplerProxy httpSampler = new HTTPSamplerProxy();
        httpSampler.setProperty("TestElement.gui_class", HttpTestSampleGui.class.getName());
        httpSampler.setProperty("TestElement.test_class",HTTPSamplerProxy.class.getName());
        httpSampler.setName("HTTP Request");
        httpSampler.setMethod("GET");
        httpSampler.setDomain("www.google.com");
        httpSampler.setPath("/");
        httpSampler.setFollowRedirects(true);
        httpSampler.setUseKeepAlive(true);
        httpSampler.setPostBodyRaw(false);
        httpSampler.setArguments(new Arguments());
        return httpSampler;
    }
}

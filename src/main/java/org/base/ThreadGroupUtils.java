package org.base;

import com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroup;
import com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroupGui;
import com.fasterxml.jackson.databind.JsonNode;
import kg.apc.jmeter.threads.UltimateThreadGroup;
import kg.apc.jmeter.threads.UltimateThreadGroupGui;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.apache.jmeter.testelement.property.StringProperty;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jorphan.collections.ListedHashTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadGroupUtils {
    private static final Logger log = LoggerFactory.getLogger(ThreadGroupUtils.class);
    Utils utils = new Utils();
    public ListedHashTree threadGroup(ListedHashTree testplan, JsonNode scenario){
        int tps = scenario.get("tps").asInt();
        log.info("TPS value: "+tps);
        int duration = scenario.get("duration").asInt();
        log.info("Duration value: "+duration);
        int rampUp = scenario.get("rampUp").asInt();
        log.info("RampUp value: "+rampUp);
        String tGName = scenario.get("name").asText();
        log.info("ThreadGroup Name: "+tGName);
        int loopCount = scenario.has("loopCount")?scenario.get("loopCount").asInt():-1;
        ThreadGroup threadGroup=new ThreadGroup();
        threadGroup.setProperty("TestElement.test_class", ThreadGroup.class.getName());
        threadGroup.setProperty("TestElement.gui_class", ThreadGroupGui.class.getName());
        threadGroup.setName(tGName);
        log.info("Thread Group name : " + tGName);
        double responseTime = scenario.get("responseTime").asDouble();
        threadGroup.setNumThreads(utils.numberUsers(tps,responseTime));
        log.info("Number of the Threads : " + tps);
        threadGroup.setRampUp(rampUp);
        log.info("Ramp Up count : " + rampUp);
        threadGroup.setScheduler(true);
        threadGroup.setProperty("ThreadGroup.on_sample_error", "continue");
        threadGroup.setDuration(duration);
        threadGroup.setSamplerController(loopController(loopCount));
        log.info("Thread Group added to TestPlan : ");
        return testplan.add(threadGroup);
    }
    public LoopController loopController(int loops){
        LoopController loopController = new LoopController();
        loopController.setLoops(loops);
        loopController.initialize();
        return loopController;
    }
    public ListedHashTree concurrentThreadGroup(ListedHashTree testPlan,JsonNode scenario){
        int tps = scenario.get("tps").asInt();
        log.info("TPS value: "+tps);
        int duration = scenario.get("duration").asInt();
        log.info("Duration value: "+duration);
        int rampUp = scenario.get("rampUp").asInt();
        log.info("RampUp value: "+rampUp);
        String tGName = scenario.get("name").asText();
        log.info("ThreadGroup Name: "+tGName);
        double responseTime = scenario.get("responseTime").asDouble();
        int loopCount = scenario.has("loopCount")?scenario.get("loopCount").asInt():-1;
        int step = scenario.has("step")?scenario.get("loopCount").asInt():3;
        ConcurrencyThreadGroup threadGroup = new ConcurrencyThreadGroup();
        threadGroup.setName(tGName);
        threadGroup.setProperty("TestElement.test_class", ConcurrencyThreadGroup.class.getName());
        threadGroup.setProperty("TestElement.gui_class", ConcurrencyThreadGroupGui.class.getName());
        threadGroup.setProperty("ThreadGroup.on_sample_error", "continue");
        threadGroup.setProperty("TargetLevel", utils.numberUsers(tps,responseTime));
        threadGroup.setProperty("RampUp", rampUp);
        threadGroup.setProperty("Steps", step);
        threadGroup.setProperty("Hold", duration);
        threadGroup.setProperty("Unit", "S");
        threadGroup.setSamplerController(loopController(loopCount));
        return testPlan.add(threadGroup);
    }
    public ListedHashTree ulimateThreadGroup(ListedHashTree testPlan,JsonNode scenario){
        UltimateThreadGroup threadGroup = new UltimateThreadGroup();
        threadGroup.setProperty(TestElement.TEST_CLASS,UltimateThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS, UltimateThreadGroupGui.class.getName());
        threadGroup.setName(scenario.get("name").asText());
        JsonNode ultimateArray = scenario.get("ultimate");
        CollectionProperty rows = new CollectionProperty();
        rows.setName(UltimateThreadGroup.DATA_PROPERTY);
        for (JsonNode threadConfig : ultimateArray) {
            CollectionProperty rowProperty = new CollectionProperty();
            rowProperty.setName(UltimateThreadGroup.EXTERNAL_DATA_PROPERTY);
            // Extract values from JSON and add them as StringProperty
            rowProperty.addItem(new StringProperty("Thread Count", threadConfig.get("thread").asText()));
            rowProperty.addItem(new StringProperty("Initial Delay", threadConfig.get("initial_delay").asText()));
            rowProperty.addItem(new StringProperty("Startup Time", threadConfig.get("startup_time").asText()));
            rowProperty.addItem(new StringProperty("Hold Time", threadConfig.get("hold_Time").asText()));
            rowProperty.addItem(new StringProperty("Shutdown Time", threadConfig.get("shutdown_time").asText()));

            // Add rowProperty to the main CollectionProperty
            rows.addItem(rowProperty);
            System.out.printf(String.valueOf(rows));
        }
        threadGroup.setData(rows);
        System.out.println(threadGroup.getData());
        threadGroup.setSamplerController(loopController(-1));
        return testPlan.add(threadGroup);
    }
}

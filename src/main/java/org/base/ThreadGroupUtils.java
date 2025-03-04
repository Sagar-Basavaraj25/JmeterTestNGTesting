package org.base;

import com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroup;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jorphan.collections.ListedHashTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadGroupUtils {
    private static final Logger log = LoggerFactory.getLogger(ThreadGroupUtils.class);
    public ListedHashTree threadGroup(String ThreadGroupName, ListedHashTree testplan, int NumThreads, int rampUpCount, int durationSec, int loops){
        org.apache.jmeter.threads.ThreadGroup threadGroup= new org.apache.jmeter.threads.ThreadGroup();
        threadGroup.setProperty("TestElement.test_class", ThreadGroup.class.getName());
        threadGroup.setProperty("TestElement.gui_class", ThreadGroupGui.class.getName());
        threadGroup.setName(ThreadGroupName);
        log.info("Thread Group name : " + ThreadGroupName);
        threadGroup.setNumThreads(NumThreads);
        log.info("Number of the Threads : " + NumThreads);
        threadGroup.setRampUp(rampUpCount);
        log.info("Ramp Up count : " + rampUpCount);
        threadGroup.setScheduler(true);
        threadGroup.setProperty("ThreadGroup.on_sample_error", "continue");
        threadGroup.setDuration(durationSec);
        threadGroup.setSamplerController(loopController(loops));
        log.info("Thread Group added to TestPlan : ");
        return testplan.add(threadGroup);
    }
    public LoopController loopController(int loops){
        LoopController loopController = new LoopController();
        loopController.setLoops(loops);
        loopController.setContinueForever(true);
        log.info("Loop Controller - Set Continue forever - true");
        loopController.initialize();
        return loopController;
    }
    public ListedHashTree concurrentThreadGroup(ListedHashTree testPlan,String threadGroupName,int NumThreads, int rampUp,int step, int durationSec, int loops){
        ConcurrencyThreadGroup threadGroup = new ConcurrencyThreadGroup();
        threadGroup.setName(threadGroupName);
        threadGroup.setProperty("ThreadGroup.on_sample_error", "continue");
        threadGroup.setProperty("TargetLevel", NumThreads);
        threadGroup.setProperty("RampUp", rampUp);
        threadGroup.setProperty("Steps", step);
        threadGroup.setProperty("Hold", durationSec);
        threadGroup.setProperty("Unit", "S");
        return testPlan.add(threadGroup);
    }
}

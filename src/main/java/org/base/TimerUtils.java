package org.base;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.jmeter.testbeans.gui.TestBeanGUI;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.timers.ConstantThroughputTimer;
import org.apache.jmeter.timers.ConstantTimer;
import org.apache.jmeter.timers.SyncTimer;
import org.apache.jmeter.timers.gui.ConstantTimerGui;
import org.apache.jorphan.collections.ListedHashTree;

import javax.swing.*;

public class TimerUtils {
    public void constantThroughputTimer(ListedHashTree tree, JsonNode timers){
        ConstantThroughputTimer timer = new ConstantThroughputTimer();
        timer.setName("Constant Throughput Timer");
        timer.setProperty(TestElement.TEST_CLASS,ConstantThroughputTimer.class.getName());
        timer.setProperty(TestElement.GUI_CLASS, TestBeanGUI.class.getName());
        timer.setEnabled(true);
        int tps = timers.get("delay/throughput").asInt();
        String calcMode = timers.get("throughput_base/groups").asText();
        int setCalcMode;
        switch (calcMode) {
            case "ThisThreadOnly":
                setCalcMode = 1;
                break;
            case "AllActiveThreads":
                 setCalcMode = 2;
                break;
            case "AllActiveThreadsInCurrentThreadGroup":
                 setCalcMode = 3;
                break;
            case "AllActiveThreads_Shared":
                 setCalcMode = 4;
                break;
            case "AllActiveThreadsInCurrentThreadGroup_Shared":
                setCalcMode = 5;
                break;
            default:
                System.out.println("Unknown CalcMode: " + calcMode);
                return;
        }

        timer.setCalcMode(setCalcMode);
        timer.setThroughput(tps * 60);
        tree.add(timer);
    }
    public void syncTimer(ListedHashTree tree,JsonNode timers){
        SyncTimer timer = new SyncTimer();
        timer.setName("Synchronizing Timer");
        timer.setProperty(TestElement.TEST_CLASS,SyncTimer.class.getName());
        timer.setProperty(TestElement.GUI_CLASS,TestBeanGUI.class.getName());
        timer.setEnabled(true);
        int groupSize = timers.get("throughput_base/groups").asInt();
        int timeoutInMs = timers.get("delay/throughput").asInt();
        timer.setProperty("groupSize",groupSize);
        timer.setProperty("timeoutInMs",timeoutInMs);
        tree.add(timer);
    }
    public void constantTimer(ListedHashTree tree,JsonNode timers){
        ConstantTimer timer = new ConstantTimer();
        timer.setName("Constant Timer");
        timer.setProperty(TestElement.TEST_CLASS,ConstantTimer.class.getName());
        timer.setProperty(TestElement.GUI_CLASS, ConstantTimerGui.class.getName());
        String delay = timers.get("delay/throughput").asText();
        timer.setDelay(delay);
        tree.add(timer);
    }
}

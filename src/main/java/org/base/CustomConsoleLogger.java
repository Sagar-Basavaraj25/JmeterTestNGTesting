package org.base;

import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.TestStateListener;

public class CustomConsoleLogger extends AbstractTestElement implements SampleListener, TestStateListener {

    private static final long serialVersionUID = 1L;

    @Override
    public void sampleOccurred(SampleEvent event) {
        SampleResult result = event.getResult();
        System.out.println("✅ Sample Completed: " + result.getSampleLabel());
        System.out.println("📡 Thread Name: " + event.getThreadGroup());
        System.out.println("⏳ Response Time: " + result.getTime() + " ms");
        System.out.println("📥 Response Data: " + new String(result.getResponseData()));
        System.out.println("🔍 Status: " + (result.isSuccessful() ? "PASS" : "FAIL"));
    }

    @Override
    public void sampleStarted(SampleEvent event) {
        SampleResult result = event.getResult();
        System.out.println("📢 Sample Started: " + result.getSampleLabel());
    }

    @Override
    public void sampleStopped(SampleEvent event) {
        System.out.println("⛔ Sample Stopped: " + event.getResult().getSampleLabel());
    }

    @Override
    public void testStarted() {
        System.out.println("🚀 JMeter Test Started");
    }

    @Override
    public void testStarted(String host) {
        System.out.println("🚀 JMeter Test Started on Host: " + host);
    }

    @Override
    public void testEnded() {
        System.out.println("🛑 JMeter Test Completed");
    }

    @Override
    public void testEnded(String host) {
        System.out.println("🛑 JMeter Test Completed on Host: " + host);
    }
}

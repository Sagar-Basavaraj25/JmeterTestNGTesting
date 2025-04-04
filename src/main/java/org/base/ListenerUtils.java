package org.base;

import kg.apc.jmeter.reporters.ConsoleStatusLogger;
import kg.apc.jmeter.reporters.ConsoleStatusLoggerGui;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.testbeans.gui.TestBeanGUI;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.TestElementProperty;
import org.apache.jmeter.visualizers.JSR223Listener;
import org.apache.jmeter.visualizers.backend.BackendListener;
import org.apache.jmeter.visualizers.backend.BackendListenerGui;
import org.apache.jorphan.collections.ListedHashTree;

public class ListenerUtils {
    public void consoleLogger(ListedHashTree testplan){
        ConsoleStatusLogger consoleLog = new ConsoleStatusLogger();
        consoleLog.setProperty("TestElement.gui_class", ConsoleStatusLoggerGui.class.getName());
        consoleLog.setProperty("TestElement.test_class", ConsoleStatusLogger.class.getName());
        consoleLog.setName("jp@gc - Console Status Logger");
        testplan.add(consoleLog);
    }
    public void addJsr223Listener(ListedHashTree testplan){
        JSR223Listener listener = new JSR223Listener();
        listener.setName("JSR223 Listener");
        listener.setProperty("TestElement.test_class",JSR223Listener.class.getName());
        listener.setProperty("TestElement.gui_class", TestBeanGUI.class.getName());
        listener.setProperty("scriptLanguage", "groovy");
        listener.setProperty("cacheKey", "true");
//        String groovyScript = """
//                // Extract basic details
//                def samplerName = prev.getSampleLabel()
//                def threadName = prev.getThreadName()
//                def responseCode = prev.getResponseCode()
//                def responseMessage = prev.getResponseMessage()
//                def responseTime = prev.getTime()
//                def status = prev.isSuccessful() ? "PASS" : "FAIL"
//                def requestHeaders = prev.getRequestHeaders()
//                def responseHeaders = prev.getResponseHeaders()
//                def responseData = prev.getResponseDataAsString()
//                def requestData = sampler.getArguments().getArgument(0).getValue()
//        // Logging data
//        println " ThreadName: ${threadName}, Sampler: ${samplerName}, Response Time: ${responseTime}ms, Status: ${status}, Message: ${responseMessage}"
//        """;
        String groovyScript = """
                import org.apache.jmeter.samplers.SampleResult
                
                // Fetch current sample details
                SampleResult result = prev
                if (result != null) {
                    String samplerName = result.getSampleLabel()
                    long avgTime = result.getTime()  // Average Response Time
                    long minTime = result.getStartTime()  // Min Time
                    long maxTime = result.getEndTime()  // Max Time
                    long medianTime = result.getTime() // Approximate median (for demo)
                    long latency = result.getLatency()
                   \s
                    // Print dynamic aggregate report in a single line using \r (carriage return)
                    print "\rSampler: ${samplerName} | Avg: ${avgTime} ms | Median: ${medianTime} ms | Min: ${minTime} | Max: ${maxTime} | Latency: ${latency} ms      "
                   \s
                    // Flush the output to ensure it's visible in real-time
                    System.out.flush()
                }
                
                """;
        listener.setProperty("script", groovyScript);
        //println "Request Body: ${requestData}"
        testplan.add(listener);
    }
    public void backendListener(ListedHashTree tree){
        BackendListener listener = new BackendListener();
        listener.setName("Backend Listener");
        listener.setProperty(TestElement.TEST_CLASS,BackendListener.class.getName());
        listener.setProperty(TestElement.GUI_CLASS, BackendListenerGui.class.getName());
        System.out.println("Backend Listener Implementation: " + listener.getPropertyAsString("classname"));
        listener.setProperty("classname", "org.apache.jmeter.visualizers.backend.influxdb.InfluxdbBackendListenerClient");

        // Create the Arguments object for listener configuration
        Arguments listenerArguments = new Arguments();
        listenerArguments.setProperty(TestElement.TEST_CLASS,Arguments.class.getName());
        listenerArguments.setProperty(TestElement.GUI_CLASS,ArgumentsPanel.class.getName());
        listenerArguments.addArgument("influxdbMetricsSender", "org.apache.jmeter.visualizers.backend.influxdb.HttpMetricsSender","=");
        listenerArguments.addArgument("influxdbUrl", "http://localhost:8086/write?db=jmeter","=");
        listenerArguments.addArgument("application", "JMeterTest","=");
        listenerArguments.addArgument("measurement", "jmeter","=");
        listenerArguments.addArgument("summaryOnly", "false","=");
        listenerArguments.addArgument("percentiles","90;95;99","=");
        listenerArguments.addArgument("eventTags","","=");
        listenerArguments.addArgument("testTitle","Test name","=");
        listenerArguments.addArgument("samplersRegex",".*","=");

// Set the arguments as a property of the Backend Listener
        listener.setProperty(new TestElementProperty(BackendListener.ARGUMENTS, listenerArguments));
        System.out.println("Backend Listener Implementation: " + listener.getPropertyAsString("classname"));
        tree.add(listener);
    }
}

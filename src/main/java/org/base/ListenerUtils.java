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

import java.io.FileInputStream;
import java.util.Properties;

public class ListenerUtils {
    public void consoleLogger(ListedHashTree testplan){
        ConsoleStatusLogger consoleLog = new ConsoleStatusLogger();
        consoleLog.setProperty("TestElement.gui_class", ConsoleStatusLoggerGui.class.getName());
        consoleLog.setProperty("TestElement.test_class", ConsoleStatusLogger.class.getName());
        consoleLog.setName("jp@gc - Console Status Logger");
        testplan.add(consoleLog);
    }
    public void addJsr223Listener(ListedHashTree testPlan){
        JSR223Listener listener = new JSR223Listener();
        listener.setName("JSR223 Listener");
        listener.setProperty("TestElement.test_class",JSR223Listener.class.getName());
        listener.setProperty("TestElement.gui_class", TestBeanGUI.class.getName());
        listener.setProperty("scriptLanguage", "groovy");
        listener.setProperty("cacheKey", "true");
        String groovyScript = """
            import java.io.File
            import java.io.FileWriter
                
            // Define file path 
            def filePath = "D:/github/PT_Jmeter/JmeterTestNGTesting/target/smokeLogs/jmeter_results.csv"  // Update this as needed
            def file = new File(filePath)
                
            // Check if file already exists to add header only once
            def writeHeader = !file.exists()
                
            // Create writer in append mode
            def writer = new FileWriter(file, true)
                
            // Write header if file is newly created
            if (writeHeader) {
               writer.write("SamplerName,Status,ResponseData,ResponseTime\\n")
            }
                
            // Fetch values from the sample
            def samplerName = sampler.getName()
            def status = prev.isSuccessful() ? "Pass" : "Fail"
            def responseData = prev.getResponseDataAsString().replaceAll("[\\\\r\\\\n]+", " ").replaceAll(",", ";")  // clean line breaks & commas
            def responseTime = prev.getTime()
                
            // Write data line
            writer.write("${samplerName},${status},\\"${responseData}\\",${responseTime}\\n")
                
            // Flush and close
            writer.flush()
            writer.close()
            """;
        listener.setProperty("script", groovyScript);
        //println "Request Body: ${requestData}"
        testPlan.add(listener);
    }
    public void backendListener(ListedHashTree tree){
        Properties file = new Properties();
        String database=null,measurement=null;
        try{
            FileInputStream fis = new FileInputStream("configuration/configuration.properties");
            file.load(fis);
            database = file.getProperty("influxdb");
            measurement = file.getProperty("measurement");
        }catch (Exception e){
            e.printStackTrace();
        }
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
        listenerArguments.addArgument("influxdbUrl", "http://localhost:8086/write?db="+database,"=");
        listenerArguments.addArgument("application", "JMeterTest","=");
        listenerArguments.addArgument("measurement", measurement,"=");
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

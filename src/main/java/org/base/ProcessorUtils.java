package org.base;

import org.apache.jmeter.extractor.BeanShellPostProcessor;
import org.apache.jmeter.extractor.json.jsonpath.JSONPostProcessor;
import org.apache.jmeter.extractor.json.jsonpath.gui.JSONPostProcessorGui;
import org.apache.jmeter.modifiers.BeanShellPreProcessor;
import org.apache.jmeter.protocol.jdbc.processor.JDBCPostProcessor;
import org.apache.jmeter.protocol.jdbc.processor.JDBCPreProcessor;
import org.apache.jmeter.testbeans.gui.TestBeanGUI;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.StringProperty;
import org.apache.jorphan.collections.ListedHashTree;

public class ProcessorUtils {
    public void jsonExtractor(ListedHashTree tree, String JsonPath, String JsonVariable){
        JSONPostProcessor jsonExtractor = new JSONPostProcessor();
        jsonExtractor.setProperty(TestElement.GUI_CLASS, JSONPostProcessorGui.class.getName());
        jsonExtractor.setProperty(TestElement.TEST_CLASS,JSONPostProcessor.class.getName());
        jsonExtractor.setName("JSON Extractor");
        jsonExtractor.setRefNames(JsonVariable);  // Variable Name to Store Extracted Data
        jsonExtractor.setJsonPathExpressions(JsonPath); // JSON Path Expression
        jsonExtractor.setMatchNumbers("1"); // First occurrence
        jsonExtractor.setDefaultValues("Not Found");
        tree.add(jsonExtractor);
    }
    public void jdbcPostProcessor(ListedHashTree tree){
        JDBCPostProcessor postProcessor = new JDBCPostProcessor();
        postProcessor.setName("JDBC Post Processor");
        postProcessor.setProperty(TestElement.GUI_CLASS, TestBeanGUI.class.getName());
        postProcessor.setProperty(TestElement.TEST_CLASS,JDBCPostProcessor.class.getName());
        postProcessor.setProperty("queryType","Select Statement");
        postProcessor.setProperty("query","Select * FROM table");
        //postProcessor.setQuery("Select * from table");
        postProcessor.setProperty("queryTimeout","10");
        postProcessor.setProperty("dataSource","organisation");
        postProcessor.setProperty("variableNames","ID,NAME");
        postProcessor.setProperty("queryArguments","ABC");
        postProcessor.setProperty("queryArgumentsTypes","DEF");
        postProcessor.setProperty("resultSetHandler","Store as a String");
        postProcessor.setProperty("resultSetMaxRows","20");
        postProcessor.setProperty("resultVariable","Hello");
        tree.add(postProcessor);
    }
    public void jdbcPreProcessor(ListedHashTree tree){
        JDBCPreProcessor preProcessor = new JDBCPreProcessor();
        preProcessor.setName("JDBC Pre Processor");
        preProcessor.setProperty(TestElement.TEST_CLASS,JDBCPreProcessor.class.getName());
        preProcessor.setProperty(TestElement.GUI_CLASS,TestBeanGUI.class.getName());
        preProcessor.setProperty("queryType","Select Statement");
        preProcessor.setProperty("query","Select * FROM table");
        preProcessor.setProperty("queryTimeout","10");
        preProcessor.setProperty("dataSource","organisation");
        preProcessor.setProperty("variableNames","ID,NAME");
        preProcessor.setProperty("queryArguments","ABC");
        preProcessor.setProperty("queryArgumentsTypes","DEF");
        preProcessor.setProperty("resultSetHandler","Store as a String");
        preProcessor.setProperty("resultSetMaxRows","20");
        preProcessor.setProperty("resultVariable","Hello");
        tree.add(preProcessor);
    }
    public void beanShellPostProcessor(ListedHashTree tree){
        BeanShellPostProcessor postProcessor = new BeanShellPostProcessor();
        postProcessor.setName("Bean Shell PostProcessor");
        postProcessor.setProperty(TestElement.TEST_CLASS,BeanShellPostProcessor.class.getName());
        postProcessor.setProperty(TestElement.GUI_CLASS,TestBeanGUI.class.getName());
        postProcessor.setProperty("filename","Hello");
        postProcessor.setProperty("parameters","World");
        postProcessor.setProperty("resetInterpreter",true);
        postProcessor.setProperty("script","Hello hello helloooo");
        tree.add(postProcessor);
    }
    public void beanShellPreProcessor(ListedHashTree tree){
        BeanShellPreProcessor preProcessor = new BeanShellPreProcessor();
        preProcessor.setName("Bean Shell PreProcessor");
        preProcessor.setProperty(TestElement.TEST_CLASS,BeanShellPreProcessor.class.getName());
        preProcessor.setProperty(TestElement.GUI_CLASS,TestBeanGUI.class.getName());
        preProcessor.setProperty("filename","Hello");
        preProcessor.setProperty("parameters","World");
        preProcessor.setProperty("resetInterpreter",true);
        preProcessor.setProperty("script","Hello hello helloooo");
        tree.add(preProcessor);
    }
}

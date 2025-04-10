package org.base;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.jmeter.extractor.BeanShellPostProcessor;
import org.apache.jmeter.extractor.RegexExtractor;
import org.apache.jmeter.extractor.gui.RegexExtractorGui;
import org.apache.jmeter.extractor.json.jsonpath.JSONPostProcessor;
import org.apache.jmeter.extractor.json.jsonpath.gui.JSONPostProcessorGui;
import org.apache.jmeter.modifiers.BeanShellPreProcessor;
import org.apache.jmeter.protocol.jdbc.processor.JDBCPostProcessor;
import org.apache.jmeter.protocol.jdbc.processor.JDBCPreProcessor;
import org.apache.jmeter.testbeans.gui.TestBeanGUI;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.collections.ListedHashTree;

public class ProcessorUtils {
    public void jsonExtractor(ListedHashTree tree,JsonNode processor){
        JSONPostProcessor jsonExtractor = new JSONPostProcessor();
        jsonExtractor.setProperty(TestElement.GUI_CLASS, JSONPostProcessorGui.class.getName());
        jsonExtractor.setProperty(TestElement.TEST_CLASS,JSONPostProcessor.class.getName());
        jsonExtractor.setName("JSON Extractor");
        String JsonVariable = processor.get("proc_variableName").asText();
        jsonExtractor.setRefNames(JsonVariable);// Variable Name to Store Extracted Data
        String jsonPath = processor.get("path").asText();
        jsonExtractor.setJsonPathExpressions(jsonPath); // JSON Path Expression
        jsonExtractor.setMatchNumbers("1"); // First occurrence
        jsonExtractor.setDefaultValues("Not Found");
        tree.add(jsonExtractor);
        beanShellPostProcessor(tree,processor);
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
    public void beanShellPostProcessor(ListedHashTree tree,JsonNode processor){
        BeanShellPostProcessor postProcessor = new BeanShellPostProcessor();
        String variableName = processor.get("proc_variableName").asText();
        postProcessor.setName("Bean Shell PostProcessor");
        postProcessor.setProperty(TestElement.TEST_CLASS,BeanShellPostProcessor.class.getName());
        postProcessor.setProperty(TestElement.GUI_CLASS,TestBeanGUI.class.getName());
        postProcessor.setProperty("filename","");
        postProcessor.setProperty("parameters","");
        postProcessor.setProperty("resetInterpreter",true);
        postProcessor.setProperty("script","props.put(\""+variableName+"\",vars.get(\""+variableName+"\"))");
        tree.add(postProcessor);
    }
    public void beanShellPreProcessor(ListedHashTree tree,JsonNode processor) {
        BeanShellPreProcessor preProcessor = new BeanShellPreProcessor();
        preProcessor.setName("Bean Shell PreProcessor");
        preProcessor.setProperty(TestElement.TEST_CLASS, BeanShellPreProcessor.class.getName());
        preProcessor.setProperty(TestElement.GUI_CLASS, TestBeanGUI.class.getName());
        preProcessor.setProperty("filename", "Hello");
        preProcessor.setProperty("parameters", "World");
        preProcessor.setProperty("resetInterpreter", true);
        preProcessor.setProperty("script", "Hello hello helloooo");
        tree.add(preProcessor);
    }
        public void regexPostProcessor(ListedHashTree tree, JsonNode processors){
            RegexExtractor regexExtractor = new RegexExtractor();
            regexExtractor.setName("Regular Expression Extractor");
            regexExtractor.setProperty(TestElement.TEST_CLASS,RegexExtractor.class.getName());
            regexExtractor.setProperty(TestElement.GUI_CLASS, RegexExtractorGui.class.getName());
            String variableName = processors.get("proc_variableName").asText();
            String regex = processors.get("path").asText();
            regexExtractor.setProperty("useHeaders",false);
            regexExtractor.setProperty("setRefName",variableName);
            regexExtractor.setProperty("setRegex",regex);
            regexExtractor.setProperty("template",1);
            regexExtractor.setProperty("default_empty_value",true);
            regexExtractor.setProperty("match_number",1);
            tree.add(regexExtractor);
            beanShellPostProcessor(tree,processors);
        }
    }

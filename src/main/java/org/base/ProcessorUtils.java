package org.base;

import org.apache.jmeter.extractor.json.jsonpath.JSONPostProcessor;
import org.apache.jmeter.extractor.json.jsonpath.gui.JSONPostProcessorGui;
import org.apache.jmeter.testelement.TestElement;
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
}

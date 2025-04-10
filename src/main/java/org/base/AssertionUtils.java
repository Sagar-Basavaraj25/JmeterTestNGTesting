package org.base;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.jmeter.assertions.DurationAssertion;
import org.apache.jmeter.assertions.JSONPathAssertion;
import org.apache.jmeter.assertions.ResponseAssertion;
import org.apache.jmeter.assertions.SizeAssertion;
import org.apache.jmeter.assertions.gui.AssertionGui;
import org.apache.jmeter.assertions.gui.DurationAssertionGui;
import org.apache.jmeter.assertions.gui.JSONPathAssertionGui;
import org.apache.jmeter.assertions.gui.SizeAssertionGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.collections.ListedHashTree;

public class AssertionUtils {
    public void responseAssertion(String statusCode, ListedHashTree tree, JsonNode assertionNode){
        ResponseAssertion responseAssertion = new ResponseAssertion();
        responseAssertion.setProperty("TestElement.gui_class", AssertionGui.class.getName());
        responseAssertion.setProperty("TestElement.test_class", ResponseAssertion.class.getName());
        responseAssertion.setName("Response Assertion");
        responseAssertion.setTestFieldResponseCode();
        responseAssertion.setToEqualsType();// This sets the assertion type to 'equals' for response code
        responseAssertion.addTestString(statusCode);
        tree.add(responseAssertion);
    }
    public void jsonAssertion(ListedHashTree tree, JsonNode assertionNode){
        JSONPathAssertion assertion = new JSONPathAssertion();
        assertion.setProperty(TestElement.TEST_CLASS,JSONPathAssertion.class.getName());
        assertion.setProperty(TestElement.GUI_CLASS, JSONPathAssertionGui.class.getName());
        assertion.setName("JSON Assertion");
        String jsonPath = assertionNode.get("jsonPath").asText();
        String expectedValue = assertionNode.get("ExpectedValue").asText();
        assertion.setJsonPath(jsonPath);
        assertion.setExpectedValue(expectedValue);
        assertion.setJsonValidationBool(true);
        assertion.setExpectNull(false);
        assertion.setInvert(false);
        assertion.setIsRegex(true);
        tree.add(assertion);
    }
    public void sizeAssertion(ListedHashTree tree,  JsonNode assertionNode){
        SizeAssertion assertion = new SizeAssertion();
        assertion.setProperty(TestElement.TEST_CLASS,SizeAssertion.class.getName());
        assertion.setProperty(TestElement.GUI_CLASS, SizeAssertionGui.class.getName());
        assertion.setName("Size Assertion");
        assertion.setTestFieldNetworkSize(); // input inside switch
        assertion.setAllowedSize(30); // input
        assertion.setCompOper(1); // input inside switch
        tree.add(assertion);
    }
    public void durationAssertion(ListedHashTree tree,  JsonNode assertionNode){
        DurationAssertion assertion = new DurationAssertion();
        assertion.setName("Duration Assertion");
        assertion.setProperty(TestElement.TEST_CLASS,DurationAssertion.class.getName());
        assertion.setProperty(TestElement.GUI_CLASS, DurationAssertionGui.class.getName());
        int allowedDuration = assertionNode.get("duration/value").asInt();
        assertion.setAllowedDuration(allowedDuration);
        tree.add(assertion);
    }
}

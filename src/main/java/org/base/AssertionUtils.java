package org.base;

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
    public void responseAssertion(String statusCode, ListedHashTree tree){
        ResponseAssertion responseAssertion = new ResponseAssertion();
        responseAssertion.setProperty("TestElement.gui_class", AssertionGui.class.getName());
        responseAssertion.setProperty("TestElement.test_class", ResponseAssertion.class.getName());
        responseAssertion.setName("Response Assertion");
        responseAssertion.setTestFieldResponseCode();
        responseAssertion.setToEqualsType();// This sets the assertion type to 'equals' for response code
        responseAssertion.addTestString(statusCode);
        tree.add(responseAssertion);
    }
    public void jsonAssertion(ListedHashTree tree){
        JSONPathAssertion assertion = new JSONPathAssertion();
        assertion.setProperty(TestElement.TEST_CLASS,JSONPathAssertion.class.getName());
        assertion.setProperty(TestElement.GUI_CLASS, JSONPathAssertionGui.class.getName());
        assertion.setName("JSON Assertion");
        assertion.setJsonPath("Json Path");
        assertion.setExpectedValue("Hello");
        assertion.setJsonValidationBool(true);
        assertion.setExpectNull(false);
        assertion.setInvert(false);
        assertion.setIsRegex(true);
        tree.add(assertion);
    }
    public void sizeAssertion(ListedHashTree tree){
        SizeAssertion assertion = new SizeAssertion();
        assertion.setProperty(TestElement.TEST_CLASS,SizeAssertion.class.getName());
        assertion.setProperty(TestElement.GUI_CLASS, SizeAssertionGui.class.getName());
        assertion.setName("Size Assertion");
        assertion.setTestFieldNetworkSize(); // input inside switch
        assertion.setAllowedSize(30); // input
        //assertion.setTestFieldResponseBody();
        //assertion.setTestFieldResponseCode();
        //assertion.setTestFieldResponseHeaders();
        //assertion.setTestFieldResponseMessage();
        assertion.setCompOper(1); // input inside switch
        tree.add(assertion);
    }
    public void durationAssertion(ListedHashTree tree){
        DurationAssertion assertion = new DurationAssertion();
        assertion.setName("Duration Assertion");
        assertion.setProperty(TestElement.TEST_CLASS,DurationAssertion.class.getName());
        assertion.setProperty(TestElement.GUI_CLASS, DurationAssertionGui.class.getName());
        assertion.setAllowedDuration(10);
        tree.add(assertion);
    }
}

package org.base;

import org.apache.jmeter.assertions.ResponseAssertion;
import org.apache.jmeter.assertions.gui.AssertionGui;
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
}

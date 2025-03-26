package org.base;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.sampler.DebugSampler;
import org.apache.jmeter.sampler.TestAction;
import org.apache.jmeter.sampler.gui.TestActionGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.collections.ListedHashTree;

public class SamplerUtils {
    public ListedHashTree httpSampler(JsonNode item, ListedHashTree threadGroup){
        HTTPSamplerProxy httpSampler = new HTTPSamplerProxy();
        StringBuilder path = new StringBuilder();
        String method;
        String protocol;
        String body=null;
        if(item.get("request").get("url").has("port")){
            int port= item.get("request").get("url").get("port").asInt();
            httpSampler.setPort(port);
        }
        StringBuilder domain = new StringBuilder();
        method = item.get("request").get("method").asText();
        protocol = item.get("request").get("url").get("protocol").asText();
        JsonNode pathNode = item.get("request").get("url").get("path");
        JsonNode domainNode = item.get("request").get("url").get("host");
        if(method.equalsIgnoreCase("POST")||method.equalsIgnoreCase("PUT")||method.equalsIgnoreCase("PATCH")){
            body = item.get("request").get("body").get("raw").asText();
        }
        for(JsonNode subpath : pathNode){
            path.append("/").append(subpath.asText());
        }
        for (int i = 0; i < domainNode.size(); i++) {
            domain.append(domainNode.get(i).asText());
            if (i < domainNode.size() - 1) {
                domain.append(".");
            }
        }
        httpSampler.setProperty("TestElement.gui_class", HttpTestSampleGui.class.getName());
        httpSampler.setProperty("TestElement.test_class",HTTPSamplerProxy.class.getName());
        httpSampler.setName(item.get("name").asText());
        httpSampler.setMethod(method);
        httpSampler.setDomain(String.valueOf(domain));
        httpSampler.setPath(path.toString());
        httpSampler.setProtocol(protocol);
        httpSampler.setFollowRedirects(true);
        httpSampler.setUseKeepAlive(true);
        if (method.equalsIgnoreCase("GET")){
            httpSampler.setPostBodyRaw(false);
            httpSampler.setArguments(new Arguments());
        }
        else {
            httpSampler.setPostBodyRaw(true);
            httpSampler.addNonEncodedArgument("",body,"");
        }
        return threadGroup.add(httpSampler);
    }
    public ListedHashTree flowControlActionSampler(ListedHashTree threadGroup){
        TestAction flowControlAction = new TestAction();
        flowControlAction.setProperty(TestElement.GUI_CLASS, TestActionGui.class.getName());
        flowControlAction.setName("Flow Control Action");
        flowControlAction.setAction(1); //input
        flowControlAction.setTarget(0);
        flowControlAction.setDuration("0");
        return threadGroup.add(flowControlAction);
    }
    public ListedHashTree debugSampler(ListedHashTree threadGroup){
        DebugSampler debugSampler = new DebugSampler();
        debugSampler.setName("Debug Sampler");
        debugSampler.setProperty("displayJMeterProperties", true);
        debugSampler.setProperty("displayJMeterVariables", true);
        debugSampler.setProperty("displaySystemProperties", true);
        return threadGroup.add(debugSampler);
    }
}

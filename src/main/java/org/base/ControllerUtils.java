package org.base;

import org.apache.jmeter.control.*;
import org.apache.jmeter.control.gui.*;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.collections.ListedHashTree;

public class ControllerUtils {
    public ListedHashTree onceOnlyController(ListedHashTree threadGroup){
        OnceOnlyController onceOnlyController = new OnceOnlyController();
        onceOnlyController.setProperty(TestElement.GUI_CLASS, OnceOnlyControllerGui.class.getName());
        onceOnlyController.setName("Only Once Controller");
        onceOnlyController.setEnabled(true);
        return threadGroup.add(onceOnlyController);
    }
    public ListedHashTree transactionController(ListedHashTree threadGroup){
        TransactionController transactionController = new TransactionController();
        transactionController.setProperty(TestElement.GUI_CLASS, TransactionControllerGui.class.getName());
        transactionController.setProperty(TestElement.TEST_CLASS, TransactionController.class.getName());
        transactionController.setName("Transaction Controller");
        transactionController.setEnabled(true);
        transactionController.setIncludeTimers(false);
        transactionController.setGenerateParentSample(true);
        transactionController.setComment("Transaction Controller Testing");
        return threadGroup.add(transactionController);
    }
    public ListedHashTree criticalSectionController(ListedHashTree threadGroup){
        CriticalSectionController criticalSectionController = new CriticalSectionController();
        criticalSectionController.setProperty(TestElement.GUI_CLASS, CriticalSectionControllerGui.class.getName());
        criticalSectionController.setProperty(TestElement.TEST_CLASS, CriticalSectionController.class.getName());
        criticalSectionController.setName("Critical Section Controller");
        criticalSectionController.setLockName("global_lock");
        return threadGroup.add(criticalSectionController);
    }
    public ListedHashTree loopController(ListedHashTree threadGroup,int loopCount){
        LoopController loopController = new LoopController();
        loopController.setProperty(TestElement.TEST_CLASS,LoopController.class.getName());
        loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
        loopController.setName("Loop Controller");
        loopController.setLoops(loopCount);
        return threadGroup.add(loopController);
    }
    public ListedHashTree randomController(ListedHashTree threadGroup){
        RandomController randomController=new RandomController();
        randomController.setProperty(TestElement.TEST_CLASS,RandomController.class.getName());
        randomController.setProperty(TestElement.GUI_CLASS, RandomControlGui.class.getName());
        randomController.setName("Random Controller");
        return threadGroup.add(randomController);
    }
    public ListedHashTree runTimeController(ListedHashTree threadGroup,Long duration){
        RunTime runTime = new RunTime();
        runTime.setName("RunTime Controller");
        runTime.setProperty(TestElement.TEST_CLASS,RunTime.class.getName());
        runTime.setProperty(TestElement.GUI_CLASS,RunTimeGui.class.getName());
        runTime.setRuntime(duration);
        return threadGroup.add(runTime);
    }
    public ListedHashTree simpleController(ListedHashTree threadGroup){
        GenericController simple = new GenericController();
        simple.setName("Simple Controller");
        simple.setProperty(TestElement.TEST_CLASS,GenericController.class.getName());
        simple.setProperty(TestElement.GUI_CLASS,LogicControllerGui.class.getName());
        return threadGroup.add(simple);
    }
}

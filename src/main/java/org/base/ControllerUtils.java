package org.base;

import org.apache.jmeter.control.CriticalSectionController;
import org.apache.jmeter.control.OnceOnlyController;
import org.apache.jmeter.control.TransactionController;
import org.apache.jmeter.control.gui.CriticalSectionControllerGui;
import org.apache.jmeter.control.gui.OnceOnlyControllerGui;
import org.apache.jmeter.control.gui.TransactionControllerGui;
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
}

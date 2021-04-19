package apiminer.internal.analysis.model;

import gr.uom.java.xmi.UMLOperation;

public class OperationModel {
    private UMLOperation umlOperation;
    private boolean isRefactored = false;
    public OperationModel(UMLOperation umlOperation){
        this.umlOperation = umlOperation;
    }

    public UMLOperation getUmlOperation() {
        return umlOperation;
    }

    public boolean isRefactored() {
        return isRefactored;
    }

    public void setRefactored(boolean refactored) {
        isRefactored = refactored;
    }
}

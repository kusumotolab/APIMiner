package apiminer.internal.analysis.model;

import gr.uom.java.xmi.UMLOperation;

public class MethodModel {
    private final UMLOperation umlOperation;
    private boolean isRefactored = false;
    public MethodModel(UMLOperation umlOperation){
        this.umlOperation = umlOperation;
    }

    public UMLOperation getUmlOperation() {
        return umlOperation;
    }

    public boolean getIsRefactored() {
        return isRefactored;
    }

    public void setRefactored(boolean refactored) {
        isRefactored = refactored;
    }
}

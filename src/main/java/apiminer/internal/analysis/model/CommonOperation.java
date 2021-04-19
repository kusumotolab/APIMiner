package apiminer.internal.analysis.model;

import gr.uom.java.xmi.UMLOperation;

public class CommonOperation {
    private UMLOperation originalOperation;
    private UMLOperation nextOperation;

    public CommonOperation(UMLOperation originalOperation,UMLOperation nextOperation){
        this.originalOperation = originalOperation;
        this.nextOperation = nextOperation;
    }

    public UMLOperation getOriginalOperation() {
        return originalOperation;
    }

    public UMLOperation getNextOperation() {
        return nextOperation;
    }
}

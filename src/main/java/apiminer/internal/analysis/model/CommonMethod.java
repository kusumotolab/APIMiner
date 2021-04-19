package apiminer.internal.analysis.model;

import gr.uom.java.xmi.UMLOperation;

public class CommonMethod {
    private final UMLOperation originalOperation;
    private final UMLOperation nextOperation;

    public CommonMethod(UMLOperation originalOperation, UMLOperation nextOperation){
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

package apiminer.internal.analysis.model;

import apiminer.internal.analysis.diff.MethodDiff;
import gr.uom.java.xmi.UMLOperation;

public class CommonMethod {
    private final UMLOperation originalOperation;
    private final UMLOperation nextOperation;
    private MethodDiff methodDiff;

    public CommonMethod(UMLOperation originalOperation, UMLOperation nextOperation) {
        this.originalOperation = originalOperation;
        this.nextOperation = nextOperation;
    }

    public UMLOperation getOriginalOperation() {
        return originalOperation;
    }

    public UMLOperation getNextOperation() {
        return nextOperation;
    }

    public MethodDiff getMethodDiff() {
        return methodDiff;
    }

    public void setMethodDiff(MethodDiff methodDiff) {
        this.methodDiff = methodDiff;
    }
}

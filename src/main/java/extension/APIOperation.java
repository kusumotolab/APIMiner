package extension;

import gr.uom.java.xmi.UMLOperation;

public class APIOperation {
    private UMLOperation parentOperation;
    private UMLOperation currentOperation;

    public APIOperation(UMLOperation parentOperation, UMLOperation currentOperation){
        this.parentOperation = parentOperation;
        this.currentOperation = currentOperation;
    }

    public UMLOperation getParentOperation() {
        return parentOperation;
    }

    public UMLOperation getCurrentOperation() {
        return currentOperation;
    }
}

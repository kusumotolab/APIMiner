package extension;

import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLOperation;
import org.refactoringminer.api.Refactoring;

public class ChangeDiffMethod {
    private UMLClass originalClass;
    private UMLOperation originalOperation;
    private UMLClass nextClass;
    private UMLOperation nextOperation;
    private Refactoring refactoring;
    public ChangeDiffMethod(UMLClass originalClass,UMLOperation originalOperation, UMLClass nextClass, UMLOperation nextOperation, Refactoring refactoring){
        this.originalClass = originalClass;
        this.originalOperation = originalOperation;
        this.nextClass = nextClass;
        this.nextOperation = nextOperation;
    }
}

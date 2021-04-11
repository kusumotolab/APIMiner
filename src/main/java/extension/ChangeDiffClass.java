package extension;

import gr.uom.java.xmi.UMLClass;
import org.refactoringminer.api.Refactoring;

public class ChangeDiffClass {
    private UMLClass originalClass;
    private UMLClass nextClass;
    private Refactoring refactoring;
    public ChangeDiffClass(UMLClass originalClass, UMLClass nextClass, Refactoring refactoring){
        this.originalClass = originalClass;
        this.nextClass = nextClass;
        this.refactoring = refactoring;
    }
}

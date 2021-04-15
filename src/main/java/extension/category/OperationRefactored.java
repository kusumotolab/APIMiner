package extension.category;

import extension.RefactoringElement;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLOperation;
import org.refactoringminer.api.Refactoring;

import java.util.ArrayList;
import java.util.List;

public class OperationRefactored {
    private UMLClass originalClass;
    private UMLOperation originalOperation;
    private UMLClass nextClass;
    private UMLOperation nextOperation;
    private List<RefactoringElement> refactoringElementList = new ArrayList<RefactoringElement>();

    public List<RefactoringElement> getRefactoringElementList() {
        return refactoringElementList;
    }

    public void setRefactoringElementList(List<RefactoringElement> refactoringElementList) {
        this.refactoringElementList = refactoringElementList;
    }

    public UMLClass getOriginalClass() {
        return originalClass;
    }

    public void setOriginalClass(UMLClass originalClass) {
        this.originalClass = originalClass;
    }

    public UMLOperation getOriginalOperation() {
        return originalOperation;
    }

    public void setOriginalOperation(UMLOperation originalOperation) {
        this.originalOperation = originalOperation;
    }

    public UMLClass getNextClass() {
        return nextClass;
    }

    public void setNextClass(UMLClass nextClass) {
        this.nextClass = nextClass;
    }

    public UMLOperation getNextOperation() {
        return nextOperation;
    }

    public void setNextOperation(UMLOperation nextOperation) {
        this.nextOperation = nextOperation;
    }
}

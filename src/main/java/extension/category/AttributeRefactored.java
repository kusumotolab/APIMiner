package extension.category;

import extension.RefactoringElement;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLOperation;

import java.util.ArrayList;
import java.util.List;

public class AttributeRefactored {
    private UMLClass originalClass;
    private UMLAttribute originalAttribute;
    private UMLClass nextClass;
    private UMLAttribute nextAttribute;
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

    public UMLAttribute getOriginalAttribute() {
        return originalAttribute;
    }

    public void setOriginalAttribute(UMLAttribute originalAttribute) {
        this.originalAttribute = originalAttribute;
    }

    public UMLClass getNextClass() {
        return nextClass;
    }

    public void setNextClass(UMLClass nextClass) {
        this.nextClass = nextClass;
    }

    public UMLAttribute getNextAttribute() {
        return nextAttribute;
    }

    public void setNextAttribute(UMLAttribute nextAttribute) {
        this.nextAttribute = nextAttribute;
    }
}

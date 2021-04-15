package extension.category;

import extension.RefactoringElement;
import gr.uom.java.xmi.UMLClass;

import java.util.ArrayList;
import java.util.List;

public class ClassRefactored {
    private UMLClass originalClass;
    private UMLClass nextClass;
    private List<RefactoringElement> refactoringElementList = new ArrayList<RefactoringElement>();

    public boolean checkEqual(UMLClass originalClass,UMLClass nextClass){
        boolean isOriginal = false;
        if(originalClass==null&&nextClass==null){
            isOriginal = true;
        }else if(this.originalClass!=null&&this.originalClass.equals(originalClass)){
            isOriginal = true;
        }
        boolean isNext = false;
        if(this.nextClass!=null&&nextClass!=null){
            isNext = true;
        }else if(this.nextClass!=null&&this.nextClass.equals(nextClass)){
            isNext = true;
        }
        return isOriginal&&isNext;
    }

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

    public UMLClass getNextClass() {
        return nextClass;
    }

    public void setNextClass(UMLClass nextClass) {
        this.nextClass = nextClass;
    }
}

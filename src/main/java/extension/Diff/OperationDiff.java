package extension.Diff;

import apiminer.enums.Category;
import apiminer.util.Change;
import apiminer.util.category.ClassChange;
import apiminer.util.category.method.FinalMethodChange;
import apiminer.util.category.type.VisibilityTypeChange;
import extension.RefactoringElement;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLOperation;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.List;

public class OperationDiff {
    private final UMLClass originalClass;
    private UMLOperation originalOperation;
    private final UMLClass nextClass;
    private UMLOperation nextOperation;
    private final RevCommit revCommit;
    private final List<Change> changeList = new ArrayList<>();

    public OperationDiff(UMLClass originalClass,UMLOperation originalOperation, UMLClass nextClass, UMLOperation nextOperation,List<Change> changeList, RevCommit revCommit) {
        this.originalClass = originalClass;
        this.originalOperation = originalOperation;
        this.nextClass = nextClass;
        this.nextOperation = nextOperation;
        this.changeList.addAll(changeList);
        this.revCommit = revCommit;
        detectOtherChange();
    }

    public List<Change> getChangeList() {
        return changeList;
    }

    private void detectOtherChange(){
        boolean isBreakingChange = false;
        if (originalClass != null && nextClass != null) {
            detectVisibilityChange();
            detectFinalModifierChange();
            detectStaticModifierChange();
            for (Change change : changeList) {
                if (change.getBreakingChange()) {
                    isBreakingChange = true;
                    break;
                }
            }
            for (Change change : changeList) {
                change.setBreakingChange(isBreakingChange);
            }
        }
    }
    private void detectVisibilityChange() {
        String originalAccessModifier = originalClass.getVisibility();
        String nextAccessModifier = nextClass.getVisibility();
        if (!originalAccessModifier.equals(nextAccessModifier)) {
            switch (originalAccessModifier) {
                case "private":
                case "default":
                    if (nextAccessModifier.equals("public") || nextAccessModifier.equals("protected")) {
                        changeList.add(new VisibilityTypeChange(originalClass, nextClass, Category.TYPE_GAIN_VISIBILITY, revCommit));
                    }
                    break;
                case "protected":
                    if (nextAccessModifier.equals("public")) {
                        changeList.add(new VisibilityTypeChange(originalClass, nextClass, Category.TYPE_GAIN_VISIBILITY, revCommit));
                    } else {
                        changeList.add(new VisibilityTypeChange(originalClass, nextClass, Category.TYPE_LOST_VISIBILITY, revCommit));
                    }
                    break;
                case "pubic":
                    changeList.add(new VisibilityTypeChange(originalClass, nextClass, Category.TYPE_LOST_VISIBILITY, revCommit));
                    break;
            }
        }
    }

    private void detectFinalModifierChange() {
        if(originalOperation.isFinal()&&!nextOperation.isFinal()){

        }else if(!originalOperation.isFinal()&&nextOperation.isFinal()){

        }
    }

    private void detectStaticModifierChange() {

    }
}

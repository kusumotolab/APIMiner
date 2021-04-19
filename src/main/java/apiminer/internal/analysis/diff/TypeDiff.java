package apiminer.internal.analysis.diff;

import apiminer.enums.Category;
import apiminer.internal.analysis.category.type.*;
import apiminer.internal.util.UtilTools;
import apiminer.util.Change;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.List;

public class TypeDiff {
    private final UMLClass originalClass;
    private final UMLClass nextClass;
    private final RevCommit revCommit;
    private final List<Change> changeList = new ArrayList<>();
    private final List<UMLClass> parentClassList;
    private final List<UMLClass> currentClassList;

    public TypeDiff(UMLClass originalClass, UMLClass nextClass, List<Change> changeList, List<UMLClass> parentClassList, List<UMLClass> currentClassList, RevCommit revCommit) {
        this.originalClass = originalClass;
        this.nextClass = nextClass;
        this.changeList.addAll(changeList);
        this.parentClassList = parentClassList;
        this.currentClassList = currentClassList;
        this.revCommit = revCommit;
        detectOtherChange();
    }

    public UMLClass getOriginalClass() {
        return originalClass;
    }

    public UMLClass getNextClass() {
        return nextClass;
    }

    public RevCommit getRevCommit() {
        return revCommit;
    }

    public List<Change> getChangeList() {
        return changeList;
    }

    private void detectOtherChange() {
        boolean isBreakingChange = false;
        if (originalClass != null && nextClass != null) {
            detectVisibilityChange();
            detectFinalModifierChange();
            detectStaticModifierChange();
            detectDeprecatedChange();
            detectSuperTypeChange();
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

    }

    private void detectStaticModifierChange() {

    }

    private void detectDeprecatedChange() {
        boolean isOriginalDeprecated = UtilTools.isDeprecatedClass(originalClass);
        boolean isNextDeprecated = UtilTools.isDeprecatedClass(nextClass);
        if (!isOriginalDeprecated && isNextDeprecated) {
            changeList.add(new DeprecateTypeChange(originalClass, nextClass, revCommit));
        }
    }

    private void detectSuperTypeChange() {
        //todo fix
        if (originalClass.getSuperclass() == null) {
            if (nextClass.getSuperclass() != null) {
                for (UMLClass currentClass : currentClassList) {
                    if (currentClass.toString().endsWith("." + nextClass.getSuperclass().toString())) {
                        changeList.add(new AddSuperTypeChange(originalClass, nextClass, currentClass, revCommit));
                        break;
                    }
                }
            }
        } else {
            if (nextClass.getSuperclass() == null) {
                for (UMLClass parentClass : parentClassList) {
                    if (parentClass.toString().endsWith("." + originalClass.getSuperclass().toString())) {
                        changeList.add(new RemoveSuperTypeChange(originalClass, nextClass, parentClass, revCommit));
                        break;
                    }
                }
            } else if (!originalClass.getSuperclass().toString().equals(nextClass.getSuperclass().toString())) {
                UMLClass originalSuperClass = null;
                UMLClass nextSuperClass = null;
                for (UMLClass parentClass : parentClassList) {
                    if (parentClass.toString().endsWith("." + originalClass.getSuperclass().toString())) {
                        originalSuperClass = parentClass;
                        break;
                    }
                }
                for (UMLClass currentClass : currentClassList) {
                    if (currentClass.toString().endsWith("." + nextClass.getSuperclass().toString())) {
                        nextSuperClass = currentClass;
                        break;
                    }
                }
                if (originalSuperClass != null && nextSuperClass != null) {
                    changeList.add(new ChangeSuperTypeChange(originalClass, nextClass, originalSuperClass, nextSuperClass, revCommit));
                }
            }
        }
    }
}

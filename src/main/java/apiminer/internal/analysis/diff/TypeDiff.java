package apiminer.internal.analysis.diff;

import apiminer.enums.Category;
import apiminer.internal.analysis.category.type.*;
import apiminer.internal.util.UtilTools;
import apiminer.util.Change;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLType;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    private void detectDeprecatedChange() {
        boolean isOriginalDeprecated = UtilTools.isDeprecatedClass(originalClass);
        boolean isNextDeprecated = UtilTools.isDeprecatedClass(nextClass);
        if (!isOriginalDeprecated && isNextDeprecated) {
            changeList.add(new DeprecateTypeChange(originalClass, nextClass, revCommit));
        }
    }

    private void detectSuperTypeChange() {
        List<UMLType> originalSuperTypeList = new ArrayList<>();
        Map<String, UMLType> removedSuperTypeMap = new HashMap<>();
        if (originalClass.getSuperclass() != null) {
            removedSuperTypeMap.put(originalClass.getSuperclass().toString(), originalClass.getSuperclass());
            originalSuperTypeList.add(originalClass.getSuperclass());
        }
        for (UMLType originalSuperType : originalClass.getImplementedInterfaces()) {
            removedSuperTypeMap.put(originalSuperType.toString(), originalSuperType);
            originalSuperTypeList.add(originalSuperType);
        }
        List<UMLType> nextSuperTypeList = new ArrayList<>();
        if (nextClass.getSuperclass() != null) {
            nextSuperTypeList.add(nextClass.getSuperclass());
        }
        nextSuperTypeList.addAll(nextClass.getImplementedInterfaces());
        List<UMLType> addedSuperTypeList = new ArrayList<>();
        for (UMLType nextSuperType : nextSuperTypeList) {
            if (removedSuperTypeMap.remove(nextSuperType.toString()) == null) {
                addedSuperTypeList.add(nextSuperType);
            }
        }
        if (removedSuperTypeMap.size() > 0 && addedSuperTypeList.size() > 0) {
            changeList.add(new ChangeSuperTypeChange(originalClass, nextClass, originalSuperTypeList, nextSuperTypeList, revCommit));
        } else if (removedSuperTypeMap.size() == 0 && addedSuperTypeList.size() > 0) {
            changeList.add(new AddSuperTypeChange(originalClass, nextClass, addedSuperTypeList, revCommit));
        } else if (removedSuperTypeMap.size() > 0) {
            List<UMLType> removedSuperTypeList = new ArrayList<>();
            for (UMLType removedSuperType : removedSuperTypeMap.values()) {
                removedSuperTypeList.add(removedSuperType);
            }
            changeList.add(new RemoveSuperTypeChange(originalClass, nextClass, removedSuperTypeList, revCommit));
        }
    }
}

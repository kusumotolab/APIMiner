package apiminer.internal.analysis.diff;

import apiminer.Change;
import apiminer.enums.Category;
import apiminer.enums.RefClassifier;
import apiminer.internal.analysis.category.TypeChange;
import apiminer.internal.analysis.category.type.*;
import apiminer.internal.analysis.model.CommonType;
import apiminer.internal.analysis.model.RefIdentifier;
import apiminer.internal.util.UtilTools;
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
    private final List<TypeChange> changeList = new ArrayList<>();
    private Map.Entry<RefIdentifier, List<TypeChange>> entry;
    private CommonType commonType;
    private boolean isBreakingChange = false;


    public TypeDiff(Map.Entry<RefIdentifier, List<TypeChange>> entry, CommonType commonType, RevCommit revCommit) {
        this.entry = entry;
        this.commonType = commonType;
        this.originalClass = entry.getKey().getOriginalClass();
        this.nextClass = entry.getKey().getNextClass();
        this.changeList.addAll(entry.getValue());
        this.revCommit = revCommit;
        detectOtherChange();
    }

    public TypeDiff(UMLClass originalClass, UMLClass nextClass, RevCommit revCommit) {
        this.originalClass = originalClass;
        this.nextClass = nextClass;
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

    public List<TypeChange> getChangeList() {
        return changeList;
    }

    public boolean isBreakingChange() {
        return isBreakingChange;
    }

    private void detectOtherChange() {
        if (originalClass != null && nextClass != null) {
            detectVisibilityChange();
            detectDeprecatedChange();
            detectSuperTypeChange();
            detectInterfaceChange();
            if (entry == null) {
                boolean isAPIOriginal = UtilTools.isAPIClass(originalClass);
                boolean isAPINext = UtilTools.isAPIClass(nextClass);
                if (isAPIOriginal && isAPINext) {
                    for (Change change : changeList) {
                        if (change.getBreakingChange()) {
                            isBreakingChange = true;
                            break;
                        }
                    }
                } else isBreakingChange = isAPIOriginal;
                for (Change change : changeList) {
                    change.setBreakingChange(isBreakingChange);
                }
            } else {
                if (commonType == null) {
                    boolean isAPIOriginal = UtilTools.isAPIClass(originalClass);
                    boolean isAPINext = UtilTools.isAPIClass(nextClass);
                    if (isAPIOriginal && isAPINext) {
                        for (Change change : changeList) {
                            if (change.getBreakingChange()) {
                                isBreakingChange = true;
                                break;
                            }
                        }
                    } else isBreakingChange = isAPIOriginal;
                    for (Change change : changeList) {
                        change.setBreakingChange(isBreakingChange);
                    }
                } else {
                    isBreakingChange = commonType.getTypeDiff().isBreakingChange();
                    for (Change change : changeList) {
                        change.setBreakingChange(isBreakingChange);
                    }
                }
            }
        } else if (entry != null && entry.getKey().getRefClassifier().equals(RefClassifier.ADD)) {
            if (commonType != null) {
                if (commonType.getTypeDiff().isBreakingChange()) {
                    isBreakingChange = true;
                    for (Change change : changeList) {
                        change.setBreakingChange(isBreakingChange);
                    }
                } else {
                    isBreakingChange = changeList.get(0).getBreakingChange();
                }
            }
        }
    }

    private void detectVisibilityChange() {
        String originalAccessModifier = originalClass.getVisibility();
        String nextAccessModifier = nextClass.getVisibility();
        if (!originalAccessModifier.equals(nextAccessModifier)) {
            switch (originalAccessModifier) {
                case "private":
                case "package":
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
                case "public":
                    changeList.add(new VisibilityTypeChange(originalClass, nextClass, Category.TYPE_LOST_VISIBILITY, revCommit));
                    break;
            }
        }
    }


    private void detectDeprecatedChange() {
        boolean isOriginalDeprecated = UtilTools.isDeprecated(originalClass.getAnnotations());
        boolean isNextDeprecated = UtilTools.isDeprecated(nextClass.getAnnotations());
        if (!isOriginalDeprecated && isNextDeprecated) {
            changeList.add(new DeprecateTypeChange(originalClass, nextClass, revCommit));
        }
    }

    private void detectSuperTypeChange() {
        UMLType originalUMLType = originalClass.getSuperclass();
        UMLType nextUMLType = nextClass.getSuperclass();
        if (originalUMLType != null && nextUMLType == null) {
            changeList.add(new RemoveSuperTypeChange(originalClass, nextClass, originalUMLType, revCommit));
        } else if (originalUMLType == null && nextUMLType != null) {
            changeList.add(new AddSuperTypeChange(originalClass, nextClass, nextUMLType, revCommit));
        } else if (originalUMLType != null && !originalUMLType.toString().equals(nextUMLType.toString())) {
            changeList.add(new ChangeSuperTypeChange(originalClass, nextClass, originalUMLType, nextUMLType, revCommit));
        }
    }

    private void detectInterfaceChange() {
        List<UMLType> originalInterfaceList = new ArrayList<>();
        Map<String, UMLType> removedInterfaceMap = new HashMap<>();
        for (UMLType originalInterface : originalClass.getImplementedInterfaces()) {
            removedInterfaceMap.put(originalInterface.toString(), originalInterface);
            originalInterfaceList.add(originalInterface);
        }
        List<UMLType> nextInterfaceList = new ArrayList<>(nextClass.getImplementedInterfaces());
        List<UMLType> addedInterfaceList = new ArrayList<>();
        for (UMLType nextInterface : nextInterfaceList) {
            if (removedInterfaceMap.remove(nextInterface.toString()) == null) {
                addedInterfaceList.add(nextInterface);
            }
        }
        if (removedInterfaceMap.size() > 0 && addedInterfaceList.size() > 0) {
            changeList.add(new ChangeInterfaceChange(originalClass, nextClass, originalInterfaceList, nextInterfaceList, revCommit));
        } else if (removedInterfaceMap.size() == 0 && addedInterfaceList.size() > 0) {
            changeList.add(new AddInterfaceChange(originalClass, nextClass, addedInterfaceList, revCommit));
        } else if (removedInterfaceMap.size() > 0) {
            List<UMLType> removedSuperTypeList = new ArrayList<>(removedInterfaceMap.values());
            changeList.add(new RemoveInterfaceChange(originalClass, nextClass, removedSuperTypeList, revCommit));
        }
    }
}

package apiminer.internal.analysis.diff;

import apiminer.Change;
import apiminer.enums.Category;
import apiminer.enums.RefClassifier;
import apiminer.internal.analysis.category.MethodChange;
import apiminer.internal.analysis.category.method.*;
import apiminer.internal.analysis.model.CommonMethod;
import apiminer.internal.analysis.model.RefIdentifier;
import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.UMLType;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodDiff {
    private final UMLClass originalClass;
    private final UMLOperation originalOperation;
    private final UMLClass nextClass;
    private final UMLOperation nextOperation;
    private final RevCommit revCommit;
    private final List<MethodChange> changeList = new ArrayList<>();
    private boolean isBreakingChange = false;
    private Map.Entry<RefIdentifier, List<MethodChange>> entry;
    private CommonMethod commonMethod;


    public MethodDiff(Map.Entry<RefIdentifier, List<MethodChange>> entry, CommonMethod commonMethod, RevCommit revCommit) {
        this.entry = entry;
        this.commonMethod = commonMethod;
        this.originalClass = entry.getKey().getOriginalClass();
        this.originalOperation = entry.getKey().getOriginalOperation();
        this.nextClass = entry.getKey().getNextClass();
        this.nextOperation = entry.getKey().getNextOperation();
        this.changeList.addAll(entry.getValue());
        this.revCommit = revCommit;
        detectOtherChange();
    }

    public MethodDiff(UMLClass originalClass, UMLOperation originalOperation, UMLClass nextClass, UMLOperation nextOperation, RevCommit revCommit) {
        this.originalClass = originalClass;
        this.originalOperation = originalOperation;
        this.nextClass = nextClass;
        this.nextOperation = nextOperation;
        this.revCommit = revCommit;
        detectOtherChange();
    }

    public UMLClass getOriginalClass() {
        return originalClass;
    }

    public UMLOperation getOriginalOperation() {
        return originalOperation;
    }

    public UMLClass getNextClass() {
        return nextClass;
    }

    public UMLOperation getNextOperation() {
        return nextOperation;
    }

    public RevCommit getRevCommit() {
        return revCommit;
    }

    public List<MethodChange> getChangeList() {
        return changeList;
    }

    public boolean isBreakingChange() {
        return isBreakingChange;
    }

    private void detectOtherChange() {
        if (originalClass != null && nextClass != null) {
            detectVisibilityChange();
            detectFinalModifierChange();
            detectStaticModifierChange();
            detectDeprecatedChange();
            detectExceptionListChange();
            if (entry == null || commonMethod == null) {
                boolean isAPIOriginal = UtilTools.isAPIClass(originalClass) && UtilTools.isAPIMethod(originalOperation);
                boolean isAPINext = UtilTools.isAPIClass(nextClass) && UtilTools.isAPIMethod(nextOperation);
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
                isBreakingChange = commonMethod.getMethodDiff().isBreakingChange();
                for (Change change : changeList) {
                    change.setBreakingChange(isBreakingChange);
                }
            }
        } else if (entry != null) {
            if (entry.getKey().getRefClassifier().equals(RefClassifier.ADD)) {
                if (commonMethod != null) {
                    if (commonMethod.getMethodDiff().isBreakingChange()) {
                        isBreakingChange = true;
                        for (Change change : changeList) {
                            change.setBreakingChange(isBreakingChange);
                        }
                    } else {
                        isBreakingChange = changeList.get(0).getBreakingChange();
                    }
                }
            } else if (entry.getKey().getRefClassifier().equals(RefClassifier.REMOVE)) {
                if (commonMethod != null) {
                    isBreakingChange = commonMethod.getMethodDiff().isBreakingChange;
                    for (Change change : changeList) {
                        change.setBreakingChange(isBreakingChange);
                    }
                }
            }
        }
    }


    private void detectVisibilityChange() {
        if (UtilTools.isAPIClass(originalClass) && UtilTools.isAPIClass(nextClass)) {
            String originalAccessModifier = originalOperation.getVisibility();
            String nextAccessModifier = nextOperation.getVisibility();
            if (!originalAccessModifier.equals(nextAccessModifier)) {
                switch (originalAccessModifier) {
                    case "private":
                    case "package":
                        if (nextAccessModifier.equals("public") || nextAccessModifier.equals("protected")) {
                            changeList.add(new VisibilityMethodChange(originalClass, originalOperation, nextClass, nextOperation, Category.METHOD_GAIN_VISIBILITY, revCommit));
                        }
                        break;
                    case "protected":
                        if (nextAccessModifier.equals("public")) {
                            changeList.add(new VisibilityMethodChange(originalClass, originalOperation, nextClass, nextOperation, Category.METHOD_GAIN_VISIBILITY, revCommit));
                        } else {
                            changeList.add(new VisibilityMethodChange(originalClass, originalOperation, nextClass, nextOperation, Category.METHOD_LOST_VISIBILITY, revCommit));
                        }
                        break;
                    case "public":
                        changeList.add(new VisibilityMethodChange(originalClass, originalOperation, nextClass, nextOperation, Category.METHOD_LOST_VISIBILITY, revCommit));
                        break;
                }
            }
        }
    }


    private void detectFinalModifierChange() {
        if (originalOperation.isFinal() && !nextOperation.isFinal()) {
            changeList.add(new FinalMethodChange(originalClass, originalOperation, nextClass, nextOperation, Category.METHOD_REMOVE_MODIFIER_FINAL, revCommit));
        } else if (!originalOperation.isFinal() && nextOperation.isFinal()) {
            changeList.add(new FinalMethodChange(originalClass, originalOperation, nextClass, nextOperation, Category.METHOD_ADD_MODIFIER_FINAL, revCommit));
        }
    }

    private void detectStaticModifierChange() {
        if (originalOperation.isStatic() && !nextOperation.isStatic()) {
            changeList.add(new StaticMethodChange(originalClass, originalOperation, nextClass, nextOperation, Category.METHOD_REMOVE_MODIFIER_STATIC, revCommit));
        } else if (!originalOperation.isStatic() && nextOperation.isStatic()) {
            changeList.add(new StaticMethodChange(originalClass, originalOperation, nextClass, nextOperation, Category.METHOD_ADD_MODIFIER_STATIC, revCommit));
        }
    }

    private void detectDeprecatedChange() {
        boolean isOriginalDeprecated = UtilTools.isDeprecated(originalClass.getAnnotations()) || UtilTools.isDeprecated(originalOperation.getAnnotations());
        boolean isNextDeprecated = UtilTools.isDeprecated(nextClass.getAnnotations()) || UtilTools.isDeprecated(nextOperation.getAnnotations());
        if (!isOriginalDeprecated && isNextDeprecated) {
            changeList.add(new DeprecateMethodChange(originalClass, originalOperation, nextClass, nextOperation, revCommit));
        }
    }

    private void detectExceptionListChange() {
        if (originalOperation.getThrownExceptionTypes().size() != nextOperation.getThrownExceptionTypes().size()) {
            changeList.add(new ChangeInExceptionList(originalClass, originalOperation, nextClass, nextOperation, revCommit));
        } else {
            Map<String, UMLType> originalExceptionMap = new HashMap<>();
            for (UMLType umlType : originalOperation.getThrownExceptionTypes()) {
                originalExceptionMap.put(umlType.toString(), umlType);
            }
            List<UMLType> nextExceptionList = new ArrayList<>();
            for (UMLType nextException : nextOperation.getThrownExceptionTypes()) {
                UMLType originalException = originalExceptionMap.remove(nextException.toString());
                if (originalException == null) {
                    nextExceptionList.add(nextException);
                }
            }
            if (originalExceptionMap.size() > 0 || nextExceptionList.size() > 0) {
                changeList.add(new ChangeInExceptionList(originalClass, originalOperation, nextClass, nextOperation, revCommit));
            }
        }
    }
}

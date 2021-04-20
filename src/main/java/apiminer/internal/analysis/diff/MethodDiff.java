package apiminer.internal.analysis.diff;

import apiminer.enums.Category;
import apiminer.internal.analysis.category.method.*;
import apiminer.internal.util.UtilTools;
import apiminer.Change;
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
    private final List<Change> changeList = new ArrayList<>();

    public MethodDiff(UMLClass originalClass, UMLOperation originalOperation, UMLClass nextClass, UMLOperation nextOperation, List<Change> changeList, RevCommit revCommit) {
        this.originalClass = originalClass;
        this.originalOperation = originalOperation;
        this.nextClass = nextClass;
        this.nextOperation = nextOperation;
        this.changeList.addAll(changeList);
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
            detectExceptionListChange();
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
        if (UtilTools.isAPIClass(originalClass) && UtilTools.isAPIClass(nextClass)) {
            String originalAccessModifier = originalOperation.getVisibility();
            String nextAccessModifier = nextOperation.getVisibility();
            if (!originalAccessModifier.equals(nextAccessModifier)) {
                switch (originalAccessModifier) {
                    case "private":
                    case "default":
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
                    case "pubic":
                        changeList.add(new VisibilityMethodChange(originalClass, originalOperation, nextClass, nextOperation, Category.METHOD_LOST_VISIBILITY, revCommit));
                        break;
                }
            }
        }
    }


    private void detectFinalModifierChange() {
        if (originalOperation.isFinal() && !nextOperation.isFinal()) {
            changeList.add(new FinalMethodChange(originalClass, originalOperation, nextClass, nextOperation, Category.METHOD_REMOVE_MODIFIER_STATIC, revCommit));
        } else if (!originalOperation.isFinal() && nextOperation.isFinal()) {
            changeList.add(new FinalMethodChange(originalClass, originalOperation, nextClass, nextOperation, Category.METHOD_ADD_MODIFIER_STATIC, revCommit));
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
        boolean isOriginalDeprecated = UtilTools.isDeprecatedClass(originalClass) || UtilTools.isDeprecatedMethod(originalOperation);
        boolean isNextDeprecated = UtilTools.isDeprecatedClass(nextClass) || UtilTools.isDeprecatedMethod(nextOperation);
        if (!isOriginalDeprecated && isNextDeprecated) {
            changeList.add(new DeprecateMethodChange(originalClass, originalOperation, nextClass, nextOperation, revCommit));
        }
    }
    private void detectExceptionListChange(){
        if(originalOperation.getThrownExceptionTypes().size()!=nextOperation.getThrownExceptionTypes().size()){
            changeList.add(new ChangeInExceptionList(originalClass,originalOperation,nextClass,nextOperation,revCommit));
        }else{
            Map<String, UMLType> originalExceptionMap = new HashMap<>();
            for(UMLType umlType:originalOperation.getThrownExceptionTypes()){
                originalExceptionMap.put(umlType.toString(),umlType);
            }
            List<UMLType> nextExceptionList = new ArrayList<>();
            for(UMLType nextException:nextOperation.getThrownExceptionTypes()){
                UMLType originalException = originalExceptionMap.remove(nextException.toString());
                if(originalException==null){
                    nextExceptionList.add(nextException);
                }
            }
            if(originalExceptionMap.size()>0||nextExceptionList.size()>0){
                changeList.add(new ChangeInExceptionList(originalClass,originalOperation,nextClass,nextOperation,revCommit));
            }
        }
    }
}

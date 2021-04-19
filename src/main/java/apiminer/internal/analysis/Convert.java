package apiminer.internal.analysis;

import apiminer.enums.Classifier;
import apiminer.enums.ChangeType;
import apiminer.internal.util.UtilTools;
import apiminer.util.Change;
import apiminer.internal.analysis.category.ClassChange;
import apiminer.internal.analysis.category.FieldChange;
import apiminer.internal.analysis.category.MethodChange;
import apiminer.internal.analysis.category.field.*;
import apiminer.internal.analysis.category.method.*;
import apiminer.internal.analysis.category.type.*;
import apiminer.internal.analysis.model.Refactored;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

import java.util.Map;

public class Convert {
    private boolean isAPI;
    private Refactored refactored;
    private Change change;

    public Convert(Refactoring refactoring, Map<String, UMLClass> parentClassMap, Map<String, UMLClass> currentClassMap, RevCommit revCommit, Classifier classifier) {
        this.change = convertClassChange(refactoring, revCommit,classifier);
        if (change == null) {
            change = convertMethodChange(refactoring, parentClassMap, currentClassMap, revCommit,classifier);
            if (change == null) {
                change = convertFieldChange(refactoring, parentClassMap, currentClassMap, revCommit,classifier);
            }
        }
    }

    private ClassChange convertClassChange(Refactoring refactoring, RevCommit revCommit,Classifier classifier) {
        ClassChange classChange;
        switch (refactoring.getRefactoringType()) {
            case EXTRACT_SUPERCLASS:
            case EXTRACT_INTERFACE:
                classChange = new ExtractSuperTypeChange(refactoring, revCommit);
                break;
            case EXTRACT_CLASS:
                classChange = new ExtractTypeChange(refactoring, revCommit);
                break;
            case EXTRACT_SUBCLASS:
                classChange = new ExtractSubTypeChange(refactoring, revCommit);
                break;
            default:
                classChange = null;
        }
        if (classChange != null) {
            this.isAPI = UtilTools.isAPIByClassifier(classChange.getNextClass(),classifier)&& UtilTools.isAPIClass(classChange.getNextClass());
            this.refactored = new Refactored();
            refactored.setRefType(ChangeType.CLASS);
            refactored.setNextClass(classChange.getNextClass());
        } else {
            switch (refactoring.getRefactoringType()) {
                case MOVE_CLASS:
                    classChange = new MoveTypeChange(refactoring, revCommit);
                    break;
                case RENAME_CLASS:
                    classChange = new RenameTypeChange(refactoring, revCommit);
                    break;
                case MOVE_RENAME_CLASS:
                    classChange = new MoveAndRenameTypeChange(refactoring, revCommit);
                    break;
                default:
                    classChange = null;
            }
            if (classChange != null) {
                this.isAPI = UtilTools.isClassBeforeAfterAPIByClassifier(classChange.getOriginalClass(),classChange.getNextClass(),classifier)&&(UtilTools.isAPIClass(classChange.getOriginalClass()) || UtilTools.isAPIClass(classChange.getNextClass()));
                this.refactored = new Refactored();
                refactored.setRefType(ChangeType.CLASS);
                refactored.setOriginalClass(classChange.getOriginalClass());
                refactored.setNextClass(classChange.getNextClass());
            }
        }
        return classChange;
    }

    private MethodChange convertMethodChange(Refactoring refactoring, Map<String, UMLClass> parentClassMap, Map<String, UMLClass> currentClassMap, RevCommit revCommit,Classifier classifier) {
        MethodChange methodChange = null;
        switch (refactoring.getRefactoringType()) {
            case EXTRACT_OPERATION:
            case EXTRACT_AND_MOVE_OPERATION:
                methodChange = new ExtractMethodChange(refactoring, parentClassMap, currentClassMap, revCommit);
                this.isAPI = UtilTools.isAPIByClassifier(methodChange.getNextClass(),classifier)&& UtilTools.isAPIClass(methodChange.getNextClass())&& UtilTools.isAPIMethod(methodChange.getNextOperation());
                this.refactored = new Refactored();
                refactored.setRefType(ChangeType.METHOD);
                refactored.setNextClass(methodChange.getNextClass());
                refactored.setNextOperation(methodChange.getNextOperation());
            case INLINE_OPERATION:
            case MOVE_AND_INLINE_OPERATION:
                methodChange = new InlineMethodChange(refactoring, parentClassMap, currentClassMap, revCommit);
                this.isAPI = UtilTools.isAPIByClassifier(methodChange.getOriginalClass(),classifier)&& UtilTools.isAPIClass(methodChange.getOriginalClass())&& UtilTools.isAPIMethod(methodChange.getOriginalOperation());
                this.refactored = new Refactored();
                refactored.setRefType(ChangeType.METHOD);
                refactored.setOriginalClass(methodChange.getOriginalClass());
                refactored.setOriginalOperation(methodChange.getOriginalOperation());
                break;
            default:
                switch (refactoring.getRefactoringType()) {
                    case RENAME_METHOD:
                        methodChange = new RenameMethodChange(refactoring, parentClassMap, currentClassMap, revCommit);
                        break;
                    case MOVE_OPERATION:
                        methodChange = new MoveMethodChange(refactoring, parentClassMap, currentClassMap, revCommit);
                        break;
                    case PULL_UP_OPERATION:
                        methodChange = new PullUpMethodChange(refactoring, parentClassMap, currentClassMap, revCommit);
                        break;
                    case PUSH_DOWN_OPERATION:
                        methodChange = new PushDownMethodChange(refactoring, parentClassMap, currentClassMap, revCommit);
                        break;
                    case MOVE_AND_RENAME_OPERATION:
                        methodChange = new MoveAndRenameMethodChange(refactoring, parentClassMap, currentClassMap, revCommit);
                        break;
                    case CHANGE_RETURN_TYPE:
                        methodChange = new ChangeInReturnType(refactoring,parentClassMap,currentClassMap,revCommit);
                        break;
                    case PARAMETERIZE_VARIABLE:
                    case MERGE_PARAMETER:
                    case SPLIT_PARAMETER:
                    case CHANGE_PARAMETER_TYPE:
                    case ADD_PARAMETER:
                    case REMOVE_PARAMETER:
                    case REORDER_PARAMETER:
                        methodChange = new ChangeInParameterListChange(refactoring, parentClassMap, currentClassMap, revCommit);
                        break;
                    default:

                }
                if (methodChange != null) {
                    this.isAPI = UtilTools.isClassBeforeAfterAPIByClassifier(methodChange.getOriginalClass(), methodChange.getNextClass(),classifier)&&((UtilTools.isAPIClass(methodChange.getOriginalClass())&& UtilTools.isAPIMethod(methodChange.getOriginalOperation()))|| (UtilTools.isAPIClass(methodChange.getNextClass())&& UtilTools.isAPIMethod(methodChange.getNextOperation())));
                    this.refactored = new Refactored();
                    refactored.setRefType(ChangeType.METHOD);
                    refactored.setOriginalClass(methodChange.getOriginalClass());
                    refactored.setNextClass(methodChange.getNextClass());
                    refactored.setOriginalOperation(methodChange.getOriginalOperation());
                    refactored.setNextOperation(methodChange.getNextOperation());
                }
        }
        return methodChange;
    }

    private FieldChange convertFieldChange(Refactoring refactoring, Map<String, UMLClass> parentClassMap, Map<String, UMLClass> currentClassMap, RevCommit revCommit,Classifier classifier) {
        FieldChange fieldChange;
        if (refactoring.getRefactoringType().equals(RefactoringType.EXTRACT_ATTRIBUTE)) {
            fieldChange = new ExtractFieldChange(refactoring, parentClassMap, currentClassMap, revCommit);
            this.isAPI = UtilTools.isAPIByClassifier(fieldChange.getNextClass(), classifier)&& UtilTools.isAPIClass(fieldChange.getNextClass())&& UtilTools.isAPIField(fieldChange.getNextAttribute());
            this.refactored = new Refactored();
            refactored.setRefType(ChangeType.FIELD);
            refactored.setNextClass(fieldChange.getNextClass());
            refactored.setNextAttribute(fieldChange.getNextAttribute());
        } else {
            switch (refactoring.getRefactoringType()) {
                case MOVE_ATTRIBUTE:
                    fieldChange = new MoveFieldChange(refactoring, parentClassMap, currentClassMap, revCommit);
                    break;
                case PULL_UP_ATTRIBUTE:
                    fieldChange = new PullUpFieldChange(refactoring, parentClassMap, currentClassMap, revCommit);
                    break;
                case PUSH_DOWN_ATTRIBUTE:
                    fieldChange = new PushDownFieldChange(refactoring, parentClassMap, currentClassMap, revCommit);
                    break;
                case MOVE_RENAME_ATTRIBUTE:
                    fieldChange = new MoveAndRenameFieldChange(refactoring, parentClassMap, currentClassMap, revCommit);
                    break;
                case RENAME_ATTRIBUTE:
                    fieldChange = new RenameFieldChange(refactoring, parentClassMap, currentClassMap, revCommit);
                    break;
                case CHANGE_ATTRIBUTE_TYPE:
                    fieldChange = new ChangeInTypeField(refactoring, parentClassMap, currentClassMap, revCommit);
                    break;
                default:
                    fieldChange = null;
            }
            if (fieldChange != null) {
                this.isAPI = UtilTools.isClassBeforeAfterAPIByClassifier(fieldChange.getOriginalClass(), fieldChange.getNextClass(), classifier)&&((UtilTools.isAPIClass(fieldChange.getOriginalClass() )&& UtilTools.isAPIField(fieldChange.getOriginalAttribute()))||(UtilTools.isAPIClass(fieldChange.getNextClass())&& UtilTools.isAPIField(fieldChange.getNextAttribute())));
                this.refactored = new Refactored();
                refactored.setRefType(ChangeType.FIELD);
                refactored.setOriginalClass(fieldChange.getOriginalClass());
                refactored.setNextClass(fieldChange.getNextClass());
                refactored.setOriginalAttribute(fieldChange.getOriginalAttribute());
                refactored.setNextAttribute(fieldChange.getNextAttribute());
            }
        }
        return fieldChange;
    }

    public boolean isAPI() {
        return isAPI;
    }

    public Refactored getRefactored() {
        return refactored;
    }

    public Change getChange() {
        return change;
    }
}

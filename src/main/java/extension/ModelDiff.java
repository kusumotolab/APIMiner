package extension;

import apiminer.Change;
import apiminer.enums.Classifier;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.diff.*;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringMinerTimedOutException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelDiff {
    private UMLModel parentUMLModel;
    private Model parentModel;
    private UMLModel currentUMLModel;
    private Model currentModel;
    private UMLModelDiff umlModelDiff;
    private Classifier classifierAPI;
    private List<Refactoring> refactorings = new ArrayList<Refactoring>();
    private List<Refactoring> refactoringClassList = new ArrayList<>();
    private List<Refactoring> refactoringMethodList = new ArrayList<>();
    private List<Refactoring> refactoringFieldList = new ArrayList<>();

    private List<ClassDiff> addedClassList = new ArrayList<ClassDiff>();
    private List<ClassDiff> removedClassList = new ArrayList<ClassDiff>();
    private List<ClassDiff> commonClassList = new ArrayList<ClassDiff>();

    private Map<UMLClass,List<UMLOperation>> addedOperationList = new HashMap<UMLClass, List<UMLOperation>>();
    private Map<UMLClass,List<UMLOperation>> removedOperationList = new HashMap<UMLClass, List<UMLOperation>>();
    private Map<Map<UMLClass,UMLOperation>,Map<UMLClass,UMLOperation>> commonOperationMap = new HashMap<Map<UMLClass, UMLOperation>, Map<UMLClass, UMLOperation>>();

    private Map<UMLClass,List<UMLAttribute>> addedAttributeList = new HashMap<UMLClass, List<UMLAttribute>>();
    private Map<UMLClass,List<UMLAttribute>> removedAttributeList = new HashMap<UMLClass, List<UMLAttribute>>();
    private Map<Map<UMLClass,UMLAttribute>,Map<UMLClass,UMLAttribute>> commonAttriubuteList = new HashMap<Map<UMLClass, UMLAttribute>, Map<UMLClass, UMLAttribute>>();

    private List<ChangeDiffClass> changeDiffClassList = new ArrayList<ChangeDiffClass>();
    private List<ChangeDiffMethod> changeDiffMethodList = new ArrayList<ChangeDiffMethod>();

    private List<Change> changeTypeList = new ArrayList<Change>();
    private List<Change> changeMethodList = new ArrayList<Change>();
    private List<Change> changeFieldList = new ArrayList<Change>();


    public ModelDiff(UMLModel parentUMLModel, UMLModel currentUMLModel, UMLModelDiff modelDiff, Classifier classifierAPI) {
        this.parentUMLModel = parentUMLModel;
        this.currentUMLModel = currentUMLModel;
        this.parentModel = new Model(parentUMLModel);
        this.currentModel = new Model(currentUMLModel);
        this.umlModelDiff = modelDiff;
        this.classifierAPI = classifierAPI;
        try {
            this.refactorings = modelDiff.getRefactorings();
        } catch (RefactoringMinerTimedOutException e) {
            e.printStackTrace();
        }
    }


    public List<Change> getChangeTypeList() {
        return changeTypeList;
    }

    public List<Change> getChangeMethodList() {
        return changeMethodList;
    }

    public List<Change> getChangeFieldList() {
        return changeFieldList;
    }

    public void detectChanges() {
        filterRefactoring();
        detectClassChanges();
    }

    private void filterRefactoring() {
        for (Refactoring refactoring : refactorings) {
            switch (refactoring.getRefactoringType()) {
                case EXTRACT_SUPERCLASS:
                case EXTRACT_INTERFACE:
                case MOVE_CLASS:
                case RENAME_CLASS:
                case MOVE_RENAME_CLASS:
                case EXTRACT_CLASS:
                case EXTRACT_SUBCLASS:
                    refactoringClassList.add(refactoring);
                    break;
                case EXTRACT_OPERATION:
                case EXTRACT_AND_MOVE_OPERATION:
                case INLINE_OPERATION:
                case MOVE_AND_INLINE_OPERATION:
                case RENAME_METHOD:
                case MOVE_OPERATION:
                case PULL_UP_OPERATION:
                case PUSH_DOWN_OPERATION:
                case MOVE_AND_RENAME_OPERATION:
                case PARAMETERIZE_VARIABLE:
                case MERGE_PARAMETER:
                case SPLIT_PARAMETER:
                case CHANGE_PARAMETER_TYPE:
                case ADD_PARAMETER:
                case REMOVE_PARAMETER:
                case REORDER_PARAMETER:
                    refactoringMethodList.add(refactoring);
                    break;
                case EXTRACT_ATTRIBUTE:
                case MOVE_ATTRIBUTE:
                case PULL_UP_ATTRIBUTE:
                case PUSH_DOWN_ATTRIBUTE:
                case MOVE_RENAME_ATTRIBUTE:
                case RENAME_ATTRIBUTE:
                case CHANGE_ATTRIBUTE_TYPE:
                    refactoringFieldList.add(refactoring);
                    break;
            }
        }
    }

    private void detectClassChanges() {
        Map<String, ClassDiff> mapClassParent = new HashMap<String, ClassDiff>();
        //Todo fix map
        Map<Map<String,UMLClass>, Map<String,UMLOperation>> mapOperationParent = new HashMap<Map<String,UMLClass>, Map<String,UMLOperation>>();
        for (UMLClass parentClass : parentUMLModel.getClassList()) {
            ClassDiff classDiff = new ClassDiff(parentClass,null);
            for(UMLOperation parentOperation:parentClass.getOperations()){
                classDiff.getParentOperation().put(getSignatureOperation(parentOperation),parentOperation);
            }
            for(UMLAttribute parentAttribute:parentClass.getAttributes()){
                classDiff.getParentAttribute().put(parentAttribute.toString(),parentAttribute);
            }
            mapClassParent.put(parentClass.toString(), classDiff);
        }
        for (UMLClass currentClass : currentUMLModel.getClassList()) {
            UMLClass parentClass = mapClassParent.remove(currentClass.toString());
            if (parentClass != null) {
                if (isAPIClass(parentClass) || isAPIClass(currentClass)) {
                    commonClassMap.put(parentClass, currentClass);
                }
            } else {
                if (isAPIClass(currentClass)) {
                    addedClassList.add(currentClass);
                }

            }
        }
        for (Map.Entry<String, UMLClass> entry : mapClassParent.entrySet()) {
            if (isAPIClass(entry.getValue())) {
                removedClassList.add(entry.getValue());
            }
        }
        for (Refactoring refactoring : refactoringClassList) {
            setRefactoredClass(refactoring);
        }
        for (UMLClass removedClass : removedClassList) {
            changeDiffClassList.add(new ChangeDiffClass(removedClass, null, null));
        }
        for (UMLClass addedClass : addedClassList) {
            changeDiffClassList.add(new ChangeDiffClass(null, addedClass, null));
        }
        System.out.println();
    }

    private boolean isAPIClass(UMLClass umlClass) {
        //Todo fix filter package
        if (true) {
            if (umlClass.getVisibility().equals("public") || umlClass.getVisibility().equals("protected")) {
                return true;
            }
        }
        return false;
    }

    private boolean isAPIOperation(UMLOperation umlOperation) {
        //Todo fix filter package
        if (true) {
            if (umlOperation.getVisibility().equals("public") || umlOperation.getVisibility().equals("protected")) {
                return true;
            }
        }
        return false;
    }

    private boolean isAPIAttribute(UMLAttribute umlAttribute) {
        //Todo fix filter package
        if (true) {
            if (umlAttribute.getVisibility().equals("public") || umlAttribute.getVisibility().equals("protected")) {
                return true;
            }
        }
        return false;
    }

    private String getSignatureOperation(UMLOperation umlOperation){
        String signature = "";
        String s = umlOperation.toString();
        return signature;
    }

    private void detectChangesAccess(UMLClass parentClass, UMLClass currentClass) {

    }

    private void detectMethodChanges(){}


    private void detectRefactoring() {

    }

    private void process() {
        setIsRefactored();
    }

    private void setIsRefactored() {
        try {
            List<Refactoring> refactorings = umlModelDiff.getRefactorings();
            for (Refactoring refactoring : refactorings) {
                setRefactoredClass(refactoring);
                setRefactoredOperation(refactoring);
                setRefactoredAttribute(refactoring);
            }
        } catch (RefactoringMinerTimedOutException e) {
            e.printStackTrace();
        }
    }

    private void setRefactoredClass(Refactoring refactoring) {
        UMLClass originalClass = null;
        UMLClass nextClass = null;
        switch (refactoring.getRefactoringType()) {
            case EXTRACT_SUPERCLASS:
            case EXTRACT_INTERFACE:
                ExtractSuperclassRefactoring extractSuperclass = (ExtractSuperclassRefactoring) refactoring;
                nextClass = extractSuperclass.getExtractedClass();
                break;
            case MOVE_CLASS:
                MoveClassRefactoring moveClass = (MoveClassRefactoring) refactoring;
                originalClass = moveClass.getOriginalClass();
                nextClass = moveClass.getMovedClass();
                break;
            case RENAME_CLASS:
                RenameClassRefactoring renameClass = (RenameClassRefactoring) refactoring;
                originalClass = renameClass.getOriginalClass();
                nextClass = renameClass.getRenamedClass();
                break;
            case MOVE_RENAME_CLASS:
                MoveAndRenameClassRefactoring moveAndRenameClass = (MoveAndRenameClassRefactoring) refactoring;
                originalClass = moveAndRenameClass.getOriginalClass();
                nextClass = moveAndRenameClass.getRenamedClass();
                break;
            case EXTRACT_CLASS:
            case EXTRACT_SUBCLASS:
                ExtractClassRefactoring extractClassRefactoring = (ExtractClassRefactoring) refactoring;
                originalClass = extractClassRefactoring.getOriginalClass();
                nextClass = extractClassRefactoring.getExtractedClass();
                break;
            default:
        }
        boolean isAPIParentClass = false;
        boolean isAPICurrentClass = false;
        if (originalClass != null) {
            isAPIParentClass = removedClassList.remove(originalClass);
        }
        if (nextClass != null) {
            isAPICurrentClass = addedClassList.remove(nextClass);
        }
        if (isAPIParentClass || isAPICurrentClass) {
            changeDiffClassList.add(new ChangeDiffClass(originalClass, nextClass, refactoring));
        }
        System.out.println();
    }

    private void setRefactoredOperation(Refactoring refactoring) {
        UMLClass originalClass = null;
        UMLOperation originalOperation = null;
        UMLClass nextClass = null;
        UMLOperation nextOperation = null;
        switch (refactoring.getRefactoringType()) {
            case EXTRACT_OPERATION:
            case EXTRACT_AND_MOVE_OPERATION:
                ExtractOperationRefactoring extractOperation = (ExtractOperationRefactoring) refactoring;
                nextOperation = extractOperation.getExtractedOperation();
                break;
            case INLINE_OPERATION:
            case MOVE_AND_INLINE_OPERATION:
                InlineOperationRefactoring inlineOperation = (InlineOperationRefactoring) refactoring;
                originalOperation = inlineOperation.getInlinedOperation();
                break;
            case RENAME_METHOD:
                RenameOperationRefactoring renameOperation = (RenameOperationRefactoring) refactoring;
                originalOperation = renameOperation.getOriginalOperation();
                nextOperation = renameOperation.getRenamedOperation();
                break;
            case MOVE_OPERATION:
            case PULL_UP_OPERATION:
            case PUSH_DOWN_OPERATION:
            case MOVE_AND_RENAME_OPERATION:
                MoveOperationRefactoring moveOperation = (MoveOperationRefactoring) refactoring;
                originalOperation = moveOperation.getOriginalOperation();
                nextOperation = moveOperation.getMovedOperation();
                break;
            case PARAMETERIZE_VARIABLE:
                RenameVariableRefactoring renameVariable = (RenameVariableRefactoring) refactoring;
                nextOperation = renameVariable.getOperationAfter();
                break;
            case MERGE_PARAMETER:
                MergeVariableRefactoring mergeVariable = (MergeVariableRefactoring) refactoring;
                originalOperation = mergeVariable.getOperationBefore();
                nextOperation = mergeVariable.getOperationAfter();
                break;
            case SPLIT_PARAMETER:
                SplitVariableRefactoring splitVariable = (SplitVariableRefactoring) refactoring;
                originalOperation = splitVariable.getOperationBefore();
                nextOperation = splitVariable.getOperationAfter();
                break;
            case CHANGE_PARAMETER_TYPE:
                ChangeVariableTypeRefactoring changeVariableType = (ChangeVariableTypeRefactoring) refactoring;
                originalOperation = changeVariableType.getOperationBefore();
                nextOperation = changeVariableType.getOperationAfter();
                break;
            case ADD_PARAMETER:
                AddParameterRefactoring addParameterRefactoring = (AddParameterRefactoring) refactoring;
                originalOperation = addParameterRefactoring.getOperationBefore();
                nextOperation = addParameterRefactoring.getOperationAfter();
                break;
            case REMOVE_PARAMETER:
                RemoveParameterRefactoring removeParameter = (RemoveParameterRefactoring) refactoring;
                originalOperation = removeParameter.getOperationBefore();
                nextOperation = removeParameter.getOperationAfter();
                break;
            case REORDER_PARAMETER:
                ReorderParameterRefactoring reorderParameter = (ReorderParameterRefactoring) refactoring;
                originalOperation = reorderParameter.getOperationBefore();
                nextOperation = reorderParameter.getOperationAfter();
                break;
        }
        if (originalOperation != null) {
            originalClass = parentModel.getUMLClass(originalOperation.getClassName());
        }
        if (nextOperation != null) {
            nextClass = currentModel.getUMLClass(nextOperation.getClassName());
        }
        parentModel.setIsRefactoredOperation(true, originalClass, originalOperation);
        currentModel.setIsRefactoredOperation(true, nextClass, nextOperation);
    }

    private void setRefactoredAttribute(Refactoring refactoring) {
        UMLClass originalClass = null;
        UMLAttribute originalAttribute = null;
        UMLClass nextClass = null;
        UMLAttribute nextAttribute = null;
        switch (refactoring.getRefactoringType()) {
            case EXTRACT_ATTRIBUTE:
                ExtractAttributeRefactoring extractAttribute = (ExtractAttributeRefactoring) refactoring;
                nextAttribute = extractAttribute.getVariableDeclaration();
                nextClass = extractAttribute.getNextClass();
                break;
            case MOVE_ATTRIBUTE:
            case PULL_UP_ATTRIBUTE:
            case PUSH_DOWN_ATTRIBUTE:
            case MOVE_RENAME_ATTRIBUTE:
                MoveAttributeRefactoring moveAttribute = (MoveAttributeRefactoring) refactoring;
                originalClass = parentModel.getUMLClass(moveAttribute.getSourceClassName());
                originalAttribute = moveAttribute.getOriginalAttribute();
                nextClass = currentModel.getUMLClass(moveAttribute.getTargetClassName());
                nextAttribute = moveAttribute.getMovedAttribute();
                break;
            case RENAME_ATTRIBUTE:
                RenameAttributeRefactoring renameAttribute = (RenameAttributeRefactoring) refactoring;
                originalClass = parentModel.getUMLClass(renameAttribute.getClassNameBefore());
                originalAttribute = renameAttribute.getOriginalAttribute();
                nextClass = currentModel.getUMLClass(renameAttribute.getClassNameAfter());
                nextAttribute = renameAttribute.getRenamedAttribute();
                break;
            case CHANGE_ATTRIBUTE_TYPE:
                ChangeAttributeTypeRefactoring changeAttributeType = (ChangeAttributeTypeRefactoring) refactoring;
                originalClass = parentModel.getUMLClass(changeAttributeType.getClassNameBefore());
                originalAttribute = changeAttributeType.getOriginalAttribute();
                nextClass = currentModel.getUMLClass(changeAttributeType.getClassNameAfter());
                nextAttribute = changeAttributeType.getChangedTypeAttribute();
                break;
        }
        parentModel.setIsRefactoredAttribute(true, originalClass, originalAttribute);
        currentModel.setIsRefactoredAttribute(true, nextClass, nextAttribute);
    }
}

package extension;

import apiminer.enums.RefType;
import apiminer.internal.util.NewUtilTools;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.diff.*;
import org.refactoringminer.api.Refactoring;

import java.util.HashMap;
import java.util.Map;

public class RefactoringElement {
    private Refactoring refactoring;
    private Map<String,UMLClass> mapParentClass = new HashMap<String, UMLClass>();
    private Map<String,UMLClass>  mapCurrentClass = new HashMap<String, UMLClass>();
    private UMLClass originalClass;
    private UMLClass nextClass;
    private UMLOperation originalOperation;
    private UMLOperation nextOperation;
    private UMLAttribute originalAttribute;
    private UMLAttribute nextAttribute;
    private RefType refType;
    private boolean isAPI;

    public RefactoringElement(Refactoring refactoring, Map<String,UMLClass> mapParentClass, Map<String,UMLClass>  mapCurrentClass){
        this.refactoring = refactoring;
        this.mapParentClass = mapParentClass;
        this.mapCurrentClass = mapCurrentClass;
        init();
    }

    private void init(){
        originalClass = null;
        nextClass = null;
        originalOperation = null;
        nextOperation = null;
        originalAttribute = null;
        nextAttribute = null;
        refType = null;
        isAPI = false;
        setRefactoredClass();
        setRefactoredOperation();
        setRefactoredAttribute();
    }

    public Refactoring getRefactoring() {
        return refactoring;
    }

    public UMLClass getOriginalClass() {
        return originalClass;
    }

    public UMLClass getNextClass() {
        return nextClass;
    }

    public UMLOperation getOriginalOperation() {
        return originalOperation;
    }

    public UMLOperation getNextOperation() {
        return nextOperation;
    }

    public UMLAttribute getOriginalAttribute() {
        return originalAttribute;
    }

    public UMLAttribute getNextAttribute() {
        return nextAttribute;
    }

    public boolean isAPI() {
        return isAPI;
    }

    public RefType getRefType() {
        return refType;
    }

    private void setRefactoredClass() {
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
        if (originalClass != null&&checkAPI(originalClass.getVisibility())) {
            isAPICurrentClass = true;
        }
        if (nextClass != null&&checkAPI(nextClass.getVisibility())) {
            isAPICurrentClass = true;
        }
        if(originalClass!=null||nextClass!=null){
            isAPI = isAPIParentClass||isAPICurrentClass;
            refType = RefType.CLASS;
        }
    }

    private void setRefactoredOperation() {
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
        boolean isAPIParentOperation = false;
        boolean isAPICurrentOperation = false;
        if (originalOperation != null) {
            originalClass = mapParentClass.get(originalOperation.getClassName());
            if(checkAPI(originalClass.getVisibility())&&checkAPI(originalOperation.getVisibility())){
                isAPIParentOperation = true;
            }
        }
        if (nextOperation != null) {
            nextClass = mapCurrentClass.get(nextOperation.getClassName());
            if(checkAPI(nextClass.getVisibility())&&checkAPI(nextOperation.getVisibility())){
                isAPICurrentOperation = true;
            }
        }
        if(originalOperation!=null||nextOperation!=null){
            isAPI = isAPIParentOperation||isAPICurrentOperation;
            refType = RefType.METHOD;
        }
    }

    private void setRefactoredAttribute() {
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
                originalClass = mapParentClass.get(moveAttribute.getSourceClassName());
                originalAttribute = moveAttribute.getOriginalAttribute();
                nextClass = mapCurrentClass.get(moveAttribute.getTargetClassName());
                nextAttribute = moveAttribute.getMovedAttribute();
                break;
            case RENAME_ATTRIBUTE:
                RenameAttributeRefactoring renameAttribute = (RenameAttributeRefactoring) refactoring;
                originalClass = mapParentClass.get(renameAttribute.getClassNameBefore());
                originalAttribute = renameAttribute.getOriginalAttribute();
                nextClass = mapCurrentClass.get(renameAttribute.getClassNameAfter());
                nextAttribute = renameAttribute.getRenamedAttribute();
                break;
            case CHANGE_ATTRIBUTE_TYPE:
                ChangeAttributeTypeRefactoring changeAttributeType = (ChangeAttributeTypeRefactoring) refactoring;
                originalClass = mapParentClass.get(changeAttributeType.getClassNameBefore());
                originalAttribute = changeAttributeType.getOriginalAttribute();
                nextClass = mapCurrentClass.get(changeAttributeType.getClassNameAfter());
                nextAttribute = changeAttributeType.getChangedTypeAttribute();
                break;
        }
        boolean isAPIParentAttribute = false;
        boolean isAPICurrentAttribute = false;
        if (originalAttribute != null) {
            if(checkAPI(originalClass.getVisibility())&&checkAPI(originalAttribute.getVisibility())){
                isAPIParentAttribute = true;
            }
        }
        if (nextAttribute != null) {
            if(checkAPI(nextClass.getVisibility())&&checkAPI(nextAttribute.getVisibility())){
                isAPICurrentAttribute = true;
            }
        }
        if(originalAttribute!=null||nextAttribute!=null){
            isAPI = isAPIParentAttribute||isAPICurrentAttribute;
            refType = RefType.ATTRIBUTE;
        }
    }
    private boolean checkAPI(String visibility){
        if(visibility.equals("public")||visibility.equals("protected")){
            return true;
        }else{
            return false;
        }
    }
}

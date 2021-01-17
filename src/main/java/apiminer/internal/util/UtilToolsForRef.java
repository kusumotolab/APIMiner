package apiminer.internal.util;

import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.UMLParameter;
import gr.uom.java.xmi.diff.*;
import org.apache.commons.lang3.StringUtils;
import org.refactoringminer.api.Refactoring;

import java.util.ArrayList;
import java.util.List;

public class UtilToolsForRef {
    public static String getTypeBefore(Refactoring refactoring) {
        String typeBefore = "";
        if (refactoring != null) {
            switch (refactoring.getRefactoringType()) {
                case EXTRACT_INTERFACE:
                case EXTRACT_SUPERCLASS:
                    List<String> listTypeBefore = new ArrayList<String>();
                    for (CodeRange codeRange : refactoring.leftSide()) {
                        listTypeBefore.add(codeRange.getCodeElement());
                    }
                    typeBefore = StringUtils.join(listTypeBefore, ", ");
                    break;
                default:
                    typeBefore = refactoring.leftSide().get(0).getCodeElement();
                    break;
            }
        }
        return typeBefore;
    }

    public static String getTypeAfter(Refactoring refactoring) {
        String typeAfter = "";
        if (refactoring != null) {
            typeAfter = refactoring.rightSide().get(0).getCodeElement();
        }
        return typeAfter;
    }


    public static UMLOperation getOperationBefore(Refactoring refactoring) {
        UMLOperation operationBefore = null;
        if (refactoring != null) {
            switch (refactoring.getRefactoringType()) {
                case MOVE_OPERATION:
                case PULL_UP_OPERATION:
                case PUSH_DOWN_OPERATION:
                case MOVE_AND_RENAME_OPERATION:
                    MoveOperationRefactoring move = (MoveOperationRefactoring) refactoring;
                    operationBefore = move.getOriginalOperation();
                    break;
                case RENAME_METHOD:
                    RenameOperationRefactoring rename = (RenameOperationRefactoring) refactoring;
                    operationBefore = rename.getOriginalOperation();
                    break;
                case INLINE_OPERATION:
                case MOVE_AND_INLINE_OPERATION:
                    InlineOperationRefactoring inline = (InlineOperationRefactoring) refactoring;
                    operationBefore = inline.getInlinedOperation();
                    break;
                case EXTRACT_OPERATION:
                case EXTRACT_AND_MOVE_OPERATION:
                    ExtractOperationRefactoring extract = (ExtractOperationRefactoring) refactoring;
                    operationBefore = extract.getSourceOperationBeforeExtraction();
                    break;
                case ADD_PARAMETER:
                    AddParameterRefactoring addParameter = (AddParameterRefactoring) refactoring;
                    operationBefore = addParameter.getOperationBefore();
                    break;
                case REMOVE_PARAMETER:
                    RemoveParameterRefactoring removeParameter = (RemoveParameterRefactoring) refactoring;
                    operationBefore = removeParameter.getOperationBefore();
                    break;
                case REORDER_PARAMETER:
                    ReorderParameterRefactoring reorder = (ReorderParameterRefactoring) refactoring;
                    operationBefore = reorder.getOperationBefore();
                    break;
                case SPLIT_PARAMETER:
                    SplitVariableRefactoring split = (SplitVariableRefactoring) refactoring;
                    operationBefore = split.getOperationBefore();
                    break;
                case MERGE_PARAMETER:
                    MergeVariableRefactoring merge = (MergeVariableRefactoring) refactoring;
                    operationBefore = merge.getOperationBefore();
                    break;
                case CHANGE_PARAMETER_TYPE:
                    ChangeVariableTypeRefactoring changeParameterType = (ChangeVariableTypeRefactoring) refactoring;
                    operationBefore = changeParameterType.getOperationBefore();
                    break;
                case PARAMETERIZE_VARIABLE:
                    RenameVariableRefactoring parameterize = (RenameVariableRefactoring) refactoring;
                    operationBefore = parameterize.getOperationBefore();
                    break;
                case CHANGE_RETURN_TYPE:
                    ChangeReturnTypeRefactoring changeReturnType = (ChangeReturnTypeRefactoring) refactoring;
                    operationBefore = changeReturnType.getOperationBefore();
                    break;
            }
        }
        return operationBefore;
    }

    public static UMLOperation getOperationAfter(Refactoring refactoring) {
        UMLOperation operationAfter = null;
        switch (refactoring.getRefactoringType()) {
            case MOVE_OPERATION:
            case PULL_UP_OPERATION:
            case PUSH_DOWN_OPERATION:
            case MOVE_AND_RENAME_OPERATION:
                MoveOperationRefactoring move = (MoveOperationRefactoring) refactoring;
                operationAfter = move.getMovedOperation();
                break;
            case RENAME_METHOD:
                RenameOperationRefactoring rename = (RenameOperationRefactoring) refactoring;
                operationAfter = rename.getRenamedOperation();
                break;
            case INLINE_OPERATION:
            case MOVE_AND_INLINE_OPERATION:
                InlineOperationRefactoring inline = (InlineOperationRefactoring) refactoring;
                operationAfter = inline.getTargetOperationAfterInline();
                break;
            case EXTRACT_OPERATION:
            case EXTRACT_AND_MOVE_OPERATION:
                ExtractOperationRefactoring extract = (ExtractOperationRefactoring) refactoring;
                operationAfter = extract.getExtractedOperation();
                break;
            case ADD_PARAMETER:
                AddParameterRefactoring addParameter = (AddParameterRefactoring) refactoring;
                operationAfter = addParameter.getOperationAfter();
                break;
            case REMOVE_PARAMETER:
                RemoveParameterRefactoring removeParameter = (RemoveParameterRefactoring) refactoring;
                operationAfter = removeParameter.getOperationAfter();
                break;
            case REORDER_PARAMETER:
                ReorderParameterRefactoring reorder = (ReorderParameterRefactoring) refactoring;
                operationAfter = reorder.getOperationAfter();
                break;
            case SPLIT_PARAMETER:
                SplitVariableRefactoring split = (SplitVariableRefactoring) refactoring;
                operationAfter = split.getOperationAfter();
                break;
            case MERGE_PARAMETER:
                MergeVariableRefactoring merge = (MergeVariableRefactoring) refactoring;
                operationAfter = merge.getOperationAfter();
                break;
            case CHANGE_PARAMETER_TYPE:
                ChangeVariableTypeRefactoring changeParameterType = (ChangeVariableTypeRefactoring) refactoring;
                operationAfter = changeParameterType.getOperationAfter();
                break;
            case PARAMETERIZE_VARIABLE:
                RenameVariableRefactoring parameterize = (RenameVariableRefactoring) refactoring;
                operationAfter = parameterize.getOperationAfter();
                break;
            case CHANGE_RETURN_TYPE:
                ChangeReturnTypeRefactoring changeReturnType = (ChangeReturnTypeRefactoring) refactoring;
                operationAfter = changeReturnType.getOperationAfter();
                break;
        }
        return operationAfter;
    }

    public static String getFullNameMethodAndPath(UMLOperation umlOperation) {
        String fullNameAndPath = "";
        if (umlOperation != null) {
            String path = umlOperation.getClassName();
            String returnMethod = (umlOperation.getReturnParameter() != null) ? (umlOperation.getReturnParameter().toString() + " ") : "";
            String name = umlOperation.getName();
            String signature = UtilToolsForRef.getSignatureMethod(umlOperation);
            ;
            fullNameAndPath = path + "#" + returnMethod + name + "(" + signature + ")";
        }
        return fullNameAndPath;
    }

    public static String getSignatureMethod(UMLOperation umlOperation) {
        String signature = "";
        if (umlOperation != null) {
            List<UMLParameter> listParameter = umlOperation.getParametersWithoutReturnType();
            List<String> listParameterType = new ArrayList<String>();
            if (listParameter != null) {
                for (UMLParameter parameter : listParameter) {
                    listParameterType.add(parameter.getType().toString());
                }
                signature = StringUtils.join(listParameterType, ", ");
            }
        }
        return signature;
    }

    public static String getSimpleNameMethod(UMLOperation umlOperation) {
        String simpleName = "";
        if (umlOperation != null) {
            List<UMLParameter> listParameter = umlOperation.getParametersWithoutReturnType();
            List<String> listParameterTypeAndName = new ArrayList<String>();
            String parametersMethod = "";
            if (listParameter != null) {
                for (UMLParameter parameter : listParameter) {
                    listParameterTypeAndName.add(parameter.getType().toString());
                }
                parametersMethod = StringUtils.join(listParameterTypeAndName, ", ");
            }
            simpleName = umlOperation.getName() + "(" + parametersMethod + ")";
        }
        return simpleName;
    }
    public static String getSimpleNameMethodWithParamName(UMLOperation umlOperation) {
        String simpleName = "";
        if (umlOperation != null) {
            List<UMLParameter> listParameter = umlOperation.getParametersWithoutReturnType();
            List<String> listParameterTypeAndName = new ArrayList<String>();
            String parametersMethod = "";
            if (listParameter != null) {
                for (UMLParameter parameter : listParameter) {
                    listParameterTypeAndName.add(parameter.getType() + " " + parameter.getName());
                }
                parametersMethod = StringUtils.join(listParameterTypeAndName, ", ");
            }
            simpleName = umlOperation.getName() + "(" + parametersMethod + ")";
        }
        return simpleName;
    }

    public static String getClassPathOfMethod(UMLOperation umlOperation) {
        String classPath = "";
        if (umlOperation != null) {
            classPath = umlOperation.getClassName();
        }
        return classPath;
    }

    public static String[] getAttributeBefore(Refactoring refactoring) {
        String[] attributeBefore = new String[2];
        if (refactoring != null) {
            switch (refactoring.getRefactoringType()) {
                case MOVE_ATTRIBUTE:
                case MOVE_RENAME_ATTRIBUTE:
                case PULL_UP_ATTRIBUTE:
                case PUSH_DOWN_ATTRIBUTE:
                    MoveAttributeRefactoring move = (MoveAttributeRefactoring) refactoring;
                    attributeBefore[0] = move.getOriginalAttribute().toString();
                    attributeBefore[1] = move.getOriginalAttribute().getClassName();
                    break;
                case RENAME_ATTRIBUTE:
                    RenameAttributeRefactoring rename = (RenameAttributeRefactoring) refactoring;
                    attributeBefore[0] = rename.getOriginalAttribute().toString();
                    attributeBefore[1] = rename.getClassNameBefore();
                    break;
                case CHANGE_ATTRIBUTE_TYPE:
                    ChangeAttributeTypeRefactoring changeType = (ChangeAttributeTypeRefactoring) refactoring;
                    attributeBefore[0] = changeType.getOriginalAttribute().toString();
                    attributeBefore[1] = changeType.getClassNameBefore();
                    break;
                case EXTRACT_ATTRIBUTE:
                    ExtractAttributeRefactoring extract = (ExtractAttributeRefactoring) refactoring;
                    attributeBefore[0] = "";
                    attributeBefore[1] = extract.getVariableDeclaration().getClassName();
                default:
                    break;
            }
        }
        return attributeBefore;
    }

    public static String[] getAttributeAfter(Refactoring refactoring) {
        String[] attributeAfter = new String[2];
        if (refactoring != null) {
            switch (refactoring.getRefactoringType()) {
                case MOVE_ATTRIBUTE:
                case MOVE_RENAME_ATTRIBUTE:
                case PULL_UP_ATTRIBUTE:
                case PUSH_DOWN_ATTRIBUTE:
                    MoveAttributeRefactoring move = (MoveAttributeRefactoring) refactoring;
                    attributeAfter[0] = move.getMovedAttribute().toString();
                    attributeAfter[1] = move.getMovedAttribute().getClassName();
                    break;
                case RENAME_ATTRIBUTE:
                    RenameAttributeRefactoring rename = (RenameAttributeRefactoring) refactoring;
                    attributeAfter[0] = rename.getRenamedAttribute().toString();
                    attributeAfter[1] = rename.getClassNameAfter();
                    break;
                case CHANGE_ATTRIBUTE_TYPE:
                    ChangeAttributeTypeRefactoring changeType = (ChangeAttributeTypeRefactoring) refactoring;
                    attributeAfter[0] = changeType.getChangedTypeAttribute().toString();
                    attributeAfter[1] = changeType.getClassNameAfter();
                    break;
                case EXTRACT_ATTRIBUTE:
                    ExtractAttributeRefactoring extract = (ExtractAttributeRefactoring) refactoring;
                    attributeAfter[0] = extract.getVariableDeclaration().toString();
                    attributeAfter[1] = extract.getVariableDeclaration().getClassName();
                default:
                    break;
            }
        }
        return attributeAfter;
    }

    public static String getFullNameFieldAndPath(String[] attribute) {
        String fullNameAndPath = "";
        if (attribute != null && attribute.length == 2) {
            String[] nameAndType = attribute[0].split(" : ");
            String fieldType = nameAndType[1] + " ";
            String[] modifierAndName = nameAndType[0].split(" ");
            String fullName = fieldType + modifierAndName[modifierAndName.length - 1];
            String path = UtilToolsForRef.getClassPathOfField(attribute);
            fullNameAndPath = path + "#" + fullName;
        }
        return fullNameAndPath;
    }

    public static String getClassPathOfField(String[] attribute) {
        String classPath = "";
        if (attribute != null && attribute.length == 2) {
            classPath = attribute[1];
        }
        return classPath;
    }

    public static String getSimpleNameField(String[] attribute) {
        String simpleName = "";
        if (attribute != null && attribute.length == 2) {
            String[] nameAndType = attribute[0].split(" : ");
            String[] modifierAndName = nameAndType[0].split(" ");
            simpleName = modifierAndName[modifierAndName.length - 1];
        }
        return simpleName;
    }

    public static String getSimpleNameFieldAndType(String fullNameAndPath){
        String simpleNameFieldAndType = "";
        if(fullNameAndPath!=null){
            String[] arrayFullNameAndPath = fullNameAndPath.split("#");
            if(arrayFullNameAndPath.length==2){
                String[] arraySimpleNameAndType = arrayFullNameAndPath[1].split(" ");
                List<String> typeList = new ArrayList<String>();
                for(int i=0;i<arraySimpleNameAndType.length-1;i++){
                    typeList.add(arraySimpleNameAndType[i]);
                }
                String type = String.join(" ",typeList);
                simpleNameFieldAndType = arraySimpleNameAndType[arraySimpleNameAndType.length-1] + " : " + type;
            }
        }
        return simpleNameFieldAndType;
    }
}

package extension;

import apiminer.Change;
import apiminer.enums.Classifier;
import apiminer.internal.util.NewUtilTools;
import extension.Model.*;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.diff.*;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringMinerTimedOutException;

import java.util.*;

public class ModelDiff {
    private UMLModel parentUMLModel;
    private UMLModel currentUMLModel;
    private UMLModelDiff umlModelDiff;
    private Classifier classifierAPI;
    private List<Refactoring> refactorings = new ArrayList<Refactoring>();
    private List<APIRefactoring> refactoringClassList = new ArrayList<>();
    private List<APIRefactoring> refactoringMethodList = new ArrayList<>();
    private List<APIRefactoring> refactoringFieldList = new ArrayList<>();

    private Diff diff = new Diff();
    private APIRefactoring apiRefactoring;

    private List<Change> changeTypeList = new ArrayList<Change>();
    private List<Change> changeMethodList = new ArrayList<Change>();
    private List<Change> changeFieldList = new ArrayList<Change>();


    public ModelDiff(UMLModel parentUMLModel, UMLModel currentUMLModel, UMLModelDiff modelDiff, Classifier classifierAPI) {
        this.parentUMLModel = parentUMLModel;
        this.currentUMLModel = currentUMLModel;
        this.umlModelDiff = modelDiff;
        this.classifierAPI = classifierAPI;
        this.apiRefactoring = null;
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
        initDiff();
        apiRefactoring = new APIRefactoring(refactorings, parentUMLModel, currentUMLModel);
        removeRefactoredClass();
        removeRefactoredOperation();
        removeRefactoredAttribute();
        System.out.println();
    }

    /*
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

     */

    private void initDiff(){
        Map<String, ModelClass> mapClassParent = new HashMap<String, ModelClass>();
        for (UMLClass parentClass : parentUMLModel.getClassList()) {
            mapClassParent.put(NewUtilTools.getClassName(parentClass), new ModelClass(parentClass));
        }
        for (UMLClass currentClass : currentUMLModel.getClassList()) {
            ModelClass parentClassModel = mapClassParent.remove(NewUtilTools.getClassName(currentClass));
            if (parentClassModel != null) {
                UMLClass parentClass = parentClassModel.getUmlClass();
                if (isAPIClass(parentClass) || isAPIClass(currentClass)) {
                    CommonClass commonClass = new CommonClass(parentClass, currentClass);
                    for (UMLOperation currentOperation : currentClass.getOperations()) {
                        UMLOperation parentOperation = parentClassModel.getOperationMap().remove(NewUtilTools.getSignatureMethod(currentOperation));
                        if (parentOperation != null) {
                            if ((isAPIClass(parentClass) && isAPIOperation(parentOperation)) || (isAPIClass(currentClass) && isAPIOperation(currentOperation))) {
                                CommonOperation commonOperation = new CommonOperation(parentOperation,currentOperation);
                                commonClass.getCommonOperationMap().put(NewUtilTools.getSignatureMethod(parentOperation), commonOperation);
                            }
                        } else {
                            if (isAPIClass(currentClass) && isAPIOperation(currentOperation)) {
                                OperationModel addedOperation = new OperationModel(currentOperation);
                                commonClass.getAddedOperationMap().put(NewUtilTools.getSignatureMethod(currentOperation), addedOperation);
                            }
                        }
                    }
                    for (Map.Entry<String, UMLOperation> umlOperationEntry : parentClassModel.getOperationMap().entrySet()) {
                        UMLOperation parentOperation = umlOperationEntry.getValue();
                        if (isAPIClass(parentClass) && isAPIOperation(parentOperation)) {
                            OperationModel removedOperation = new OperationModel(parentOperation);
                            commonClass.getRemovedOperationMap().put(NewUtilTools.getSignatureMethod(parentOperation), removedOperation);
                        }
                    }
                    for (UMLAttribute currentAttribute : currentClass.getAttributes()) {
                        UMLAttribute parentAttribute = parentClassModel.getAttributeMap().remove(currentAttribute.toString());
                        if (parentAttribute != null) {
                            if ((isAPIClass(parentClass) && isAPIAttribute(parentAttribute)) || (isAPIClass(currentClass) && isAPIAttribute(currentAttribute))) {
                                CommonAttribute commonAttribute = new CommonAttribute(parentAttribute,currentAttribute);
                                commonClass.getCommonAttributeMap().put(parentAttribute.toString(), commonAttribute);
                            }
                        } else {
                            if (isAPIClass(currentClass) && isAPIAttribute(currentAttribute)) {
                                AttributeModel addedAttribute = new AttributeModel(currentAttribute);
                                commonClass.getAddedAttributeMap().put(currentAttribute.toString(), addedAttribute);
                            }
                        }
                    }
                    for (Map.Entry<String, UMLAttribute> umlAttributeEntry : parentClassModel.getAttributeMap().entrySet()) {
                        UMLAttribute parentAttribute = umlAttributeEntry.getValue();
                        if (isAPIClass(parentClass) && isAPIAttribute(parentAttribute)) {
                            AttributeModel removedAttribute = new AttributeModel(parentAttribute);
                            commonClass.getRemovedAttributeMap().put(parentAttribute.toString(), removedAttribute);
                        }
                    }
                    diff.getCommonClassMap().put(parentClass.toString(), commonClass);
                }
            } else {
                if (isAPIClass(currentClass)) {
                    ClassModel addedClass = new ClassModel(currentClass);
                    diff.getAddedClassMap().put(currentClass.toString(), addedClass);
                }

            }
        }
        for (Map.Entry<String, ModelClass> removedClassEntry : mapClassParent.entrySet()) {
            UMLClass parentClass = removedClassEntry.getValue().getUmlClass();
            if (isAPIClass(parentClass)) {
                ClassModel removedClass = new ClassModel(parentClass);
                diff.getRemovedClassMap().put(parentClass.toString(), removedClass);
            }
        }
    }

    private void removeRefactoredClass() {
        for (RefactoringElement refactoringClass : apiRefactoring.getApiClassRefactorings()) {
            UMLClass originalClass = refactoringClass.getOriginalClass();
            UMLClass nextClass = refactoringClass.getNextClass();
            ClassModel originalClassModel = null;
            ClassModel nextClassModel = null;
            if (originalClass != null) {
                originalClassModel = diff.getRemovedClassMap().get(NewUtilTools.getClassName(originalClass));
                originalClassModel.setRefactored(true);
            }
            if (nextClass != null) {
                nextClassModel = diff.getAddedClassMap().get(NewUtilTools.getClassName(nextClass));
                nextClassModel.setRefactored(true);
            }
            if(originalClassModel!=null||nextClassModel!=null){
                //add change
            }
            //if be able to remove,add refactoring as a change
        }
    }


    private void removeRefactoredOperation() {
        for (RefactoringElement refactoringOperation : apiRefactoring.getApiOperationRefactorings()) {
            UMLClass originalClass = refactoringOperation.getOriginalClass();
            UMLClass nextClass = refactoringOperation.getNextClass();
            UMLOperation originalOperation = refactoringOperation.getOriginalOperation();
            UMLOperation nextOperation = refactoringOperation.getNextOperation();
            OperationModel originalOperationModel = null;
            OperationModel nextOperationModel = null;
            if (originalClass != null && originalOperation != null) {
                CommonClass commonClass = diff.getCommonClassMap().get(originalClass.toString());
                if (commonClass != null) {
                    commonClass.getRemovedOperationMap().get()
                    commonClass.getRemovedOperationMap().remove(NewUtilTools.getSignatureMethod(originalOperation));
                }
            }
            if (nextClass != null && nextOperation != null) {
                CommonClass commonClass = diff.getCommonClassList().get(nextClass.toString());
                if (commonClass != null) {
                    commonClass.getRemovedOperationMap().remove(NewUtilTools.getSignatureMethod(nextOperation));
                }
            }
        }

    }

    private void removeRefactoredAttribute() {
        for (RefactoringElement refactoringAttribute : apiRefactoring.getApiAttributeRefactorings()) {
            UMLClass originalClass = refactoringAttribute.getOriginalClass();
            UMLClass nextClass = refactoringAttribute.getNextClass();
            UMLAttribute originalAttribute = refactoringAttribute.getOriginalAttribute();
            UMLAttribute nextAttribute = refactoringAttribute.getNextAttribute();
            if (originalClass != null && originalAttribute != null) {
                CommonClass commonClass = diff.getCommonClassList().get(originalClass.toString());
                if (commonClass != null) {
                    commonClass.getRemovedAttributeMap().remove(originalAttribute.toString());
                }
            }
            if (nextClass != null && nextAttribute != null) {
                CommonClass commonClass = diff.getCommonClassList().get(nextClass.toString());
                if (commonClass != null) {
                    commonClass.getRemovedOperationMap().remove(nextAttribute.toString());
                }
            }
        }
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

    private String getSignatureOperation(UMLOperation umlOperation) {
        String signature = "";
        String s = umlOperation.toString();
        return signature;
    }

    private void detectChangesAccess(UMLClass parentClass, UMLClass currentClass) {

    }

    private void detectMethodChanges() {
    }


    private void detectRefactoring() {

    }
}

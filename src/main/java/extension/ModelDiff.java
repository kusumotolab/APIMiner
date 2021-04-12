package extension;

import apiminer.Change;
import apiminer.enums.Classifier;
import apiminer.internal.util.NewUtilTools;
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
    private UMLModel currentUMLModel;
    private UMLModelDiff umlModelDiff;
    private Classifier classifierAPI;
    private List<Refactoring> refactorings = new ArrayList<Refactoring>();
    private List<APIRefactoring> refactoringClassList = new ArrayList<>();
    private List<APIRefactoring> refactoringMethodList = new ArrayList<>();
    private List<APIRefactoring> refactoringFieldList = new ArrayList<>();

    private Diff diff = new Diff();


    private List<Change> changeTypeList = new ArrayList<Change>();
    private List<Change> changeMethodList = new ArrayList<Change>();
    private List<Change> changeFieldList = new ArrayList<Change>();


    public ModelDiff(UMLModel parentUMLModel, UMLModel currentUMLModel, UMLModelDiff modelDiff, Classifier classifierAPI) {
        this.parentUMLModel = parentUMLModel;
        this.currentUMLModel = currentUMLModel;
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
        detectClassChanges();
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

    private void detectClassChanges() {
        Map<String, ModelClass> mapClassParent = new HashMap<String, ModelClass>();
        for (UMLClass parentClass : parentUMLModel.getClassList()) {
            mapClassParent.put(parentClass.toString(),new ModelClass(parentClass));
        }
        for (UMLClass currentClass : currentUMLModel.getClassList()) {
            ModelClass parentClassModel = mapClassParent.remove(currentClass.toString());
            if (parentClassModel != null) {
                UMLClass parentClass = parentClassModel.getUmlClass();
                if (isAPIClass(parentClass) || isAPIClass(currentClass)) {
                    APIClass commonClass = new APIClass(parentClass,currentClass);
                    for(UMLOperation currentOperation:currentClass.getOperations()){
                        UMLOperation parentOperation = parentClassModel.getOperationMap().remove(NewUtilTools.getSignatureMethod(currentOperation));
                        if(parentOperation!=null){
                            if((isAPIClass(parentClass)&&isAPIOperation(parentOperation))||(isAPIClass(currentClass)&&isAPIOperation(currentOperation))){
                                commonClass.getCommonOperationList().add(new APIOperation(parentOperation,currentOperation));
                            }
                        }else{
                            if(isAPIClass(currentClass)&&isAPIOperation(currentOperation)){
                                commonClass.getAddedOperationList().add(new APIOperation(null,currentOperation));
                            }
                        }
                    }
                    for(Map.Entry<String,UMLOperation> umlOperationEntry:parentClassModel.getOperationMap().entrySet()){
                        UMLOperation removedOperation = umlOperationEntry.getValue();
                        if(isAPIClass(parentClass)&&isAPIOperation(removedOperation)){
                            commonClass.getRemovedOperationList().add(new APIOperation(removedOperation,null));
                        }
                    }
                    for(UMLAttribute currentAttribute:currentClass.getAttributes()){
                        UMLAttribute parentAttribute = parentClassModel.getAttributeMap().remove(currentAttribute.toString());
                        if(parentAttribute!=null){
                            if((isAPIClass(parentClass)&&isAPIAttribute(parentAttribute))||(isAPIClass(currentClass)&&isAPIAttribute(currentAttribute))){
                                commonClass.getCommonAttributeList().add(new APIAttribute(parentAttribute,currentAttribute));
                            }
                        }else{
                            if(isAPIClass(currentClass)&&isAPIAttribute(currentAttribute)){
                                commonClass.getAddedAttributeList().add(new APIAttribute(null,currentAttribute));
                            }
                        }
                    }
                    for(Map.Entry<String,UMLAttribute> umlAttributeEntry:parentClassModel.getAttributeMap().entrySet()){
                        UMLAttribute removedAttribute = umlAttributeEntry.getValue();
                        if(isAPIClass(parentClass)&&isAPIAttribute(removedAttribute)){
                            commonClass.getRemovedAttributeList().add(new APIAttribute(removedAttribute,null));
                        }
                    }
                    diff.getCommonClassList().add(commonClass);
                }
            } else {
                if (isAPIClass(currentClass)) {
                    APIClass addedClass = new APIClass(null,currentClass);
                    for(UMLOperation addedOperation:currentClass.getOperations()){
                        if(isAPIOperation(addedOperation)){
                            addedClass.getAddedOperationList().add(new APIOperation(null,addedOperation));
                        }
                    }
                    for(UMLAttribute addedAttribute:currentClass.getAttributes()){
                        if(isAPIAttribute(addedAttribute)){
                            addedClass.getAddedAttributeList().add(new APIAttribute(null,addedAttribute));
                        }
                    }
                    diff.getAddedClassList().add(addedClass);
                }

            }
        }
        for(Map.Entry<String,ModelClass> removedClassEntry:mapClassParent.entrySet()){
            UMLClass parentClass = removedClassEntry.getValue().getUmlClass();
            if(isAPIClass(parentClass)){
                APIClass removedClass = new APIClass(parentClass,null);
                for(UMLOperation removedOperation:parentClass.getOperations()){
                    if(isAPIOperation(removedOperation)){
                        removedClass.getRemovedOperationList().add(new APIOperation(removedOperation,null));
                    }
                }
                for(UMLAttribute removedAttribute:parentClass.getAttributes()){
                    if(isAPIAttribute(removedAttribute)){
                        removedClass.getRemovedAttributeList().add(new APIAttribute(removedAttribute,null));
                    }
                }
                diff.getRemovedClassList().add(removedClass);
            }
        }
        APIRefactoring apiRefactoring = new APIRefactoring(refactorings,parentUMLModel,currentUMLModel);
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
    }

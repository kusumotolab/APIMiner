package extension;

import apiminer.enums.Category;
import apiminer.enums.Classifier;
import apiminer.internal.util.NewUtilTools;
import apiminer.util.Change;
import extension.Diff.ClassDiff;
import extension.Model.*;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.diff.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringMinerTimedOutException;

import java.util.*;

public class ModelDiff {
    private UMLModel parentUMLModel;
    private UMLModel currentUMLModel;
    private UMLModelDiff umlModelDiff;
    private Classifier classifierAPI;
    private List<Refactoring> refactorings = new ArrayList<Refactoring>();
    private RevCommit revCommit;

    private List<APIRefactoring> refactoringClassList = new ArrayList<>();
    private List<APIRefactoring> refactoringMethodList = new ArrayList<>();
    private List<APIRefactoring> refactoringFieldList = new ArrayList<>();

    private Diff diff = new Diff();
    private APIRefactoring apiRefactoring;

    private List<Change> changeTypeList = new ArrayList<>();
    private List<Change> changeMethodList = new ArrayList<>();
    private List<Change> changeFieldList = new ArrayList<>();


    public ModelDiff(UMLModel parentUMLModel, UMLModel currentUMLModel, UMLModelDiff modelDiff, Classifier classifierAPI,RevCommit revCommit) {
        this.parentUMLModel = parentUMLModel;
        this.currentUMLModel = currentUMLModel;
        this.umlModelDiff = modelDiff;
        this.classifierAPI = classifierAPI;
        this.revCommit = revCommit;
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
        detectClassRefactoring();
        detectOperationRefactoring();
        detectAttributeRefactoring();
        detectOtherClassChange();
        //System.out.println();
    }

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

    private void detectClassRefactoring() {
        for (RefactoringElement refactoringClass : apiRefactoring.getApiClassRefactorings()) {
            UMLClass originalClass = refactoringClass.getOriginalClass();
            UMLClass nextClass = refactoringClass.getNextClass();
            ClassModel originalClassModel = null;
            ClassModel nextClassModel = null;
            if (originalClass != null) {
                originalClassModel = diff.getRemovedClassMap().get(NewUtilTools.getClassName(originalClass));
                if(originalClassModel!=null){
                    originalClassModel.setRefactored(true);
                }
            }
            if (nextClass != null) {
                nextClassModel = diff.getAddedClassMap().get(NewUtilTools.getClassName(nextClass));
                if(nextClassModel!=null){
                    nextClassModel.setRefactored(true);
                }
            }
            if(originalClassModel!=null||nextClassModel!=null){
                changeTypeList.addAll(new ClassDiff(refactoringClass,revCommit).getClassChangeList());
            }
            //if be able to remove,add refactoring as a change
        }
    }


    private void detectOperationRefactoring() {
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
                    originalOperationModel = commonClass.getRemovedOperationMap().get(NewUtilTools.getSignatureMethod(originalOperation));
                    if(originalOperationModel!=null){
                        originalOperationModel.setRefactored(true);
                    }
                }
            }
            if (nextClass != null && nextOperation != null) {
                CommonClass commonClass = diff.getCommonClassMap().get(nextClass.toString());
                if (commonClass != null) {
                    nextOperationModel = commonClass.getAddedOperationMap().get(NewUtilTools.getSignatureMethod(nextOperation));
                    if(nextOperationModel!=null){
                        nextOperationModel.setRefactored(true);
                    }
                }
            }
            if(originalOperationModel!=null||nextOperationModel!=null){
                //add change
            }
        }

    }

    private void detectAttributeRefactoring() {
        for (RefactoringElement refactoringAttribute : apiRefactoring.getApiAttributeRefactorings()) {
            UMLClass originalClass = refactoringAttribute.getOriginalClass();
            UMLClass nextClass = refactoringAttribute.getNextClass();
            UMLAttribute originalAttribute = refactoringAttribute.getOriginalAttribute();
            UMLAttribute nextAttribute = refactoringAttribute.getNextAttribute();
            AttributeModel originalAttributeModel = null;
            AttributeModel nextAttributeModel = null;
            if (originalClass != null && originalAttribute != null) {
                CommonClass commonClass = diff.getCommonClassMap().get(originalClass.toString());
                if (commonClass != null) {
                    originalAttributeModel = commonClass.getRemovedAttributeMap().get(NewUtilTools.getAttributeName(originalAttribute));
                    if(originalAttributeModel!=null){
                        originalAttributeModel.setRefactored(true);
                    }
                }
            }
            if (nextClass != null && nextAttributeModel != null) {
                CommonClass commonClass = diff.getCommonClassMap().get(nextClass.toString());
                if (commonClass != null) {
                    nextAttributeModel = commonClass.getAddedAttributeMap().get(NewUtilTools.getAttributeName(nextAttribute));
                    if(nextAttributeModel!=null){
                        nextAttributeModel.setRefactored(true);
                    }
                }
            }
            if(originalAttributeModel!=null||nextAttributeModel!=null){
                //add change
            }
        }
    }

    private void detectOtherClassChange(){
        for(ClassModel removedClassModel:diff.getRemovedClassMap().values()){
            if(!removedClassModel.isRefactored()){
                changeTypeList.addAll(new ClassDiff(Category.TYPE_REMOVE,removedClassModel.getUmlClass(),revCommit).getClassChangeList());
            }
        }
        for(ClassModel addedClassModel:diff.getAddedClassMap().values()){
            if(!addedClassModel.isRefactored()){
                changeTypeList.addAll(new ClassDiff(Category.TYPE_ADD,addedClassModel.getUmlClass(),revCommit).getClassChangeList());
            }
        }
        for(CommonClass commonClass:diff.getCommonClassMap().values()){
            changeTypeList.addAll(new ClassDiff(commonClass,revCommit).getClassChangeList());
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

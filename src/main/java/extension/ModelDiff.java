package extension;

import apiminer.enums.Classifier;
import apiminer.internal.util.NewUtilTools;
import apiminer.util.Change;
import apiminer.util.category.ClassChange;
import apiminer.util.category.field.AddFieldChange;
import apiminer.util.category.field.RemoveFieldChange;
import apiminer.util.category.method.AddMethodChange;
import apiminer.util.category.method.RemoveMethodChange;
import apiminer.util.category.type.AddTypeChange;
import apiminer.util.category.type.RemoveTypeChange;
import extension.Diff.AttributeDiff;
import extension.Diff.ClassDiff;
import extension.Diff.OperationDiff;
import extension.Model.*;
import extension.category.Refactored;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.diff.UMLModelDiff;
import org.eclipse.jgit.revwalk.RevCommit;
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
    private RevCommit revCommit;

    private Map<Refactored, List<Change>> apiClassRefactoredMap = new HashMap<>();

    private Map<Refactored, List<Change>> apiOperationRefactoredMap = new HashMap<>();

    private Map<Refactored, List<Change>> apiAttributeRefactoredMap = new HashMap<>();

    private Diff diff = new Diff();

    private List<Change> changeTypeList = new ArrayList<>();
    private List<Change> changeMethodList = new ArrayList<>();
    private List<Change> changeFieldList = new ArrayList<>();

    public ModelDiff(UMLModel parentUMLModel, UMLModel currentUMLModel, UMLModelDiff modelDiff, Classifier classifierAPI, RevCommit revCommit) {
        this.parentUMLModel = parentUMLModel;
        this.currentUMLModel = currentUMLModel;
        this.umlModelDiff = modelDiff;
        this.classifierAPI = classifierAPI;
        this.revCommit = revCommit;
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
        detectAPIRefactoring();
        changeTypeList.addAll(detectClassChange());
        changeMethodList.addAll(detectMethodChange());
        changeFieldList.addAll(detectFieldChange());
        //detectClassRefactoring();
        //detectOperationRefactoring();
        //detectAttributeRefactoring();
        //detectOtherClassChange();
        //System.out.println();
    }

    private void initDiff() {
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
                                CommonOperation commonOperation = new CommonOperation(parentOperation, currentOperation);
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
                                CommonAttribute commonAttribute = new CommonAttribute(parentAttribute, currentAttribute);
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

    private void detectAPIRefactoring() {
        Map<String, UMLClass> parentClassMap = new HashMap<String, UMLClass>();
        for (UMLClass parentClass : parentUMLModel.getClassList()) {
            parentClassMap.put(NewUtilTools.getClassName(parentClass), parentClass);
        }
        Map<String, UMLClass> currentClassMap = new HashMap<String, UMLClass>();
        for (UMLClass currentClass : currentUMLModel.getClassList()) {
            currentClassMap.put(NewUtilTools.getClassName(currentClass), currentClass);
        }
        for (Refactoring refactoring : refactorings) {
            Convert convert = new Convert(refactoring, parentClassMap, currentClassMap, revCommit);
            if (convert.isAPI()) {
                Refactored refactored = convert.getRefactored();
                boolean isExist = false;
                switch (refactored.getRefType()) {
                    case CLASS:
                        for (Map.Entry<Refactored, List<Change>> entry : apiClassRefactoredMap.entrySet()) {
                            if (entry.getKey().equalRefactored(refactored)) {
                                entry.getValue().add(convert.getChange());
                                isExist = true;
                                break;
                            }
                        }
                        if (!isExist) {
                            List<Change> changeList = new ArrayList<>();
                            changeList.add(convert.getChange());
                            apiClassRefactoredMap.put(convert.getRefactored(), changeList);
                        }
                        break;
                    case METHOD:
                        for (Map.Entry<Refactored, List<Change>> entry : apiOperationRefactoredMap.entrySet()) {
                            if (entry.getKey().equalRefactored(refactored)) {
                                entry.getValue().add(convert.getChange());
                                isExist = true;
                                break;
                            }
                        }
                        if (!isExist) {
                            List<Change> changeList = new ArrayList<>();
                            changeList.add(convert.getChange());
                            apiOperationRefactoredMap.put(convert.getRefactored(), changeList);
                        }
                        break;
                    case ATTRIBUTE:
                        for (Map.Entry<Refactored, List<Change>> entry : apiAttributeRefactoredMap.entrySet()) {
                            if (entry.getKey().equalRefactored(refactored)) {
                                entry.getValue().add(convert.getChange());
                                isExist = true;
                                break;
                            }
                        }
                        if (!isExist) {
                            List<Change> changeList = new ArrayList<>();
                            changeList.add(convert.getChange());
                            apiAttributeRefactoredMap.put(convert.getRefactored(), changeList);
                        }
                        break;
                }
            }
        }

    }

    private List<Change> detectClassChange(){
        List<Change> changeList = new ArrayList<>();
        for(Map.Entry<Refactored,List<Change>> entry:apiClassRefactoredMap.entrySet()){
            Refactored refactored = entry.getKey();
            if(refactored.getOriginalClass()!=null){
                ClassModel classModel = diff.getRemovedClassMap().get(NewUtilTools.getClassName(refactored.getOriginalClass()));
                if(classModel!=null){
                    classModel.setRefactored(true);
                }
            }
            if(refactored.getNextClass()!=null){
                ClassModel classModel = diff.getAddedClassMap().get(NewUtilTools.getClassName(refactored.getNextClass()));
                if(classModel!=null){
                    classModel.setRefactored(true);
                }
            }
            changeList.addAll(new ClassDiff(refactored.getOriginalClass(),refactored.getNextClass(),entry.getValue(),revCommit).getChangeList());
        }
        for(CommonClass commonClass:diff.getCommonClassMap().values()){
            changeList.addAll(new ClassDiff(commonClass.getOriginalClass(),commonClass.getNextClass(),new ArrayList<>(),revCommit).getChangeList());
        }
        for(ClassModel removedClassModel:diff.getRemovedClassMap().values()){
            if(!removedClassModel.isRefactored()){
                changeList.add(new RemoveTypeChange(removedClassModel.getUmlClass(),revCommit));
            }
        }
        for(ClassModel addedClassModel:diff.getAddedClassMap().values()){
            if(!addedClassModel.isRefactored()){
                changeList.add(new AddTypeChange(addedClassModel.getUmlClass(),revCommit));
            }
        }
        return changeList;
    }

    private List<Change> detectMethodChange(){
        List<Change> changeList = new ArrayList<>();
        for(Map.Entry<Refactored,List<Change>> entry:apiOperationRefactoredMap.entrySet()){
            Refactored refactored = entry.getKey();
            if(refactored.getOriginalClass()!=null&&refactored.getOriginalOperation()!=null){
                CommonClass commonClass = diff.getCommonClassMap().get(NewUtilTools.getClassName(refactored.getOriginalClass()));
                if(commonClass!=null){
                    OperationModel operationModel = commonClass.getRemovedOperationMap().get(refactored.getOriginalOperation());
                    if(operationModel!=null){
                        operationModel.setRefactored(true);
                    }
                }
            }
            if(refactored.getNextClass()!=null&&refactored.getNextOperation()!=null){
                CommonClass commonClass = diff.getCommonClassMap().get(NewUtilTools.getClassName(refactored.getNextClass()));
                if(commonClass!=null){
                    OperationModel operationModel = commonClass.getAddedOperationMap().get(refactored.getNextOperation());
                    if(operationModel!=null){
                        operationModel.setRefactored(true);
                    }
                }
            }
            changeList.addAll(new OperationDiff(refactored.getOriginalClass(),refactored.getOriginalOperation(),refactored.getNextClass(),refactored.getNextOperation(),entry.getValue(),revCommit).getChangeList());
        }
        for(CommonClass commonClass:diff.getCommonClassMap().values()){
            for(CommonOperation commonOperation:commonClass.getCommonOperationMap().values()){
                changeList.addAll(new OperationDiff(commonClass.getOriginalClass(),commonOperation.getOriginalOperation(),commonClass.getNextClass(),commonOperation.getNextOperation(),new ArrayList<>(),revCommit).getChangeList());
            }
            for(OperationModel removedOperationModel:commonClass.getRemovedOperationMap().values()){
                if(!removedOperationModel.isRefactored()){
                    changeList.add(new RemoveMethodChange(commonClass.getOriginalClass(),removedOperationModel.getUmlOperation(),revCommit));
                }
            }
            for(OperationModel addedOperationModel:commonClass.getAddedOperationMap().values()){
                if(!addedOperationModel.isRefactored()){
                    changeList.add(new AddMethodChange(commonClass.getNextClass(),addedOperationModel.getUmlOperation(),revCommit));
                }
            }
        }
        return changeList;
    }

    private List<Change> detectFieldChange(){
        List<Change> changeList = new ArrayList<>();
        for(Map.Entry<Refactored,List<Change>> entry:apiAttributeRefactoredMap.entrySet()){
            Refactored refactored = entry.getKey();
            if(refactored.getOriginalClass()!=null&&refactored.getOriginalAttribute()!=null){
                CommonClass commonClass = diff.getCommonClassMap().get(NewUtilTools.getAttributeName(refactored.getOriginalAttribute()));
                if(commonClass!=null){
                    AttributeModel attributeModel = commonClass.getRemovedAttributeMap().get(refactored.getOriginalAttribute());
                    if(attributeModel!=null){
                        attributeModel.setRefactored(true);
                    }
                }
            }
            if(refactored.getNextClass()!=null&&refactored.getNextAttribute()!=null){
                CommonClass commonClass = diff.getCommonClassMap().get(NewUtilTools.getAttributeName(refactored.getNextAttribute()));
                if(commonClass!=null){
                    AttributeModel attributeModel = commonClass.getAddedAttributeMap().get(refactored.getNextAttribute());
                    if(attributeModel!=null){
                        attributeModel.setRefactored(true);
                    }
                }
            }
            changeList.addAll(new AttributeDiff(refactored.getOriginalClass(),refactored.getOriginalAttribute(),refactored.getNextClass(),refactored.getNextAttribute(),entry.getValue(),revCommit).getChangeList());
        }
        for(CommonClass commonClass:diff.getCommonClassMap().values()){
            for(CommonAttribute commonAttribute:commonClass.getCommonAttributeMap().values()){
                changeList.addAll(new AttributeDiff(commonClass.getOriginalClass(),commonAttribute.getOriginalAttribute(),commonClass.getNextClass(),commonAttribute.getNextAttribute(),new ArrayList<>(),revCommit).getChangeList());
            }
            for(AttributeModel removedAttributeModel:commonClass.getRemovedAttributeMap().values()){
                if(!removedAttributeModel.isRefactored()){
                    changeList.add(new RemoveFieldChange(commonClass.getOriginalClass(),removedAttributeModel.getUmlAttribute(),revCommit));
                }
            }
            for(AttributeModel addedAttributeModel:commonClass.getAddedAttributeMap().values()){
                if(!addedAttributeModel.isRefactored()){
                    changeList.add(new AddFieldChange(commonClass.getNextClass(),addedAttributeModel.getUmlAttribute(),revCommit));
                }
            }
        }
        return changeList;
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

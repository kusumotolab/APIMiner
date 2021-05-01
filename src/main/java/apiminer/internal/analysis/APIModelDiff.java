package apiminer.internal.analysis;

import apiminer.Change;
import apiminer.enums.Category;
import apiminer.enums.Classifier;
import apiminer.internal.analysis.category.FieldChange;
import apiminer.internal.analysis.category.MethodChange;
import apiminer.internal.analysis.category.TypeChange;
import apiminer.internal.analysis.category.field.AddFieldChange;
import apiminer.internal.analysis.category.field.RemoveFieldChange;
import apiminer.internal.analysis.category.method.AddMethodChange;
import apiminer.internal.analysis.category.method.RemoveMethodChange;
import apiminer.internal.analysis.category.type.AddTypeChange;
import apiminer.internal.analysis.category.type.RemoveTypeChange;
import apiminer.internal.analysis.diff.FieldDiff;
import apiminer.internal.analysis.diff.MethodDiff;
import apiminer.internal.analysis.diff.TypeDiff;
import apiminer.internal.analysis.model.*;
import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.UMLOperation;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringMinerTimedOutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class APIModelDiff {
    private final UMLModel parentUMLModel;
    private final UMLModel currentUMLModel;
    private final Map<String, String> renamedFilesHint;
    private final Classifier classifierAPI;
    private final RevCommit revCommit;

    private final Map<RefIdentifier, List<TypeChange>> apiClassRefactoredMap = new HashMap<>();

    private final Map<RefIdentifier, List<MethodChange>> apiOperationRefactoredMap = new HashMap<>();

    private final Map<RefIdentifier, List<FieldChange>> apiAttributeRefactoredMap = new HashMap<>();

    private final Diff diff = new Diff();

    private final List<TypeChange> changeTypeList = new ArrayList<>();
    private final List<MethodChange> changeMethodList = new ArrayList<>();
    private final List<FieldChange> changeFieldList = new ArrayList<>();

    private final Logger logger = LoggerFactory.getLogger(APIModelDiff.class);

    public APIModelDiff(UMLModel parentUMLModel, UMLModel currentUMLModel, Map<String, String> renamedFilesHint, Classifier classifierAPI, RevCommit revCommit) {
        this.parentUMLModel = parentUMLModel;
        this.currentUMLModel = currentUMLModel;
        this.renamedFilesHint = renamedFilesHint;
        this.classifierAPI = classifierAPI;
        this.revCommit = revCommit;
    }

    public List<TypeChange> getChangeTypeList() {
        return changeTypeList;
    }

    public List<MethodChange> getChangeMethodList() {
        return changeMethodList;
    }

    public List<FieldChange> getChangeFieldList() {
        return changeFieldList;
    }

    public void detectChanges() {
        try {
            if (parentUMLModel != null && currentUMLModel != null) {
                detectBothExist();
            } else if (parentUMLModel == null && currentUMLModel != null) {
                detectOnlyCurrentExist();
            } else if (parentUMLModel != null) {
                detectOnlyParentExist();
            }
        } catch (Exception e) {
            this.logger.error("Error in detectChanges in APIModelDiff ", e);
        }
    }

    private void detectBothExist() {
        initDiff();
        detectAPIRefactoring();
        detectClassChange();
        detectMethodChange();
        detectFieldChange();
    }

    private void detectOnlyCurrentExist() {
        this.logger.info("Processing Types...");
        for (UMLClass addedClass : currentUMLModel.getClassList()) {
            if (UtilTools.isAPIByClassifier(addedClass, classifierAPI) && UtilTools.isAPIClass(addedClass)) {
                TypeChange change = new AddTypeChange(addedClass, revCommit);
                changeTypeList.add(change);
            }
        }
    }

    private void detectOnlyParentExist() {
        this.logger.info("Processing Types...");
        for (UMLClass removedClass : parentUMLModel.getClassList()) {
            if (UtilTools.isAPIByClassifier(removedClass, classifierAPI) && UtilTools.isAPIClass(removedClass)) {
                TypeChange change = new RemoveTypeChange(removedClass, revCommit);
                changeTypeList.add(change);
            }
        }
    }

    private void initDiff() {
        Map<String, ModelType> mapClassParent = new HashMap<>();
        for (UMLClass parentClass : parentUMLModel.getClassList()) {
            mapClassParent.put(UtilTools.getClassName(parentClass), new ModelType(parentClass));
        }
        for (UMLClass currentClass : currentUMLModel.getClassList()) {
            ModelType parentClassModel = mapClassParent.remove(UtilTools.getClassName(currentClass));
            if (parentClassModel != null) {
                UMLClass parentClass = parentClassModel.getUmlClass();
                if (UtilTools.isClassBeforeAfterAPIByClassifier(parentClass, currentClass, classifierAPI) && (UtilTools.isAPIClass(parentClass) || UtilTools.isAPIClass(currentClass))) {
                    CommonType commonType = new CommonType(parentClass, currentClass);
                    for (UMLOperation currentOperation : currentClass.getOperations()) {
                        UMLOperation parentOperation = parentClassModel.getOperationMap().remove(UtilTools.getSignatureMethod(currentOperation));
                        if (parentOperation != null) {
                            if (UtilTools.isClassBeforeAfterAPIByClassifier(parentClass, currentClass, classifierAPI) && (UtilTools.isAPIClass(parentClass) && UtilTools.isAPIMethod(parentOperation)) || (UtilTools.isAPIClass(currentClass) && UtilTools.isAPIMethod(currentOperation))) {
                                CommonMethod commonMethod = new CommonMethod(parentOperation, currentOperation);
                                commonType.getCommonOperationMap().put(UtilTools.getSignatureMethod(parentOperation), commonMethod);
                            }
                        } else {
                            if (UtilTools.isAPIByClassifier(currentClass, classifierAPI) && UtilTools.isAPIClass(currentClass) && UtilTools.isAPIMethod(currentOperation)) {
                                MethodModel addedOperation = new MethodModel(currentOperation);
                                commonType.getAddedOperationMap().put(UtilTools.getSignatureMethod(currentOperation), addedOperation);
                            }
                        }
                    }
                    for (Map.Entry<String, UMLOperation> umlOperationEntry : parentClassModel.getOperationMap().entrySet()) {
                        UMLOperation parentOperation = umlOperationEntry.getValue();
                        if (UtilTools.isAPIByClassifier(parentClass, classifierAPI) && UtilTools.isAPIClass(parentClass) && UtilTools.isAPIMethod(parentOperation)) {
                            MethodModel removedOperation = new MethodModel(parentOperation);
                            commonType.getRemovedOperationMap().put(UtilTools.getSignatureMethod(parentOperation), removedOperation);
                        }
                    }
                    for (UMLAttribute currentAttribute : currentClass.getAttributes()) {
                        UMLAttribute parentAttribute = parentClassModel.getAttributeMap().remove(UtilTools.getAttributeName(currentAttribute));
                        if (parentAttribute != null) {
                            if (UtilTools.isClassBeforeAfterAPIByClassifier(parentClass, currentClass, classifierAPI) && (UtilTools.isAPIClass(parentClass) && UtilTools.isAPIField(parentAttribute)) || (UtilTools.isAPIClass(currentClass) && UtilTools.isAPIField(currentAttribute))) {
                                CommonField commonField = new CommonField(parentAttribute, currentAttribute);
                                commonType.getCommonAttributeMap().put(UtilTools.getAttributeName(parentAttribute), commonField);
                            }
                        } else {
                            if (UtilTools.isAPIByClassifier(currentClass, classifierAPI) && UtilTools.isAPIClass(currentClass) && UtilTools.isAPIField(currentAttribute)) {
                                FieldModel addedAttribute = new FieldModel(currentAttribute);
                                commonType.getAddedAttributeMap().put(UtilTools.getAttributeName(currentAttribute), addedAttribute);
                            }
                        }
                    }
                    for (Map.Entry<String, UMLAttribute> umlAttributeEntry : parentClassModel.getAttributeMap().entrySet()) {
                        UMLAttribute parentAttribute = umlAttributeEntry.getValue();
                        if (UtilTools.isAPIByClassifier(parentClass, classifierAPI) && UtilTools.isAPIClass(parentClass) && UtilTools.isAPIField(parentAttribute)) {
                            FieldModel removedAttribute = new FieldModel(parentAttribute);
                            commonType.getRemovedAttributeMap().put(UtilTools.getAttributeName(parentAttribute), removedAttribute);
                        }
                    }
                    diff.getCommonClassMap().put(UtilTools.getClassName(parentClass), commonType);
                }
            } else {
                if (UtilTools.isAPIByClassifier(currentClass, classifierAPI) && UtilTools.isAPIClass(currentClass)) {
                    ClassModel addedClass = new ClassModel(currentClass);
                    diff.getAddedClassMap().put(UtilTools.getClassName(currentClass), addedClass);
                }

            }
        }
        for (Map.Entry<String, ModelType> removedClassEntry : mapClassParent.entrySet()) {
            UMLClass parentClass = removedClassEntry.getValue().getUmlClass();
            if (UtilTools.isAPIByClassifier(parentClass, classifierAPI) && UtilTools.isAPIClass(parentClass
            )) {
                ClassModel removedClass = new ClassModel(parentClass);
                diff.getRemovedClassMap().put(UtilTools.getClassName(parentClass), removedClass);
            }
        }
    }

    private void detectAPIRefactoring() {
        List<Refactoring> refactorings = new ArrayList<>();
        try {
            refactorings = parentUMLModel.diff(currentUMLModel, renamedFilesHint).getRefactorings();
        } catch (RefactoringMinerTimedOutException e) {
            e.printStackTrace();
        }
        Map<String, UMLClass> parentClassMap = new HashMap<>();
        for (UMLClass parentClass : parentUMLModel.getClassList()) {
            parentClassMap.put(UtilTools.getClassName(parentClass), parentClass);
        }
        Map<String, UMLClass> currentClassMap = new HashMap<>();
        for (UMLClass currentClass : currentUMLModel.getClassList()) {
            currentClassMap.put(UtilTools.getClassName(currentClass), currentClass);
        }
        for (Refactoring refactoring : refactorings) {
            try {
                Convert convert = new Convert(refactoring, parentClassMap, currentClassMap, revCommit, classifierAPI);
                if (convert.isAPI()) {
                    RefIdentifier refIdentifier = convert.getRefIdentifier();
                    boolean isExist = false;
                    switch (refIdentifier.getRefType()) {
                        case CLASS:
                            for (Map.Entry<RefIdentifier, List<TypeChange>> entry : apiClassRefactoredMap.entrySet()) {
                                if (entry.getKey().equalIdentifier(refIdentifier)) {
                                    entry.getValue().add((TypeChange) convert.getChange());
                                    isExist = true;
                                    break;
                                }
                            }
                            if (!isExist) {
                                List<TypeChange> changeList = new ArrayList<>();
                                changeList.add((TypeChange) convert.getChange());
                                apiClassRefactoredMap.put(convert.getRefIdentifier(), changeList);
                            }
                            break;
                        case METHOD:
                            for (Map.Entry<RefIdentifier, List<MethodChange>> entry : apiOperationRefactoredMap.entrySet()) {
                                if (entry.getKey().equalIdentifier(refIdentifier)) {
                                    if (convert.getChange().getCategory().equals(Category.METHOD_CHANGE_PARAMETER_LIST)) {
                                        List<MethodChange> changeList = entry.getValue();
                                        boolean isExistParameterChange = false;
                                        for (Change change : changeList) {
                                            if (change.getCategory().equals(Category.METHOD_CHANGE_PARAMETER_LIST)) {
                                                isExistParameterChange = true;
                                                break;
                                            }
                                        }
                                        if (!isExistParameterChange) {
                                            entry.getValue().add((MethodChange) convert.getChange());
                                        }
                                    } else {
                                        entry.getValue().add((MethodChange) convert.getChange());
                                    }
                                    isExist = true;
                                    break;
                                }
                            }
                            if (!isExist) {
                                List<MethodChange> changeList = new ArrayList<>();
                                changeList.add((MethodChange) convert.getChange());
                                apiOperationRefactoredMap.put(convert.getRefIdentifier(), changeList);
                            }
                            break;
                        case FIELD:
                            for (Map.Entry<RefIdentifier, List<FieldChange>> entry : apiAttributeRefactoredMap.entrySet()) {
                                if (entry.getKey().equalIdentifier(refIdentifier)) {
                                    entry.getValue().add((FieldChange) convert.getChange());
                                    isExist = true;
                                    break;
                                }
                            }
                            if (!isExist) {
                                List<FieldChange> changeList = new ArrayList<>();
                                changeList.add((FieldChange) convert.getChange());
                                apiAttributeRefactoredMap.put(convert.getRefIdentifier(), changeList);
                            }
                            break;
                    }
                }
            } catch (Exception ignored) {

            }
        }

    }

    private void detectClassChange() {
        this.logger.info("Processing Types...");
        for (CommonType commonType : diff.getCommonClassMap().values()) {
            TypeDiff typeDiff = new TypeDiff(commonType.getOriginalClass(), commonType.getNextClass(), revCommit);
            commonType.setTypeDiff(typeDiff);
            changeTypeList.addAll(typeDiff.getChangeList());
        }
        for (Map.Entry<RefIdentifier, List<TypeChange>> entry : apiClassRefactoredMap.entrySet()) {
            RefIdentifier refIdentifier = entry.getKey();
            UMLClass originalClass = refIdentifier.getOriginalClass();
            UMLClass nextClass = refIdentifier.getNextClass();
            ClassModel removedClassModel = null;
            ClassModel addedClassModel = null;
            CommonType originalCommonType = null;
            CommonType nextCommonType = null;
            TypeDiff typeDiff = null;
            if (refIdentifier.getOriginalClass() != null) {
                originalCommonType = diff.getCommonClassMap().get(UtilTools.getClassName(originalClass));
                removedClassModel = diff.getRemovedClassMap().get(UtilTools.getClassName(originalClass));
            }
            if (refIdentifier.getNextClass() != null) {
                nextCommonType = diff.getCommonClassMap().get(UtilTools.getClassName(nextClass));
                addedClassModel = diff.getAddedClassMap().get(UtilTools.getClassName(nextClass));
            }
            if (removedClassModel != null) {
                removedClassModel.setRefactored(true);
            }
            if (addedClassModel != null) {
                addedClassModel.setRefactored(true);
            }
            switch (refIdentifier.getRefClassifier()) {
                case ADD:
                    if (nextCommonType != null) {
                        typeDiff = new TypeDiff(entry, nextCommonType, revCommit);
                    } else {
                        typeDiff = new TypeDiff(entry, null, revCommit);
                    }
                    break;
                case CHANGE:
                    if (originalCommonType == null && nextCommonType == null) {
                        typeDiff = new TypeDiff(entry, null, revCommit);
                    } else if (originalCommonType == null) {
                        if (UtilTools.isAPIClass(nextClass)) {
                            typeDiff = new TypeDiff(entry, nextCommonType, revCommit);
                        }
                    } else if (nextCommonType == null) {
                        if (UtilTools.isAPIClass(originalClass)) {
                            typeDiff = new TypeDiff(entry, null, revCommit);
                        }
                    }
                    break;
            }
            if (typeDiff != null) {
                changeTypeList.addAll(typeDiff.getChangeList());
            }
        }
        for (ClassModel removedClassModel : diff.getRemovedClassMap().values()) {
            if (!removedClassModel.getIsRefactored()) {
                TypeChange change = new RemoveTypeChange(removedClassModel.getUmlClass(), revCommit);
                changeTypeList.add(change);
            }
        }
        for (ClassModel addedClassModel : diff.getAddedClassMap().values()) {
            if (!addedClassModel.getIsRefactored()) {
                TypeChange change = new AddTypeChange(addedClassModel.getUmlClass(), revCommit);
                changeTypeList.add(change);
            }
        }
    }

    private void detectMethodChange() {
        this.logger.info("Processing Methods...");
        for (CommonType commonType : diff.getCommonClassMap().values()) {
            for (CommonMethod commonMethod : commonType.getCommonOperationMap().values()) {
                MethodDiff methodDiff = new MethodDiff(commonType.getOriginalClass(), commonMethod.getOriginalOperation(), commonType.getNextClass(), commonMethod.getNextOperation(), revCommit);
                commonMethod.setMethodDiff(methodDiff);
                changeMethodList.addAll(methodDiff.getChangeList());
            }
        }
        for (Map.Entry<RefIdentifier, List<MethodChange>> entry : apiOperationRefactoredMap.entrySet()) {
            RefIdentifier refIdentifier = entry.getKey();
            UMLClass originalClass = refIdentifier.getOriginalClass();
            UMLOperation originalOperation = refIdentifier.getOriginalOperation();
            UMLClass nextClass = refIdentifier.getNextClass();
            UMLOperation nextOperation = refIdentifier.getNextOperation();
            MethodModel removedMethodModel = null;
            MethodModel addedMethodModel = null;
            CommonMethod originalCommonMethod = null;
            CommonMethod nextCommonMethod = null;
            MethodDiff methodDiff = null;
            if (originalClass != null && originalOperation != null) {
                CommonType commonType = diff.getCommonClassMap().get(UtilTools.getClassName(originalClass));
                if (commonType != null) {
                    originalCommonMethod = commonType.getCommonOperationMap().get(UtilTools.getSignatureMethod(originalOperation));
                    removedMethodModel = commonType.getRemovedOperationMap().get(UtilTools.getSignatureMethod(originalOperation));
                }
            }
            if (nextClass != null && nextOperation != null) {
                CommonType commonType = diff.getCommonClassMap().get(UtilTools.getClassName(refIdentifier.getNextClass()));
                if (commonType != null) {
                    nextCommonMethod = commonType.getCommonOperationMap().get(UtilTools.getSignatureMethod(nextOperation));
                    addedMethodModel = commonType.getAddedOperationMap().get(UtilTools.getSignatureMethod(nextOperation));
                }
            }
            if (removedMethodModel != null) {
                removedMethodModel.setRefactored(true);
            }
            if (addedMethodModel != null) {
                addedMethodModel.setRefactored(true);
            }
            switch (refIdentifier.getRefClassifier()) {
                case ADD:
                    if (nextCommonMethod != null) {
                        methodDiff = new MethodDiff(entry, nextCommonMethod, revCommit);
                    } else {
                        methodDiff = new MethodDiff(entry, null, revCommit);
                    }
                    break;
                case REMOVE:
                    if (originalCommonMethod != null) {
                        methodDiff = new MethodDiff(entry, originalCommonMethod, revCommit);
                    } else {
                        methodDiff = new MethodDiff(entry, null, revCommit);
                    }
                    break;
                case CHANGE:
                    if (originalCommonMethod == null && nextCommonMethod == null) {
                        methodDiff = new MethodDiff(entry, null, revCommit);
                    } else if (originalCommonMethod == null) {
                        if (UtilTools.isAPIClass(nextClass) && UtilTools.isAPIMethod(nextOperation)) {
                            methodDiff = new MethodDiff(entry, nextCommonMethod, revCommit);
                        }
                    } else if (nextCommonMethod == null) {
                        if (UtilTools.isAPIClass(originalClass) && UtilTools.isAPIMethod(originalOperation)) {
                            methodDiff = new MethodDiff(entry, null, revCommit);
                        }
                    }
                    break;
            }
            if (methodDiff != null) {
                changeMethodList.addAll(methodDiff.getChangeList());
            }
        }
        for (CommonType commonType : diff.getCommonClassMap().values()) {
            for (MethodModel removedMethodModel : commonType.getRemovedOperationMap().values()) {
                if (!removedMethodModel.getIsRefactored()) {
                    MethodChange change = new RemoveMethodChange(commonType.getOriginalClass(), removedMethodModel.getUmlOperation(), revCommit);
                    changeMethodList.add(change);
                }
            }
            for (MethodModel addedMethodModel : commonType.getAddedOperationMap().values()) {
                if (!addedMethodModel.getIsRefactored()) {
                    MethodChange change = new AddMethodChange(commonType.getNextClass(), addedMethodModel.getUmlOperation(), revCommit);
                    changeMethodList.add(change);
                }
            }
        }
    }


    private void detectFieldChange() {
        this.logger.info("Processing Fields...");
        for (CommonType commonType : diff.getCommonClassMap().values()) {
            for (CommonField commonField : commonType.getCommonAttributeMap().values()) {
                FieldDiff fieldDiff = new FieldDiff(commonType.getOriginalClass(), commonField.getOriginalAttribute(), commonType.getNextClass(), commonField.getNextAttribute(), revCommit);
                commonField.setFieldDiff(fieldDiff);
                changeFieldList.addAll(fieldDiff.getChangeList());
            }
        }
        for (Map.Entry<RefIdentifier, List<FieldChange>> entry : apiAttributeRefactoredMap.entrySet()) {
            RefIdentifier refIdentifier = entry.getKey();
            UMLClass originalClass = refIdentifier.getOriginalClass();
            UMLAttribute originalAttribute = refIdentifier.getOriginalAttribute();
            UMLClass nextClass = refIdentifier.getNextClass();
            UMLAttribute nextAttribute = refIdentifier.getNextAttribute();
            FieldModel removedFieldModel = null;
            FieldModel addedFieldModel = null;
            CommonField originalCommonField = null;
            CommonField nextCommonField = null;
            FieldDiff fieldDiff = null;
            if (originalClass != null && originalAttribute != null) {
                CommonType commonType = diff.getCommonClassMap().get(UtilTools.getClassName(originalClass));
                if (commonType != null) {
                    originalCommonField = commonType.getCommonAttributeMap().get(UtilTools.getAttributeName(originalAttribute));
                    removedFieldModel = commonType.getRemovedAttributeMap().get(UtilTools.getAttributeName(originalAttribute));
                }
            }
            if (nextClass != null && nextAttribute != null) {
                CommonType commonType = diff.getCommonClassMap().get(UtilTools.getClassName(refIdentifier.getNextClass()));
                if (commonType != null) {
                    nextCommonField = commonType.getCommonAttributeMap().get(UtilTools.getAttributeName(nextAttribute));
                    addedFieldModel = commonType.getAddedAttributeMap().get(UtilTools.getAttributeName(nextAttribute));
                }
            }
            if (removedFieldModel != null) {
                removedFieldModel.setRefactored(true);
            }
            if (addedFieldModel != null) {
                addedFieldModel.setRefactored(true);
            }
            switch (refIdentifier.getRefClassifier()) {
                case ADD:
                    if (nextCommonField != null) {
                        fieldDiff = new FieldDiff(entry, nextCommonField, revCommit);
                    } else {
                        fieldDiff = new FieldDiff(entry, null, revCommit);
                    }
                    break;
                case CHANGE:
                    if (originalCommonField == null && nextCommonField == null) {
                        fieldDiff = new FieldDiff(entry, null, revCommit);
                    } else if (originalCommonField == null) {
                        if (UtilTools.isAPIClass(nextClass) && UtilTools.isAPIField(nextAttribute)) {
                            fieldDiff = new FieldDiff(entry, nextCommonField, revCommit);
                        }
                    } else if (nextCommonField == null) {
                        if (UtilTools.isAPIClass(originalClass) && UtilTools.isAPIField(originalAttribute)) {
                            fieldDiff = new FieldDiff(entry, null, revCommit);
                        }
                    }
                    break;
            }
            if (fieldDiff != null) {
                changeFieldList.addAll(fieldDiff.getChangeList());
            }
        }
        for (CommonType commonType : diff.getCommonClassMap().values()) {
            for (FieldModel removedFieldModel : commonType.getRemovedAttributeMap().values()) {
                if (!removedFieldModel.getIsRefactored()) {
                    FieldChange change = new RemoveFieldChange(commonType.getOriginalClass(), removedFieldModel.getUmlAttribute(), revCommit);
                    changeFieldList.add(change);
                }
            }
            for (FieldModel addedFieldModel : commonType.getAddedAttributeMap().values()) {
                if (!addedFieldModel.getIsRefactored()) {
                    FieldChange change = new AddFieldChange(commonType.getNextClass(), addedFieldModel.getUmlAttribute(), revCommit);
                    changeFieldList.add(change);
                }
            }
        }
    }
}

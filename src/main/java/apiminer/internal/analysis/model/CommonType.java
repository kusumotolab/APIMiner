package apiminer.internal.analysis.model;

import apiminer.internal.analysis.diff.TypeDiff;
import gr.uom.java.xmi.UMLClass;

import java.util.HashMap;
import java.util.Map;

public class CommonType {
    private UMLClass originalClass;
    private UMLClass nextClass;
    private TypeDiff typeDiff;

    private final Map<String, MethodModel> removedOperationMap = new HashMap<>();
    private final Map<String, CommonMethod> commonOperationMap = new HashMap<>();
    private final Map<String, MethodModel> addedOperationMap = new HashMap<>();

    private final Map<String, FieldModel> removedAttributeMap = new HashMap<>();
    private final Map<String, CommonField> commonAttributeMap = new HashMap<>();
    private final Map<String, FieldModel> addedAttributeMap = new HashMap<>();

    public CommonType(UMLClass originalClass, UMLClass nextClass){
        this.originalClass = originalClass;
        this.nextClass = nextClass;
    }

    public UMLClass getOriginalClass() {
        return originalClass;
    }

    public void setOriginalClass(UMLClass originalClass) {
        this.originalClass = originalClass;
    }

    public UMLClass getNextClass() {
        return nextClass;
    }

    public void setNextClass(UMLClass nextClass) {
        this.nextClass = nextClass;
    }

    public Map<String, MethodModel> getRemovedOperationMap() {
        return removedOperationMap;
    }

    public Map<String, CommonMethod> getCommonOperationMap() {
        return commonOperationMap;
    }

    public Map<String, MethodModel> getAddedOperationMap() {
        return addedOperationMap;
    }

    public Map<String, FieldModel> getRemovedAttributeMap() {
        return removedAttributeMap;
    }

    public Map<String, CommonField> getCommonAttributeMap() {
        return commonAttributeMap;
    }

    public Map<String, FieldModel> getAddedAttributeMap() {
        return addedAttributeMap;
    }

    public TypeDiff getTypeDiff() {
        return typeDiff;
    }

    public void setTypeDiff(TypeDiff typeDiff) {
        this.typeDiff = typeDiff;
    }
}

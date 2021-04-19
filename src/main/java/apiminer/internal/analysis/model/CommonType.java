package apiminer.internal.analysis.model;

import gr.uom.java.xmi.UMLClass;

import java.util.HashMap;
import java.util.Map;

public class CommonType {
    private UMLClass originalClass;
    private UMLClass nextClass;

    private Map<String, MethodModel> removedOperationMap = new HashMap<String, MethodModel>();
    private Map<String, CommonMethod> commonOperationMap = new HashMap<String, CommonMethod>();
    private Map<String, MethodModel> addedOperationMap = new HashMap<String, MethodModel>();

    private Map<String, FieldModel> removedAttributeMap = new HashMap<String, FieldModel>();
    private Map<String, CommonField> commonAttributeMap = new HashMap<String, CommonField>();
    private Map<String, FieldModel> addedAttributeMap = new HashMap<String, FieldModel>();

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

    public void setRemovedOperationMap(Map<String, MethodModel> removedOperationMap) {
        this.removedOperationMap = removedOperationMap;
    }

    public Map<String, CommonMethod> getCommonOperationMap() {
        return commonOperationMap;
    }

    public void setCommonOperationMap(Map<String, CommonMethod> commonOperationMap) {
        this.commonOperationMap = commonOperationMap;
    }

    public Map<String, MethodModel> getAddedOperationMap() {
        return addedOperationMap;
    }

    public void setAddedOperationMap(Map<String, MethodModel> addedOperationMap) {
        this.addedOperationMap = addedOperationMap;
    }

    public Map<String, FieldModel> getRemovedAttributeMap() {
        return removedAttributeMap;
    }

    public void setRemovedAttributeMap(Map<String, FieldModel> removedAttributeMap) {
        this.removedAttributeMap = removedAttributeMap;
    }

    public Map<String, CommonField> getCommonAttributeMap() {
        return commonAttributeMap;
    }

    public void setCommonAttributeMap(Map<String, CommonField> commonAttributeMap) {
        this.commonAttributeMap = commonAttributeMap;
    }

    public Map<String, FieldModel> getAddedAttributeMap() {
        return addedAttributeMap;
    }

    public void setAddedAttributeMap(Map<String, FieldModel> addedAttributeMap) {
        this.addedAttributeMap = addedAttributeMap;
    }
}

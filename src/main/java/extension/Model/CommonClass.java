package extension.Model;

import gr.uom.java.xmi.UMLClass;

import java.util.HashMap;
import java.util.Map;

public class CommonClass {
    private UMLClass originalClass;
    private UMLClass nextClass;

    private Map<String, OperationModel> removedOperationMap = new HashMap<String, OperationModel>();
    private Map<String,CommonOperation> commonOperationMap = new HashMap<String,CommonOperation>();
    private Map<String, OperationModel> addedOperationMap = new HashMap<String, OperationModel>();

    private Map<String,AttributeModel> removedAttributeMap = new HashMap<String,AttributeModel>();
    private Map<String,CommonAttribute> commonAttributeMap = new HashMap<String, CommonAttribute>();
    private Map<String,AttributeModel> addedAttributeMap = new HashMap<String,AttributeModel>();

    public CommonClass(UMLClass originalClass,UMLClass nextClass){
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

    public Map<String, OperationModel> getRemovedOperationMap() {
        return removedOperationMap;
    }

    public void setRemovedOperationMap(Map<String, OperationModel> removedOperationMap) {
        this.removedOperationMap = removedOperationMap;
    }

    public Map<String, CommonOperation> getCommonOperationMap() {
        return commonOperationMap;
    }

    public void setCommonOperationMap(Map<String, CommonOperation> commonOperationMap) {
        this.commonOperationMap = commonOperationMap;
    }

    public Map<String, OperationModel> getAddedOperationMap() {
        return addedOperationMap;
    }

    public void setAddedOperationMap(Map<String, OperationModel> addedOperationMap) {
        this.addedOperationMap = addedOperationMap;
    }

    public Map<String, AttributeModel> getRemovedAttributeMap() {
        return removedAttributeMap;
    }

    public void setRemovedAttributeMap(Map<String, AttributeModel> removedAttributeMap) {
        this.removedAttributeMap = removedAttributeMap;
    }

    public Map<String, CommonAttribute> getCommonAttributeMap() {
        return commonAttributeMap;
    }

    public void setCommonAttributeMap(Map<String, CommonAttribute> commonAttributeMap) {
        this.commonAttributeMap = commonAttributeMap;
    }

    public Map<String, AttributeModel> getAddedAttributeMap() {
        return addedAttributeMap;
    }

    public void setAddedAttributeMap(Map<String, AttributeModel> addedAttributeMap) {
        this.addedAttributeMap = addedAttributeMap;
    }
}

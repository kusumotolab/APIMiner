package extension;

import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLOperation;

import java.util.HashMap;
import java.util.Map;

public class CommonClass {
    private UMLClass parentClass;
    private UMLClass currentClass;
    private Map<String, UMLOperation> removedOperation = new HashMap<String,UMLOperation>();
    private Map<String,Map<UMLOperation,UMLOperation>> commonOperation = new HashMap<String, Map<UMLOperation, UMLOperation>>();
    private Map<String, UMLOperation> addedOperation = new HashMap<String,UMLOperation>();
    private Map<String, UMLAttribute> removedAttribute = new HashMap<String,UMLAttribute>();
    private Map<String,Map<UMLAttribute,UMLAttribute>> commonAttribute = new HashMap<String, Map<UMLAttribute, UMLAttribute>>();
    private Map<String, UMLAttribute> addedAttribute = new HashMap<String,UMLAttribute>();

    public CommonClass(UMLClass parentClass,UMLClass currentClass){
        this.parentClass = parentClass;
        this.currentClass = currentClass;
    }

    public UMLClass getParentClass() {
        return parentClass;
    }

    public UMLClass getCurrentClass() {
        return currentClass;
    }

    public Map<String, UMLOperation> getRemovedOperation() {
        return removedOperation;
    }

    public Map<String, Map<UMLOperation, UMLOperation>> getCommonOperation() {
        return commonOperation;
    }

    public Map<String, UMLOperation> getAddedOperation() {
        return addedOperation;
    }

    public Map<String, UMLAttribute> getRemovedAttribute() {
        return removedAttribute;
    }

    public Map<String, Map<UMLAttribute, UMLAttribute>> getCommonAttribute() {
        return commonAttribute;
    }

    public Map<String, UMLAttribute> getAddedAttribute() {
        return addedAttribute;
    }
}

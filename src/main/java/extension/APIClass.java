package extension;

import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLOperation;

import java.util.ArrayList;
import java.util.List;

public class APIClass {
    private UMLClass parentClass;
    private UMLClass currentClass;

    private List<APIOperation> removedOperationList = new ArrayList<APIOperation>();
    private List<APIOperation> commonOperationList = new ArrayList<APIOperation>();
    private List<APIOperation> addedOperationList = new ArrayList<APIOperation>();

    private  List<APIAttribute> removedAttributeList = new ArrayList<APIAttribute>();
    private  List<APIAttribute> commonAttributeList = new ArrayList<APIAttribute>();
    private List<APIAttribute> addedAttributeList = new ArrayList<APIAttribute>();

    public APIClass(UMLClass parentClass, UMLClass currentClass){
        this.parentClass = parentClass;
        this.currentClass = currentClass;
    }

    public UMLClass getParentClass() {
        return parentClass;
    }

    public UMLClass getCurrentClass() {
        return currentClass;
    }

    public List<APIOperation> getRemovedOperationList() {
        return removedOperationList;
    }

    public List<APIOperation> getCommonOperationList() {
        return commonOperationList;
    }

    public List<APIOperation> getAddedOperationList() {
        return addedOperationList;
    }

    public List<APIAttribute> getRemovedAttributeList() {
        return removedAttributeList;
    }

    public List<APIAttribute> getCommonAttributeList() {
        return commonAttributeList;
    }

    public List<APIAttribute> getAddedAttributeList() {
        return addedAttributeList;
    }
}

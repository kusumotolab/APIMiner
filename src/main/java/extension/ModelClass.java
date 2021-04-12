package extension;

import apiminer.internal.util.NewUtilTools;
import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLOperation;

import java.util.HashMap;
import java.util.Map;

public class ModelClass {
    private UMLClass umlClass;
    private Map<String , UMLOperation> operationMap = new HashMap<String,UMLOperation>();
    private Map<String, UMLAttribute> attributeMap = new HashMap<String,UMLAttribute>();

    public ModelClass(UMLClass umlClass){
        this.umlClass = umlClass;
        for(UMLOperation umlOperation:umlClass.getOperations()){
            operationMap.put(NewUtilTools.getSignatureMethod(umlOperation),umlOperation);
        }
        for(UMLAttribute umlAttribute: umlClass.getAttributes()){
            attributeMap.put(umlAttribute.toString(),umlAttribute);
        }
    }

    public UMLClass getUmlClass() {
        return umlClass;
    }

    public Map<String, UMLOperation> getOperationMap() {
        return operationMap;
    }

    public Map<String, UMLAttribute> getAttributeMap() {
        return attributeMap;
    }
}

package apiminer.internal.analysis.model;

import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLOperation;

import java.util.HashMap;
import java.util.Map;

public class ModelType {
    private final UMLClass umlClass;
    private final Map<String, UMLOperation> operationMap = new HashMap<>();
    private final Map<String, UMLAttribute> attributeMap = new HashMap<>();

    public ModelType(UMLClass umlClass) {
        this.umlClass = umlClass;
        for (UMLOperation umlOperation : umlClass.getOperations()) {
            operationMap.put(UtilTools.getSignatureMethod(umlOperation), umlOperation);
        }
        for (UMLAttribute umlAttribute : umlClass.getAttributes()) {
            attributeMap.put(UtilTools.getAttributeName(umlAttribute), umlAttribute);
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

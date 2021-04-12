package extension;

import apiminer.enums.Classifier;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassDiff {
    private UMLClass originalClass;
    private UMLClass nextClass;

    private List<UMLOperation> addedOperations = new ArrayList<UMLOperation>();
    private Map<UMLOperation,UMLOperation> commonOperationsMap = new HashMap<UMLOperation,UMLOperation>();
    private List<UMLOperation> removedOperations = new ArrayList<UMLOperation>();

    private Map<String,UMLOperation> parentOperation = new HashMap<String ,UMLOperation>();
    private Map<String, UMLAttribute> parentAttribute = new HashMap<String,UMLAttribute>();

    public ClassDiff(UMLClass originalClass,UMLClass nextClass){
        this.originalClass = originalClass;
        this.nextClass = nextClass;
    }

    public Map<String, UMLOperation> getParentOperation() {
        return parentOperation;
    }

    public Map<String, UMLAttribute> getParentAttribute() {
        return parentAttribute;
    }
}

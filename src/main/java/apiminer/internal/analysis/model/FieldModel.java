package apiminer.internal.analysis.model;

import gr.uom.java.xmi.UMLAttribute;

public class FieldModel {
    private UMLAttribute umlAttribute;
    private boolean isRefactored = false;

    public FieldModel(UMLAttribute umlAttribute){
        this.umlAttribute = umlAttribute;
    }

    public UMLAttribute getUmlAttribute() {
        return umlAttribute;
    }

    public boolean isRefactored() {
        return isRefactored;
    }

    public void setRefactored(boolean refactored) {
        isRefactored = refactored;
    }
}

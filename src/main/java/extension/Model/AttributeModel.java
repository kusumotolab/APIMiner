package extension.Model;

import gr.uom.java.xmi.UMLAttribute;

public class AttributeModel {
    private UMLAttribute umlAttribute;
    private boolean isRefactored = false;

    public AttributeModel(UMLAttribute umlAttribute){
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

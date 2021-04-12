package extension;

import gr.uom.java.xmi.UMLAttribute;

public class APIAttribute {
    private UMLAttribute parentAttribute;
    private UMLAttribute currentAttribute;

    public APIAttribute(UMLAttribute parentAttribute, UMLAttribute currentAttribute) {
        this.parentAttribute = parentAttribute;
        this.currentAttribute = currentAttribute;
    }

    public UMLAttribute getParentAttribute() {
        return parentAttribute;
    }

    public UMLAttribute getCurrentAttribute() {
        return currentAttribute;
    }
}


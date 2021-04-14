package extension.Model;

import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;

public class CommonAttribute {
    private UMLAttribute originalAttribute;
    private UMLAttribute nextAttribute;

    public CommonAttribute(UMLAttribute originalAttribute,UMLAttribute nextAttribute){
        this.originalAttribute = originalAttribute;
        this.nextAttribute = nextAttribute;
    }

    public UMLAttribute getOriginalAttribute() {
        return originalAttribute;
    }

    public UMLAttribute getNextAttribute() {
        return nextAttribute;
    }

}

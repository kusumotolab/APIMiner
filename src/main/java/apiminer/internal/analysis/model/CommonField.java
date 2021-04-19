package apiminer.internal.analysis.model;

import gr.uom.java.xmi.UMLAttribute;

public class CommonField {
    private final UMLAttribute originalAttribute;
    private final UMLAttribute nextAttribute;

    public CommonField(UMLAttribute originalAttribute, UMLAttribute nextAttribute){
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

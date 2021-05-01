package apiminer.internal.analysis.model;

import apiminer.internal.analysis.diff.FieldDiff;
import gr.uom.java.xmi.UMLAttribute;

public class CommonField {
    private final UMLAttribute originalAttribute;
    private final UMLAttribute nextAttribute;
    private FieldDiff fieldDiff;

    public CommonField(UMLAttribute originalAttribute, UMLAttribute nextAttribute) {
        this.originalAttribute = originalAttribute;
        this.nextAttribute = nextAttribute;
    }

    public UMLAttribute getOriginalAttribute() {
        return originalAttribute;
    }

    public UMLAttribute getNextAttribute() {
        return nextAttribute;
    }

    public FieldDiff getFieldDiff() {
        return fieldDiff;
    }

    public void setFieldDiff(FieldDiff fieldDiff) {
        this.fieldDiff = fieldDiff;
    }
}

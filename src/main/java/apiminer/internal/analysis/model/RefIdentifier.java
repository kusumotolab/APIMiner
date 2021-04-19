package apiminer.internal.analysis.model;

import apiminer.enums.ChangeType;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLOperation;

public class RefIdentifier {
    private ChangeType changeType;
    private UMLClass originalClass;
    private UMLOperation originalOperation;
    private UMLClass nextClass;
    private UMLOperation nextOperation;
    private UMLAttribute originalAttribute;
    private UMLAttribute nextAttribute;

    public ChangeType getRefType() {
        return changeType;
    }

    public void setRefType(ChangeType changeType) {
        this.changeType = changeType;
    }

    public UMLClass getOriginalClass() {
        return originalClass;
    }

    public void setOriginalClass(UMLClass originalClass) {
        this.originalClass = originalClass;
    }

    public UMLOperation getOriginalOperation() {
        return originalOperation;
    }

    public void setOriginalOperation(UMLOperation originalOperation) {
        this.originalOperation = originalOperation;
    }

    public UMLClass getNextClass() {
        return nextClass;
    }

    public void setNextClass(UMLClass nextClass) {
        this.nextClass = nextClass;
    }

    public UMLOperation getNextOperation() {
        return nextOperation;
    }

    public void setNextOperation(UMLOperation nextOperation) {
        this.nextOperation = nextOperation;
    }

    public UMLAttribute getOriginalAttribute() {
        return originalAttribute;
    }

    public void setOriginalAttribute(UMLAttribute originalAttribute) {
        this.originalAttribute = originalAttribute;
    }

    public UMLAttribute getNextAttribute() {
        return nextAttribute;
    }

    public void setNextAttribute(UMLAttribute nextAttribute) {
        this.nextAttribute = nextAttribute;
    }

    public boolean equalIdentifier(RefIdentifier refIdentifier) {
        boolean isEqualOriginal = false;
        boolean isEqualNext = false;
        if (changeType.equals(refIdentifier.getRefType())) {
            switch (this.changeType) {
                case CLASS:
                    if (this.originalClass != null) {
                        isEqualOriginal = this.originalClass.equals(refIdentifier.getOriginalClass());
                    } else {
                        isEqualOriginal = refIdentifier.getOriginalClass() == null;
                    }
                    if (this.nextClass != null) {
                        isEqualNext = this.nextClass.equals(refIdentifier.getNextClass());
                    } else {
                        isEqualNext = refIdentifier.getNextClass() == null;
                    }
                    break;
                case METHOD:
                    if (this.originalOperation != null) {
                        isEqualOriginal = this.originalOperation.equals(refIdentifier.getOriginalOperation());
                    } else {
                        isEqualOriginal = refIdentifier.getOriginalOperation() == null;
                    }
                    if (this.nextOperation != null) {
                        isEqualNext = this.nextOperation.equals(refIdentifier.getNextOperation());
                    } else {
                        isEqualNext = refIdentifier.getNextOperation() == null;
                    }
                    break;
                case FIELD:
                    if (this.originalAttribute != null) {
                        isEqualOriginal = this.originalAttribute.equals(refIdentifier.getOriginalAttribute());
                    } else {
                        isEqualOriginal = refIdentifier.getOriginalAttribute() == null;
                    }
                    if (this.nextAttribute != null) {
                        isEqualNext = this.nextAttribute.equals(refIdentifier.getNextAttribute());
                    } else {
                        isEqualNext = refIdentifier.getNextAttribute() == null;
                    }
                    break;
            }
        }
        return isEqualOriginal && isEqualNext;
    }
}

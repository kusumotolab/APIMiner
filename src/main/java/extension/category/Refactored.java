package extension.category;

import apiminer.enums.Category;
import apiminer.enums.RefType;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLOperation;

public class Refactored {
    private RefType refType;
    private UMLClass originalClass;
    private UMLOperation originalOperation;
    private UMLClass nextClass;
    private UMLOperation nextOperation;
    private UMLAttribute originalAttribute;
    private UMLAttribute nextAttribute;

    public RefType getRefType() {
        return refType;
    }

    public void setRefType(RefType refType) {
        this.refType = refType;
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

    public boolean equalRefactored(Refactored refactored){
        if(refType.equals(refactored.getRefType())){
            switch (this.refType){
                case CLASS:
                    boolean isEqualOriginalClass;
                    if(this.originalClass!=null){
                        if(this.originalClass.equals(refactored.getOriginalClass())){
                            isEqualOriginalClass = true;
                        }else{
                            isEqualOriginalClass =false;
                        }
                    }else{
                        if(refactored.getOriginalClass()==null){
                            isEqualOriginalClass = true;
                        }else{
                            isEqualOriginalClass = false;
                        }
                    }
                    boolean isEqualNextClass;
                    if(this.nextClass!=null){
                        if(this.nextClass.equals(refactored.getNextClass())){
                            isEqualNextClass = true;
                        }else{
                            isEqualNextClass =false;
                        }
                    }else{
                        if(refactored.getNextClass()==null){
                            isEqualNextClass = true;
                        }else{
                            isEqualNextClass = false;
                        }
                    }
                    return isEqualOriginalClass&&isEqualNextClass;
                case METHOD:

                case ATTRIBUTE:
            }
        }
        return false;
    }
}

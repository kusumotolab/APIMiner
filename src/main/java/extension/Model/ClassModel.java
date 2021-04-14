package extension.Model;

import gr.uom.java.xmi.UMLClass;

public class ClassModel {
    private UMLClass umlClass;
    private boolean isRefactored = false;

    public ClassModel(UMLClass umlClass){
        this.umlClass = umlClass;
    }

    public UMLClass getUmlClass() {
        return umlClass;
    }

    public boolean isRefactored() {
        return isRefactored;
    }

    public void setRefactored(boolean refactored) {
        isRefactored = refactored;
    }
}

package extension;

import gr.uom.java.xmi.UMLClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Diff {
    private Map<String,UMLClass> removedClassList = new HashMap<String,UMLClass>();
    private Map<String,CommonClass> commonClassList = new HashMap<String, CommonClass>();
    private Map<String,UMLClass> addedClassList = new HashMap<String,UMLClass>();

    public Map<String, UMLClass> getRemovedClassList() {
        return removedClassList;
    }

    public Map<String, CommonClass> getCommonClassList() {
        return commonClassList;
    }

    public Map<String, UMLClass> getAddedClassList() {
        return addedClassList;
    }
}


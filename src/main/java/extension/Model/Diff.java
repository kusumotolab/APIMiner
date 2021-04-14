package extension.Model;

import gr.uom.java.xmi.UMLClass;

import java.util.HashMap;
import java.util.Map;

public class Diff {
    private Map<String, ClassModel> removedClassMap = new HashMap<String,ClassModel>();
    private Map<String, CommonClass> commonClassMap = new HashMap<String, CommonClass>();
    private Map<String,ClassModel> addedClassMap = new HashMap<String,ClassModel>();

    public Map<String, ClassModel> getRemovedClassMap() {
        return removedClassMap;
    }

    public Map<String, CommonClass> getCommonClassMap() {
        return commonClassMap;
    }

    public Map<String, ClassModel> getAddedClassMap() {
        return addedClassMap;
    }
}


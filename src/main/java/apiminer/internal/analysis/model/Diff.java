package apiminer.internal.analysis.model;

import java.util.HashMap;
import java.util.Map;

public class Diff {
    private Map<String, ClassModel> removedClassMap = new HashMap<String,ClassModel>();
    private Map<String, CommonType> commonClassMap = new HashMap<String, CommonType>();
    private Map<String,ClassModel> addedClassMap = new HashMap<String,ClassModel>();

    public Map<String, ClassModel> getRemovedClassMap() {
        return removedClassMap;
    }

    public Map<String, CommonType> getCommonClassMap() {
        return commonClassMap;
    }

    public Map<String, ClassModel> getAddedClassMap() {
        return addedClassMap;
    }
}


package apiminer.internal.analysis.model;

import java.util.HashMap;
import java.util.Map;

public class Diff {
    private final Map<String, ClassModel> removedClassMap = new HashMap<>();
    private final Map<String, CommonType> commonClassMap = new HashMap<>();
    private final Map<String,ClassModel> addedClassMap = new HashMap<>();

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


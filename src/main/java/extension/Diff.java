package extension;

import gr.uom.java.xmi.UMLClass;

import java.util.ArrayList;
import java.util.List;

public class Diff {
    private List<APIClass> removedClassList = new ArrayList<APIClass>();
    private List<APIClass> commonClassList = new ArrayList<APIClass>();
    private List<APIClass> addedClassList = new ArrayList<APIClass>();

    public List<APIClass> getRemovedClassList() {
        return removedClassList;
    }

    public List<APIClass> getCommonClassList() {
        return commonClassList;
    }

    public List<APIClass> getAddedClassList() {
        return addedClassList;
    }
}


package extension;

import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.UMLOperation;

import java.util.HashMap;
import java.util.Map;

public class Model {
    private UMLModel umlModel;
    private Map<UMLClass,ModelClass> mapClass =  new HashMap<UMLClass,ModelClass>();

    public Model(UMLModel umlModel){
        this.umlModel = umlModel;
        for(UMLClass umlClass:this.umlModel.getClassList()){
            mapClass.put(umlClass,new ModelClass(umlClass));
        }
    }
    public void setIsRefactoredClass(boolean b,UMLClass umlClass){
        if(umlClass!=null){
            ModelClass modelClass = mapClass.get(umlClass);
            modelClass.setIsRefactored(b);
        }
    }
    public void setIsRefactoredOperation(boolean b,UMLClass umlClass,UMLOperation umlOperation){
        if(umlClass!=null&&umlOperation!=null){
            ModelClass modelClass = mapClass.get(umlClass);
            ModelOperation modelOperation = modelClass.mapOperation.get(umlOperation);
            modelOperation.setIsRefactored(b);
        }
    }
    public void setIsRefactoredAttribute(boolean b,UMLClass umlClass,UMLAttribute umlAttribute){
        if(umlClass!=null&&umlAttribute!=null){
            ModelClass modelClass = mapClass.get(umlClass);
            ModelAttribute modelAttribute = modelClass.mapAttribute.get(umlAttribute);
            modelAttribute.setIsRefactored(b);
        }
    }
    public UMLClass getUMLClass(String className){
        for(UMLClass umlClass:mapClass.keySet()){
            if(umlClass.getName().equals(className)){
                return umlClass;
            }
        }
        return null;
    }

    private class ModelClass{
        private UMLClass umlClass;
        private boolean isAPI;
        private boolean isRefactored;
        Map<UMLOperation,ModelOperation> mapOperation = new HashMap<UMLOperation,ModelOperation>();;
        Map<UMLAttribute,ModelAttribute> mapAttribute = new HashMap<UMLAttribute,ModelAttribute>();;

        private ModelClass(UMLClass umlClass){
            this.umlClass = umlClass;
            if(this.umlClass.getVisibility().equals("public")||this.umlClass.getVisibility().equals("protected")){
                this.isAPI = true;;
            }else {
                this.isAPI = false;
            }
            this.isRefactored = false;
            for(UMLOperation umlOperation:this.umlClass.getOperations()){
                mapOperation.put(umlOperation,new ModelOperation(umlOperation));
            }
            for(UMLAttribute umlAttribute:this.umlClass.getAttributes()){
                mapAttribute.put(umlAttribute,new ModelAttribute(umlAttribute));
            }
        }
        private void setIsRefactored(boolean b){
            isRefactored = b;
        }
    }
    private class ModelOperation{
        private UMLOperation umlOperation;
        private boolean isAPI;
        private boolean isRefactored;
        private ModelOperation(UMLOperation umlOperation){
            this.umlOperation = umlOperation;
            if(this.umlOperation.getVisibility().equals("public")||this.umlOperation.getVisibility().equals("protected")){
                this.isAPI = true;;
            }else {
                this.isAPI = false;
            }
            this.isRefactored = false;
        }
        private void setIsRefactored(boolean b){
            isRefactored = b;
        }
    }
    private class ModelAttribute{
        private UMLAttribute umlAttribute;
        private boolean isAPI;
        private boolean isRefactored;
        private ModelAttribute(UMLAttribute umlAttribute){
            this.umlAttribute = umlAttribute;
            if(this.umlAttribute.getVisibility().equals("public")||this.umlAttribute.getVisibility().equals("protected")){
                this.isAPI = true;;
            }else {
                this.isAPI = false;
            }
            this.isRefactored = false;
        }
        private void setIsRefactored(boolean b){
            isRefactored = b;
        }
    }
}

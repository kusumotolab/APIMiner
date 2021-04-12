package extension;

import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLModel;
import org.refactoringminer.api.Refactoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class APIRefactoring {
    private List<RefactoringElement> apiClassRefactorings = new ArrayList<RefactoringElement>();
    private List<RefactoringElement> apiOperationRefactorings = new ArrayList<RefactoringElement>();
    private List<RefactoringElement> apiAttributeRefactorings = new ArrayList<RefactoringElement>();

    private List<Refactoring> refactorings = new ArrayList<Refactoring>();
    private UMLModel parentModel;
    private UMLModel currentModel;
    private Map<String, UMLClass> mapParentClass = new HashMap<String,UMLClass>();
    private Map<String,UMLClass> mapCurrentClass = new HashMap<String,UMLClass>();

    public List<RefactoringElement> getApiClassRefactorings() {
        return apiClassRefactorings;
    }

    public List<RefactoringElement> getApiOperationRefactorings() {
        return apiOperationRefactorings;
    }

    public List<RefactoringElement> getApiAttributeRefactorings() {
        return apiAttributeRefactorings;
    }

    public APIRefactoring(List<Refactoring> refactorings, UMLModel parentModel, UMLModel currentModel){
        this.refactorings = refactorings;
        this.parentModel = parentModel;
        this.currentModel = currentModel;
        for(UMLClass parentClass:parentModel.getClassList()){
            mapParentClass.put(parentClass.toString(),parentClass);
        }
        for(UMLClass currentClass:currentModel.getClassList()){
            mapCurrentClass.put(currentClass.toString(),currentClass);
        }
        filterAPIRefactoring();
    }


    private void filterAPIRefactoring(){
        for(Refactoring refactoring:refactorings){
            RefactoringElement refactoringElement = new RefactoringElement(refactoring,mapParentClass,mapCurrentClass);
            if(refactoringElement.isAPI()){
                switch (refactoringElement.getRefType()){
                    case CLASS:
                        apiClassRefactorings.add(refactoringElement);
                        break;
                    case METHOD:
                        apiOperationRefactorings.add(refactoringElement);
                        break;
                    case ATTRIBUTE:
                        apiAttributeRefactorings.add(refactoringElement);
                        break;
                }
            }
        }

    }
}

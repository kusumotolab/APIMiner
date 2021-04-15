package extension;

import extension.Diff.AttributeDiff;
import extension.Diff.ClassDiff;
import extension.Diff.OperationDiff;
import extension.category.AttributeRefactored;
import extension.category.ClassRefactored;
import extension.category.OperationRefactored;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLModel;
import org.refactoringminer.api.Refactoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class APIRefactoring {
    private List<Refactoring> refactorings = new ArrayList<Refactoring>();
    private UMLModel parentModel;
    private UMLModel currentModel;
    private Map<String, UMLClass> mapParentClass = new HashMap<String,UMLClass>();
    private Map<String,UMLClass> mapCurrentClass = new HashMap<String,UMLClass>();

    private List<ClassRefactored> refactoringClassList = new ArrayList<>();
    private List<OperationRefactored> refactoringMethodList = new ArrayList<>();
    private List<AttributeRefactored> refactoringFieldList = new ArrayList<>();

    public List<ClassRefactored> getRefactoringClassList() {
        return refactoringClassList;
    }

    public List<OperationRefactored> getRefactoringMethodList() {
        return refactoringMethodList;
    }

    public List<AttributeRefactored> getRefactoringFieldList() {
        return refactoringFieldList;
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

                        //apiClassRefactorings.add(refactoringElement);
                        break;
                    case METHOD:
                        //apiOperationRefactorings.add(refactoringElement);
                        break;
                    case ATTRIBUTE:
                        //apiAttributeRefactorings.add(refactoringElement);
                        break;
                }
            }
        }

    }
}

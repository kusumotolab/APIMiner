package apiminer.internal.analysis.description;

import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.diff.UMLModelDiff;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringMinerTimedOutException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UMLModelDiffExtend extends UMLModelDiff {
    private UMLModel parentUMLModel;
    private UMLModel currentUMLModel;
    private Map<String, String> renamedFilesHint;
    private UMLModelDiff umlModelDiff;

    public UMLModelDiffExtend(UMLModel parentUMLModel, UMLModel currentUMLModel, Map<String, String> renamedFilesHint){
        this.parentUMLModel=parentUMLModel;
        this.currentUMLModel=currentUMLModel;
        this.renamedFilesHint = renamedFilesHint;
        try {
            this.umlModelDiff = parentUMLModel.diff(currentUMLModel,renamedFilesHint);
        } catch (RefactoringMinerTimedOutException e) {
            e.printStackTrace();
        }
    }
    public void getChanges(){
        try {
            List<Refactoring> refactorings = this.filter(umlModelDiff.getRefactorings());

        } catch (RefactoringMinerTimedOutException e) {
            e.printStackTrace();
        }
    }
    protected List<Refactoring> filter(List<Refactoring> refactoringsAtRevision) {
        if (this.refactoringTypesToConsider == null) {
            return refactoringsAtRevision;
        } else {
            List<Refactoring> filteredList = new ArrayList();
            Iterator var3 = refactoringsAtRevision.iterator();

            while(var3.hasNext()) {
                Refactoring ref = (Refactoring)var3.next();
                if (this.refactoringTypesToConsider.contains(ref.getRefactoringType())) {
                    filteredList.add(ref);
                }
            }

            return filteredList;
        }
    }


}


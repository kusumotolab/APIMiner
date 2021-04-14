package extension;

import apiminer.enums.Category;
import gr.uom.java.xmi.UMLClass;

import java.security.cert.Extension;
import java.util.ArrayList;
import java.util.List;

public class ClassChange {
    private RefactoringElement refactoringElement;
    private Category category;
    private boolean isBreakingChange;
    private UMLClass removedClass;
    private UMLClass addedClass;
    private List<extension.Change.Change> changeList = new ArrayList<>();

    public ClassChange(RefactoringElement refactoringElement){
        this.refactoringElement = refactoringElement;
        this.changeList.addAll(detectChanges());
    }
    public ClassChange(UMLClass umlClass,Category category){
        this.category = category;
        if(category.equals(Category.TYPE_ADD)){
            this.removedClass = null;
            this.addedClass = umlClass;
        }else if(category.equals(Category.TYPE_REMOVE)){
            this.removedClass = umlClass;
            this.addedClass = null;
        }
        this.changeList.addAll(changeList);
    }
    private List<extension.Change.Change> detectChanges(){
        List<extension.Change.Change> changeList = new ArrayList<extension.Change.Change>();
        if(refactoringElement==null){
            if(category.equals(Category.TYPE_REMOVE)){

            }else if(category.equals(Category.TYPE_ADD)){

            }
        }else{

        }

        return changeList;
    }

    private void addChange(boolean isBreakingChange){

    }

    public List<extension.Change.Change> getChangeList(){
        return changeList;
    }
}

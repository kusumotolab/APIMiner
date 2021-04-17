package extension.Diff;

import apiminer.enums.Category;
import apiminer.util.category.type.*;
import apiminer.util.category.ClassChange;
import extension.Model.CommonClass;
import extension.RefactoringElement;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLOperation;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.List;

public class ClassDiff {
    private UMLClass originalClass;
    private UMLClass nextClass;
    private List<ClassChange> classChangeList = new ArrayList<ClassChange>();
    private  RevCommit revCommit;

    public ClassDiff(Category category, UMLClass umlClass, RevCommit revCommit){
        this.revCommit = revCommit;

        if(category.equals(Category.TYPE_REMOVE)){
            this.originalClass = umlClass;
            this.nextClass = null;
            classChangeList.add(new RemoveTypeChange(originalClass,revCommit));
        }else if(category.equals(Category.TYPE_ADD)) {
            this.originalClass = null;
            this.nextClass = umlClass;
            classChangeList.add(new AddTypeChange(nextClass, revCommit));
        }
    }
    public ClassDiff(CommonClass commonClass,RevCommit revCommit){
        this.revCommit = revCommit;
        classChangeList.addAll(detectOtherChange(commonClass.getOriginalClass(),commonClass.getNextClass()));
    }

    public ClassDiff(RefactoringElement refactoringElement,RevCommit revCommit){
        this.revCommit = revCommit;
        switch (refactoringElement.getRefactoring().getRefactoringType()){
            case EXTRACT_SUPERCLASS:
            case EXTRACT_INTERFACE:
                //classChangeList.add(new ExtractSuperTypeChange(refactoringElement,revCommit));
                break;
            case MOVE_CLASS:
                //classChangeList.add(new MoveTypeChange(refactoringElement,revCommit));
                classChangeList.addAll(detectOtherChange(refactoringElement.getOriginalClass(),refactoringElement.getNextClass()));
                break;
            case RENAME_CLASS:
                //classChangeList.add(new RenameTypeChange(refactoringElement,revCommit));
                classChangeList.addAll(detectOtherChange(refactoringElement.getOriginalClass(),refactoringElement.getNextClass()));
                break;
            case MOVE_RENAME_CLASS:
                //classChangeList.add(new MoveAndRenameTypeChange(refactoringElement,revCommit));
                classChangeList.addAll(detectOtherChange(refactoringElement.getOriginalClass(),refactoringElement.getNextClass()));
                break;
            case EXTRACT_CLASS:
                //classChangeList.add(new ExtractTypeChange(refactoringElement,revCommit));
                break;
            case EXTRACT_SUBCLASS:
                //classChangeList.add(new ExtractSubTypeChange(refactoringElement,revCommit));
                break;
            default:
        }
    }
    private List<ClassChange> detectOtherChange(UMLClass originalClass,UMLClass nextClass){
        List<ClassChange> classChangeList = new ArrayList<ClassChange>();
        boolean isBreakingChange = false;

        return classChangeList;
    }

    public List<ClassChange> getClassChangeList() {
        return classChangeList;
    }
    public boolean isLostVisibility(UMLClass originalClass, UMLClass nextClass){
        return false;
    }
    public boolean isGainVisibility(UMLOperation originalOperation, UMLOperation nextOperation){
        return false;
    }
    public boolean isAddFinalModifier(UMLClass originalClass, UMLClass nextClass){
        return false;
    }
    public boolean isRemoveFinalModifier(UMLOperation originalOperation, UMLOperation nextOperation){
        if(originalOperation.isFinal()&&!nextOperation.isFinal()){
            return true;
        }
        return false;
    }
    public boolean isRemoveStaticModifier(UMLOperation originalOperation, UMLOperation nextOperation){
        if(originalOperation.isStatic()&&!nextOperation.isStatic()){
            return true;
        }
        return false;
    }
    public boolean isAddStaticModifier(UMLOperation originalOperation, UMLOperation nextOperation){
        if(!originalOperation.isStatic()&&nextOperation.isStatic()){
            return true;
        }
        return false;
    }
}

package extension;

import apiminer.enums.Category;
import apiminer.enums.RefType;
import apiminer.internal.util.NewUtilTools;
import apiminer.internal.util.UtilTools;
import apiminer.util.Change;
import apiminer.util.category.ClassChange;
import apiminer.util.category.type.*;
import extension.category.Refactored;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.Refactoring;

import java.util.ArrayList;
import java.util.List;

public class Convert {
    private boolean isAPI;
    private Refactored refactored;
    private Change change;
    public Convert(Refactoring refactoring, RevCommit revCommit){
        ClassChange classChange = null;
        switch (refactoring.getRefactoringType()){
            case EXTRACT_SUPERCLASS:
            case EXTRACT_INTERFACE:
                classChange = new ExtractSuperTypeChange(refactoring,revCommit);
                this.isAPI = NewUtilTools.isAPIClass(classChange.getNextClass());
                this.refactored = new Refactored();
                refactored.setRefType(RefType.CLASS);
                refactored.setNextClass(classChange.getNextClass());
                this.change = classChange;
                break;
            case MOVE_CLASS:
                classChange =new MoveTypeChange(refactoring,revCommit);
                this.isAPI = NewUtilTools.isAPIClass(classChange.getNextClass());
                this.refactored = new Refactored();
                refactored.setRefType(RefType.CLASS);
                refactored.setOriginalClass(classChange.getOriginalClass());
                refactored.setNextClass(classChange.getNextClass());
                break;
            case RENAME_CLASS:
                classChange =new RenameTypeChange(refactoring,revCommit);
                this.isAPI = NewUtilTools.isAPIClass(classChange.getNextClass());
                this.refactored = new Refactored();
                refactored.setRefType(RefType.CLASS);
                refactored.setOriginalClass(classChange.getOriginalClass());
                refactored.setNextClass(classChange.getNextClass());
                break;
            case MOVE_RENAME_CLASS:
                 break;
            case EXTRACT_CLASS:
                break;
            case EXTRACT_SUBCLASS:
                 break;
            default:
        }
    }

    public boolean isAPI() {
        return isAPI;
    }

    public Refactored getRefactored() {
        return refactored;
    }

    public Change getChange() {
        return change;
    }
}

package apiminer.util.category.type;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.util.category.ClassChange;
import extension.RefactoringElement;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.diff.RenameClassRefactoring;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.Refactoring;

public class RenameTypeChange extends ClassChange {
    private RenameClassRefactoring renameClass;

    public RenameTypeChange(Refactoring refactoring, RevCommit revCommit){
        super(revCommit);
        this.renameClass = (RenameClassRefactoring)refactoring;
        this.setOriginalClass(renameClass.getOriginalClass());
        this.setNextClass(renameClass.getRenamedClass());
        this.setOriginalPath(this.getOriginalClass().toString());
        this.setNextPath(this.getNextClass().toString());
        this.setOriginalElement(this.getOriginalClass().toString());
        this.setNextElement(this.getNextClass().toString());
        this.setCategory(Category.TYPE_RENAME);
        this.setBreakingChange(true);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(this.getNextClass()));
        this.setDeprecated(isDeprecated(this.getNextClass()));
        this.setRevCommit(revCommit);
        if(this.getNextClass().isInterface()){
            this.setElementType(ElementType.INTERFACE);
        }else if(this.getNextClass().isEnum()){
            this.setElementType(ElementType.ENUM);
        }else{
            this.setElementType(ElementType.CLASS);
        }
    }

    private String isDescription(){
        String message = "";
        message += "<br>type <code>" + this.getOriginalElement() + "</code>";
        message += "<br>renamed to";
        message += "<br><code>" + this.getNextElement() + "</code>";
        message += "<br>";
        return message;
    }
}

package apiminer.util.category.type;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.util.category.ClassChange;
import extension.RefactoringElement;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

public class MoveAndRenameTypeChange extends ClassChange {
    private UMLClass originalClass;
    private UMLClass moveAndRenamedClass;
    public MoveAndRenameTypeChange(RefactoringElement refactoringElement, RevCommit revCommit){
        super(revCommit);
        this.originalClass = refactoringElement.getOriginalClass();
        this.moveAndRenamedClass = refactoringElement.getNextClass();
        this.setOriginalPath(originalClass.getSourceFile());
        this.setNextPath(moveAndRenamedClass.getSourceFile());
        this.setOriginalElement(originalClass.getName());
        this.setNextElement(moveAndRenamedClass.getName());
        this.setCategory(Category.TYPE_MOVE_AND_RENAME);
        this.setBreakingChange(true);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(moveAndRenamedClass));
        this.setDeprecated(isDeprecated(moveAndRenamedClass));
        this.setRevCommit(revCommit);
        if(moveAndRenamedClass.isInterface()){
            this.setElementType(ElementType.INTERFACE);
        }else if(moveAndRenamedClass.isEnum()){
            this.setElementType(ElementType.ENUM);
        }else{
            this.setElementType(ElementType.CLASS);
        }
    }

    private String isDescription(){
        String message = "";
        message += "<br>type <code>" + originalClass.getName() + "</code>";
        message += "<br>moved and renamed to";
        message += "<br><code>" + moveAndRenamedClass.getName() + "</code>";
        message += "<br>";
        return message;
    }
}

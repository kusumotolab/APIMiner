package apiminer.util.category.type;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.util.category.ClassChange;
import extension.RefactoringElement;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

public class RenameTypeChange extends ClassChange {
    private UMLClass originalClass;
    private UMLClass renamedClass;
    public RenameTypeChange(RefactoringElement refactoringElement, RevCommit revCommit){
        super(revCommit);
        this.originalClass = refactoringElement.getOriginalClass();
        this.renamedClass = refactoringElement.getNextClass();
        this.setOriginalPath(originalClass.getSourceFile());
        this.setNextPath(renamedClass.getSourceFile());
        this.setOriginalElement(originalClass.getName());
        this.setNextElement(renamedClass.getName());
        this.setCategory(Category.TYPE_RENAME);
        this.setBreakingChange(true);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(renamedClass));
        this.setDeprecated(isDeprecated(renamedClass));
        this.setRevCommit(revCommit);
        if(renamedClass.isInterface()){
            this.setElementType(ElementType.INTERFACE);
        }else if(renamedClass.isEnum()){
            this.setElementType(ElementType.ENUM);
        }else{
            this.setElementType(ElementType.CLASS);
        }
    }

    private String isDescription(){
        String message = "";
        message += "<br>type <code>" + originalClass.getName() + "</code>";
        message += "<br>renamed to";
        message += "<br><code>" + renamedClass.getName() + "</code>";
        message += "<br>";
        return message;
    }
}

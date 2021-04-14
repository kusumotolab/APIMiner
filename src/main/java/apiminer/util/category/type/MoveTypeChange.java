package apiminer.util.category.type;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.util.category.ClassChange;
import extension.RefactoringElement;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

public class MoveTypeChange extends ClassChange {
    private UMLClass originalClass;
    private UMLClass movedClass;

    public MoveTypeChange(RefactoringElement refactoringElement, RevCommit revCommit){
        super(revCommit);
        this.originalClass = refactoringElement.getOriginalClass();
        this.movedClass = refactoringElement.getNextClass();
        this.setOriginalPath(originalClass.getSourceFile());
        this.setNextPath(movedClass.getSourceFile());
        this.setOriginalElement(originalClass.getName());
        this.setNextElement(movedClass.getName());
        this.setCategory(Category.TYPE_MOVE);
        this.setBreakingChange(true);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(movedClass));
        this.setDeprecated(isDeprecated(movedClass));
        this.setRevCommit(revCommit);
        if(movedClass.isInterface()){
            this.setElementType(ElementType.INTERFACE);
        }else if(movedClass.isEnum()){
            this.setElementType(ElementType.ENUM);
        }else{
            this.setElementType(ElementType.CLASS);
        }
    }

    private String isDescription(){
            String message = "";
            message += "<br>type <code>" + originalClass.getName() + "</code>";
            message += "<br>moved to";
            message += "<br><code>" + movedClass.getName() + "</code>";
            message += "<br>";
            return message;
    }
}

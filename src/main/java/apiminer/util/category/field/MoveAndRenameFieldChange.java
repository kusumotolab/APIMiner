package apiminer.util.category.field;

import apiminer.enums.Category;
import apiminer.util.category.FieldChange;
import extension.RefactoringElement;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

public class MoveAndRenameFieldChange extends FieldChange {
    private UMLClass originalClass;
    private UMLAttribute originalAttribute;
    private UMLClass nextClass;
    private UMLAttribute movedAttribute;

    public MoveAndRenameFieldChange(RefactoringElement refactoringElement, RevCommit revCommit) {
        super(revCommit);
        this.originalClass = refactoringElement.getOriginalClass();
        this.originalAttribute = refactoringElement.getOriginalAttribute();
        this.nextClass = refactoringElement.getNextClass();
        this.movedAttribute = refactoringElement.getNextAttribute();
        this.setOriginalPath(originalClass.getSourceFile());
        this.setNextPath(nextClass.getSourceFile());
        this.setOriginalElement(originalAttribute.getName());
        this.setNextElement(movedAttribute.getName());
        this.setCategory(Category.FIELD_PULL_UP);
        this.setBreakingChange(false);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(movedAttribute));
        this.setDeprecated(isDeprecated(movedAttribute));
        this.setRevCommit(revCommit);
    }

    private String isDescription() {
        String message = "";
        message += "<br>field <code>" + originalAttribute.getName() + "</code>";
        message += "<br>renamed to <code>" + movedAttribute.getName() + "</code>";
        message += "<br>and moved from <code>" + originalClass.toString() + "</code>";
        message += "<br>to <code>" + nextClass.toString() + "</code>";
        message += "<br>";
        return message;
    }
}

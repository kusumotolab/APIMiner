package apiminer.util.category.field;

import apiminer.enums.Category;
import apiminer.util.category.FieldChange;
import extension.RefactoringElement;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

public class ChangeInDefaultValue extends FieldChange {
    private UMLClass originalClass;
    private UMLAttribute originalAttribute;
    private UMLClass nextClass;
    private UMLAttribute nextAttribute;

    public ChangeInDefaultValue(RefactoringElement refactoringElement, RevCommit revCommit) {
        super(revCommit);
        this.originalClass = refactoringElement.getOriginalClass();
        this.originalAttribute = refactoringElement.getOriginalAttribute();
        this.nextClass = refactoringElement.getNextClass();
        this.nextAttribute = refactoringElement.getNextAttribute();
        this.setOriginalPath(originalClass.getSourceFile());
        this.setNextPath(nextClass.getSourceFile());
        this.setOriginalElement(originalAttribute.getName());
        this.setNextElement(nextAttribute.getName());
        this.setCategory(Category.FIELD_PULL_UP);
        this.setBreakingChange(false);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(nextAttribute));
        this.setDeprecated(isDeprecated(nextAttribute));
        this.setRevCommit(revCommit);
    }

    private String isDescription() {
        String message = "";
        message += "<br>field <code>" + nextAttribute + "</code>";
        message += "<br>changed default value";
        message += "<br>in <code>" + nextClass.toString() + "</code>";
        message += "<br>";
        return message;
    }
}

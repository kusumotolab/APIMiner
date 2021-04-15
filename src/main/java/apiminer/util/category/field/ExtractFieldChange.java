package apiminer.util.category.field;

import apiminer.enums.Category;
import apiminer.util.category.FieldChange;
import extension.RefactoringElement;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

public class ExtractFieldChange extends FieldChange {
    private UMLClass originalClass;
    private UMLClass nextClass;
    private UMLAttribute extractedAttribute;

    public ExtractFieldChange(RefactoringElement refactoringElement, RevCommit revCommit) {
        super(revCommit);
        this.originalClass = refactoringElement.getOriginalClass();
        //this.originalAttribute = refactoringElement.getOriginalAttribute();
        this.nextClass = refactoringElement.getNextClass();
        this.extractedAttribute = refactoringElement.getNextAttribute();
        this.setOriginalPath(originalClass.getSourceFile());
        this.setNextPath(nextClass.getSourceFile());
        this.setOriginalElement("");
        this.setNextElement(extractedAttribute.getName());
        this.setCategory(Category.FIELD_PULL_UP);
        this.setBreakingChange(false);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(extractedAttribute));
        this.setDeprecated(isDeprecated(extractedAttribute));
        this.setRevCommit(revCommit);
    }

    private String isDescription() {
        String message = "";
        message += "<br>field <code>" + extractedAttribute + "</code>";
        message += "<br>extracted in <code>" + originalClass.toString() + "</code>";
        message += "<br>";
        return message;
    }
}

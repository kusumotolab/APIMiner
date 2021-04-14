package apiminer.util.category.field;

import apiminer.enums.Category;
import apiminer.internal.util.UtilTools;
import apiminer.util.category.FieldChange;
import extension.RefactoringElement;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

public class RenameFieldChange extends FieldChange {
    private UMLClass originalClass;
    private UMLAttribute originalAttribute;
    private UMLClass nextClass;
    private UMLAttribute renamedAttribute;

    public RenameFieldChange(RefactoringElement refactoringElement, RevCommit revCommit) {
        super(revCommit);
        this.originalClass = refactoringElement.getOriginalClass();
        this.originalAttribute = refactoringElement.getOriginalAttribute();
        this.nextClass = refactoringElement.getNextClass();
        this.renamedAttribute = refactoringElement.getNextAttribute();
        this.setOriginalPath(originalClass.getSourceFile());
        this.setNextPath(nextClass.getSourceFile());
        this.setOriginalElement(originalAttribute.getName());
        this.setNextElement(renamedAttribute.getName());
        this.setCategory(Category.FIELD_RENAME);
        this.setBreakingChange(false);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(renamedAttribute));
        this.setDeprecated(isDeprecated(renamedAttribute));
        this.setRevCommit(revCommit);
    }

    private String isDescription(){
        String message = "";
        message += "<br>field <code>" + originalAttribute.getName() +"</code>";
        message += "<br>renamed to <code>" + renamedAttribute.getName() +"</code>";
        message += "<br>in <code>" + nextClass.getName() +"</code>";
        message += "<br>";
        return message;
    }

}

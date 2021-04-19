package apiminer.internal.analysis.category.field;

import apiminer.enums.Category;
import apiminer.internal.analysis.category.FieldChange;
import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

public class AddFieldChange extends FieldChange {

    public AddFieldChange(UMLClass umlClass, UMLAttribute addedAttribute, RevCommit revCommit) {
        super(revCommit);
        this.setNextClass(umlClass);
        this.setNextAttribute(addedAttribute);
        this.setOriginalPath("");
        this.setNextPath(UtilTools.getTypeDescriptionName(umlClass));
        this.setOriginalElement("");
        this.setNextElement(UtilTools.getFieldDescriptionName(addedAttribute));
        this.setCategory(Category.FIELD_ADD);
        this.setBreakingChange(false);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(addedAttribute));
        this.setDeprecated(isDeprecated(addedAttribute));
        this.setRevCommit(revCommit);
    }

    private String isDescription() {
        String message = "";
        message += "<br>field <code>" + this.getNextElement() + "</code>";
        message += "<br>added in <code>" + this.getNextPath() + "</code>";
        message += "<br>";
        return message;
    }
}

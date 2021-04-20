package apiminer.internal.analysis.category.field;

import apiminer.enums.Category;
import apiminer.internal.analysis.category.FieldChange;
import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

public class DeprecateFieldChange extends FieldChange {
    public DeprecateFieldChange(UMLClass originalClass, UMLAttribute originalAttribute, UMLClass nextClass, UMLAttribute nextAttribute, RevCommit revCommit) {
        super(revCommit);
        this.setOriginalClass(originalClass);
        this.setOriginalAttribute(originalAttribute);
        this.setNextClass(nextClass);
        this.setNextAttribute(nextAttribute);
        this.setOriginalPath(UtilTools.getTypeDescriptionName(originalClass));
        this.setNextPath(UtilTools.getTypeDescriptionName(nextClass));
        this.setOriginalElement(UtilTools.getFieldDescriptionName(originalAttribute));
        this.setNextElement(UtilTools.getFieldDescriptionName(nextAttribute));
        this.setCategory(Category.FIELD_DEPRECATED);
        this.setBreakingChange(false);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(nextAttribute));
        this.setDeprecated(isDeprecated(nextAttribute));
        this.setRevCommit(revCommit);
    }

    private String isDescription() {
        String message = "";
        message += "<br>field <code>" + this.getNextElement() +"</code>";
        message += "<br>deprecated in <code>" + this.getNextPath() +"</code>";
        message += "<br>";
        return message;
    }
}

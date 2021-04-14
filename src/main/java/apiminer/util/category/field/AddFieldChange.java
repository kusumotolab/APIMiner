package apiminer.util.category.field;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.util.category.FieldChange;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

public class AddFieldChange extends FieldChange {
    private UMLClass umlClass;
    private UMLAttribute addedAttribute;

    public AddFieldChange(UMLClass umlClass,UMLAttribute addedAttribute,RevCommit revCommit){
        super(revCommit);
        this.umlClass = umlClass;
        this.addedAttribute = addedAttribute;
        this.setOriginalPath("");
        this.setNextPath(umlClass.getSourceFile());
        this.setOriginalElement("");
        this.setNextElement(addedAttribute.getName());
        this.setCategory(Category.FIELD_ADD);
        this.setBreakingChange(false);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(addedAttribute));
        this.setDeprecated(isDeprecated(addedAttribute));
        this.setRevCommit(revCommit);
    }

    private String isDescription(){
        String message = "";
        message += "<br>field <code>" + addedAttribute.getName() +"</code>";
        message += "<br>added in <code>" + umlClass.getName() +"</code>";
        message += "<br>";
        return message;
    }
}

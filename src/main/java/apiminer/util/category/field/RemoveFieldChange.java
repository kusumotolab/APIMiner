package apiminer.util.category.field;

import apiminer.enums.Category;
import apiminer.util.category.FieldChange;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

public class RemoveFieldChange extends FieldChange {
    private UMLClass umlClass;
    private UMLAttribute removedAttribute;

    public RemoveFieldChange(UMLClass umlClass, UMLAttribute removedAttribute, RevCommit revCommit){
        super(revCommit);
        this.umlClass = umlClass;
        this.removedAttribute = removedAttribute;
        this.setOriginalPath(umlClass.getSourceFile());
        this.setNextPath("");
        this.setOriginalElement(removedAttribute.getName());
        this.setNextElement("");
        this.setCategory(Category.FIELD_REMOVE);
        this.setBreakingChange(false);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(removedAttribute));
        this.setDeprecated(isDeprecated(removedAttribute));
        this.setRevCommit(revCommit);
    }

    private String isDescription(){
        String message = "";
        message += "<br>field <code>" + removedAttribute.getName() +"</code>";
        message += "<br>removed from <code>" + umlClass.getName() + "</code>";
        message += "<br>";
        return message;
    }
}

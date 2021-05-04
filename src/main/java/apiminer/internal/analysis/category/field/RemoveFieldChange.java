package apiminer.internal.analysis.category.field;

import apiminer.enums.Category;
import apiminer.internal.analysis.category.FieldChange;
import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

public class RemoveFieldChange extends FieldChange {

    public RemoveFieldChange(UMLClass umlClass, UMLAttribute removedAttribute, RevCommit revCommit){
        super(revCommit);
        this.setOriginalClass(umlClass);
        this.setOriginalAttribute(removedAttribute);
        this.setOriginalPath(UtilTools.getTypeDescriptionName(this.getOriginalClass()));
        this.setNextPath("");
        this.setOriginalElement(UtilTools.getFieldDescriptionName(this.getOriginalAttribute()));
        this.setNextElement("");
        this.setCategory(Category.FIELD_REMOVE);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(removedAttribute));
        this.setDeprecated(checkDeprecated(this.getOriginalClass(),this.getOriginalAttribute()));
        this.setBreakingChange(this.checkDeprecated(this.getOriginalClass(), this.getOriginalAttribute()) ? false : true);
        this.setRevCommit(revCommit);
    }

    private String isDescription(){
        String message = "";
        message += "<br>field <code>" + this.getOriginalElement() +"</code>";
        message += "<br>removed from <code>" + this.getOriginalPath() + "</code>";
        message += "<br>";
        return message;
    }
}

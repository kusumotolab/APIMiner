package apiminer.internal.analysis.category.field;

import apiminer.enums.Category;
import apiminer.internal.analysis.category.FieldChange;
import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

public class StaticFieldChange extends FieldChange {
    public StaticFieldChange(UMLClass originalClass, UMLAttribute originalAttribute, UMLClass nextClass, UMLAttribute nextAttribute, Category category, RevCommit revCommit) {
        super(revCommit);
        this.setOriginalClass(originalClass);
        this.setOriginalAttribute(originalAttribute);
        this.setNextClass(nextClass);
        this.setNextAttribute(nextAttribute);
        this.setOriginalPath(UtilTools.getTypeDescriptionName(originalClass));
        this.setNextPath(UtilTools.getTypeDescriptionName(nextClass));
        this.setOriginalElement(UtilTools.getFieldDescriptionName(originalAttribute));
        this.setNextElement(UtilTools.getFieldDescriptionName(nextAttribute));
        this.setCategory(category);
        this.setBreakingChange(category.equals(Category.FIELD_REMOVE_MODIFIER_STATIC));
        this.setBreakingChange(false);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(nextAttribute));
        this.setDeprecated(isDeprecated(nextAttribute));
        this.setRevCommit(revCommit);
    }

    private String isDescription() {
        String message = "";
        message += "<br>field <code>" + this.getNextElement() +"</code>";
        if(getCategory().equals(Category.FIELD_ADD_MODIFIER_STATIC)){
            message += "<br>received the modifier <code>static</code>";
        }else{
            message += "<br>lost the modifier <code>static</code>";
        }
        message += "<br>in <code>" + this.getNextPath() + "</code>";
        message += "<br>";
        return message;
    }
}

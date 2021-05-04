package apiminer.internal.analysis.category.field;

import apiminer.enums.Category;
import apiminer.internal.analysis.category.FieldChange;
import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

public class FinalFieldChange extends FieldChange {

    public FinalFieldChange(UMLClass originalClass, UMLAttribute originalAttribute, UMLClass nextClass, UMLAttribute nextAttribute, Category category, RevCommit revCommit) {
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
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(nextAttribute));
        this.setDeprecated(checkDeprecated(this.getNextClass(),this.getNextAttribute()));
        boolean isBreakingChange = category.equals(Category.FIELD_ADD_MODIFIER_FINAL);
        this.setBreakingChange(this.checkDeprecated(this.getOriginalClass(), this.getOriginalAttribute()) ? false : isBreakingChange);
        this.setRevCommit(revCommit);
    }

    private String isDescription() {
        String message = "";
        message += "<br>field <code>" + this.getNextElement() +"</code>";
        if(getCategory().equals(Category.FIELD_ADD_MODIFIER_FINAL)){
            message += "<br>received the modifier <code>final</code>";
        }else{
            message += "<br>lost the modifier <code>final</code>";
        }
        message += "<br>in <code>" + this.getNextPath() + "</code>";
        message += "<br>";
        return message;
    }
}

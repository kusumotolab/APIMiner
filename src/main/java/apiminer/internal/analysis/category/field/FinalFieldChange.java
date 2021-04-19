package apiminer.internal.analysis.category.field;

import apiminer.enums.Category;
import apiminer.internal.analysis.category.FieldChange;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

public class FinalFieldChange extends FieldChange {
    private UMLClass originalClass;
    private UMLAttribute originalAttribute;
    private UMLClass nextClass;
    private UMLAttribute nextAttribute;

    public FinalFieldChange(UMLClass originalClass, UMLAttribute originalAttribute, UMLClass nextClass, UMLAttribute nextAttribute, Category category, RevCommit revCommit) {
        super(revCommit);
        this.originalClass = originalClass;
        this.originalAttribute = originalAttribute;
        this.nextClass = nextClass;
        this.nextAttribute = nextAttribute;
        this.setOriginalPath(originalClass.getSourceFile());
        this.setNextPath(nextClass.getSourceFile());
        this.setOriginalElement(originalAttribute.getName());
        this.setNextElement(this.nextAttribute.getName());
        this.setCategory(category);
        this.setBreakingChange(false);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(this.nextAttribute));
        this.setDeprecated(isDeprecated(this.nextAttribute));
        this.setRevCommit(revCommit);
    }

    private String isDescription() {
        String message = "";
        message += "<br>field <code>" + nextAttribute.getName() +"</code>";
        if(getCategory().equals(Category.FIELD_ADD_MODIFIER_FINAL)){
            message += "<br>received the modifier <code>final</code>";
        }else{
            message += "<br>lost the modifier <code>final</code>";
        }
        message += "<br>in <code>" + nextClass.toString() + "</code>";
        message += "<br>";
        return message;
    }
}

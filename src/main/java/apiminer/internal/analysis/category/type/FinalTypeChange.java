package apiminer.internal.analysis.category.type;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.internal.analysis.category.TypeChange;
import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

public class FinalTypeChange extends TypeChange {

    public FinalTypeChange(UMLClass originalClass, UMLClass nextClass, Category category, RevCommit revCommit) {
        super(revCommit);
        this.setOriginalClass(originalClass);
        this.setNextClass(nextClass);
        this.setOriginalPath(UtilTools.getTypeDescriptionName(originalClass));
        this.setNextPath(UtilTools.getTypeDescriptionName(nextClass));
        this.setOriginalElement(UtilTools.getTypeDescriptionName(originalClass));
        this.setNextElement(UtilTools.getTypeDescriptionName(nextClass));
        this.setCategory(category);
        if(category.equals(Category.TYPE_REMOVE_MODIFIER_FINAL)){
            this.setBreakingChange(false);
        }else{
            this.setBreakingChange(true);
        }
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(nextClass));
        this.setDeprecated(isDeprecated(nextClass));
        this.setRevCommit(revCommit);
        if (nextClass.isInterface()) {
            this.setElementType(ElementType.INTERFACE);
        } else if (nextClass.isEnum()) {
            this.setElementType(ElementType.ENUM);
        } else {
            this.setElementType(ElementType.CLASS);
        }
    }

    private String isDescription(){
        String message = "";
        message += "<br>type <code>" + this.getNextPath() + "</code>";
        if(getCategory().equals(Category.TYPE_ADD_MODIFIER_FINAL)){
            message += "<br>received the modifier <code>final</code>";
        }else{
            message += "<br>lost the modifier <code>final</code>";
        }
        message += "<br>";
        return message;
    }
}

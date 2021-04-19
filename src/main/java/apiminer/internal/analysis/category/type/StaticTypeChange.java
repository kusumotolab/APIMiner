package apiminer.internal.analysis.category.type;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.internal.analysis.category.TypeChange;
import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

public class StaticTypeChange extends TypeChange {


    public StaticTypeChange(UMLClass originalClass,UMLClass nextClass,Category category, RevCommit revCommit) {
        super(revCommit);
        this.setOriginalClass(originalClass);
        this.setNextClass(nextClass);
        this.setOriginalPath(UtilTools.getTypeDescriptionName(originalClass));
        this.setNextPath(UtilTools.getTypeDescriptionName(nextClass));
        this.setOriginalElement(UtilTools.getTypeDescriptionName(originalClass));
        this.setNextElement(UtilTools.getTypeDescriptionName(nextClass));
        this.setCategory(category);
        this.setBreakingChange(category.equals(Category.TYPE_REMOVE_MODIFIER_STATIC));
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
        message += "<br>type <code>" + this.getNextElement() + "</code>";
        if(getCategory().equals(Category.TYPE_ADD_MODIFIER_STATIC)){
            message += "<br>received the modifier <code>static</code>";
        }else{
            message += "<br>lost the modifier <code>static</code>";
        }
        message += "<br>";
        return message;
    }
}

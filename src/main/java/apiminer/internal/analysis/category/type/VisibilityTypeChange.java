package apiminer.internal.analysis.category.type;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.internal.analysis.category.TypeChange;
import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

public class VisibilityTypeChange extends TypeChange {

    public VisibilityTypeChange(UMLClass originalClass, UMLClass nextClass, Category category, RevCommit revCommit) {
        super(revCommit);
        this.setOriginalClass(originalClass);
        this.setNextClass(nextClass);
        this.setOriginalPath(UtilTools.getTypeDescriptionName(originalClass));
        this.setNextPath(UtilTools.getTypeDescriptionName(nextClass));
        this.setOriginalElement(UtilTools.getTypeDescriptionName(originalClass));
        this.setNextElement(UtilTools.getTypeDescriptionName(nextClass));
        this.setCategory(category);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(nextClass));
        this.setDeprecated(checkDeprecated(this.getNextClass()));
        boolean isBreakingChange = this.getCategory().equals(Category.TYPE_LOST_VISIBILITY);
        this.setBreakingChange(isBreakingChange);
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
        message += "<br>changed visibility from <code>" + UtilTools.getVisibilityDescriptionName(this.getOriginalClass().getVisibility()) + "</code>to <code>" + UtilTools.getVisibilityDescriptionName(this.getNextClass().getVisibility()) + "</code>";
        message += "<br>";
        return message;
    }
}

package apiminer.internal.analysis.category.type;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.internal.analysis.category.TypeChange;
import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

public class ChangeSuperTypeChange extends TypeChange {
    //Todo fix
    public ChangeSuperTypeChange(UMLClass originalClass, UMLClass nextClass, UMLClass originalSuperClass, UMLClass nextSuperClass, RevCommit revCommit) {
        super(revCommit);
        this.setOriginalClass(originalClass);
        this.setNextClass(nextClass);
        this.setOriginalPath(UtilTools.getTypeDescriptionName(originalClass));
        this.setNextPath(UtilTools.getTypeDescriptionName(nextClass));
        this.setOriginalElement(UtilTools.getTypeDescriptionName(originalSuperClass));
        this.setNextElement(UtilTools.getTypeDescriptionName(nextSuperClass));
        this.setCategory(Category.TYPE_CHANGE_SUPERCLASS);
        this.setBreakingChange(true);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(this.getNextClass()));
        this.setDeprecated(isDeprecated(this.getNextClass()));
        this.setRevCommit(revCommit);
        if (this.getNextClass().isInterface()) {
            this.setElementType(ElementType.INTERFACE);
        } else if (this.getNextClass().isEnum()) {
            this.setElementType(ElementType.ENUM);
        } else {
            this.setElementType(ElementType.CLASS);
        }
    }

    private String isDescription() {
        String message = "";
        message += "<br>type <code>" + this.getNextPath() + "</code>";
        message += "<br>changed the superType";
        message += "<br>from <code>" + this.getOriginalElement() + "</code>";
        message += "<br>to <code>" + this.getNextElement() + "</code>";
        message += "<br>";
        return message;
    }
}

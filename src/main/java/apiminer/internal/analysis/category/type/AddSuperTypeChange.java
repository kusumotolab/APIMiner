package apiminer.internal.analysis.category.type;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.internal.analysis.category.TypeChange;
import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

public class AddSuperTypeChange extends TypeChange {
    public AddSuperTypeChange(UMLClass originalClass, UMLClass nextClass, UMLClass addedSuperClass, RevCommit revCommit){
        super(revCommit);
        this.setOriginalClass(originalClass);
        this.setNextClass(nextClass);
        this.setOriginalPath(UtilTools.getTypeDescriptionName(originalClass));
        this.setNextPath(UtilTools.getTypeDescriptionName(nextClass));
        this.setOriginalElement("");
        this.setNextElement(UtilTools.getTypeDescriptionName(addedSuperClass));
        this.setCategory(Category.TYPE_ADD_SUPER_CLASS);
        this.setBreakingChange(false);
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
        message += "<br>added superType <code>" + this.getNextElement() + "</code>";
        message += "<br>";
        return message;
    }
}

package apiminer.internal.analysis.category.type;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.internal.analysis.category.TypeChange;
import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

public class RemoveTypeChange extends TypeChange {

    public RemoveTypeChange(UMLClass removedClass, RevCommit revCommit) {
        super(revCommit);
        this.setOriginalClass(removedClass);
        this.setOriginalPath(UtilTools.getTypeDescriptionName(this.getOriginalClass()));
        this.setNextPath("");
        this.setOriginalElement(UtilTools.getTypeDescriptionName(this.getOriginalClass()));
        this.setNextElement("");
        this.setCategory(Category.TYPE_REMOVE);
        this.setBreakingChange(true);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(removedClass));
        this.setDeprecated(isDeprecated(removedClass));
        this.setRevCommit(revCommit);
        if (removedClass.isInterface()) {
            this.setElementType(ElementType.INTERFACE);
        } else if (removedClass.isEnum()) {
            this.setElementType(ElementType.ENUM);
        } else {
            this.setElementType(ElementType.CLASS);
        }
    }

    private String isDescription() {
        String message = "";
        message += "<br>type <code>" + this.getOriginalElement() + "</code>";
        message += "<br>was removed";
        message += "<br>";
        return message;
    }
}

package apiminer.internal.analysis.category.type;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.internal.analysis.category.TypeChange;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

public class RemoveTypeChange extends TypeChange {
    private final UMLClass removedClass;

    public RemoveTypeChange(UMLClass removedClass, RevCommit revCommit) {
        super(revCommit);
        this.removedClass = removedClass;
        this.setOriginalPath(removedClass.getSourceFile());
        this.setNextPath("");
        this.setOriginalElement(removedClass.getName());
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
        message += "<br>type <code>" + removedClass.getName() + "</code>";
        message += "<br>was removed";
        message += "<br>";
        return message;
    }
}

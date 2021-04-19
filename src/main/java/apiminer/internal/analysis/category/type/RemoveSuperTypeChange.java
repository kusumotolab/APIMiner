package apiminer.internal.analysis.category.type;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.internal.analysis.category.ClassChange;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

public class RemoveSuperTypeChange extends ClassChange {
    public RemoveSuperTypeChange(UMLClass originalClass, UMLClass nextClass, UMLClass removedSuperClass, RevCommit revCommit){
        super(revCommit);
        this.setOriginalClass(originalClass);
        this.setNextClass(nextClass);
        this.setOriginalPath(this.getOriginalClass().toString());
        this.setNextPath(this.getNextClass().toString());
        this.setOriginalElement(removedSuperClass.toString());
        this.setNextElement("");
        this.setCategory(Category.TYPE_REMOVE_SUPERCLASS);
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
        message += "<br>removed superType <code>" + this.getOriginalElement() + "</code>";
        message += "<br>";
        return message;
    }
}

package apiminer.internal.analysis.category.type;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.internal.analysis.category.TypeChange;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

public class DeprecateTypeChange extends TypeChange {
    private UMLClass originalClass;
    private UMLClass nextClass;

    public DeprecateTypeChange(UMLClass originalClass, UMLClass nextClass, RevCommit revCommit) {
        super(revCommit);
        this.originalClass = originalClass;
        this.nextClass = nextClass;
        this.setOriginalPath(originalClass.getSourceFile());
        this.setNextPath(nextClass.getSourceFile());
        this.setOriginalElement(originalClass.getName());
        this.setNextElement(nextClass.getName());
        this.setCategory(Category.TYPE_DEPRECATED);
        this.setBreakingChange(true);
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
        message += "<br>type <code>" + nextClass.getName() + "</code> was deprecated";
        message += "<br>";
        return message;
    }
}

package apiminer.internal.analysis.category.type;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.internal.analysis.category.ClassChange;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

public class FinalTypeChange extends ClassChange {
    private UMLClass originalClass;
    private UMLClass nextClass;

    public FinalTypeChange(UMLClass originalClass, UMLClass nextClass, Category category, RevCommit revCommit) {
        super(revCommit);
        this.originalClass = originalClass;
        this.nextClass = nextClass;
        this.setOriginalPath(originalClass.getSourceFile());
        this.setNextPath(nextClass.getSourceFile());
        this.setOriginalElement(originalClass.getName());
        this.setNextElement(nextClass.getName());
        this.setCategory(category);
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
        message += "<br>type <code>" + nextClass.getName() + "</code>";
        if(getCategory().equals(Category.TYPE_ADD_MODIFIER_FINAL)){
            message += "<br>received the modifier <code>final</code>";
        }else{
            message += "<br>lost the modifier <code>final</code>";
        }
        message += "<br>";
        return message;
    }
}

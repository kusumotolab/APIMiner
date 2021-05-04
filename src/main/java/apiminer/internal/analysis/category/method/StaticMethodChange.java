package apiminer.internal.analysis.category.method;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.internal.analysis.category.MethodChange;
import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLOperation;
import org.eclipse.jgit.revwalk.RevCommit;

public class StaticMethodChange extends MethodChange {
    public StaticMethodChange(UMLClass originalClass, UMLOperation originalOperation, UMLClass nextClass, UMLOperation nextOperation, Category category, RevCommit revCommit) {
        super(revCommit);
        this.setOriginalClass(originalClass);
        this.setNextClass(nextClass);
        this.setOriginalOperation(originalOperation);
        this.setNextOperation(nextOperation);
        this.setOriginalPath(UtilTools.getTypeDescriptionName(originalClass));
        this.setNextPath(UtilTools.getTypeDescriptionName(nextClass));
        this.setOriginalElement(UtilTools.getMethodDescriptionName(originalOperation));
        this.setNextElement(UtilTools.getMethodDescriptionName(nextOperation));
        this.setCategory(category);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(this.getNextOperation()));
        this.setDeprecated(checkDeprecated(this.getNextClass(),this.getNextOperation()));
        boolean isBreakingChange = category.equals(Category.METHOD_REMOVE_MODIFIER_STATIC);
        this.setBreakingChange(this.checkDeprecated(this.getOriginalClass(), this.getOriginalOperation()) ? false : isBreakingChange);
        this.setRevCommit(revCommit);
        if (this.getNextOperation().isConstructor()) {
            this.setElementType(ElementType.CONSTRUCTOR);
        } else {
            this.setElementType(ElementType.METHOD);
        }
    }

    private String isDescription() {
        String message = "";
        message += "<br>method <code>" + this.getNextElement() + "</code>";
        if (this.getCategory().equals(Category.METHOD_REMOVE_MODIFIER_STATIC)) {
            message += "<br>lost the modifier <code>static</code>";
        } else {
            message += "<br>received the modifier <code>static</code>";
        }
        message += "<br>in <code>" + this.getNextPath() + "</code>";
        message += "<br>";
        return message;
    }
}

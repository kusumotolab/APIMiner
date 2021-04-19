package apiminer.internal.analysis.category.method;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.internal.analysis.category.MethodChange;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLOperation;
import org.eclipse.jgit.revwalk.RevCommit;

public class DeprecatedMethodChange extends MethodChange {
    public DeprecatedMethodChange(UMLClass originalClass, UMLOperation originalOperation,UMLClass nextClass,UMLOperation nextOperation, RevCommit revCommit) {
        super(revCommit);
        this.setOriginalClass(originalClass);
        this.setNextClass(nextClass);
        this.setOriginalOperation(originalOperation);
        this.setNextOperation(nextOperation);
        this.setOriginalPath(this.getOriginalClass().toString());
        this.setNextPath(this.getNextClass().toString());
        this.setOriginalElement(this.getOriginalOperation().toString());
        this.setNextElement(this.getNextOperation().toString());
        this.setCategory(Category.METHOD_DEPRECATED);
        this.setBreakingChange(true);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(this.getNextOperation()));
        this.setDeprecated(isDeprecated(this.getNextOperation()));
        this.setRevCommit(revCommit);
        if (this.getNextOperation().isConstructor()) {
            this.setElementType(ElementType.CONSTRUCTOR);
        } else {
            this.setElementType(ElementType.METHOD);
        }

    }

    private String isDescription() {
        String message = "";
        message += "<br>method <code>" + this.getNextOperation() +"</code>";
        message += "<br>deprecated in <code>" + this.getNextPath() +"</code>";
        message += "<br>";
        return message;
    }
}

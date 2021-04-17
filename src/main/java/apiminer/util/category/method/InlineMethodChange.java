package apiminer.util.category.method;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.util.category.MethodChange;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.diff.InlineOperationRefactoring;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.Refactoring;

import java.util.Map;

public class InlineMethodChange extends MethodChange {
    InlineOperationRefactoring inlineOperation;

    public InlineMethodChange(Refactoring refactoring, Map<String, UMLClass> parentClassMap, Map<String, UMLClass> currentClassMap, RevCommit revCommit) {
        super(revCommit);
        this.inlineOperation = (InlineOperationRefactoring) refactoring;
        this.setOriginalClass(parentClassMap.get(inlineOperation.getInlinedOperation().getClassName()));
        this.setOriginalOperation(inlineOperation.getInlinedOperation());
        this.setOriginalPath(this.getOriginalClass().toString());
        this.setNextPath(inlineOperation.getTargetOperationAfterInline().getClassName());
        this.setOriginalElement(this.getOriginalOperation().toString());
        this.setNextElement(inlineOperation.getTargetOperationAfterInline().toString());
        this.setCategory(Category.METHOD_INLINE);
        this.setBreakingChange(true);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(this.getOriginalOperation()));
        this.setDeprecated(isDeprecated(this.getOriginalOperation()));
        this.setRevCommit(revCommit);
        if (this.getNextOperation().isConstructor()) {
            this.setElementType(ElementType.CONSTRUCTOR);
        } else {
            this.setElementType(ElementType.METHOD);
        }

    }

    private String isDescription() {
        String message = "";
        message += "<br>method <code>" + this.getOriginalElement() + "</code>";
        message += "<br>from <code>" + this.getOriginalPath() + "</code>";
        message += "<br>inlined to <code>" + this.getNextElement() + "</code>";
        message += "<br>in <code>" + this.getNextPath() + "</code>";
        message += "<br>";
        return message;
    }
}

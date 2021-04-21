package apiminer.internal.analysis.category.method;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.internal.analysis.category.MethodChange;
import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.diff.InlineOperationRefactoring;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.Refactoring;

import java.util.Map;

public class InlineMethodChange extends MethodChange {

    public InlineMethodChange(Refactoring refactoring, Map<String, UMLClass> parentClassMap, Map<String, UMLClass> currentClassMap, RevCommit revCommit) {
        super(revCommit);
        InlineOperationRefactoring inlineOperation = (InlineOperationRefactoring) refactoring;
        this.setOriginalClass(parentClassMap.get(inlineOperation.getInlinedOperation().getClassName()));
        this.setOriginalOperation(inlineOperation.getInlinedOperation());
        this.setOriginalPath(UtilTools.getTypeDescriptionName(this.getOriginalClass()));
        this.setNextPath(UtilTools.getTypeDescriptionName(currentClassMap.get(inlineOperation.getTargetOperationAfterInline().getClassName())));
        this.setOriginalElement(UtilTools.getMethodDescriptionName(this.getOriginalOperation()));
        this.setNextElement(UtilTools.getMethodDescriptionName(inlineOperation.getTargetOperationAfterInline()));
        this.setCategory(Category.METHOD_INLINE);
        this.setBreakingChange(true);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(this.getOriginalOperation()));
        this.setDeprecated(isDeprecated(this.getOriginalOperation()));
        this.setRevCommit(revCommit);
        if (this.getOriginalOperation().isConstructor()) {
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

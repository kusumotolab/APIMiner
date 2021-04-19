package apiminer.internal.analysis.category.method;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.internal.analysis.category.MethodChange;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.diff.ChangeReturnTypeRefactoring;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.Refactoring;

import java.util.Map;

public class ChangeInReturnType extends MethodChange {
    public ChangeInReturnType(Refactoring refactoring, Map<String, UMLClass> parentClassMap, Map<String, UMLClass> currentClassMap, RevCommit revCommit) {
        super(revCommit);
        ChangeReturnTypeRefactoring changeReturnType = (ChangeReturnTypeRefactoring) refactoring;
        this.setOriginalClass(parentClassMap.get(changeReturnType.getOperationBefore().getClassName()));
        this.setNextClass(currentClassMap.get(changeReturnType.getOperationAfter().getClassName()));
        this.setOriginalOperation(changeReturnType.getOperationBefore());
        this.setNextOperation(changeReturnType.getOperationAfter());
        this.setOriginalPath(this.getOriginalClass().toString());
        this.setNextPath(this.getNextClass().toString());
        this.setOriginalElement(this.getOriginalOperation().toString());
        this.setNextElement(this.getNextOperation().toString());
        this.setCategory(Category.METHOD_CHANGE_RETURN_TYPE);
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
        message += "<br>method <code>" + this.getNextElement() +"</code>";
        message += "<br>changed the return type";
        message += "<br>in <code>" + this.getNextPath() + "</code>";
        message += "<br>";
        return message;
    }
}

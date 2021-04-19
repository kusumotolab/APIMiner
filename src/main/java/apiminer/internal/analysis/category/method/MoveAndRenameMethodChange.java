package apiminer.internal.analysis.category.method;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.internal.analysis.category.MethodChange;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.diff.MoveOperationRefactoring;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.Refactoring;

import java.util.Map;

public class MoveAndRenameMethodChange extends MethodChange {

    public MoveAndRenameMethodChange(Refactoring refactoring, Map<String, UMLClass> parentClassMap, Map<String, UMLClass> currentClassMap, RevCommit revCommit) {
        super(revCommit);
        MoveOperationRefactoring moveOperation = (MoveOperationRefactoring) refactoring;
        this.setOriginalClass(parentClassMap.get(moveOperation.getOriginalOperation().getClassName()));
        this.setNextClass(currentClassMap.get(moveOperation.getMovedOperation().getClassName()));
        this.setOriginalOperation(moveOperation.getOriginalOperation());
        this.setNextOperation(moveOperation.getMovedOperation());
        this.setOriginalPath(this.getOriginalClass().toString());
        this.setNextPath(this.getNextClass().toString());
        this.setOriginalElement(this.getOriginalOperation().toString());
        this.setNextElement(this.getNextOperation().toString());
        this.setCategory(Category.METHOD_MOVE_AND_RENAME);
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
        message += "<br>method <code>" + this.getOriginalElement() + "</code>";
        message += "<br>renamed to <code>" + this.getNextElement() + "</code>";
        message += "<br>and moved from <code>" + this.getOriginalPath() + "</code>";
        message += "<br>to <code>" + this.getNextPath() + "</code>";
        message += "<br>";
        return message;
    }
}

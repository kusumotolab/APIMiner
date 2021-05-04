package apiminer.internal.analysis.category.method;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.internal.analysis.category.MethodChange;
import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.diff.MoveOperationRefactoring;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.Refactoring;

import java.util.Map;

public class MoveMethodChange extends MethodChange {

    public MoveMethodChange(Refactoring refactoring, Map<String, UMLClass> parentClassMap, Map<String, UMLClass> currentClassMap, RevCommit revCommit) {
        super(revCommit);
        MoveOperationRefactoring moveOperation = (MoveOperationRefactoring) refactoring;
        this.setOriginalClass(parentClassMap.get(moveOperation.getOriginalOperation().getClassName()));
        this.setNextClass(currentClassMap.get(moveOperation.getMovedOperation().getClassName()));
        this.setOriginalOperation(moveOperation.getOriginalOperation());
        this.setNextOperation(moveOperation.getMovedOperation());
        this.setOriginalPath(UtilTools.getTypeDescriptionName(this.getOriginalClass()));
        this.setNextPath(UtilTools.getTypeDescriptionName(this.getNextClass()));
        this.setOriginalElement(UtilTools.getMethodDescriptionName(this.getOriginalOperation()));
        this.setNextElement(UtilTools.getMethodDescriptionName(this.getNextOperation()));
        this.setCategory(Category.METHOD_MOVE);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(this.getNextOperation()));
        this.setDeprecated(checkDeprecated(this.getNextClass(),this.getNextOperation()));
        this.setBreakingChange(this.checkDeprecated(this.getOriginalClass(), this.getOriginalOperation()) ? false : true);
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
        message += "<br>moved from <code>" + this.getOriginalPath() + "</code>";
        message += "<br>to <code>" + this.getNextPath() + "</code>";
        message += "<br>";
        return message;
    }
}

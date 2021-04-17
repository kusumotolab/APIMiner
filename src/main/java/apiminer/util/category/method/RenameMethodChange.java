package apiminer.util.category.method;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.util.category.MethodChange;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.diff.MoveClassRefactoring;
import gr.uom.java.xmi.diff.RenameOperationRefactoring;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.Refactoring;

import java.util.Map;

public class RenameMethodChange extends MethodChange{
    private RenameOperationRefactoring renameOperation;

    public RenameMethodChange(Refactoring refactoring, Map<String, UMLClass> parentClassMap, Map<String,UMLClass> currentClassMap,RevCommit revCommit){
        super(revCommit);
        this.renameOperation = (RenameOperationRefactoring) refactoring;
        this.setOriginalClass(parentClassMap.get(renameOperation.getOriginalOperation().getClassName()));
        this.setNextClass(currentClassMap.get(renameOperation.getRenamedOperation().getClassName()));
        this.setOriginalOperation(renameOperation.getOriginalOperation());
        this.setNextOperation(renameOperation.getRenamedOperation());
        this.setOriginalPath(this.getOriginalClass().toString());
        this.setNextPath(this.getNextClass().toString());
        this.setOriginalElement(this.getOriginalOperation().toString());
        this.setNextElement(this.getNextOperation().toString());
        this.setCategory(Category.METHOD_RENAME);
        this.setBreakingChange(true);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(this.getNextOperation()));
        this.setDeprecated(isDeprecated(this.getNextOperation()));
        this.setRevCommit(revCommit);
        if(this.getNextOperation().isConstructor()){
            this.setElementType(ElementType.CONSTRUCTOR);
        }else{
            this.setElementType(ElementType.METHOD);
        }

    }

    private String isDescription(){
        String message = "";
        message += "<br>extract subType <code>" + this.getNextElement() + "</code>";
        message += "<br>from <code>" + this.getOriginalElement() + "</code>";
        message += "<br>";
        return message;
    }
}

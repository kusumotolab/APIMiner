package apiminer.internal.analysis.category.field;

import apiminer.enums.Category;
import apiminer.internal.analysis.category.FieldChange;
import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.diff.MoveAttributeRefactoring;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.Refactoring;

import java.util.Map;

public class MoveFieldChange extends FieldChange {

    public MoveFieldChange(Refactoring refactoring, Map<String, UMLClass> parentClassMap, Map<String,UMLClass> currentClassMap, RevCommit revCommit){
        super(revCommit);
        MoveAttributeRefactoring moveAttribute = (MoveAttributeRefactoring) refactoring;
        this.setOriginalClass(parentClassMap.get(moveAttribute.getSourceClassName()));
        this.setNextClass(currentClassMap.get(moveAttribute.getTargetClassName()));
        this.setOriginalAttribute(moveAttribute.getOriginalAttribute());
        this.setNextAttribute(moveAttribute.getMovedAttribute());
        this.setOriginalPath(UtilTools.getTypeDescriptionName(this.getOriginalClass()));
        this.setNextPath(UtilTools.getTypeDescriptionName(this.getNextClass()));
        this.setOriginalElement(UtilTools.getFieldDescriptionName(this.getOriginalAttribute()));
        this.setNextElement(UtilTools.getFieldDescriptionName(this.getNextAttribute()));
        this.setCategory(Category.FIELD_MOVE);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(this.getNextAttribute()));
        this.setDeprecated(checkDeprecated(this.getNextClass(),this.getNextAttribute()));
        this.setBreakingChange(this.checkDeprecated(this.getOriginalClass(), this.getOriginalAttribute()) ? false : true);
        this.setRevCommit(revCommit);
    }
    private String isDescription() {
        String message = "";
        message += "<br>field <code>" + this.getOriginalElement() +"</code>";
        message += "<br>moved from <code>" + this.getOriginalPath() +"</code>";
        message += "<br>to <code>" + this.getNextPath() +"</code>";
        message += "<br>";
        return message;
    }
}

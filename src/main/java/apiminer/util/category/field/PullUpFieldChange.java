package apiminer.util.category.field;

import apiminer.enums.Category;
import apiminer.util.category.FieldChange;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.diff.MoveAttributeRefactoring;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.Refactoring;

import java.util.Map;

public class PullUpFieldChange extends FieldChange {

    public PullUpFieldChange(Refactoring refactoring, Map<String, UMLClass> parentClassMap, Map<String, UMLClass> currentClassMap, RevCommit revCommit) {
        super(revCommit);
        MoveAttributeRefactoring moveAttribute = (MoveAttributeRefactoring) refactoring;
        this.setOriginalClass(parentClassMap.get(moveAttribute.getSourceClassName()));
        this.setNextClass(currentClassMap.get(moveAttribute.getTargetClassName()));
        this.setOriginalAttribute(moveAttribute.getOriginalAttribute());
        this.setNextAttribute(moveAttribute.getMovedAttribute());
        this.setOriginalPath(this.getOriginalClass().toString());
        this.setNextPath(this.getNextClass().toString());
        this.setOriginalElement(this.getOriginalAttribute().toString());
        this.setNextElement(this.getNextAttribute().toString());
        this.setCategory(Category.FIELD_PULL_UP);
        this.setBreakingChange(true);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(this.getNextAttribute()));
        this.setDeprecated(isDeprecated(this.getNextAttribute()));
        this.setRevCommit(revCommit);
    }

    private String isDescription() {
        String message = "";
        message += "<br>pull up field <code>" + this.getNextElement() + "</code>";
        message += "<br>from <code>" + this.getOriginalPath() + "</code>";
        message += "<br>to <code>" + this.getNextPath() + "</code>";
        message += "<br>";
        return message;
    }
}

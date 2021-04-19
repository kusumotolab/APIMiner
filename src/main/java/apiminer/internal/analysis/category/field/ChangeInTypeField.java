package apiminer.internal.analysis.category.field;

import apiminer.enums.Category;
import apiminer.internal.analysis.category.FieldChange;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.diff.ChangeAttributeTypeRefactoring;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.Refactoring;

import java.util.Map;

public class ChangeInTypeField extends FieldChange {

    public ChangeInTypeField(Refactoring refactoring, Map<String, UMLClass> parentClassMap, Map<String, UMLClass> currentClassMap, RevCommit revCommit) {
        super(revCommit);
        ChangeAttributeTypeRefactoring changeAttributeType = (ChangeAttributeTypeRefactoring) refactoring;
        this.setOriginalClass(parentClassMap.get(changeAttributeType.getClassNameBefore()));
        this.setNextClass(currentClassMap.get(changeAttributeType.getClassNameAfter()));
        this.setOriginalAttribute(changeAttributeType.getOriginalAttribute());
        this.setNextAttribute(changeAttributeType.getChangedTypeAttribute());
        this.setOriginalPath(this.getOriginalClass().toString());
        this.setNextPath(this.getNextClass().toString());
        this.setOriginalElement(this.getOriginalAttribute().toString());
        this.setNextElement(this.getNextAttribute().toString());
        this.setCategory(Category.FIELD_CHANGE_TYPE);
        this.setBreakingChange(true);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(this.getNextAttribute()));
        this.setDeprecated(isDeprecated(this.getNextAttribute()));
        this.setRevCommit(revCommit);
    }

    private String isDescription() {
        String message = "";
        message += "<br>field <code>" + this.getOriginalElement() + "</code>";
        message += "<br>changed field type to <code>" + this.getNextElement() + "</code>";
        message += "<br>in <code>" + this.getNextPath() + "</code>";
        message += "<br>";
        return message;
    }
}

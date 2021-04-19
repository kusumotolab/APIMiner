package apiminer.internal.analysis.category.field;

import apiminer.enums.Category;
import apiminer.internal.analysis.category.FieldChange;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.diff.ExtractAttributeRefactoring;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.Refactoring;

import java.util.Map;

public class ExtractFieldChange extends FieldChange {

    public ExtractFieldChange(Refactoring refactoring, Map<String, UMLClass> parentClassMap, Map<String, UMLClass> currentClassMap, RevCommit revCommit) {
        super(revCommit);
        ExtractAttributeRefactoring extractAttribute = (ExtractAttributeRefactoring) refactoring;
        this.setNextClass(extractAttribute.getNextClass());
        this.setNextAttribute(extractAttribute.getVariableDeclaration());
        this.setOriginalPath(extractAttribute.getOriginalClass().toString());
        this.setNextPath(this.getNextClass().toString());
        this.setOriginalElement(extractAttribute.getOriginalClass().toString());
        this.setNextElement(this.getNextAttribute().toString());
        this.setCategory(Category.FIELD_EXTRACT);
        this.setBreakingChange(true);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(this.getNextAttribute()));
        this.setDeprecated(isDeprecated(this.getNextAttribute()));
        this.setRevCommit(revCommit);
    }

    private String isDescription() {
        String message = "";
        message += "<br>field <code>" + this.getNextElement() + "</code>";
        message += "<br>extracted in <code>" + this.getNextPath() + "</code>";
        message += "<br>";
        return message;
    }
}

package apiminer.internal.analysis.category.field;

import apiminer.enums.Category;
import apiminer.internal.analysis.category.FieldChange;
import apiminer.internal.util.UtilTools;
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
        this.setOriginalPath(UtilTools.getTypeDescriptionName(extractAttribute.getOriginalClass()));
        this.setNextPath(UtilTools.getTypeDescriptionName(this.getNextClass()));
        this.setOriginalElement(UtilTools.getTypeDescriptionName(extractAttribute.getOriginalClass()));
        this.setNextElement(UtilTools.getFieldDescriptionName(this.getNextAttribute()));
        this.setCategory(Category.FIELD_EXTRACT);
        this.setBreakingChange(false);
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

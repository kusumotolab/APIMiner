package apiminer.internal.analysis.category.field;

import apiminer.enums.Category;
import apiminer.internal.analysis.category.FieldChange;
import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.diff.RenameAttributeRefactoring;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.Refactoring;

import java.util.Map;

public class RenameFieldChange extends FieldChange {

    public RenameFieldChange(Refactoring refactoring, Map<String, UMLClass> parentClassMap, Map<String, UMLClass> currentClassMap, RevCommit revCommit) {
        super(revCommit);
        RenameAttributeRefactoring renameAttribute = (RenameAttributeRefactoring) refactoring;
        this.setOriginalClass(parentClassMap.get(renameAttribute.getClassNameBefore()));
        this.setNextClass(currentClassMap.get(renameAttribute.getClassNameAfter()));
        this.setOriginalAttribute(renameAttribute.getOriginalAttribute());
        this.setNextAttribute(renameAttribute.getRenamedAttribute());
        this.setOriginalPath(UtilTools.getTypeDescriptionName(this.getOriginalClass()));
        this.setNextPath(UtilTools.getTypeDescriptionName(this.getNextClass()));
        this.setOriginalElement(UtilTools.getFieldDescriptionName(this.getOriginalAttribute()));
        this.setNextElement(UtilTools.getFieldDescriptionName(this.getNextAttribute()));
        this.setCategory(Category.FIELD_RENAME);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(this.getNextAttribute()));
        this.setDeprecated(checkDeprecated(this.getNextClass(),this.getNextAttribute()));
        this.setBreakingChange(true);
        this.setRevCommit(revCommit);
    }

    private String isDescription() {
        String message = "";
        message += "<br>field <code>" + this.getOriginalElement() + "</code>";
        message += "<br>renamed to <code>" + this.getNextElement() + "</code>";
        message += "<br>in <code>" + this.getNextPath() + "</code>";
        message += "<br>";
        return message;
    }

}

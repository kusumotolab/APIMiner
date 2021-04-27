package apiminer.internal.analysis.category.field;

import apiminer.enums.Category;
import apiminer.internal.analysis.category.FieldChange;
import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.diff.ChangeAttributeTypeRefactoring;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.Refactoring;

import java.util.Map;

public class ChangeInTypeField extends FieldChange {

    public ChangeInTypeField(Refactoring refactoring, Map<String, UMLClass> parentClassMap, Map<String, UMLClass> currentClassMap, RevCommit revCommit) throws Exception {
        super(revCommit);
        ChangeAttributeTypeRefactoring changeAttributeType = (ChangeAttributeTypeRefactoring) refactoring;
        if(parentClassMap.get(changeAttributeType.getClassNameBefore())==null||currentClassMap.get(changeAttributeType.getClassNameAfter())==null){
            throw new Exception();
        }
        this.setOriginalClass(parentClassMap.get(changeAttributeType.getClassNameBefore()));
        this.setNextClass(currentClassMap.get(changeAttributeType.getClassNameAfter()));
        this.setOriginalAttribute(changeAttributeType.getOriginalAttribute());
        this.setNextAttribute(changeAttributeType.getChangedTypeAttribute());
        this.setOriginalPath(UtilTools.getTypeDescriptionName(this.getOriginalClass()));
        this.setNextPath(UtilTools.getTypeDescriptionName(this.getNextClass()));
        this.setOriginalElement(UtilTools.getFieldDescriptionName(this.getOriginalAttribute()));
        this.setNextElement(UtilTools.getFieldDescriptionName(this.getNextAttribute()));
        this.setCategory(Category.FIELD_CHANGE_TYPE);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(this.getNextAttribute()));
        this.setDeprecated(this.checkDeprecated(this.getNextClass(),this.getNextAttribute()));
        this.setBreakingChange(this.isDeprecated()?false:true);
        this.setRevCommit(revCommit);
    }

    private String isDescription() {
        String message = "";
        message += "<br>field <code>" + this.getNextElement() + "</code>";
        message += "<br>changed field type";
        message += "<br>in <code>" + this.getNextPath() + "</code>";
        message += "<br>";
        return message;
    }
}

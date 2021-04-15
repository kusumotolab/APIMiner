package extension.Diff;

import apiminer.enums.Category;
import apiminer.util.category.ClassChange;
import apiminer.util.category.FieldChange;
import apiminer.util.category.field.AddFieldChange;
import apiminer.util.category.field.RemoveFieldChange;
import apiminer.util.category.type.AddTypeChange;
import apiminer.util.category.type.RemoveTypeChange;
import extension.Model.CommonClass;
import extension.RefactoringElement;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLOperation;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttributeDiff {
    private UMLClass originalClass;
    private UMLAttribute originalAttribute;
    private UMLClass nextClass;
    private UMLAttribute nextAttribute;
    private List<FieldChange> fieldChangeList = new ArrayList<FieldChange>();
    private RevCommit revCommit;

    public AttributeDiff(Category category, UMLClass umlClass, UMLAttribute umlAttribute, RevCommit revCommit){
        this.revCommit = revCommit;
        if(category.equals(Category.FIELD_REMOVE)){
            this.originalClass = umlClass;
            this.originalAttribute = umlAttribute;
            this.nextClass = null;
            this.nextAttribute = null;
            fieldChangeList.add(new RemoveFieldChange(originalClass,originalAttribute,revCommit));
        }else if(category.equals(Category.FIELD_ADD)) {
            this.originalClass = null;
            this.originalAttribute = null;
            this.nextClass = umlClass;
            this.nextAttribute = umlAttribute;
            fieldChangeList.add(new AddFieldChange(nextClass, nextAttribute,revCommit));
        }
    }
    public AttributeDiff(RefactoringElement refactoringElement,RevCommit revCommit) {
        this.revCommit = revCommit;
        switch (refactoringElement.getRefactoring().getRefactoringType()) {

        }
        fieldChangeList.addAll(detectOtherChange(refactoringElement.getOriginalClass(),refactoringElement.getOriginalAttribute(),refactoringElement.getNextClass(), refactoringElement.getNextAttribute()));
    }

    public AttributeDiff(CommonClass commonClass, Map.Entry<UMLAttribute,UMLAttribute> entry,RevCommit revCommit){
        this.revCommit = revCommit;
        fieldChangeList.addAll(detectOtherChange(commonClass.getOriginalClass(),entry.getKey(),commonClass.getNextClass(), entry.getValue()));
    }

    private List<FieldChange> detectOtherChange(UMLClass originalClass, UMLAttribute originalAttribute,UMLClass nextClass, UMLAttribute nextAttribute){
        List<FieldChange> fieldChangeList = new ArrayList<FieldChange>();

        return fieldChangeList;
    }
}

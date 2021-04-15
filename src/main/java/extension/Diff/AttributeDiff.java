package extension.Diff;

import apiminer.enums.Category;
import apiminer.util.category.FieldChange;
import apiminer.util.category.field.*;
import extension.Model.CommonAttribute;
import extension.Model.CommonClass;
import extension.RefactoringElement;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.List;

public class AttributeDiff {
    private UMLClass originalClass;
    private UMLAttribute originalAttribute;
    private UMLClass nextClass;
    private UMLAttribute nextAttribute;
    private final List<FieldChange> fieldChangeList = new ArrayList<FieldChange>();
    private final RevCommit revCommit;

    public AttributeDiff(Category category, UMLClass umlClass, UMLAttribute umlAttribute, RevCommit revCommit) {
        this.revCommit = revCommit;
        if (category.equals(Category.FIELD_REMOVE)) {
            this.originalClass = umlClass;
            this.originalAttribute = umlAttribute;
            this.nextClass = null;
            this.nextAttribute = null;
            fieldChangeList.add(new RemoveFieldChange(originalClass, originalAttribute, revCommit));
        } else if (category.equals(Category.FIELD_ADD)) {
            this.originalClass = null;
            this.originalAttribute = null;
            this.nextClass = umlClass;
            this.nextAttribute = umlAttribute;
            fieldChangeList.add(new AddFieldChange(nextClass, nextAttribute, revCommit));
        }
    }

    public AttributeDiff(RefactoringElement refactoringElement, RevCommit revCommit) {
        this.revCommit = revCommit;
        switch (refactoringElement.getRefactoring().getRefactoringType()) {
            case EXTRACT_ATTRIBUTE:
                fieldChangeList.add(new ExtractFieldChange(refactoringElement, revCommit));
                break;
            case MOVE_ATTRIBUTE:
                fieldChangeList.add(new MoveFieldChange(refactoringElement, revCommit));
                fieldChangeList.addAll(detectOtherChange(refactoringElement.getOriginalClass(), refactoringElement.getOriginalAttribute(), refactoringElement.getNextClass(), refactoringElement.getOriginalAttribute()));
                break;
            case PULL_UP_ATTRIBUTE:
                fieldChangeList.add(new PullUpFieldChange(refactoringElement, revCommit));
                fieldChangeList.addAll(detectOtherChange(refactoringElement.getOriginalClass(), refactoringElement.getOriginalAttribute(), refactoringElement.getNextClass(), refactoringElement.getOriginalAttribute()));
                break;
            case PUSH_DOWN_ATTRIBUTE:
                fieldChangeList.add(new PushDownFieldChange(refactoringElement, revCommit));
                fieldChangeList.addAll(detectOtherChange(refactoringElement.getOriginalClass(), refactoringElement.getOriginalAttribute(), refactoringElement.getNextClass(), refactoringElement.getOriginalAttribute()));
                break;
            case MOVE_RENAME_ATTRIBUTE:
                fieldChangeList.add(new MoveAndRenameFieldChange(refactoringElement, revCommit));
                fieldChangeList.addAll(detectOtherChange(refactoringElement.getOriginalClass(), refactoringElement.getOriginalAttribute(), refactoringElement.getNextClass(), refactoringElement.getOriginalAttribute()));
                break;
            case RENAME_ATTRIBUTE:
                fieldChangeList.add(new RenameFieldChange(refactoringElement, revCommit));
                fieldChangeList.addAll(detectOtherChange(refactoringElement.getOriginalClass(), refactoringElement.getOriginalAttribute(), refactoringElement.getNextClass(), refactoringElement.getOriginalAttribute()));
                break;
            case CHANGE_ATTRIBUTE_TYPE:
                fieldChangeList.add(new ChangeInTypeField(refactoringElement, revCommit));
                fieldChangeList.addAll(detectOtherChange(refactoringElement.getOriginalClass(), refactoringElement.getOriginalAttribute(), refactoringElement.getNextClass(), refactoringElement.getOriginalAttribute()));
                break;
        }
    }

    public AttributeDiff(CommonClass commonClass, CommonAttribute commonAttribute, RevCommit revCommit) {
        this.revCommit = revCommit;
        fieldChangeList.addAll(detectOtherChange(commonClass.getOriginalClass(), commonAttribute.getOriginalAttribute(), commonClass.getNextClass(), commonAttribute.getNextAttribute()));
    }

    private List<FieldChange> detectOtherChange(UMLClass originalClass, UMLAttribute originalAttribute, UMLClass nextClass, UMLAttribute nextAttribute) {
        List<FieldChange> fieldChangeList = new ArrayList<FieldChange>();

        return fieldChangeList;
    }

    public List<FieldChange> getFieldChangeList() {
        return fieldChangeList;
    }
}

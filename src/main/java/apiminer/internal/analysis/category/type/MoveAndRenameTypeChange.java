package apiminer.internal.analysis.category.type;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.internal.analysis.category.TypeChange;
import gr.uom.java.xmi.diff.MoveAndRenameClassRefactoring;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.Refactoring;

public class MoveAndRenameTypeChange extends TypeChange {

    public MoveAndRenameTypeChange(Refactoring refactoring, RevCommit revCommit) {
        super(revCommit);
        MoveAndRenameClassRefactoring moveAndRenameClass = (MoveAndRenameClassRefactoring) refactoring;
        this.setOriginalClass(moveAndRenameClass.getOriginalClass());
        this.setNextClass(moveAndRenameClass.getRenamedClass());
        this.setOriginalPath(this.getOriginalClass().toString());
        this.setNextPath(this.getNextClass().toString());
        this.setOriginalElement(this.getOriginalClass().toString());
        this.setNextElement(this.getNextClass().toString());
        this.setCategory(Category.TYPE_MOVE_AND_RENAME);
        this.setBreakingChange(true);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(this.getNextClass()));
        this.setDeprecated(isDeprecated(this.getNextClass()));
        this.setRevCommit(revCommit);
        if (this.getNextClass().isInterface()) {
            this.setElementType(ElementType.INTERFACE);
        } else if (this.getNextClass().isEnum()) {
            this.setElementType(ElementType.ENUM);
        } else {
            this.setElementType(ElementType.CLASS);
        }
    }

    private String isDescription() {
        String message = "";
        message += "<br>type <code>" + this.getOriginalElement() + "</code>";
        message += "<br>moved and renamed to";
        message += "<br><code>" + this.getNextElement() + "</code>";
        message += "<br>";
        return message;
    }
}

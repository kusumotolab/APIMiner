package apiminer.internal.analysis.category.type;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.internal.analysis.category.TypeChange;
import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.diff.RenameClassRefactoring;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.Refactoring;

public class RenameTypeChange extends TypeChange {

    public RenameTypeChange(Refactoring refactoring, RevCommit revCommit) {
        super(revCommit);
        RenameClassRefactoring renameClass = (RenameClassRefactoring) refactoring;
        this.setOriginalClass(renameClass.getOriginalClass());
        this.setNextClass(renameClass.getRenamedClass());
        this.setOriginalPath(UtilTools.getTypeDescriptionName(this.getOriginalClass()));
        this.setNextPath(UtilTools.getTypeDescriptionName(this.getNextClass()));
        this.setOriginalElement(UtilTools.getTypeDescriptionName(this.getOriginalClass()));
        this.setNextElement(UtilTools.getTypeDescriptionName(this.getNextClass()));
        this.setCategory(Category.TYPE_RENAME);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(this.getNextClass()));
        this.setDeprecated(checkDeprecated(this.getNextClass()));
        this.setBreakingChange(true);
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
        message += "<br>renamed to";
        message += "<br><code>" + this.getNextElement() + "</code>";
        message += "<br>";
        return message;
    }
}

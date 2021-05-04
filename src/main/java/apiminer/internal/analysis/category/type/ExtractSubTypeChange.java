package apiminer.internal.analysis.category.type;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.internal.analysis.category.TypeChange;
import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.diff.ExtractClassRefactoring;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.Refactoring;

public class ExtractSubTypeChange extends TypeChange {

    public ExtractSubTypeChange(Refactoring refactoring, RevCommit revCommit) {
        super(revCommit);
        ExtractClassRefactoring extractClass = (ExtractClassRefactoring) refactoring;
        this.setNextClass(extractClass.getExtractedClass());
        this.setOriginalPath(UtilTools.getTypeDescriptionName(extractClass.getOriginalClass()));
        this.setNextPath(UtilTools.getTypeDescriptionName(this.getNextClass()));
        this.setOriginalElement(UtilTools.getTypeDescriptionName(extractClass.getOriginalClass()));
        this.setNextElement(UtilTools.getTypeDescriptionName(this.getNextClass()));
        this.setCategory(Category.TYPE_EXTRACT_SUBTYPE);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(this.getNextClass()));
        this.setDeprecated(checkDeprecated(this.getNextClass()));
        this.setBreakingChange(this.checkDeprecated(extractClass.getOriginalClass()) ? false : true);
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
        message += "<br>extract subType <code>" + this.getNextElement() + "</code>";
        message += "<br>from <code>" + this.getOriginalElement() + "</code>";
        message += "<br>";
        return message;
    }
}

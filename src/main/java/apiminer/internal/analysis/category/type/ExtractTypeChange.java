package apiminer.internal.analysis.category.type;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.internal.analysis.category.TypeChange;
import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.diff.ExtractClassRefactoring;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.Refactoring;

public class ExtractTypeChange extends TypeChange {

    public ExtractTypeChange(Refactoring refactoring, RevCommit revCommit) {
        super(revCommit);
        ExtractClassRefactoring extractClass = (ExtractClassRefactoring) refactoring;
        this.setNextClass(extractClass.getExtractedClass());
        this.setOriginalPath(UtilTools.getTypeDescriptionName(extractClass.getOriginalClass()));
        this.setNextPath(UtilTools.getTypeDescriptionName(extractClass.getExtractedClass()));
        this.setOriginalElement(UtilTools.getTypeDescriptionName(extractClass.getOriginalClass()));
        this.setNextElement(UtilTools.getTypeDescriptionName(extractClass.getExtractedClass()));
        this.setCategory(Category.TYPE_EXTRACT_TYPE);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(this.getNextClass()));
        this.setDeprecated(checkDeprecated(this.getNextClass()));
        this.setBreakingChange(this.isDeprecated()?false:true);
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
        message += "<br>extract type <code>" + this.getNextElement() + "</code>";
        message += "<br>from <code>" + this.getOriginalElement() + "</code>";
        message += "<br>";
        return message;
    }
}

package apiminer.internal.analysis.category.type;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.internal.analysis.category.TypeChange;
import gr.uom.java.xmi.diff.ExtractSuperclassRefactoring;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.Refactoring;

public class ExtractSuperTypeChange extends TypeChange {

    public ExtractSuperTypeChange(Refactoring refactoring, RevCommit revCommit) {
        super(revCommit);
        ExtractSuperclassRefactoring extractSuperclass = (ExtractSuperclassRefactoring) refactoring;
        this.setNextClass(extractSuperclass.getExtractedClass());
        String originalClassArray = extractSuperclass.getSubclassSet().toString();
        String originalClassPath = originalClassArray.substring(1, originalClassArray.length() - 1);
        this.setOriginalPath(originalClassPath);
        this.setNextPath(this.getNextClass().toString());
        this.setOriginalElement(originalClassPath);
        this.setNextElement(this.getNextClass().toString());
        this.setCategory(Category.TYPE_EXTRACT_SUPERTYPE);
        this.setBreakingChange(true);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(extractSuperclass.getExtractedClass()));
        this.setDeprecated(isDeprecated(extractSuperclass.getExtractedClass()));
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
        message += "<br>extract superType <code>" + this.getNextElement() + "</code>";
        message += "<br>from <code>" + this.getOriginalElement() + "</code>";
        message += "<br>";
        return message;
    }
}

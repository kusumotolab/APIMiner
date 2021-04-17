package apiminer.util.category.type;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.util.category.ClassChange;
import extension.RefactoringElement;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.diff.ExtractClassRefactoring;
import gr.uom.java.xmi.diff.ExtractSuperclassRefactoring;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.Refactoring;

public class ExtractSubTypeChange extends ClassChange {
    private ExtractClassRefactoring extractClass;

    public ExtractSubTypeChange(Refactoring refactoring, RevCommit revCommit){
        super(revCommit);
        this.extractClass = (ExtractClassRefactoring) refactoring;
        this.setNextClass(extractClass.getExtractedClass());
        this.setOriginalPath(extractClass.getOriginalClass().toString());
        this.setNextPath(this.getNextClass().toString());
        this.setOriginalElement(extractClass.getOriginalClass().toString());
        this.setNextElement(this.getNextClass().toString());
        this.setCategory(Category.TYPE_EXTRACT_SUBTYPE);
        this.setBreakingChange(true);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(this.getNextClass()));
        this.setDeprecated(isDeprecated(this.getNextClass()));
        this.setRevCommit(revCommit);
        if(this.getNextClass().isInterface()){
            this.setElementType(ElementType.INTERFACE);
        }else if(this.getNextClass().isEnum()){
            this.setElementType(ElementType.ENUM);
        }else{
            this.setElementType(ElementType.CLASS);
        }
    }

    private String isDescription(){
        String message = "";
        message += "<br>extract subType <code>" + this.getNextElement() + "</code>";
        message += "<br>from <code>" + this.getOriginalElement() + "</code>";
        message += "<br>";
        return message;
    }
}

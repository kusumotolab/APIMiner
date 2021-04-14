package apiminer.util.category.type;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.util.category.ClassChange;
import extension.RefactoringElement;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

public class ExtractSubTypeChange extends ClassChange {
    private UMLClass extractedClass;
    private UMLClass originalClass;

    public ExtractSubTypeChange(RefactoringElement refactoringElement, RevCommit revCommit){
        super(revCommit);
        this.originalClass = refactoringElement.getOriginalClass();
        this.extractedClass = refactoringElement.getNextClass();
        this.setOriginalPath(originalClass.getSourceFile());
        this.setNextPath(extractedClass.getSourceFile());
        this.setOriginalElement(originalClass.getName());
        this.setNextElement(extractedClass.getName());
        this.setCategory(Category.TYPE_EXTRACT_SUBTYPE);
        this.setBreakingChange(true);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(extractedClass));
        this.setDeprecated(isDeprecated(extractedClass));
        this.setRevCommit(revCommit);
        if(extractedClass.isInterface()){
            this.setElementType(ElementType.INTERFACE);
        }else if(extractedClass.isEnum()){
            this.setElementType(ElementType.ENUM);
        }else{
            this.setElementType(ElementType.CLASS);
        }
    }

    private String isDescription(){
        String message = "";
        message += "<br>extract subType <code>" + extractedClass.getName() + "</code>";
        message += "<br>from <code>" + originalClass.getName() + "</code>";
        message += "<br>";
        return message;
    }
}

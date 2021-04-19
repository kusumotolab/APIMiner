package apiminer.internal.analysis.category.type;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.internal.analysis.category.ClassChange;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

public class AddTypeChange extends ClassChange {
    private UMLClass addedClass;
    public AddTypeChange(UMLClass addedClass, RevCommit revCommit){
        super(revCommit);
        this.addedClass = addedClass;
        this.setOriginalPath("");
        this.setNextPath(addedClass.getSourceFile());
        this.setOriginalElement("");
        this.setNextElement(addedClass.getName());
        this.setCategory(Category.TYPE_ADD);
        this.setBreakingChange(false);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(addedClass));
        this.setDeprecated(isDeprecated(addedClass));
        this.setRevCommit(revCommit);
        if(addedClass.isInterface()){
            this.setElementType(ElementType.INTERFACE);
        }else if(addedClass.isEnum()){
            this.setElementType(ElementType.ENUM);
        }else{
            this.setElementType(ElementType.CLASS);
        }
    }

    private String isDescription(){
        String message = "";
        message += "<br>type <code>" + addedClass.getName() + "</code> added";
        message += "<br>";
        return message;
    }
}

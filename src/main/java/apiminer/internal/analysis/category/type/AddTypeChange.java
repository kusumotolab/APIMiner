package apiminer.internal.analysis.category.type;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.internal.analysis.category.TypeChange;
import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

public class AddTypeChange extends TypeChange {
    public AddTypeChange(UMLClass addedClass, RevCommit revCommit){
        super(revCommit);
        this.setOriginalClass(addedClass);
        this.setOriginalPath("");
        this.setNextPath(UtilTools.getTypeDescriptionName(addedClass));
        this.setOriginalElement("");
        this.setNextElement(UtilTools.getTypeDescriptionName(addedClass));
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
        message += "<br>type <code>" + this.getNextPath() + "</code> added";
        message += "<br>";
        return message;
    }
}

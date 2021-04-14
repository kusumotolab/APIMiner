package extension.Change;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import gr.uom.java.xmi.UMLClass;
import org.eclipse.jgit.revwalk.RevCommit;

public class RemoveTypeChange extends ClassChange{
    private UMLClass removedClass;
    public RemoveTypeChange(UMLClass removedClass,RevCommit revCommit){
        super(revCommit);
        this.removedClass = removedClass;
        this.setOriginalPath(removedClass.getSourceFile());
        this.setNextPath("");
        this.setOriginalElement(removedClass.getName());
        this.setNextElement("");
        this.setCategory(Category.TYPE_REMOVE);
        this.setBreakingChange(true);
        this.setDescription(isDescription(removedClass));
        //this.setJavadoc();
        //this.setDeprecated();
        //this.setRevCommit(rev);
        if(removedClass.isInterface()){
            this.setElementType(ElementType.INTERFACE);
        }else if(removedClass.isEnum()){
            this.setElementType(ElementType.ENUM);
        }else{
            this.setElementType(ElementType.CLASS);
        }
    }

    private String isDescription(UMLClass umlClass){
        String message = "";
        message += "<br>type <code>" + umlClass.getName() + "</code> ";
        message += "<br>was removed";
        message += "<br>";
        return message;
    }

}

package apiminer.internal.analysis.category.method;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.internal.analysis.category.MethodChange;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLOperation;
import org.eclipse.jgit.revwalk.RevCommit;

public class RemoveMethodChange extends MethodChange {

    public RemoveMethodChange(UMLClass umlClass, UMLOperation removedOperation, RevCommit revCommit){
        super(revCommit);
        this.setOriginalClass(umlClass);
        this.setOriginalOperation(removedOperation);
        this.setOriginalPath(this.getOriginalClass().toString());
        this.setNextPath("");
        this.setOriginalElement(this.getOriginalOperation().toString());
        this.setNextElement("");
        this.setCategory(Category.METHOD_REMOVE);
        this.setBreakingChange(false);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(this.getOriginalOperation()));
        this.setDeprecated(isDeprecated(this.getOriginalOperation()));
        this.setRevCommit(revCommit);
        if(this.getOriginalOperation().isConstructor()){
            this.setElementType(ElementType.CONSTRUCTOR);
        }else{
            this.setElementType(ElementType.METHOD);
        }
    }

    private String isDescription(){
        String message = "";
        message += "<br>method <code>" + this.getOriginalElement() +"</code>";
        message += "<br>removed from <code>" + this.getOriginalPath() + "</code>";
        message += "<br>";
        return message;
    }
}

package apiminer.internal.analysis.category.method;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.internal.analysis.category.MethodChange;
import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLOperation;
import org.eclipse.jgit.revwalk.RevCommit;

public class RemoveMethodChange extends MethodChange {

    public RemoveMethodChange(UMLClass umlClass, UMLOperation removedOperation, RevCommit revCommit){
        super(revCommit);
        this.setOriginalClass(umlClass);
        this.setOriginalOperation(removedOperation);
        this.setOriginalPath(UtilTools.getTypeDescriptionName(this.getOriginalClass()));
        this.setNextPath("");
        this.setOriginalElement(UtilTools.getMethodDescriptionName(this.getOriginalOperation()));
        this.setNextElement("");
        this.setCategory(Category.METHOD_REMOVE);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(this.getOriginalOperation()));
        this.setDeprecated(checkDeprecated(this.getOriginalClass(),this.getOriginalOperation()));
        this.setBreakingChange(this.checkDeprecated(this.getOriginalClass(), this.getOriginalOperation()) ? false : true);
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

package apiminer.internal.analysis.category.method;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.internal.analysis.category.MethodChange;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLOperation;
import org.eclipse.jgit.revwalk.RevCommit;

public class AddMethodChange extends MethodChange {
    private UMLClass umlClass;
    private UMLOperation addedOperation;

    public AddMethodChange(UMLClass umlClass, UMLOperation addedOperation, RevCommit revCommit){
        super(revCommit);
        this.umlClass = umlClass;
        this.addedOperation = addedOperation;
        this.setOriginalPath("");
        this.setNextPath(umlClass.toString());
        this.setOriginalElement("");
        this.setNextElement(addedOperation.toString());
        this.setCategory(Category.METHOD_ADD);
        this.setBreakingChange(false);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(addedOperation));
        this.setDeprecated(isDeprecated(addedOperation));
        this.setRevCommit(revCommit);
        if(addedOperation.isConstructor()){
            this.setElementType(ElementType.CONSTRUCTOR);
        }else{
            this.setElementType(ElementType.METHOD);
        }
    }

    private String isDescription(){
        String message = "";
        message += "<br>method <code>" + this.getNextElement() +"</code>";
        message += "<br>added in <code>" + this.getNextPath() +"</code>";
        message += "<br>";
        return message;
    }
}

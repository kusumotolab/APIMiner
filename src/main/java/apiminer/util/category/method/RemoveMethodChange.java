package apiminer.util.category.method;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.util.category.MethodChange;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLOperation;
import org.eclipse.jgit.revwalk.RevCommit;

public class RemoveMethodChange extends MethodChange {
    private UMLClass umlClass;
    private UMLOperation addedOperation;

    public RemoveMethodChange(UMLClass umlClass, UMLOperation addedOperation, RevCommit revCommit){
        super(revCommit);
        this.umlClass = umlClass;
        this.addedOperation = addedOperation;
        this.setOriginalPath("");
        this.setNextPath(umlClass.getSourceFile());
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
        message += "<br>method <code>" + addedOperation.toString() +"</code>";
        message += "<br>added in <code>" + umlClass.getName() +"</code>";
        message += "<br>";
        return message;
    }
}

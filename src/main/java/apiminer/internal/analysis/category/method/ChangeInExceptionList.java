package apiminer.internal.analysis.category.method;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.internal.analysis.category.MethodChange;
import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLOperation;
import org.eclipse.jgit.revwalk.RevCommit;

public class ChangeInExceptionList extends MethodChange {
    public ChangeInExceptionList(UMLClass originalClass, UMLOperation originalOperation, UMLClass nextClass, UMLOperation nextOperation, RevCommit revCommit) {
        super(revCommit);
        this.setOriginalClass(originalClass);
        this.setNextClass(nextClass);
        this.setOriginalOperation(originalOperation);
        this.setNextOperation(nextOperation);
        this.setOriginalPath(UtilTools.getTypeDescriptionName(this.getOriginalClass()));
        this.setNextPath(UtilTools.getTypeDescriptionName(this.getNextClass()));
        this.setOriginalElement(UtilTools.getMethodDescriptionName(this.getOriginalOperation()));
        this.setNextElement(UtilTools.getMethodDescriptionName(this.getNextOperation()));
        this.setCategory(Category.METHOD_CHANGE_EXCEPTION_LIST);
        this.setBreakingChange(true);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(this.getNextOperation()));
        this.setDeprecated(isDeprecated(this.getNextOperation()));
        this.setRevCommit(revCommit);
        if (this.getNextOperation().isConstructor()) {
            this.setElementType(ElementType.CONSTRUCTOR);
        } else {
            this.setElementType(ElementType.METHOD);
        }

    }

    private String isDescription() {
        String message = "";
        message += "<br>method <code>" + this.getNextElement() + "</code>";
        if (this.getOriginalOperation().getThrownExceptionTypes().size() > 0 && this.getNextOperation().getThrownExceptionTypes().size() > 0) {
            String listOriginal = this.getOriginalOperation().getThrownExceptionTypes().toString();
            String listNext = this.getNextOperation().getThrownExceptionTypes().toString();
            message += "<br>changed the list exception";
            message += "<br>from <code>" + listOriginal.substring(1, listOriginal.length() - 1) + "</code>";
            message += "<br>to <code>" + listNext.substring(1, listNext.length() - 1) + "</code>";
        }

        if (this.getOriginalOperation().getThrownExceptionTypes().size() == 0 && this.getNextOperation().getThrownExceptionTypes().size() > 0) {
            String listNext = this.getNextOperation().getThrownExceptionTypes().toString();
            message += "<br>added list exception <code>" + listNext.substring(1, listNext.length() - 1) + "</code>";
        }

        if (this.getOriginalOperation().getThrownExceptionTypes().size() > 0 && this.getNextOperation().getThrownExceptionTypes().size() == 0) {
            String listOriginal = this.getOriginalOperation().getThrownExceptionTypes().toString();
            message += "<br>removed list exception <code>" + listOriginal.substring(1, listOriginal.length() - 1) + "</code>";
        }
        message += "<br>in <code>" + this.getNextPath() + "</code>";
        message += "<br>";
        return message;
    }
}

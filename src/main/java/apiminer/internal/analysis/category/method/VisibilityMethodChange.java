package apiminer.internal.analysis.category.method;

import apiminer.enums.Category;
import apiminer.internal.analysis.category.MethodChange;
import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLOperation;
import org.eclipse.jgit.revwalk.RevCommit;

public class VisibilityMethodChange extends MethodChange {
    public VisibilityMethodChange(UMLClass originalClass, UMLOperation originalOperation, UMLClass nextClass, UMLOperation nextOperation, Category category, RevCommit revCommit) {
        super(revCommit);
        this.setOriginalClass(originalClass);
        this.setNextClass(nextClass);
        this.setOriginalOperation(originalOperation);
        this.setNextOperation(nextOperation);
        this.setOriginalPath(UtilTools.getTypeDescriptionName(this.getOriginalClass()));
        this.setNextPath(UtilTools.getTypeDescriptionName(this.getNextClass()));
        this.setOriginalElement(UtilTools.getMethodDescriptionName(this.getOriginalOperation()));
        this.setNextElement(UtilTools.getMethodDescriptionName(this.getNextOperation()));
        this.setCategory(category);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(this.getNextOperation()));
        this.setDeprecated(checkDeprecated(this.getNextClass(),this.getNextOperation()));
        boolean isBreakingChange = category.equals(Category.METHOD_LOST_VISIBILITY);
        this.setBreakingChange(this.checkDeprecated(this.getOriginalClass(), this.getOriginalOperation()) ? false : isBreakingChange);
        this.setRevCommit(revCommit);
    }

    private String isDescription() {
        String message = "";
        message += "<br>method <code>" + this.getNextElement() +"</code>";
        message += "<br>changed visibility from <code>" + UtilTools.getVisibilityDescriptionName(this.getOriginalOperation().getVisibility())  + "</code>to <code>"  + UtilTools.getVisibilityDescriptionName(this.getNextOperation().getVisibility()) + "</code>";
        message += "<br>in <code>" + this.getNextPath() + "</code>";
        message += "<br>";
        return message;
    }
}

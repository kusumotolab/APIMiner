package apiminer.internal.analysis.category;

import apiminer.Change;
import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.*;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;

public class MethodChange extends Change {
    private UMLClass originalClass;
    private UMLClass nextClass;
    private UMLOperation originalOperation;
    private UMLOperation nextOperation;

    public MethodChange(RevCommit revCommit){
        super(revCommit);
    }

    public UMLClass getOriginalClass() {
        return originalClass;
    }

    protected void setOriginalClass(UMLClass originalClass) {
        this.originalClass = originalClass;
    }

    public UMLClass getNextClass() {
        return nextClass;
    }

    protected void setNextClass(UMLClass nextClass) {
        this.nextClass = nextClass;
    }

    public UMLOperation getOriginalOperation() {
        return originalOperation;
    }

    protected void setOriginalOperation(UMLOperation originalOperation) {
        this.originalOperation = originalOperation;
    }

    public UMLOperation getNextOperation() {
        return nextOperation;
    }

    protected void setNextOperation(UMLOperation nextOperation) {
        this.nextOperation = nextOperation;
    }

    protected boolean isJavaDoc(UMLOperation umlOperation){
        UMLJavadoc javaDoc = umlOperation.getJavadoc();
        if(javaDoc!=null&&!javaDoc.toString().equals("")){
            return true;
        }
        return false;
    }

    protected boolean checkDeprecated(UMLClass umlClass, UMLOperation umlOperation){
        return UtilTools.isDeprecated(umlClass.getAnnotations())||UtilTools.isDeprecated(umlOperation.getAnnotations());
    }
}

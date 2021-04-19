package apiminer.internal.analysis.category;

import apiminer.util.Change;
import gr.uom.java.xmi.UMLAnnotation;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLJavadoc;
import gr.uom.java.xmi.UMLOperation;
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

    protected boolean isDeprecated(UMLOperation umlOperation){
        List<UMLAnnotation> annotationList = umlOperation.getAnnotations();
        for(UMLAnnotation annotation:annotationList){
            if(annotation.toString().equals("@Deprecated")){
                return true;
            }
        }
        return false;
    }
}

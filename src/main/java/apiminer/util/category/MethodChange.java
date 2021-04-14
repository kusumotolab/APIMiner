package apiminer.util.category;

import apiminer.util.Change;
import gr.uom.java.xmi.UMLAnnotation;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLJavadoc;
import gr.uom.java.xmi.UMLOperation;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;

public class MethodChange extends Change {
    public MethodChange(RevCommit revCommit){
        super(revCommit);
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
            if(annotation.toString().equals("Deprecated")){
                return true;
            }
        }
        return false;
    }
}

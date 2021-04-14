package apiminer.util.category;

import apiminer.util.Change;
import gr.uom.java.xmi.UMLAnnotation;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLJavadoc;
import gr.uom.java.xmi.UMLOperation;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;

public class FieldChange extends Change {
    public FieldChange(RevCommit revCommit){
        super(revCommit);
    }

    protected boolean isJavaDoc(UMLAttribute umlAttribute){
        UMLJavadoc javaDoc = umlAttribute.getJavadoc();
        if(javaDoc!=null&&!javaDoc.toString().equals("")){
            return true;
        }
        return false;
    }

    protected boolean isDeprecated(UMLAttribute umlAttribute){
        List<UMLAnnotation> annotationList = umlAttribute.getAnnotations();
        for(UMLAnnotation annotation:annotationList){
            if(annotation.toString().equals("Deprecated")){
                return true;
            }
        }
        return false;
    }
}

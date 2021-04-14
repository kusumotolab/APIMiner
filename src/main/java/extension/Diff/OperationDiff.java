package extension.Diff;

import apiminer.util.category.ClassChange;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLOperation;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.List;

public class OperationDiff {
    private UMLClass originalClass;
    private UMLOperation originalOperation;
    private UMLClass nextClass;
    private UMLOperation nextOperation;
    private List<ClassChange> classChangeList = new ArrayList<ClassChange>();
    private RevCommit revCommit;




    public static boolean isLostVisibility(UMLOperation originalOperation, UMLOperation nextOperation){
        return false;
    }
    public static boolean isGainVisibility(UMLOperation originalOperation, UMLOperation nextOperation){
        return false;
    }
    public static boolean isAddFinalModifier(UMLOperation originalOperation, UMLOperation nextOperation){
        if(!originalOperation.isFinal()&&nextOperation.isFinal()){
            return true;
        }
        return false;
    }
    public static boolean isRemoveFinalModifier(UMLOperation originalOperation, UMLOperation nextOperation){
        if(originalOperation.isFinal()&&!nextOperation.isFinal()){
            return true;
        }
        return false;
    }
    public static boolean isRemoveStaticModifier(UMLOperation originalOperation, UMLOperation nextOperation){
        if(originalOperation.isStatic()&&!nextOperation.isStatic()){
            return true;
        }
        return false;
    }
    public static boolean isAddStaticModifier(UMLOperation originalOperation, UMLOperation nextOperation){
        if(!originalOperation.isStatic()&&nextOperation.isStatic()){
            return true;
        }
        return false;
    }
}

package extension.comparator;

import gr.uom.java.xmi.UMLOperation;

public class ComparatorMethod {
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

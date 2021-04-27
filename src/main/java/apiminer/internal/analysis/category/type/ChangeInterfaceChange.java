package apiminer.internal.analysis.category.type;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.internal.analysis.category.TypeChange;
import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLType;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;

public class ChangeInterfaceChange extends TypeChange {
    public ChangeInterfaceChange(UMLClass originalClass, UMLClass nextClass, List<UMLType> originalSuperClassList, List<UMLType> nextSuperClassList, RevCommit revCommit) {
        super(revCommit);
        this.setOriginalClass(originalClass);
        this.setNextClass(nextClass);
        this.setOriginalPath(UtilTools.getTypeDescriptionName(originalClass));
        this.setNextPath(UtilTools.getTypeDescriptionName(nextClass));
        String originalSuperClass = originalSuperClassList.toString();
        String nextSuperClass = nextSuperClassList.toString();
        this.setOriginalElement(originalSuperClass.substring(1,originalSuperClass.length()-1));
        this.setNextElement(nextSuperClass.substring(1,nextSuperClass.length()-1));
        this.setCategory(Category.TYPE_CHANGE_INTERFACE);
        this.setDescription(isDescription());
        this.setJavadoc(isJavaDoc(this.getNextClass()));
        this.setDeprecated(checkDeprecated(this.getNextClass()));
        this.setBreakingChange(this.isDeprecated()?false:true);
        this.setRevCommit(revCommit);
        if (this.getNextClass().isInterface()) {
            this.setElementType(ElementType.INTERFACE);
        } else if (this.getNextClass().isEnum()) {
            this.setElementType(ElementType.ENUM);
        } else {
            this.setElementType(ElementType.CLASS);
        }
    }

    private String isDescription() {
        String message = "";
        message += "<br>type <code>" + this.getNextPath() + "</code>";
        message += "<br>changed the interface";
        message += "<br>from <code>" + this.getOriginalElement() + "</code>";
        message += "<br>to <code>" + this.getNextElement() + "</code>";
        message += "<br>";
        return message;
    }
}
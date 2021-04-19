package apiminer.internal.analysis.category.method;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import apiminer.internal.analysis.category.MethodChange;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.diff.ExtractOperationRefactoring;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.Refactoring;

import java.util.Map;

public class ExtractMethodChange extends MethodChange {
    ExtractOperationRefactoring extractOperation;

    public ExtractMethodChange(Refactoring refactoring, Map<String, UMLClass> parentClassMap, Map<String, UMLClass> currentClassMap, RevCommit revCommit) {
        super(revCommit);
        this.extractOperation = (ExtractOperationRefactoring) refactoring;
        this.setNextClass(currentClassMap.get(extractOperation.getExtractedOperation().getClassName()));
        this.setNextOperation(extractOperation.getExtractedOperation());
        this.setOriginalPath(extractOperation.getSourceOperationBeforeExtraction().getClassName());
        this.setNextPath(this.getNextClass().toString());
        this.setOriginalElement(extractOperation.getSourceOperationBeforeExtraction().getClassName());
        this.setNextElement(this.getNextOperation().toString());
        this.setCategory(Category.METHOD_EXTRACT);
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
        message += "<br>in <code>" + this.getNextPath() + "</code>";
        message += "<br>extracted from <code>" + this.getOriginalElement() + "</code>";
        message += "<br>in <code>" + this.getOriginalPath() + "</code>";
        message += "<br>";
        return message;
    }
}

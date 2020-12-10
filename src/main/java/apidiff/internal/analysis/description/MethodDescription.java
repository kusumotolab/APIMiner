package apidiff.internal.analysis.description;

import apidiff.enums.Category;
import apidiff.internal.util.UtilTools;
import apidiff.internal.util.UtilToolsForRef;
import gr.uom.java.xmi.UMLOperation;
import org.eclipse.jdt.core.dom.SimpleType;
import org.refactoringminer.api.Refactoring;

import java.util.List;

public class MethodDescription extends TemplateDescription {


    public String remove(final String nameMethod, final String nameClass) {
        return super.messageRemoveTemplate("method", nameMethod, nameClass);
    }

    public String visibility(final String nameMethod, final String nameClass, final String visibility1, final String visibility2) {
        return super.messageVisibilityTemplate("method", nameMethod, "type", nameClass, visibility1, visibility2);
    }

    public String parameter(final String nameMethodAfter, final String nameMethodBefore, final String nameClass) {
        return super.messageParameterTemplate("method", nameMethodAfter, nameMethodBefore, "type", nameClass);
    }

    public String exception(final String nameMethodBefore, final List<SimpleType> listExceptionBefore, final List<SimpleType> listExceptionAfter, final String nameClassBefore) {
        String message = "";
        message += "<br><code>" + nameMethodBefore + "</code>";

        String listBefore = (listExceptionBefore == null || listExceptionBefore.isEmpty()) ? "" : listExceptionBefore.toString();
        String listAfter = (listExceptionAfter == null || listExceptionAfter.isEmpty()) ? "" : listExceptionAfter.toString();

        if (!UtilTools.isNullOrEmpty(listBefore) && !UtilTools.isNullOrEmpty(listAfter)) {
            message += "<br>changed the list exception";
            message += "<br>from <code>" + listBefore + "</code>";
            message += "<br>to <code>" + listAfter + "</code>";
        }

        if (UtilTools.isNullOrEmpty(listBefore) && !UtilTools.isNullOrEmpty(listAfter)) {
            message += "<br>added list exception " + listAfter + "</code>";
        }

        if (!UtilTools.isNullOrEmpty(listBefore) && UtilTools.isNullOrEmpty(listAfter)) {
            message += "<br>removed list exception " + listBefore + "</code>";
        }

        message += "<br>in <code>" + nameClassBefore + "</code>";
        message += "<br>";
        return message;
    }

    public String returnType(final String nameMethod, final String nameClass) {
        return super.messageReturnTypeTemplate("method", nameMethod, "class", nameClass);
    }

    public String modifierStatic(final String nameMethod, final String nameClass, final Boolean isGain) {
        return this.messageStaticTemplate("method", nameMethod, "class", nameClass, isGain);
    }

    public String modifierFinal(final String nameMethod, final String nameClass, final Boolean isGain) {
        return this.messageFinalTemplate("method", nameMethod, "class", nameClass, isGain);
    }

    public String changeInNameOrPath(final Category category, final Refactoring refactoring) {
        String description = "";
        UMLOperation operationBefore = UtilToolsForRef.getOperationBefore(refactoring);
        String nameMethodBefore = UtilToolsForRef.getSimpleNameMethod(operationBefore);
        String nameClassBefore = UtilToolsForRef.getClassPathOfMethod(operationBefore);
        UMLOperation operationAfter = UtilToolsForRef.getOperationAfter(refactoring);
        String nameMethodAfter = UtilToolsForRef.getSimpleNameMethod(operationAfter);
        String nameClassAfter = UtilToolsForRef.getClassPathOfMethod(operationAfter);
        switch (category) {
            case METHOD_MOVE:
                description = this.move(nameMethodAfter, nameClassBefore, nameClassAfter);
                break;
            case METHOD_RENAME:
                description = this.rename(nameMethodBefore, nameMethodAfter, nameClassAfter);
                break;
            case METHOD_PULL_UP:
                description = this.pullUp(nameMethodAfter, nameClassBefore, nameClassAfter);
                break;
            case METHOD_MOVE_AND_RENAME:
                description = this.moveAndRename(nameMethodBefore, nameMethodAfter, nameClassBefore, nameClassAfter);
                break;
            case METHOD_INLINE:
                description = this.inline(nameMethodBefore,nameMethodAfter,nameClassBefore,nameClassAfter);
                break;
            default:
                break;
        }
        return description;
    }

    public String rename(final String nameMethodBefore, final String nameMethodAfter, final String nameClass) {
        return this.messageRenameTemplate("method", nameMethodBefore, nameMethodAfter, nameClass);
    }

    public String moveAndRename(final String nameMethodBefore, final String nameMethodAfter, String nameClassBefore, String nameClassAfter) {
        String message = "";
        message += "<br>method <code>" + nameMethodBefore + "</code>";
        message += "<br>renamed to <code>" + nameMethodAfter + "</code>";
        message += "<br>and moved from <code>" + nameClassBefore + "</code>";
        message += "<br>to <code>" + nameClassAfter + "</code>";
        message += "<br>";
        return message;
    }

    public String extract(final String nameMethodBefore, final String nameClassBefore, final String nameMethodAfter, final String nameClassAfter){
        String message = "";
        message += "<br>Method <code>" + nameMethodAfter +"</code>";
        message += "<br>in <code>" + nameClassAfter +"</code>";
        message += "<br>extracted from <code>" + nameMethodBefore +"</code>";
        message += "<br>in <code>" + nameClassBefore  +"</code>";
        message += "<br>";
        return message;
    }

    public String pullUp(final String nameMethod, final String nameClassBefore, final String nameClassAfter) {
        return this.messagePullUpTemplate("method", nameMethod, nameClassBefore, nameClassAfter);
    }

    public String pushDown(final String nameMethod, final String nameClassBefore, final String nameClassAfter) {
        return this.messagePushDownTemplate("method", nameMethod, nameClassBefore, nameClassAfter);
    }

    public String addition(final String nameMethod, final String nameClass) {
        return this.messageAddition("method", nameMethod, nameClass);
    }

    public String inline(final String nameMethodBefore, final String nameMethodAfter,  final String nameClassBefore, final String nameClassAfter){
        String message = "";
        message += "<br>Method <code>" + nameMethodBefore +"</code>";
        message += "<br>from <code>" + nameClassBefore +"</code>";
        message += "<br>inlined to  <code>" + nameMethodAfter +"</code>";
        message += "<br>in <code>" + nameClassAfter +"</code>";
        message += "<br>";
        return message;
    }


    public String deprecate(final String nameMethodBefore, final String nameClassBefore) {
        return this.messageDeprecate("method", nameMethodBefore, nameClassBefore);
    }

    public String move(final String nameMethodAfter, final String nameClassBefore, final String nameClassAfter) {
        return this.messageMoveTemplate("method", nameMethodAfter, nameClassBefore, nameClassAfter);
    }

}

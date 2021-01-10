package apiminer.internal.analysis.description;

import apiminer.enums.Category;
import apiminer.internal.util.UtilToolsForRef;
import org.refactoringminer.api.Refactoring;


public class FieldDescription extends TemplateDescription {


    public String remove(final String nameField, final String nameClass) {
        return super.messageRemoveTemplate("field", nameField, nameClass);
    }

    public String changeInNameOrPath(final Category category, final Refactoring refactoring) {
        String description = "";
        String[] attributeBefore = UtilToolsForRef.getAttributeBefore(refactoring);
        String nameFieldBefore = UtilToolsForRef.getSimpleNameField(attributeBefore);
        String nameClassBefore = UtilToolsForRef.getClassPathOfField(attributeBefore);
        String[] attributeAfter = UtilToolsForRef.getAttributeAfter(refactoring);
        String nameFieldAfter = UtilToolsForRef.getSimpleNameField(attributeAfter);
        String nameClassAfter = UtilToolsForRef.getClassPathOfField(attributeAfter);
        switch (category) {
            case FIELD_MOVE:
                description = this.move(nameFieldBefore, nameClassBefore, nameClassAfter);
                break;
            case FIELD_RENAME:
                description = this.rename(nameFieldBefore, nameFieldAfter, nameClassBefore);
                break;
            case FIELD_MOVE_AND_RENAME:
                description = this.moveAndRename(nameFieldBefore, nameFieldAfter, nameClassBefore, nameClassAfter);
                break;
            case FIELD_PULL_UP:
                description = this.pullUp(nameFieldBefore, nameClassBefore, nameClassAfter);
                break;
            case FIELD_PUSH_DOWN:
                description = this.pushDown(nameFieldBefore, nameClassBefore, nameClassAfter);
                break;
            default:
                description = "";
                break;
        }
        return description;
    }

    public String move(final String nameFieldAfter, final String nameClassBefore, final String nameClassAfter) {
        return this.messageMoveTemplate("field", nameFieldAfter, nameClassBefore, nameClassAfter);
    }

    public String rename(final String nameFieldBefore, final String nameFieldAfter, final String nameClass) {
        return this.messageRenameTemplate("field", nameFieldBefore, nameFieldAfter, nameClass);
    }

    public String moveAndRename(final String nameFieldBefore, final String nameFieldAfter, String nameClassBefore, String nameClassAfter) {
        String message = "";
        message += "<br>field <code>" + nameFieldBefore + "</code>";
        message += "<br>renamed to <code>" + nameFieldAfter + "</code>";
        message += "<br>and moved from <code>" + nameClassBefore + "</code>";
        message += "<br>to <code>" + nameClassAfter + "</code>";
        message += "<br>";
        return message;
    }

	public String extract(final String nameFieldExtracted, final String nameClass) {
        String message = "";
        message += "<br>field <code>" + nameFieldExtracted + "</code>";
        message += "<br>extracted in <code>" + nameClass + "</code>";
        message += "<br>";
        return message;
    }

    public String addition(final String nameField, final String nameClass) {
        return this.messageAddition("field", nameField, nameClass);
    }

    public String deprecate(final String nameFieldAfter, final String nameClassAfter) {
        return this.messageDeprecate("field", nameFieldAfter, nameClassAfter);
    }

    public String changeDefaultValue(final String nameField, final String nameClass) {
        String message = "";
        message += "<br>field <code>" + nameField + "</code>";
        message += "<br>changed default value";
        message += "<br>in <code>" + nameClass + "</code>";
        message += "<br>";
        return message;
    }

    public String changeType(final String nameFieldBefore, final String nameFieldAfter, final String nameClass) {
        String message = "";
        message += "<br>field <code>" + nameFieldBefore + "</code>";
        message += "<br>changed field type to <code>" + nameFieldAfter + "</code>";
        message += "<br>in <code>"+ nameClass + "</code>";
        message += "<br>";
        return message;
    }

    public String visibility(final String nameField, final String nameClass, final String visibility1, final String visibility2) {
        return super.messageVisibilityTemplate("field", nameField, nameClass, visibility1, visibility2);
    }

    public String modifierFinal(final String nameField, final String nameClass, final Boolean isGain) {
        return this.messageFinalTemplate("field", nameField, nameClass, isGain);
    }

    public String pullUp(final String nameField, final String nameClassBefore, final String nameClassAfter) {
        return this.messagePullUpTemplate("field", nameField, nameClassBefore, nameClassAfter);
    }

    public String pushDown(final String nameField, final String nameClassBefore, final String nameClassAfter) {
        return this.messagePushDownTemplate("field", nameField, nameClassBefore, nameClassAfter);
    }

}

package apidiff.internal.analysis.description;

import apidiff.enums.Category;
import apidiff.internal.util.UtilTools;

public class TypeDescription extends TemplateDescription {

    public String moveRefactoring(final Category category, final String fullNameBefore, final String fullNameAfter) {
        String description = "";
        switch (category) {
            case TYPE_MOVE:
                description = move(fullNameBefore, fullNameAfter);
                break;
            case TYPE_RENAME:
                description = rename(fullNameBefore, fullNameAfter);
                break;
            case TYPE_MOVE_AND_RENAME:
                description = moveAndRename(fullNameBefore, fullNameAfter);
                break;
            default:
                description = "";
                break;
        }
        return description;
    }

    public String extract(final Category category, final String extractedClass, final String originalClass) {
        String description = "";
        switch (category) {
            case TYPE_EXTRACT_SUPERTYPE:
                description = extractSuperClass(extractedClass, originalClass);
                break;
            case TYPE_EXTRACT_TYPE:
                description = extractClass(extractedClass, originalClass);
                break;
            case TYPE_EXTRACT_SUBTYPE:
                description = extractSubClass(extractedClass, originalClass);
                break;
            default:
                description = "";
                break;
        }
        return description;
    }

    public String extractSuperClass(final String extractedClass, final String originalClass) {
        String message = "";
        message += "<br>Extract SuperType <code>" + extractedClass + "</code>";
        message += "<br>from <code>" + originalClass + "</code>";
        message += "<br>";
        return message;
    }

    public String extractSubClass(final String extractedClass, final String originalClass) {
        String message = "";
        message += "<br>Extract SubType <code>" + extractedClass + "</code>";
        message += "<br>from <code>" + originalClass + "</code>";
        message += "<br>";
        return message;
    }

    public String extractClass(final String extractedClass, final String originalClass) {
        String message = "";
        message += "<br>Extract Type <code>" + extractedClass + "</code>";
        message += "<br>from <code>" + originalClass + "</code>";
        message += "<br>";
        return message;
    }

    public String rename(final String fullNameBefore, final String fullNameAfter) {
        String message = "";
        message += "type <code>" + fullNameBefore + "</code>";
        message += "<br>renamed to";
        message += "<br><code>" + fullNameAfter + "</code>";
        message += "<br>";
        return message;
    }

    public String move(final String fullNameBefore, final String fullNameAfter) {
        String message = "";
        message += "<br>type <code>" + fullNameBefore + "</code>";
        message += "<br>moved to";
        message += "<br><code>" + fullNameAfter + "</code>";
        message += "<br>";
        return message;
    }

    public String moveAndRename(final String fullNameBefore, final String fullNameAfter) {
        String message = "";
        message += "<br>type <code>" + fullNameBefore + "</code>";
        message += "<br>moved and renamed to";
        message += "<br><code>" + fullNameAfter + "</code>";
        message += "<br>";
        return message;
    }

    public String remove(final String nameClass) {
        String message = "";
        message += "<br>type <code>" + nameClass + "</code> ";
        message += "<br>was removed";
        message += "<br>";
        return message;
    }

    public String visibility(final String nameClass, final String visibility1, final String visibility2) {
        String message = "";
        message += "<br> type <code>" + nameClass + "</code>";
        message += "<br> changed visibility from <code>" + visibility1 + "</code> to <code>" + visibility2 + "</code>";
        message += "<br>";
        return message;
    }

    public String modifierStatic(final String nameClass, final Boolean isGain) {
        String message = "";
        message += "type <code>" + nameClass + "</code>";
        message += isGain ? "<br>received the modifier <code>static</code>" : "<br>lost the modifier <code>static</code>";
        message += "<br>";
        return message;
    }

    public String modifierFinal(final String nameClass, final Boolean isGain) {
        String message = "";
        message += "type <code>" + nameClass + "</code>";
        message += isGain ? "<br>received the modifier <code>final</code>" : "<br>lost the modifier <code>final</code>";
        message += "<br>";
        return message;
    }

    public String changeSuperClass(final String nameClassComplete, final String listSuperClass1, final String listSuperClass2) {
        String message = "";
        message += "<b>Category Superclass</b>:";
        message += "<br>class <code>" + nameClassComplete + "</code>";
        message += "<br>changed the superclass list";
        message += "<br>from <code>" + listSuperClass1 + "</code>";
        message += "<br>to <code>" + listSuperClass2 + "</code>";
        message += "<br>";
        return message;
    }

    public String changeSuperType(final String nameClassComplete, final String superTypeBefore, final String superTypeAfter) {
        String message = "";
        message += "<br><code>" + nameClassComplete + "</code>";


        if (!UtilTools.isNullOrEmpty(superTypeBefore) && !UtilTools.isNullOrEmpty(superTypeAfter)) {
            message += "<br>changed the superClass";
            message += "<br>from <code>" + superTypeBefore + "</code>";
            message += "<br>to <code>" + superTypeAfter + "</code>";
        }

        if (UtilTools.isNullOrEmpty(superTypeBefore) && !UtilTools.isNullOrEmpty(superTypeAfter)) {
            message += "<br>added superClass " + superTypeAfter + "</code>";
        }

        if (!UtilTools.isNullOrEmpty(superTypeBefore) && UtilTools.isNullOrEmpty(superTypeAfter)) {
            message += "<br>removed superClass " + superTypeBefore + "</code>";
        }

        message += "<br>";
        return message;
    }

    public String addition(final String nameClass) {
        String message = "";
        message += "<br>Type <code>" + nameClass + "</code> added";
        message += "<br>";
        return message;
    }

    public String deprecate(final String nameClass) {
        String message = "";
        message += "<br> type " + nameClass + " <code> was deprecated";
        message += "<br>";
        return message;
    }

}

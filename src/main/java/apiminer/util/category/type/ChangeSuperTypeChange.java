package apiminer.util.category.type;

import gr.uom.java.xmi.UMLClass;

public class ChangeSuperTypeChange {
    private UMLClass originalClass;
    private UMLClass nextClass;

    private String isDescription() {
        String message = "";
        message += "<br>type <code>" + nextClass.getName() + "</code>";

        /*
        if (!UtilTools.isNullOrEmpty(superTypeBefore) && !UtilTools.isNullOrEmpty(superTypeAfter)) {
            message += "<br>changed the superType";
            message += "<br>from <code>" + superTypeBefore + "</code>";
            message += "<br>to <code>" + superTypeAfter + "</code>";
        }

        if (UtilTools.isNullOrEmpty(superTypeBefore) && !UtilTools.isNullOrEmpty(superTypeAfter)) {
            message += "<br>added superType <code>" + superTypeAfter + "</code>";
        }

        if (!UtilTools.isNullOrEmpty(superTypeBefore) && UtilTools.isNullOrEmpty(superTypeAfter)) {
            message += "<br>removed superType <code>" + superTypeBefore + "</code>";
        }

         */

        message += "<br>";
        return message;
    }
}

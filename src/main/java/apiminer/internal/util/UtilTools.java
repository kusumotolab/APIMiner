package apiminer.internal.util;

import apiminer.enums.Classifier;
import gr.uom.java.xmi.*;
import org.eclipse.jgit.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilTools {
    public static String getClassName(UMLClass umlClass) {
        return umlClass.toString();
    }

    public static String getSignatureMethod(UMLOperation umlOperation) {
        String signature;
        String methodName = umlOperation.getName();
        List<String> parameterTypeList = new ArrayList<>();
        String parameters;
        for (UMLParameter parameter : umlOperation.getParametersWithoutReturnType()) {
            parameterTypeList.add(parameter.getType().toString());
        }
        parameters = StringUtils.join(parameterTypeList, ", ");
        String returnType = "";
        if (umlOperation.getReturnParameter() != null) {
            returnType = " : " + umlOperation.getReturnParameter().toString();
        }
        signature = methodName + "(" + parameters + ")" + returnType;
        return signature;
    }

    public static String getAttributeName(UMLAttribute umlAttribute) {
        String attributeName = umlAttribute.getName();
        String attributeType = umlAttribute.getType().toString();
        return attributeName + " : " + attributeType;
    }

    public static String getTypeDescriptionName(UMLClass umlClass) {
        return umlClass.toString();
    }

    public static String getMethodDescriptionName(UMLOperation umlOperation) {
        String signature;
        String methodName = umlOperation.getName();
        List<String> parameterTypeList = new ArrayList<>();
        String parameters;
        for (UMLParameter parameter : umlOperation.getParametersWithoutReturnType()) {
            parameterTypeList.add(parameter.getType().toString() + " " + parameter.getName());
        }
        parameters = StringUtils.join(parameterTypeList, ", ");
        signature = methodName + "(" + parameters + ")";
        return signature;
    }

    public static String getFieldDescriptionName(UMLAttribute umlAttribute) {
        return umlAttribute.getName();
    }

    public static String getVisibilityDescriptionName(String visibility){
        if(visibility.equals("package")){
            return "default";
        }
        return visibility;
    }

    public static boolean isAPIClass(UMLClass umlClass) {
        return umlClass.getVisibility().equals("public") || umlClass.getVisibility().equals("protected");
    }

    public static boolean isAPIMethod(UMLOperation umlOperation) {
        return umlOperation.getVisibility().equals("public") || umlOperation.getVisibility().equals("protected");
    }

    public static boolean isAPIField(UMLAttribute umlAttribute) {
        return umlAttribute.getVisibility().equals("public") || umlAttribute.getVisibility().equals("protected");
    }

    public static boolean isClassBeforeAfterAPIByClassifier(UMLClass originalClass, UMLClass nextClass, Classifier classifier) {
        return isAPIByClassifier(originalClass, classifier) && isAPIByClassifier(nextClass, classifier);
    }

    public static Boolean isAPIByClassifier(UMLClass umlClass, Classifier classifierAPI) {
        boolean isAPI = false;
        switch (classifierAPI) {
            case EXAMPLE:
                isAPI = isNonAPIExample(umlClass);
                break;
            case INTERNAL:
                isAPI = isNonAPIInternal(umlClass);
                break;
            case TEST:
                isAPI = isNonAPITest(umlClass);
                break;
            case EXPERIMENTAL:
                isAPI = isNonAPIExperimental(umlClass);
                break;
            case API:
                isAPI = isInterfaceStable(umlClass);
                break;
            default:
                break;
        }
        return isAPI;
    }

    public static Boolean isNonAPITest(UMLClass umlClass) {
        String pathAPI = umlClass.toString();
        String regexTest = "(?i)(^test)|(.test)|(test.)|(test$)|(^tests)|(.tests)|(tests.)|(tests$)";
        return pathAPI != null && !"".equals(pathAPI) && checkContainsByRegex(regexTest, pathAPI);
    }

    public static Boolean isNonAPIInternal(UMLClass umlClass) {
        String pathAPI = umlClass.toString();
        return pathAPI != null && !"".equals(pathAPI) && pathAPI.toLowerCase().contains(".internal.");
    }

    public static Boolean isNonAPIExperimental(UMLClass umlClass) {
        String pathAPI = umlClass.toString();
        return pathAPI != null && !"".equals(pathAPI) && pathAPI.toLowerCase().contains(".experimental.");
    }

    public static Boolean isNonAPIDemo(UMLClass umlClass) {
        String pathAPI = umlClass.toString();
        String regexDemo = "(?i)(^demo)|(.demo)|(demo.)|(demo$)";
        return pathAPI != null && !"".equals(pathAPI) && checkContainsByRegex(regexDemo, pathAPI);
    }

    public static Boolean isNonAPISample(UMLClass umlClass) {
        String pathAPI = umlClass.toString();
        String regexSample = "(?i)(^sample)|(.sample)|(sample.)|(sample$)|(^samples)|(.samples)|(samples.)|(samples$)";
        return pathAPI != null && !"".equals(pathAPI) && checkContainsByRegex(regexSample, pathAPI);
    }

    public static Boolean isNonAPIExample(UMLClass umlClass) {
        String pathAPI = umlClass.toString();
        String regexExample = "(?i)(^example)|(.example)|(example.)|(example$)|(^examples)|(.examples)|(examples.)|(examples$)";
        boolean isNonAPIExample = checkContainsByRegex(regexExample, pathAPI) || isNonAPIDemo(umlClass) || isNonAPISample(umlClass);
        return !"".equals(pathAPI) && isNonAPIExample;
    }

    public static Boolean isInterfaceStable(UMLClass umlClass) {
        return !"".equals(umlClass.getPackageName()) && !isNonAPIExample(umlClass) && !isNonAPIExperimental(umlClass) && !isNonAPIInternal(umlClass) && !isNonAPITest(umlClass);
    }

    public static boolean isDeprecatedClass(UMLClass umlClass) {
        for (UMLAnnotation umlAnnotation : umlClass.getAnnotations()) {
            if (umlAnnotation.toString().equals("@Deprecated")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDeprecatedMethod(UMLOperation umlOperation) {
        for (UMLAnnotation umlAnnotation : umlOperation.getAnnotations()) {
            if (umlAnnotation.toString().equals("@Deprecated")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDeprecatedField(UMLAttribute umlAttribute) {
        for (UMLAnnotation umlAnnotation : umlAttribute.getAnnotations()) {
            if (umlAnnotation.toString().equals("@Deprecated")) {
                return true;
            }
        }
        return false;
    }


    public static Boolean checkContainsByRegex(String regex, String pathAPI) {
        String path = pathAPI.toLowerCase();
        Pattern r = Pattern.compile(regex);
        Matcher m = r.matcher(path);
        return m.find();
    }


    public static Boolean isJavaFile(final String nameFile) {
        return nameFile != null && nameFile.endsWith(".java");
    }

    public static String getPathProject(final String path, final String nameProject) {
        String pathComplete = isNullOrEmpty(path) ? "./" : path + "/";
        pathComplete += isNullOrEmpty(nameProject) ? "" : nameProject;
        return pathComplete;
    }

    public static Boolean isNullOrEmpty(final String text) {
        return (text == null || "".equals(text));
    }

}

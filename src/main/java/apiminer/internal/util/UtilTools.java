package apiminer.internal.util;

import apiminer.enums.Classifier;
import gr.uom.java.xmi.*;
import org.aspectj.lang.annotation.DeclareWarning;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jgit.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilTools {
    public static String getClassName(UMLClass umlClass){
        return umlClass.toString();
    }
    public static String getSignatureMethod(UMLOperation umlOperation){
        String signature = "";
        String methodName = umlOperation.getName();
        List<String> parameterTypeList = new ArrayList<>();
        String parameters ="";
        for(UMLParameter parameter:umlOperation.getParametersWithoutReturnType()){
            parameterTypeList.add(parameter.getType().toString());
        }
        parameters = StringUtils.join(parameterTypeList,", ");
        String returnType = "";
        if(umlOperation.getReturnParameter()!=null){
            returnType = " : "+ umlOperation.getReturnParameter().toString();
        }
        signature = methodName + "("+ parameters +")" + returnType;
        return signature;
    }
    public static String getAttributeName(UMLAttribute umlAttribute){
        String attributeName = umlAttribute.getName();
        String attributeType = umlAttribute.getType().toString();
        return attributeName+ " : "+ attributeType;
    }

    public static boolean isAPIClass(UMLClass umlClass) {
        if(umlClass.getVisibility().equals("public")||umlClass.getVisibility().equals("protected")){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isAPIMethod(UMLOperation umlOperation) {
            if(umlOperation.getVisibility().equals("public")||umlOperation.getVisibility().equals("protected")){
                return true;
            }
        return false;
    }

    public static boolean isAPIField(UMLAttribute umlAttribute)  {
            if(umlAttribute.getVisibility().equals("public")||umlAttribute.getVisibility().equals("protected")){
                return true;
            }
        return false;
    }

    public static boolean isClassBeforeAfterAPIByClassifier(UMLClass originalClass,UMLClass nextClass,Classifier classifier){
        return (isAPIByClassifier(originalClass,classifier)&&isAPIByClassifier(nextClass,classifier))? true:false;
    }

    public static Boolean isAPIByClassifier(UMLClass umlClass, Classifier classifierAPI) {
        Boolean isAPI = false;
        switch (classifierAPI){
            case EXAMPLE:
                isAPI = isNonAPIExample(umlClass)?true:false;
                break;
            case INTERNAL:
                isAPI = isNonAPIInternal(umlClass)?true:false;
                break;
            case TEST:
                isAPI = isNonAPITest(umlClass)?true:false;
                break;
            case EXPERIMENTAL:
                isAPI = isNonAPIExperimental(umlClass)?true:false;
                break;
            case API:
                isAPI = isInterfaceStable(umlClass)?true:false;
                break;
            default:
                break;
        }
        return isAPI;
    }
    public static Boolean isNonAPITest(UMLClass umlClass){
        String pathAPI = umlClass.getPackageName();
        Boolean isTestJava = (umlClass.getSourceFile().endsWith("test.java")||umlClass.getSourceFile().endsWith("tests.java"))? true:false;
        String regexTest =  "(?i)(^test)|(.test)|(test.)|(test$)|(^tests)|(.tests)|(tests.)|(tests$)";
        return (pathAPI !=null  && !"".equals(pathAPI) && isTestJava &&checkCountainsByRegex(regexTest, pathAPI))?true:false;
    }

    public static Boolean isNonAPIInternal(UMLClass umlClass){
        String pathAPI = umlClass.getPackageName();
        return (pathAPI !=null  && !"".equals(pathAPI)  && pathAPI.toLowerCase().contains(".internal."))?true:false;
    }

    public static Boolean isNonAPIExperimental(UMLClass umlClass){
        String pathAPI = umlClass.getPackageName();
        return (pathAPI !=null  && !"".equals(pathAPI)  && pathAPI.toLowerCase().contains(".experimental."))?true:false;
    }

    public static Boolean isNonAPIDemo(UMLClass umlClass){
        String pathAPI = umlClass.getPackageName();
        String regexDemo = "(?i)(^demo)|(.demo)|(demo.)|(demo$)";
        return (pathAPI !=null  && !"".equals(pathAPI)  &&  checkCountainsByRegex(regexDemo, pathAPI))?true:false;
    }

    public static Boolean isNonAPISample(UMLClass umlClass){
        String pathAPI = umlClass.getPackageName();
        String regexSample = "(?i)(^sample)|(.sample)|(sample.)|(sample$)|(^samples)|(.samples)|(samples.)|(samples$)";
        return (pathAPI !=null  && !"".equals(pathAPI)  && checkCountainsByRegex(regexSample, pathAPI))?true:false;
    }

    public static Boolean isNonAPIExample(UMLClass umlClass){
        String pathAPI = umlClass.getPackageName();
        String regexExample = "(?i)(^example)|(.example)|(example.)|(example$)|(^examples)|(.examples)|(examples.)|(examples$)";
        Boolean isNonAPIExample = checkCountainsByRegex(regexExample, pathAPI) || isNonAPIDemo(umlClass) || isNonAPISample(umlClass);
        return (pathAPI !=null  && !"".equals(pathAPI) && isNonAPIExample)?true:false;
    }

    public static Boolean isInterfaceStable(UMLClass umlClass){
        if((!"".equals(umlClass.getPackageName()) && !isNonAPIExample(umlClass) && !isNonAPIExperimental(umlClass) && !isNonAPIInternal(umlClass) && !isNonAPITest(umlClass))){
            return true;
        }
        return false;
    }

    public static  boolean isDeprecatedClass(UMLClass umlClass){
        for(UMLAnnotation umlAnnotation: umlClass.getAnnotations()){
            if(umlAnnotation.toString().equals("@Deprecated")){
                return true;
            }
        }
        return false;
    }

    public static boolean isDeprecatedMethod(UMLOperation umlOperation){
        for(UMLAnnotation umlAnnotation: umlOperation.getAnnotations()){
            if(umlAnnotation.toString().equals("@Deprecated")){
                return true;
            }
        }
        return false;
    }

    public static boolean isDeprecatedField(UMLAttribute umlAttribute){
        for(UMLAnnotation umlAnnotation: umlAttribute.getAnnotations()){
            if(umlAnnotation.toString().equals("@Deprecated")){
                return true;
            }
        }
        return false;
    }

    public static String readFileToString(String filePath) throws IOException {
        StringBuilder fileData = new StringBuilder(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        char[] buf = new char[10];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }

        reader.close();

        return fileData.toString();
    }


    public static Boolean checkCountainsByRegex(String regex, String pathAPI){
        String path = pathAPI.toLowerCase();
        Pattern r = Pattern.compile(regex);
        Matcher m = r.matcher(path);
        return m.find();
    }


    public static Boolean isJavaFile(final String nameFile){
        return (nameFile!=null && nameFile.endsWith(".java"))?true:false;
    }

    public static String getPathProject(final String path, final String nameProject) throws IOException{
        String pathComplete = isNullOrEmpty(path) ? "./" : path + "/";
        pathComplete += isNullOrEmpty(nameProject) ? "" : nameProject;
        return pathComplete;
    }
    public static String getFileAbsolutePath(File file){
        String absoulutePath = file.getAbsolutePath();
        if(File.separator.toString().equals("\\")){
            String regex = "\\\\";
            absoulutePath = absoulutePath.replaceAll(regex,"/");
        }
        return absoulutePath;
    }

    public static Boolean isNullOrEmpty(final String text){
        return (text == null || "".equals(text));
    }

}

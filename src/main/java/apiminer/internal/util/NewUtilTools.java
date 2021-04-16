package apiminer.internal.util;

import apiminer.enums.Classifier;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLOperation;
import org.aspectj.lang.annotation.DeclareWarning;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewUtilTools {
    public static String getClassName(UMLClass umlClass){
        return umlClass.toString();
    }
    public static String getSignatureMethod(UMLOperation umlOperation){
        String signature = "";

        return signature;
    }
    public static String getAttributeName(UMLAttribute umlAttribute){
        return umlAttribute.toString();
    }

    public static boolean isAPIClass(UMLClass umlClass){
        //todo fix pacage filter
        if(umlClass.getVisibility().equals("public")||umlClass.getVisibility().equals("protected")){
            return true;
        }else{
            return false;
        }
    }

    //Todo fix
    public static Boolean isAPIByClassifier(String packageName, Classifier classifierAPI) throws IOException {
        Boolean isAPI = false;
        switch (classifierAPI){
            case EXAMPLE:
                isAPI = isNonAPIExample(packageName)?true:false;
                break;
            case INTERNAL:
                isAPI = isNonAPIInternal(packageName)?true:false;
                break;
            case TEST:
                isAPI = isNonAPITest(packageName)?true:false;
                break;
            case EXPERIMENTAL:
                isAPI = isNonAPIExperimental(packageName)?true:false;
                break;
            case API:
                isAPI = isInterfaceStable(packageName)?true:false;
                break;
            default:
                break;
        }
        return isAPI;
    }
    public static Boolean isNonAPITest(String pathAPI){
        String regexTest =  "(?i)(\\/test)|(test\\/)|(tests\\/)|(test\\.java$)|(tests\\.java$)";
        return (pathAPI !=null  && !"".equals(pathAPI) && checkCountainsByRegex(regexTest, pathAPI))?true:false;
    }

    public static String getSimpleNameFileWithouPackageWithNameLibrary(String path, String absolutePath, final String nameProject) throws IOException{
        String simpleNameFile = absolutePath.replaceAll(path + "/" + nameProject, "");
        String[] names = nameProject.split("/");
        for(int i=0; i<names.length; i++){
            simpleNameFile = simpleNameFile.replaceAll(names[i] + "/", "");
        }
        return simpleNameFile;
    }

    public static Boolean isNonAPIInternal(String pathAPI){
        return (pathAPI !=null  && !"".equals(pathAPI)  && pathAPI.toLowerCase().contains("/internal/"))?true:false;
    }

    public static Boolean isNonAPIExperimental(String pathAPI){
        return (pathAPI !=null  && !"".equals(pathAPI)  && pathAPI.toLowerCase().contains("/experimental/"))?true:false;
    }

    public static Boolean isNonAPIDemo(String pathAPI){
        String regexDemo = "(?i)(\\/demo)|(demo\\/)";
        return (pathAPI !=null  && !"".equals(pathAPI)  &&  checkCountainsByRegex(regexDemo, pathAPI))?true:false;
    }

    public static Boolean isNonAPISample(String pathAPI){
        String regexSample = "(?i)(sample\\/)|(samples\\/)";
        return (pathAPI !=null  && !"".equals(pathAPI)  && checkCountainsByRegex(regexSample, pathAPI))?true:false;
    }

    public static Boolean isNonAPIExample(String pathAPI){
        String regexExample = "(?i)(\\/example)|(example\\/)|(examples\\/)";
        Boolean isNonAPIExample = checkCountainsByRegex(regexExample, pathAPI) || isNonAPIDemo(pathAPI) || isNonAPISample(pathAPI);
        return (pathAPI !=null  && !"".equals(pathAPI) && isNonAPIExample)?true:false;
    }

    public static Boolean checkCountainsByRegex(String regex, String pathAPI){
        String path = pathAPI.toLowerCase();
        Pattern r = Pattern.compile(regex);
        Matcher m = r.matcher(path);
        return m.find();
    }
    public static Boolean isInterfaceStable(String pathLibrary) throws IOException{
        if((!"".equals(pathLibrary) && !isNonAPIExample(pathLibrary) && !isNonAPIExperimental(pathLibrary) && !isNonAPIInternal(pathLibrary) && !isNonAPITest(pathLibrary))){
            return true;
        }
        return false;
    }
}

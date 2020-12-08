package apidiff.internal.util;

import apidiff.enums.Classifier;
import apidiff.internal.visitor.APIVersion;
import org.eclipse.jdt.core.dom.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilTools {
	
	private static Properties properties = null;
	
	public static boolean isEqualAnnotationMember(AnnotationTypeMemberDeclaration member1, AnnotationTypeMemberDeclaration member2){
		if(!member1.getName().toString().equals(member2.getName().toString())){
			return false;
		}
		return true;
	}
	
	public static boolean isEqualMethod(MethodDeclaration method1, MethodDeclaration method2){
		if(!method1.getName().toString().equals(method2.getName().toString()))
			return false;
		if(method1.parameters().size() != method2.parameters().size())
			return false;
		for(int i = 0; i < method1.parameters().size(); i++){
			String typeP1 = ((SingleVariableDeclaration) method1.parameters().get(i)).getType().toString();
			String typeP2 = ((SingleVariableDeclaration) method2.parameters().get(i)).getType().toString();
			if(!typeP1.equals(typeP2))
				return false;
		}
		
		return true;
	}
	
	public static String getFieldName(FieldDeclaration field){
		String name = null;
		List<VariableDeclarationFragment> variableFragments = field.fragments();
		for (VariableDeclarationFragment variableDeclarationFragment : variableFragments) {
			if(variableDeclarationFragment.resolveBinding() != null){
				name = variableDeclarationFragment.resolveBinding().getName();
			}
		}
		return name;
	}
	
	public static String getTypeName(TypeDeclaration type){
		return type.resolveBinding() == null ? "" : type.resolveBinding().getQualifiedName();
	}
	
	public static boolean isVisibilityPrivate(BodyDeclaration node){
		return getVisibility(node).equals("private");
	}
	
	public static boolean isVisibilityPublic(BodyDeclaration node){
		return getVisibility(node).equals("public");
	}
	
	public static boolean isVisibilityDefault(BodyDeclaration node){
		return getVisibility(node).equals("default");
	}
	
	public static boolean isVisibilityProtected(BodyDeclaration node){
		return getVisibility(node).equals("protected");
	}
	
	
	public static Boolean isFinal(BodyDeclaration node){
		return containsModifier(node, "final");
	}
	
	public static Boolean isStatic(BodyDeclaration node){
		return containsModifier(node, "static");
	}
	
	public static Boolean containsModifier(BodyDeclaration node, String modifier){
		for (Object m : node.modifiers()) {
			if(m.toString().equals(modifier)){
				return true;
			}
		}
		return false;
	}
	
	public static String getVisibility(BodyDeclaration node){
		for (Object modifier : node.modifiers()) {
			if(modifier.toString().equals("public") || modifier.toString().equals("private")
					|| modifier.toString().equals("protected")){
				return modifier.toString();
			}
		}
		
		return "default";
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
	
	public static void addChangeToTypeMap(TypeDeclaration type, BodyDeclaration change, 
			HashMap<TypeDeclaration, ArrayList<BodyDeclaration>> changeMap) {
			
			if(changeMap.containsKey(type)){
				changeMap.get(type).add(change);
			}else{
				changeMap.put(type, new ArrayList<BodyDeclaration>());
				changeMap.get(type).add(change);
			}	
	}
	
	public static void addChangeToEnumMap(EnumDeclaration type, BodyDeclaration change, 
			HashMap<EnumDeclaration, ArrayList<BodyDeclaration>> changeMap) {
			
			if(changeMap.containsKey(type)){
				changeMap.get(type).add(change);
			}else{
				changeMap.put(type, new ArrayList<BodyDeclaration>());
				changeMap.get(type).add(change);
			}	
	}
	
	public static  String getPath(final AbstractTypeDeclaration node){
		return ((node == null) || (node.resolveBinding() == null) || (node.resolveBinding().getQualifiedName() == null))? "" : node.resolveBinding().getQualifiedName();
	}

	public static Boolean isAPIByClassifier(String pathLibrary, Classifier classifierAPI) throws IOException{
		Boolean isAPI = false;
		switch (classifierAPI){
			case EXAMPLE:
				isAPI = isNonAPIExample(pathLibrary)?true:false;
				break;
			case INTERNAL:
				isAPI = isNonAPIInternal(pathLibrary)?true:false;
				break;
			case TEST:
				isAPI = isNonAPITest(pathLibrary)?true:false;
				break;
			case EXPERIMENTAL:
				isAPI = isNonAPIExperimental(pathLibrary)?true:false;
				break;
			case API:
				isAPI = isInterfaceStable(pathLibrary)?true:false;
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
	
	public static Boolean isJavaFile(final String nameFile){
		return (nameFile!=null && nameFile.endsWith(".java"))?true:false;
	}
	
	public static String getPathProject(final String path, final String nameProject) throws IOException{
		String pathComplete = isNullOrEmpty(path) ? "./" : path + "/";
		pathComplete += isNullOrEmpty(nameProject) ? "" : nameProject;
		return pathComplete;
	}
	
	public static Boolean isNullOrEmpty(final String text){
		return (text == null || "".equals(text));
	}
	
	public static List<AbstractTypeDeclaration> getAcessibleTypes(APIVersion version){
		List<AbstractTypeDeclaration> list = new ArrayList<AbstractTypeDeclaration>();
		for(AbstractTypeDeclaration type: version.getTypesPublicAndProtected()){
			if(UtilTools.isVisibilityPublic(type) || UtilTools.isVisibilityProtected(type)){
				list.add(type);
			}
		}
		return list;
	}
	
	public static  List<TypeDeclaration> getIntersectionListTypes(List<TypeDeclaration> listVersion1, List<TypeDeclaration> listVersion2){
		List<TypeDeclaration> list = new ArrayList<TypeDeclaration>();
		for(TypeDeclaration type: listVersion2){
			if(listVersion1.contains(type)){
				list.add(type);
			}
		}
		return list;
	}
	

	public static Boolean containsJavadoc(final AbstractTypeDeclaration node){
		return ((node != null) && (node.getJavadoc() != null) && (!node.getJavadoc().equals("")))? true : false;
	} 
	
	public static String getSufixJavadoc(final AbstractTypeDeclaration node){
		return ((node != null) && (node.getJavadoc() != null) && (!node.getJavadoc().equals("")))? "" : " WITHOUT JAVADOC";
	}
	
	public static Boolean containsJavadoc(final AbstractTypeDeclaration node, final MethodDeclaration methodDeclaration){
		Boolean typeContainsJavadoc = containsJavadoc(node);
		return (typeContainsJavadoc && (methodDeclaration != null) && (methodDeclaration.getJavadoc() != null) && (!methodDeclaration.getJavadoc().equals("")))? true : false;
	}
	
	public static Boolean containsJavadoc(final AbstractTypeDeclaration node, final FieldDeclaration fieldInVersion){
		return containsJavadoc(node) && containsJavadoc(fieldInVersion);
	}
	
	public static Boolean containsJavadoc(final FieldDeclaration fieldInVersion){
		return ((fieldInVersion != null) && (fieldInVersion.getJavadoc() != null) && (!fieldInVersion.getJavadoc().equals("")))? true : false;
	}
	
	public static String getSufixJavadoc(final MethodDeclaration methodDeclaration){
		return ((methodDeclaration != null) && (methodDeclaration.getJavadoc() != null) && (!methodDeclaration.getJavadoc().equals("")))? "" : " WITHOUT JAVADOC";
	}
	
	public static String getSufixJavadoc(final AnnotationTypeMemberDeclaration annotationMember){
		return ((annotationMember != null) && (annotationMember.getJavadoc() != null) && (!annotationMember.getJavadoc().equals("")))? "" : " WITHOUT JAVADOC";
	}

	public static String getSufixJavadoc(final FieldDeclaration fieldInVersion){
		return ((fieldInVersion != null) && (fieldInVersion.getJavadoc() != null) && (!fieldInVersion.getJavadoc().equals("")))? "" : " WITHOUT JAVADOC";
	}
	
	public static String upperCaseFirstLetter(String str){
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	public static String downCaseFirstLetter(String str){
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}
	
}
package apiminer.internal.analysis.description;

import apiminer.internal.util.UtilTools;

public class TemplateDescription {
	
	protected String messageRemoveTemplate(final String typeStruture, final String nameStruture, final String path){
		String message = "";
		message += "<br>" + typeStruture + " <code>" + nameStruture +"</code>";
		message += "<br>removed from <code>" + path + "</code>";
		message += "<br>";
		return message;
	}

	protected String messageVisibilityTemplate(final String typeStruture, final String nameStruture, final String path,  final String visibility1, final String visibility2){
		String message = "";
		message += "<br>" + typeStruture + " <code>" + nameStruture +"</code>";
		message += "<br>changed visibility from <code>" + visibility1  + "</code> to <code>"  + visibility2 + "</code>";
		message += "<br>in <code>" + path + "</code>";
		message += "<br>";
		return message;
	}
	
	protected String messageChangeDefaultValueTemplate(final String typeStruture, final String nameStruture, final String path,  final String value1, final String value2){
		String message = "";
		message += "<b>Category Default Value:</b>";
		message += "<br>" + UtilTools.downCaseFirstLetter(typeStruture) + "<code>" + nameStruture +"</code>";
		message += "<br>changed default value from " + value1  + " to "  + value2;
		message += "<br>in <code>" + path + "</code>";
		message += "<br>";
		return message;
	}

	protected String messageFinalTemplate(final String typeStruture, final String nameStruture, final String path, final Boolean gain){
		String message = "";
		message += "<br>" + UtilTools.downCaseFirstLetter(typeStruture) + " <code>" + nameStruture +"</code>";
		message += gain ? "<br>received the modifier <code>final</code>" : "<br>lost the modifier <code>final</code>";
		message += "<br>in <code>" + path + "</code>";
		message += "<br>";
		return message;
	}
	
	protected String messageStaticTemplate(final String typeStruture, final String nameStruture, final String path, final Boolean gain){
		String message = "";
		message += "<br>" + UtilTools.downCaseFirstLetter(typeStruture) + " <code>" + nameStruture +"</code>";
		message += gain ? "<br>received the modifier <code>static</code>" : "<br>lost the modifier <code>static</code>";
		message += "<br>in <code>" + path + "</code>";
		message += "<br>";
		return message;
	}
	
	protected String messageReturnTypeTemplate(final String typeStruture,  final String nameStruture, final String path){
		String message = "";
		message += "<br>" + UtilTools.downCaseFirstLetter(typeStruture) + " <code>" + nameStruture +"</code>";
		message += "<br>changed the return type";
		message += "<br>in <code>" + path + "</code>";
		message += "<br>";
		return message;
	}
	
	protected String messageParameterTemplate(final String typeStruture, final String nameStrutureBefore, final String nameStrutureAfter, final String pathBefore, final String pathAfter){
		String message = "";
		message += "<br>" + UtilTools.downCaseFirstLetter(typeStruture) + " <code>" + nameStrutureBefore +"</code>";
		message += "<br>in <code>" + pathBefore + "</code>";
		message += "<br>changed the list parameters";
		message += "<br>to <code>" + nameStrutureAfter +"</code>";
		message += "<br>in <code>" + pathAfter + "</code>";
		message += "<br>";
		return message;
	}
	
	protected String messageMoveTemplate(final String typeStruture,final String fullName, final String pathBefore, final String pathAfter){
		String message = "";
		message += "<br>" + UtilTools.downCaseFirstLetter(typeStruture) + " <code>" + fullName +"</code>";
		message += "<br>moved from <code>" + pathBefore +"</code>";
		message += "<br>to <code>" + pathAfter +"</code>";
		message += "<br>";
		return message;
	}

	protected String messageRenameTemplate(final String typeStruture, final String nameBefore, final String nameAfter, final String path){
		String message = "";
		message += "<br>"+ UtilTools.downCaseFirstLetter(typeStruture) + " <code>" + nameBefore +"</code>";
		message += "<br>renamed to <code>" + nameAfter +"</code>";
		message += "<br>in <code>" + path +"</code>";
		message += "<br>";
		return message;
	}
	
	public String messagePullUpTemplate(final String typeStruture,  String nameStruture, final String nameClassBefore, final String nameClassAfter){
		String message = "";
		message += "<br>pull up " + UtilTools.upperCaseFirstLetter(typeStruture) +" <code>" + nameStruture +"</code>";
		message += "<br>from <code>" + nameClassBefore +"</code>";
		message += "<br>to <code>" + nameClassAfter +"</code>";
		message += "<br>";
		return message;
	}
	
	public String messagePushDownTemplate(final String typeStruture,  String nameStruture, final String nameClassBefore, final String nameClassAfter){
		String message = "";
		message += "<br>push down " + UtilTools.downCaseFirstLetter(typeStruture) +" <code>" + nameStruture +"</code>";
		message += "<br>from <code>" + nameClassBefore +"</code>";
		message += "<br>to <code>" + nameClassAfter +"</code>";
		message += "<br>";
		return message;
	}
	
	public String messageDeprecate(final String typeStruture, final String nameMethodBefore, final String nameClassBefore){
		String message = "";
		message += "<br>" + typeStruture + " <code>" + nameMethodBefore +"</code> ";
		message += "<br>deprecated in <code>" + nameClassBefore +"</code>";
		message += "<br>";
		return message;
	}
	
	public String messageAddition(final String typeStruture, final String nameStruture, final String nameClass){
		String message = "";
		message += "<br>" + typeStruture + " <code>" + nameStruture +"</code>";
		message += "<br>added in <code>" + nameClass +"</code>";
		message += "<br>";
		return message;
	}
}

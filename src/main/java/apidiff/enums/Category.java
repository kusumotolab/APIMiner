package apidiff.enums;

public enum Category {
	
	TYPE_RENAME("Rename Type"),
	TYPE_MOVE("Move Type"),
	TYPE_ADD("Addition Type"),
	TYPE_MOVE_AND_RENAME("Move and Rename Type"),
	TYPE_EXTRACT_SUPERTYPE("Extract Supertype"),
	TYPE_REMOVE("Remove Type"),
	TYPE_LOST_VISIBILITY("Lost Visibility in Type"),
	TYPE_GAIN_VISIBILITY("Gain Visibility in Type"),
	TYPE_LOST_MODIFIER_FINAL("Lost Final Modifier in Type"),
	TYPE_GAIN_MODIFIER_FINAL("Gain Final Modifier in Type"),
	TYPE_LOST_MODIFIER_STATIC("Lost Static Modifier in Type"),
	TYPE_GAIN_MODIFIER_STATIC("Gain Static Modifier in Type"),
	TYPE_CHANGE_SUPERCLASS("Change Superclass"),
	TYPE_REMOVE_SUPERCLASS("Remove Superclass"),
	TYPE_ADD_SUPER_CLASS("Addition Superclass"),
	TYPE_DEPRECIATE("Depreciate Type"),
	
	METHOD_MOVE("Move Method"),
	METHOD_RENAME("Rename Method"),
	METHOD_REMOVE("Remove Method"),
	METHOD_PULL_UP("Pull Up Method"),
	METHOD_PUSH_DOWN("Push Down Method"),
	METHOD_INLINE("Inline Method"),
	METHOD_CHANGE_PARAMETER_LIST("Change in Parameter List"),
	METHOD_CHANGE_EXCEPTION_LIST("Change in Exception List"),
	METHOD_CHANGE_RETURN_TYPE("Change in Return Method"),
	METHOD_GAIN_VISIBILITY("Gain Visibility in Method"),
	METHOD_LOST_VISIBILITY("Lost Visibility in Method"),
	METHOD_LOST_MODIFIER_FINAL("Lost Final Modifier in Method"),
	METHOD_GAIN_MODIFIER_FINAL("Gain Final Modifier in Method"),
	METHOD_LOST_MODIFIER_STATIC("Lost Static Modifier in Method"),
	METHOD_GAIN_MODIFIER_STATIC("Gain Static Modifier in Method"),
	METHOD_DEPRECIATE("Depreciate Method"),
	METHOD_ADD("Addition Method"),
	
	FIELD_REMOVE("Remove Field"),
	FIELD_MOVE("Move Field"),
	FIELD_PULL_UP("Pull Up Field"),
	FIELD_PUSH_DOWN("Push Down Field"),
	FIELD_ADD("Addition Field"),
	FIELD_DEPRECIATE("Depreciate Field"),
	FIELD_CHANGE_DEFAULT_VALUE("Change in Default Value"),
	FIELD_CHANGE_TYPE("Change in Type Field"),
	FIELD_LOST_VISIBILITY("Lost Visibility in Field"),
	FIELD_GAIN_VISIBILITY("Gain Visibility in Field"),
	FIELD_LOST_MODIFIER_FINAL("Lost Final Modifier in Field"),
	FIELD_GAIN_MODIFIER_FINAL("Gain Final Modifier in Field");
	
	private String displayName;
	
	private Boolean isBreakingChange;
	
	private Category(final String displayName, final Boolean isBreakingChange) {
		this.displayName = displayName;
		this.isBreakingChange = isBreakingChange;
	}
	
	private Category(final String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Boolean getIsBreakingChange() {
		return isBreakingChange;
	}

}
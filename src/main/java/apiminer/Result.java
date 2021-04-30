package apiminer;

import apiminer.internal.analysis.category.FieldChange;
import apiminer.internal.analysis.category.MethodChange;
import apiminer.internal.analysis.category.TypeChange;

import java.util.List;
import java.util.ArrayList;

public class Result {

	private List<TypeChange> changeType = new ArrayList<TypeChange>();

	private List<MethodChange> changeMethod = new ArrayList<MethodChange>();

	private List<FieldChange> changeField = new ArrayList<FieldChange>();

	public List<TypeChange> getChangeType() {
		return changeType;
	}

	public void setChangeType(List<TypeChange> changeType) {
		this.changeType = changeType;
	}

	public List<MethodChange> getChangeMethod() {
		return changeMethod;
	}

	public void setChangeMethod(List<MethodChange> changeMethod) {
		this.changeMethod = changeMethod;
	}

	public List<FieldChange> getChangeField() {
		return changeField;
	}

	public void setChangeField(List<FieldChange> changeField) {
		this.changeField = changeField;
	}
}
package apiminer.enums;

public enum ElementType {

	ENUM("enum"),
	INTERFACE("interface"),
	CLASS("class"),
	METHOD("method"),
	CONSTRUCTOR("constructor");
	
	private String type;
	
	ElementType(final String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return type;
	}
	
}

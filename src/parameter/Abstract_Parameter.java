package parameter;

abstract class Abstract_Parameter {
	private String Library;
	protected int Method;
	
	public String getLibrary() {
		return Library;
	}
	public void setLibrary(String library) {
		Library = library;
	}
	public int getMethod() {
		return Method;
	}
	abstract public void setMethod(String methodName);
}

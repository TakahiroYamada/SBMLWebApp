package parameter;

import beans.modelparameter.ModelParameter_Beans;

abstract class Abstract_Parameter {
	private String FileString;
	private String FileName;
	private String PathToFile;
	private String sessionInfo;
	private int type;
	private String Library;
	private ModelParameter_Beans sbmlParam;
	protected int Method;
	
	public String getFileName() {
		return FileName;
	}
	public void setFileName(String fileName) {
		FileName = fileName;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public ModelParameter_Beans getSbmlParam() {
		return sbmlParam;
	}
	public void setSbmlParam(ModelParameter_Beans sbmlParam) {
		this.sbmlParam = sbmlParam;
	}
	public String getSessionInfo() {
		return sessionInfo;
	}
	public void setSessionInfo(String sessionInfo) {
		this.sessionInfo = sessionInfo;
	}
	public String getFileString() {
		return FileString;
	}
	public void setFileString(String fileString) {
		FileString = fileString;
	}
	public String getPathToFile() {
		return PathToFile;
	}
	public void setPathToFile(String pathToFile) {
		PathToFile = pathToFile;
	}
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

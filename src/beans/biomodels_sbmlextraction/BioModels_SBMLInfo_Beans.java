package beans.biomodels_sbmlextraction;

import beans.superclass_beans.SuperClass_Beans;

public class BioModels_SBMLInfo_Beans extends SuperClass_Beans{
	private String ModelFileName;
	public String getModelFileName() {
		return ModelFileName;
	}
	public void setModelFileName(String modelFileName) {
		ModelFileName = modelFileName;
	}
	private String ModelString;

	public String getModelString() {
		return ModelString;
	}
	public void setModelString(String modelString) {
		ModelString = modelString;
	}
}

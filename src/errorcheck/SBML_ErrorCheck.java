package errorcheck;

import java.util.HashMap;
import java.util.List;

import org.sbml.jsbml.SBMLError;
import org.sbml.jsbml.SBMLErrorLog;
import org.sbml.jsbml.validator.SBMLValidator;

public class SBML_ErrorCheck {
	private String sbml_filename;
	private String errorMessage;
	public SBML_ErrorCheck( String fileName ){
		this.sbml_filename = fileName;
	}
	public void checkError(){
		this.errorMessage = "";
		checkGeneralConsistency();
	}
	private void checkGeneralConsistency() {
		HashMap< String, String> tmp_validater = new HashMap<>();
		tmp_validater.put(SBMLValidator.CHECK_CATEGORY.GENERAL_CONSISTENCY.toString() , "true");
		SBMLErrorLog errorLog = SBMLValidator.checkConsistency( this.sbml_filename , tmp_validater );
		List<SBMLError> errorList = errorLog.getValidationErrors();
		if( errorLog.getNumErrors() != 0 ){
	    	for (SBMLError e : errorList) {
	    		errorMessage = errorMessage + e.getLine() + " " + e.getMessage() + "\n";
	    	}
	    }
	}
	public String getErrorMessage() {
		return errorMessage;
	}
}

package exception;

public class JSBML_ReadException extends Exception{
	private static final long serialVersionUID = 1L;

	public JSBML_ReadException(NullPointerException e){
		super("The input SBML file cannot be read.");
	}
}

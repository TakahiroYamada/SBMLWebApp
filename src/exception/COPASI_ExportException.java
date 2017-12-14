package exception;

public class COPASI_ExportException extends Exception{
	private static final long serialVersionUID = 1L;
	public COPASI_ExportException(){
		super("Temporarilly export of your SBML file is failed.");
	}
}

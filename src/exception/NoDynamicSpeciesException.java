package exception;

public class NoDynamicSpeciesException extends Exception{
	private static final long serialVersionUID = 1L;
	public NoDynamicSpeciesException(){
		super("The status of all species is fixed. There is no visualized result.");
	}
}

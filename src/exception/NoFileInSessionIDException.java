package exception;

public class NoFileInSessionIDException extends Exception{
	private static final long serialVersionUID = 1L;
	
	public NoFileInSessionIDException( String fileName){
		super(fileName + " does not exist in this session.");
	}

}

package exception;

public class SessionIDPrefixException extends Exception{
	private static final long serialVersionUID = 1L;
	public SessionIDPrefixException(){
		super("The Session ID is not started with 'ID'.");
	}
}

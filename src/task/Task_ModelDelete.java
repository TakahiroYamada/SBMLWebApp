package task;

import java.io.File;

import com.sun.mail.handlers.image_gif;

import exception.NoFileInSessionIDException;
import exception.SessionIDPrefixException;
import net.arnx.jsonic.JSON;
import parameter.Abstract_Parameter;

public class Task_ModelDelete extends Super_Task {
	private Abstract_Parameter deleteParam;
	public Task_ModelDelete( String message ) throws SessionIDPrefixException, NoFileInSessionIDException{
		deleteParam = JSON.decode( message , Abstract_Parameter.class );
		File deleteFile = new File( deleteParam.getPathToFile());
		
		if( deleteFile.getName().startsWith("ID")){
			File[] files = deleteFile.listFiles();
			this.checkFileInDirectory( files , deleteParam.getFileName());
			
			for( int i = 0 ; i < files.length ; i ++){
				File tmpFile = files[ i ];
				tmpFile.delete();
			}
			deleteFile.delete();
		}
		else{
			throw new SessionIDPrefixException();
		}
	}
	public Abstract_Parameter getDeleteParam() {
		return deleteParam;
	}
	private void checkFileInDirectory(File[] files, String fileName ) throws NoFileInSessionIDException {
		for( int i = 0 ; i < files.length ; i ++){
			if( files[ i ].getName().equals( fileName )){
				return;
			}
		}
		throw new NoFileInSessionIDException( fileName );
		
	}
}

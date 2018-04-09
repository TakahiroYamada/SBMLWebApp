package task;

import java.io.File;

import net.arnx.jsonic.JSON;
import parameter.Abstract_Parameter;

public class Task_ModelDelete extends Super_Task {
	private Abstract_Parameter deleteParam;
	public Task_ModelDelete( String message ){
		deleteParam = JSON.decode( message , Abstract_Parameter.class );
		System.out.println( deleteParam.getPathToFile() );
		File deleteFile = new File( deleteParam.getPathToFile() + "/" + deleteParam.getFileName() );
		deleteFile.delete();
	}
}

package task;

import java.io.IOException;

import net.arnx.jsonic.JSON;
import parameter.Abstract_Parameter;

public class Task_ModelSave extends Super_Task {
	private Abstract_Parameter modelParam;
	public Task_ModelSave( String message ) throws IOException{
		this.modelParam = JSON.decode( message , Abstract_Parameter.class );
		super.saveFile( modelParam.getPathToFile() , modelParam.getFileName() , modelParam.getFileString() );
	}

}

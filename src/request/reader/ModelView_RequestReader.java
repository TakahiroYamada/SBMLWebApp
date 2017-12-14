package request.reader;


import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;

import general.task_type.Task_Type;
import net.arnx.jsonic.JSON;
import parameter.Abstract_Parameter;

public class ModelView_RequestReader {
	private Abstract_Parameter modelviewParam;
	public ModelView_RequestReader(List<FileItem> fields , String path , String sessionId ){
		this.modelviewParam = new Abstract_Parameter();
		this.modelviewParam.setPathToFile( path );
		this.modelviewParam.setType( Task_Type.MODEL_VIEW );
		this.modelviewParam.setSessionInfo( sessionId );
		
		Iterator< FileItem > it = fields.iterator();
		while( it.hasNext() ){
			FileItem item = it.next();
			if( item.getFieldName().equals("file")){
				this.modelviewParam.setFileString( item.getString() );
				this.modelviewParam.setFileName( item.getName());
			}
		}
	}
	public String getModelviewParamAsJSON() {
		return JSON.encode( modelviewParam );
	}
}

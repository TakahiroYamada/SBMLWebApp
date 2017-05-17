package servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.COPASI.UIntStdVector;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import analyze.steadystate.SteadyState_COPASI;

/**
 * Servlet implementation class SteadyState_Servlet
 */
public class SteadyState_Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String filename;
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String path = getServletContext().getRealPath("/tmp");
		response.setContentType("text/plane");
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload( factory );
		
		try {
			List<FileItem> fields = upload.parseRequest( request );
			Iterator< FileItem > it = fields.iterator();
			while( it.hasNext() ){
				FileItem item = it.next();
				if( !item.isFormField()){
					filename = item.getName();
					File tmpDir = new File( path );
					File analyzeFile = new File( path + "/" + filename );
					tmpDir.mkdir();
					try {
						item.write( analyzeFile );
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					SteadyState_COPASI analyzeSteadyState = new SteadyState_COPASI( path + "/result_steadystate.txt", analyzeFile.getPath());
					response.setHeader("Content-Disposition", "attachment; filename=result_steadystate.txt");
					ServletContext ctx = getServletContext();
					InputStream is = ctx.getResourceAsStream( "/tmp/result_steadystate.txt");
					int read = 0;
					byte[] bytes = new byte[ 1024 ];
					OutputStream os = response.getOutputStream();
					while(( read = is.read(bytes)) != -1 ){
						os.write( bytes , 0 , read);
					}
					os.flush();
					os.close();
				}
			}
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

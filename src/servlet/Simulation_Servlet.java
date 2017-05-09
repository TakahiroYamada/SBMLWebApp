package servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import analyze.simulation.Simulation_COPASI;

/**
 * Servlet implementation class simulation_servlet
 */
public class Simulation_Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private String filename;   
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub 
		String path = getServletContext().getRealPath("/WEB-INF/tmp");
		response.setContentType("text/csv");
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload( factory );
		
		try {
			List<FileItem> fields = upload.parseRequest( request );
			Iterator< FileItem > it = fields.iterator();
			while( it.hasNext()){
				FileItem item = it.next();
				if(!item.isFormField()){
					filename = item.getName();
					File newFile = new File( path + "/" + filename);
					File tmpDir = new File( path );
					tmpDir.mkdir();
					try {
						item.write( newFile );
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Simulation_COPASI simCOPASI = new Simulation_COPASI( newFile.getPath());
					simCOPASI.getTimeSeries().save( path + "/result.csv" , false , ",");
					response.setHeader("Content-Disposition", "attachment; filename=result.csv");
					ServletContext ctx = getServletContext();
					InputStream is = ctx.getResourceAsStream("WEB-INF/tmp/result.csv");
					
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

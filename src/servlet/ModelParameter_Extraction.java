package servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.ha.session.SessionIDMessage;

import com.thoughtworks.xstream.io.path.Path;

import general.unique_id.UniqueId;

/**
 * Servlet implementation class ModelParameter_Extraction
 */
public class ModelParameter_Extraction extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private String sessionId;
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.sessionId = UniqueId.getUniqueId();
		
	}
}

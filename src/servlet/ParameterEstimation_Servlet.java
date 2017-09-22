package servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.sbml.jsbml.SBMLException;

import analyze.parameter.ParameterEstimation_COPASI;
import analyze.simulation.Simulation_COPASI;
import beans.modelparameter.ModelParameter_Beans;
import beans.parameter.ParameterEstimation_AllBeans;
import coloring.Coloring;
import errorcheck.SBML_ErrorCheck;
import exception.NoDynamicSpeciesException;
import general.unique_id.UniqueId;
import manipulator.SBML_Manipulator;
import net.arnx.jsonic.JSON;
import parameter.ParameterEstimation_Parameter;
import parameter.Simulation_Parameter;

/**
 * Servlet implementation class ParameterEstimation_Servlet
 */
public class ParameterEstimation_Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String sessionId;
	private String path;
	private File SBMLFile;
	private File ExperimentFile;
	private List<FileItem> fields;
	private ParameterEstimation_AllBeans paramBeans;
	private ParameterEstimation_Parameter paramestParam;
	private ModelParameter_Beans sbmlParam;
	private Simulation_Parameter paramSim;
	private Coloring colorOfVis;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			sessionCheck(request, upload);
		} catch (FileUploadException e) {
			response.setStatus( 400 );
			PrintWriter out = response.getWriter();
			out.print( e.getMessage());
			out.flush();
			e.printStackTrace();
			return;
		}
		if (sessionId.equals("")) {
			sessionId = UniqueId.getUniqueId();
		}
		path = getServletContext().getRealPath("/tmp/" + sessionId);
		paramBeans = new ParameterEstimation_AllBeans();
		configureAnalysisEmvironment(request, upload);
		
		//check the validity of given SBML model
		//SBML_ErrorCheck errorCheck = new SBML_ErrorCheck( this.SBMLFile.getPath()  );
		//errorCheck.checkError();
		
		SBML_Manipulator sbml_Manipulator = new SBML_Manipulator( SBMLFile );
		try {
			sbml_Manipulator.editModelParameter( this.sbmlParam );
		} catch (SBMLException e) {
			response.setStatus( 400 );
			PrintWriter out = response.getWriter();
			out.print( e.getMessage());
			out.flush();
			e.printStackTrace();
			return;
		} catch (IllegalArgumentException e) {
			response.setStatus( 400 );
			PrintWriter out = response.getWriter();
			out.print( e.getMessage());
			out.flush();
			e.printStackTrace();
			return;
		} catch (XMLStreamException e) {
			response.setStatus( 400 );
			PrintWriter out = response.getWriter();
			out.print( e.getMessage());
			out.flush();
			e.printStackTrace();
			return;
		}
		// COPASI ParameterEstimation Execution
		ParameterEstimation_COPASI paramEstCopasi = new ParameterEstimation_COPASI(paramestParam, SBMLFile,
				ExperimentFile , sbmlParam );
		paramEstCopasi.estimateParameter();
		
		// The simulation in order to visualize in Client side using parameters
		// before parameter fitting
		try {
			simulateFittedResult(paramEstCopasi);
		} catch (NoDynamicSpeciesException e) {
			response.setStatus( 400 );
			PrintWriter out = response.getWriter();
			out.print( e.getMessage() );
			out.flush();
			e.printStackTrace();
			return;
		}

		// Experiment data is set to beans
		paramBeans.setExpDataSets(paramEstCopasi.configureParamEstBeans(colorOfVis));
		paramBeans.setUpdateParam(paramEstCopasi.configureParameterUpdateInformationBeans());

		// Session ID is set
		paramBeans.setSessionId(this.sessionId);
		
		paramBeans.setModelParameters( sbml_Manipulator.getModelParameter() );
		//paramBeans.setWarningText( errorCheck.getErrorMessage());
		// response to client side sending JSON format data
		String jsonParamEst = JSON.encode(paramBeans, true);
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print(jsonParamEst);
	}

	private void simulateFittedResult(ParameterEstimation_COPASI paramEstCopasi) throws NoDynamicSpeciesException {
		// Simulation condition is set
		int endTime = (int) Math.ceil(paramEstCopasi.getTimeData().get(paramEstCopasi.getTimeData().size() - 1));
		this.paramSim = new Simulation_Parameter();
		this.paramSim.setLibrary("copasi");
		this.paramSim.setNumTime(endTime);
		this.paramSim.setEndTime(endTime);

		colorOfVis = new Coloring((int) paramEstCopasi.getDependentData().size(), 1.0);
		// Simulation execution using parameters before and after fitting
		Simulation_COPASI beforeFitting = new Simulation_COPASI(SBMLFile.getPath(), paramSim);
		paramBeans.setBeforeFitting(beforeFitting.configureSimulationBeans(colorOfVis));
		Simulation_COPASI afterFitting = new Simulation_COPASI(paramEstCopasi.getDataModel(), paramSim);
		paramBeans.setAfterFitting(afterFitting.configureSimulationBeans(colorOfVis));
		try {
			paramEstCopasi.getDataModel().exportSBML( path + "/Updated_" + SBMLFile.getName() );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sessionCheck(HttpServletRequest request, ServletFileUpload upload) throws FileUploadException {

		this.fields = upload.parseRequest(request);
		Iterator<FileItem> it = fields.iterator();
		while (it.hasNext()) {
			FileItem item = it.next();
			if (item.getFieldName().equals("SessionId")) {
				sessionId = item.getString();
			}
		}

	}

	private void configureAnalysisEmvironment(HttpServletRequest request, ServletFileUpload upload) {
		// TODO Auto-generated method stub
		paramestParam = new ParameterEstimation_Parameter();
		Iterator<FileItem> it = this.fields.iterator();
		while (it.hasNext()) {
			FileItem item = it.next();
			// SBML Model file is inputed
			if (item.getFieldName().equals("SBMLFile")) {
				String filename = item.getName();
				SBMLFile = new File(path + "/" + filename);
				File tmpDir = new File(path);
				tmpDir.mkdirs();
				try {
					item.write(SBMLFile);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// Experiment Data file is inputed
			else if (item.getFieldName().equals("ExpFile")) {
				String filename = item.getName();
				this.ExperimentFile = new File(path + "/" + filename);
				try {
					item.write(ExperimentFile);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
			else if( item.getFieldName().equals("parameter")){
				this.sbmlParam = JSON.decode( item.getString() , ModelParameter_Beans.class );
			}
			else if (item.getFieldName().equals("algorithm")) {
				paramestParam.setMethod(item.getString());
			}
			// If Leven-Berg , Nelder or Particle Swarm is selected
			else if (item.getFieldName().equals("itermax")) {
				paramestParam.setIteLimit(Integer.parseInt(item.getString()));
			} else if (item.getFieldName().equals("tolerance")) {
				paramestParam.setTolerance(Double.parseDouble(item.getString()));
			}
			// If GA is selected
			else if (item.getFieldName().equals("generation")) {
				paramestParam.setNumGenerations(Integer.parseInt(item.getString()));
			} else if (item.getFieldName().equals("population")) {
				paramestParam.setPopSize(Integer.parseInt(item.getString()));
			}
			// If Particle Swarm is selected
			else if (item.getFieldName().equals("swarmsize")) {
				paramestParam.setSwarmSize(Integer.parseInt(item.getString()));
			} else if (item.getFieldName().equals("stdDeviation")) {
				paramestParam.setStdDeviation(Double.parseDouble(item.getString()));
			} else if (item.getFieldName().equals("randomNumGenerator")) {
				paramestParam.setRandomNumGenerator(Integer.parseInt(item.getString()));
			} else if (item.getFieldName().equals("seed")) {
				paramestParam.setSeed(Integer.parseInt(item.getString()));
			}
		}
	}
}

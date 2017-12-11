package request.reader;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;

import beans.modelparameter.ModelParameter_Beans;
import net.arnx.jsonic.JSON;
import parameter.ParameterEstimation_Parameter;

public class ParameterEstimation_RequestReader {
	private ParameterEstimation_Parameter paramestParam;
	public ParameterEstimation_RequestReader( List< FileItem > fields){
		this.paramestParam = new ParameterEstimation_Parameter();
		Iterator<FileItem> it = fields.iterator();
		while (it.hasNext()) {
			FileItem item = it.next();
			// SBML Model file is inputed
			if (item.getFieldName().equals("SBMLFile")) {
				this.paramestParam.setFileString( item.getString() );
			}
			// Experiment Data file is inputed
			else if (item.getFieldName().equals("ExpFile")) {
				this.paramestParam.setAnsData( item.getString() );
			} 
			else if( item.getFieldName().equals("parameter")){
				this.paramestParam.setSbmlParam( JSON.decode( item.getString() , ModelParameter_Beans.class ));
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
	public String getparamEstParamAsJSON() {
		return JSON.encode( paramestParam );
	}
}

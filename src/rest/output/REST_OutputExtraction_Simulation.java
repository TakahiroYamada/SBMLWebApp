package rest.output;

import beans.simulation.Simulation_AllBeans;
import net.arnx.jsonic.JSON;

public class REST_OutputExtraction_Simulation {
	private String responseData;
	private Simulation_AllBeans simResult;
	public REST_OutputExtraction_Simulation( String responseData ){
		this.responseData = responseData;
		simResult = JSON.decode( responseData , Simulation_AllBeans.class );
	}
	public String getCSVFormat(){
		StringBuilder sb = new StringBuilder();
		sb.append("time,");
		
		// For Header of CSV
		for( int i = 0 ; i < simResult.getData().length ; i ++){
			if( i != ( simResult.getData().length - 1) ){
				sb.append( simResult.getData()[ i ].getLabel() + ",");
			}
			else{
				sb.append( simResult.getData()[ i ].getLabel() + "\n");
			}
		}
		int numTime = simResult.getData()[ 0 ].getData().length;
		for( int i = 0 ; i < numTime ; i ++ ){
			sb.append( simResult.getData()[ 0 ].getData()[ i ].getX() + ",");
			
			for( int j = 0 ; j < simResult.getData().length ; j ++){
				if( j != (simResult.getData().length - 1) ){
					sb.append( simResult.getData()[ j ].getData()[ i ].getY() + ",");
				}
				else{
					sb.append( simResult.getData()[ j ].getData()[ i ].getY() + "\n");
				}
			}
		}
		return sb.toString();
	}
}

package analyze.simulation;

import jp.ac.keio.bio.fun.libsbmlsim.*;


public class Simulation_libSBMLsim {
	static{
		System.loadLibrary("sbmlsimj");
	}
	public Simulation_libSBMLsim( String sbmlFile ){
		myResult r = libsbmlsim.simulateSBMLFromFile( sbmlFile , 1.0, 0.1, 1, 0, libsbmlsim.MTHD_RUNGE_KUTTA_FEHLBERG_5, 0);
	}
}

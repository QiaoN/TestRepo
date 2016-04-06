package dcs.gridscheduler.model;

public enum ServerStatus {
    
	/**
	 * 	(Remote) Server is still running. This state is identified by original server - not replicas
	 * */
	Running,
	
	/**
	 * 	(Remote) Server is died. This state is identified by original server - not replicas
	 * */
	Died
}

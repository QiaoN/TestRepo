package dcs.gridscheduler.model;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMServerInterface extends Remote {
	/**
	 *  load job to cluster server (1 job per time)
	 * */
	public void loadJob (Job job) throws RemoteException;
}

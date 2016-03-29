package dcs.gridscheduler.model;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * The rm off load method. off load job from rm back to gs node.
 * This interface can be integrated to SyncServerInterface with the same offLoadJob method.
 * Or keep it separate for RM-GS communication only  
 * */
public interface RMOffLoadInterface extends Remote {
	/**
	 *  off load job from cluster server back to GS Node (1 job per time)
	 * */
	public void offLoadJob (Job job) throws RemoteException;
}

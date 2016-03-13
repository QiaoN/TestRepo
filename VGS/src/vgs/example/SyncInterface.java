package vgs.example;

import java.rmi.Remote;

public interface SyncInterface extends Remote {
	
	/** get status of a gs node. Return the queue buffer size of node*/
	public int getStatus() throws java.rmi.RemoteException;
	
	/**The RMI keyword*/
	public final static String rmiKeyword = "NodeStatus";
}

package dcs.gridscheduler.model;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *  Here is RMI interface using in communication between VGS Nodes
 * */ 
public interface SyncServerInterface extends Remote{
	
	/**
	 *  heartbeat - update the status of sending node (alive or not); update it's current workloads
	 * */
	public void heartBeat (int remoteID, List<Job> processJobList) throws RemoteException;
	
	/**
	 * 	When remote server is back, it will do registration procedure in the current of group servers.
	 *  including remoteServer, remoteJobs and remoteRely
	 * */
	public void recoverFromCrash (String url,int remoteServerID, int currentJobs) throws RemoteException;
	
	/**
	 *  offload jobs to less busy server (1 job per time)
	 * */
	public void offloadJob (int senderID, Job offjob) throws RemoteException;
		
	/**
	 *  feedback when jobs done
	 * */
	public void finishJob (long jobID) throws RemoteException;
	
	/**
	 * 	When a server dies, it is rejected out the list in other server. When it's back, it has to
	 *  send notification to other server to rejoin network.
	 * */
	public void recover (int remoteServerID) throws RemoteException;
	
	/**
	 * 	Here is ACK message from other servers to notify recovered server
	 * */
	public void recoverACK () throws RemoteException;
}

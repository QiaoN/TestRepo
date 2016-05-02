package dcs.gridscheduler.model;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientServerInterface extends Remote{

	/**
	 * 	Client connect to server
	 * */
	public void connectToServer(int clientID, String ClientName) throws RemoteException;
	
	/**
	 * 	Client disconnect
	 * */
	public void disconnect(int clientID)throws RemoteException;
	
	/**
	 * 	Client submit a job
	 * */
	public void addJob(boolean firstTime, Job job) throws RemoteException;
}

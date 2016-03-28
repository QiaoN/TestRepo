package dcs.gridscheduler.model;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client{
	private int ID;
	private String Name;
	private ClientServerInterface serverObject = null;
	private final Logger logger = Logger.getLogger(DistributedServer.class.getName());

	public Client(int clientID,String clientName){
		this.ID = clientID;
		this.Name = clientName;
		// Logger configuration
		logger.setLevel(Level.ALL);
	}
	
	public int getID(){
		return this.ID;
	}
	
	public boolean connectToServer(ServerURL[] serverURL){
		// This client will connect to a random server in the list
		int randomItem = new Random().nextInt(serverURL.length);
		try {
			this.serverObject = (ClientServerInterface)Naming.lookup(serverURL[randomItem].url);
			this.serverObject.connectToServer(this.ID, this.Name);
			logger.log(Level.INFO, "Client -id = "+this.ID+" connect to server "+serverURL[randomItem].url);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			logger.log(Level.SEVERE, "Cannot connect to server "+serverURL[randomItem].url);
			return false;
		}
	}
	
	public void submitAJobToServer(Job job){
		try {
			this.serverObject.addJob(job);
			logger.log(Level.INFO, "Job before submit ID=" + job.getId());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE, "cannot add job to server client ID=" + this.ID+ " Exception ="+ e);
		}
	}
}

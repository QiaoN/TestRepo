package dcs.gridscheduler.model;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client{
	private int ID;
	private String Name;
	private List<ClientServerInterface> serverObjects;
	
	private final Logger logger = Logger.getLogger(DistributedServer.class.getName());

	public Client(int clientID,String clientName){
		this.ID = clientID;
		this.Name = clientName;
		// Logger configuration
		logger.setLevel(Level.ALL);
		this.serverObjects = new ArrayList<ClientServerInterface>();
	}
	
	public int getID(){
		return this.ID;
	}
	
	public boolean connectToServer(ServerURL[] serverURL){
		// This client will connect to a random server in the list
		//
		this.serverObjects.clear();
		for (ServerURL url:serverURL) {
			try {
				ClientServerInterface serverObj = (ClientServerInterface)Naming.lookup(url.url);
				this.serverObjects.add(serverObj);
				serverObj.connectToServer(this.ID, this.Name);
				logger.log(Level.INFO, "Client -id = "+this.ID+" connect to server "+url.url);
			} catch (Exception e) {
				// TODO: handle exception
				logger.log(Level.SEVERE, "Cannot connect to server "+url.url);
			
			}
		}
		if (this.serverObjects.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public void submitAJobToServer(Job job){
		//int serverId = new Random().nextInt(this.serverObjects.size());
		long jobID = job.getId();
		int serverSize = this.serverObjects.size();
		int serverId = Math.toIntExact(jobID % serverSize); 
		try {
			this.serverObjects.get(serverId).addJob(job);
			logger.log(Level.INFO, "Job before submit ID=" + job.getId());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE, "cannot add job to server client ID=" + this.ID+ " Exception ="+ e);
		}
	}
}

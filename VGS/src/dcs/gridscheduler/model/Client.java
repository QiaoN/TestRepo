package dcs.gridscheduler.model;

import java.rmi.Naming;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client{
	private int ID;
	private ClientServerInterface serverObject = null;
	private final Logger logger = Logger.getLogger(DistributedServer.class.getName());


	public Client(int clientID){
		this.ID = clientID;
		
		// Logger configuration
		logger.setLevel(Level.ALL);
	}
	
	public int getID(){
		return this.ID;
	}
	
	public boolean connectToServer(String[] serverURL){
		// This client will connect to a random server in the list
		int randomItem = new Random().nextInt(serverURL.length);
		try {
			this.serverObject = (ClientServerInterface)Naming.lookup(serverURL[randomItem]);
			logger.log(Level.INFO, "Client -id = "+this.ID+" connect to server "+serverURL[randomItem]);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			logger.log(Level.SEVERE, "Cannot connect to server "+serverURL[randomItem]);
			return false;
			
		}
	}
	
}

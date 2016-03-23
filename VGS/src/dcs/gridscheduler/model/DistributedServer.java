package dcs.gridscheduler.model;

import java.net.MalformedURLException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DistributedServer extends UnicastRemoteObject implements SyncServerInterface{

	
	//Job Queue
	private ConcurrentLinkedQueue<Job> jobQueue;
	
	/*Servers*/
	private int ID; // server's ID
	private String URL; // server's address
	private Map <Integer, SyncServerInterface> remoteServer; // remoteObject
	private Map <Integer, Integer> remoteJobQueue; // total current Job at remote server
	private List ServerULR; // Co can khong nhi - co SyncServerInterface o tren roi... -> co the convert ra list of server
	private int totalJob; // total Job in queue + in clusters; when a job done --
	
	/*RM*/
	
	/*Client*/
	
	
	public DistributedServer(int serverID,ServerURL[] listURL) throws RemoteException{
		// import server's id and url
		this.ID = serverID;
		assert (listURL.length >0):"The number of server should be > 0";
		for (ServerURL object : listURL){
			if (object.id == ID)
				this.URL = object.url;		
		}
		// Remote Server
		remoteServer = new HashMap<Integer,SyncServerInterface>();
	}
	public void starting() throws RemoteException{
		// Binding with the name is its URL
				try {
					System.out.println("url ="+this.URL);
					Naming.rebind(this.URL, this);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println ("starting exception ="+e.toString());
				}
	}
	/**
	 * 	Connect to other servers (Schedulers) 
	 * */
	public void connectToRemoteServers (ServerURL[] listURL) throws RemoteException{
		assert (listURL.length >0); // Check list URL 
		for (int i =0; i < listURL.length; i++)
		{
			if (listURL[i].id != this.ID){
			try {
				//System.out.println ("connectToRemoteServers:: id "+listURL[i].id+" url "+listURL[i].url);
				remoteServer.put(listURL[i].id, (SyncServerInterface)Naming.lookup(listURL[i].url));
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println ("connectToRemoteServers exception" + e.toString());
				// Add to Logger
			}
			}
		}
	}
	
	/**
	 *  Return server ID
	 * */
	public int getID(){
		return this.ID;
	}
	
	public void sendHeartBeat (int receiverID, int senderID) throws RemoteException{
		remoteServer.get(receiverID).heartBeat(senderID);
	}
	
	/**
	 *  heartbeat - update the status of sending node (alive or not); update it's current workloads
	 * */
	
	public void heartBeat (int currentWorkloads) throws RemoteException{
		System.out.println("receiverID =" +this.ID +" passing senderID ="+currentWorkloads);
	}
	
	/**
	 *  heartbeat - reply ACK message to sender
	 * */
	public void heartBeatACK (int remoteServerID) throws RemoteException{}
	
	/**
	 *  offload jobs to less busy server (1 job per time)
	 * */
	public void offloadJob (Job offjob) throws RemoteException{}
		
	/**
	 *  feedback when jobs done
	 * */
	public void finishJob (long jobID) throws RemoteException{}
	
	/**
	 * 	When a server dies, it is rejected out the list in other server. When it's back, it has to
	 *  send notification to other server to rejoin network.
	 * */
	public void recover (int remoteServerID) throws RemoteException{}
	
	/**
	 * 	Here is ACK message from other servers to notify recovered server
	 * */
	public void recoverACK () throws RemoteException{}
}

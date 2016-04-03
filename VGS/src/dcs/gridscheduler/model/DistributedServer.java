package dcs.gridscheduler.model;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.Handler;
import java.util.logging.FileHandler;
import java.util.logging.ConsoleHandler;
import java.util.logging.SimpleFormatter;

public class DistributedServer extends UnicastRemoteObject implements SyncServerInterface, ClientServerInterface{

	//Job Queue
	private ConcurrentLinkedQueue<Job> jobQueue;
	
	/*Servers*/
	private int ID; // server's ID
	private String URL; // server's address
	private boolean running;
	private final Logger logger = Logger.getLogger(DistributedServer.class.getName());
	private int totalJob=0; // total Job in queue + in clusters; when a job done --
	private List<Job> waitingJobQueue; // Queue of comming job from clients.
	
	/*Remote Servers*/
	private List ServerULR; // Co can khong nhi - co SyncServerInterface o tren roi... -> co the convert ra list of server
	private Map <Integer, SyncServerInterface> remoteServer; // remoteObject
	private Map <Integer, Integer> remoteJobs; // total current Job at remote server
	private Map <Integer, List<Job>> remoteJobQueues;
	
	/* Mechanism of Updating heart beat state - starting with 0. when send heartbeat successful the state
	 * will be update 0. If cannot send heartbeat the state will be minused by 1 (-1,-2,..). If there is a remote server die
	 * the state will be = -2 or less --> Timer will trigger process leave the 
	 * remote server out. In case that the remote reply heartbeat */
	private Map <Integer, Integer> remoteReply; // response heartbeat
	private Timer heartBeatTimer; // send heartbeat at regular intervals. time t = 10 ms ?
	
	/*RM*/
	private Map <Integer, Integer> RMJobs; // total current Jobs at each RM.
	/*This object will control the interface between Scheduler (Distributed Server) and its clusters
	 * When a DistributedServer get a job it will add directly to clusterManager.
	 * That's all. clusterManager will do commnucation with clusters.
	 * */
	private ClusterManager clusterManager; 
	
	
	/*Client*/
	private Map <Integer,String> clientList; // the list of connected client
	
	/*Test*/
	private int loopNumber = 0; // Increase after each loop of sending heartbeat
	
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
		
		// Map
		remoteJobs = new HashMap<Integer, Integer>();
		remoteJobQueues = new HashMap<Integer,List<Job>>();
		remoteReply = new HashMap<Integer, Integer>();
		RMJobs = new  HashMap <Integer, Integer>();
		
		// Client
		clientList = new HashMap<Integer, String>();
		waitingJobQueue = new ArrayList<Job>();
				
		// Logger configuration
		logger.setLevel(Level.ALL);
		// Log to a file when deploying to AWS. Nomarlly, we put log to console.
		/*FileHandler fileHander = null;
		SimpleFormatter simpleFormatter = new SimpleFormatter();
		try {
			 fileHander = new FileHandler("./vgs-"+this.ID+".log");
			 fileHander.setLevel(Level.ALL);
			 fileHander.setFormatter(simpleFormatter);
			 logger.addHandler(fileHander);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE, "Error in setting logger", e);
		}*/
	}
	public void starting() throws RemoteException{
		// Binding with the name is its URL
			try {
				logger.log(Level.INFO,"url ="+this.URL);
				Naming.rebind(this.URL, this);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.log (Level.SEVERE, "starting exception ="+e.toString());
			}
		// After starting server, create workers and run (clusterManager)
		/*Test communication from client to GS, then to ClusterManger, to RM and nodes */
		// ClusterManager
			try {
				clusterManager = new ClusterManager(2,5,this.URL+"-cl", this);
			} catch (Exception e) {
				// TODO: handle exception
				logger.log(Level.SEVERE, "ClusterManager Exception ="+ e);
			}

			
		//create heartBeatTimer but wait for other remote servers launch then start heartBeatTimer
			heartBeatTimer = new Timer();
			heartBeatTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						/* Test case - When a remote server dies -- other servers have to detect and reject
						 * In this case, 103 will stop and 101 and 102 detect and reject 103.
						 * */
						/*loopNumber++;
						if ((loopNumber==3) && (ID == 103)){
							logger.log(Level.INFO, "loopNumber =5 and stop server 103");
							// Stop remote server ID = 103
							stopRemoteServer();
						}*/
						
						// Send heartbeat message to remote Servers
						for (Map.Entry<Integer, SyncServerInterface> entry : remoteServer.entrySet()){
							sendHeartBeat (entry.getKey(), ID);
						}
						// In ra o day xem remote servers co bao nhieu phan tu
						logger.log(Level.INFO,"Send heartbeat message repeatedly. Sender: "+ID);
						// Check whether any remote server works or not
						for (Map.Entry<Integer, Integer> object : remoteReply.entrySet()){
							if (object.getValue() <= -2) removeRemoteServer(object.getKey());
						}
					} catch (Exception e) {
						// TODO: handle exception
						logger.log(Level.SEVERE," Cannot send heartbeat message repeatedly. Exception: "+ e);
						}
					}
				},100,1000);// Delay after 0.1s and repeat in 0.5s
	}
	/**
	 * 	Test case - Remove a remote server by rebind his url --> other server cannot reach to the server
	 *  Heartbeat is changed. And should remove remoteserver list in order to disconnected server
	 *  cannot update in other servers (if this test's successful, test remove info of disconnected
	 *  server in other servers)
	 * @throws MalformedURLException 
	 * @throws RemoteException 
	 * @throws InterruptedException 
	 * */
	public void stopRemoteServer() throws RemoteException, MalformedURLException, InterruptedException{
		Naming.rebind("abc",this);
		this.remoteServer.clear();
		/*Removes the remote object, obj, from the RMI runtime. 
		 * If successful, the object can no longer accept incoming RMI calls
		 */
		UnicastRemoteObject.unexportObject(this, true);
		// Never run message repeatedly 
		Thread.sleep(10000000);
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
				// Create the map - total number of jobs in each remote server - starting with 0
				remoteJobs.put(listURL[i].id,0);
				// Create the map - status of remote server updated through heartbeat - starting with 0
				remoteReply.put(listURL[i].id,0);
				logger.log (Level.INFO,"connectToRemoteServers "+listURL[i].id+" by server " + this.ID);
			} catch (Exception e) {
				// TODO: handle exception
				logger.log (Level.SEVERE,"connectToRemoteServers exception" + e.toString());
				// Cannot connect this server --> remove it
				this.removeRemoteServer(listURL[i].id);
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
	/**
	 * 	HeartBeat Tasks run repeatedly
	 * */
	public void heartBeatIterator(){
		
	}
	/**
	 * 	Send heart beat to remote Servers
	 * */
	public void sendHeartBeat (int receiverID, int senderID) throws RemoteException{
		try {
			// Test with number 10
			remoteServer.get(receiverID).heartBeat(senderID, 10, this.waitingJobQueue);
			
			//remoteServer.get(receiverID).heartBeat(senderID, this.getTotalofJobs());
			// Update heart beat
			remoteReply.put(receiverID, 0);
			logger.log(Level.INFO, senderID+" sends heartbeat to "+ receiverID);
		} catch (Exception e) {
			// TODO: handle exception
			int updateValue = remoteReply.get(receiverID)-1;
			remoteReply.put(receiverID, updateValue);
			logger.log(Level.SEVERE, receiverID +" cannot receive heartbeat message. value ="+ updateValue+ " Exception :"+e.toString());
		}
	}
	
	/**
	 * 	When a remote server crashed, the server will remove it out server's lists: remoteServer, remoteJobs and remoteReply.
	 * */
	public void removeRemoteServer (int remoteServerID){
		try{
			remoteServer.remove(remoteServerID);
			remoteJobs.remove(remoteServerID);
			remoteReply.remove(remoteServerID);
			logger.log(Level.INFO, remoteServerID +" is removed by ID="+this.ID);
			
			List<Job> resignJobs = remoteJobQueues.get(remoteServerID);
			
			//Handle all server IDs, all server have the same order of IDs.
			List<Integer> serverIDs = new ArrayList<Integer>(remoteServer.keySet());
			serverIDs.add(this.ID);
			Collections.sort(serverIDs);
			
			//Calculation
			Integer serverSize = serverIDs.size();
			Integer selfIndex = serverIDs.indexOf(this.ID);
			Integer rangeOfJobSize = resignJobs.size()/serverSize;
			List<Job> resignSelfJobList = new ArrayList<Job>();
			if (selfIndex == (serverSize-1)) {
				Integer finalRangeIndex = 0;
				if(resignJobs.size()>0) {
					finalRangeIndex = resignJobs.size()-1;
				}
				resignSelfJobList = resignJobs.subList((selfIndex*rangeOfJobSize), finalRangeIndex);
			} else {
				resignSelfJobList = resignJobs.subList((selfIndex*rangeOfJobSize), ((selfIndex+1)*rangeOfJobSize));
			}
			
			for (Job j: resignSelfJobList) {
				addJob(j);
			}
			remoteJobQueues.remove(remoteServerID);
			
		}catch(Exception e){
			logger.log(Level.SEVERE, remoteServerID +" cannot be removed - Exception: "+e);
		}
	}
	/** 
	 * 	The crashed server re-registers in remote servers
	 * */
	public void reRegisterInRemoteServers() throws RemoteException{
		for (Map.Entry<Integer,SyncServerInterface>entry:remoteServer.entrySet()){
			// Temporary value of current jobs = 10
			logger.log(Level.INFO, "reRegisterInRemoteServers remote server is "+entry.getValue());
			entry.getValue().recoverFromCrash(this.URL, this.ID, 10);
		}
	}
	/**
	 * 	When remote server is back, it will do registration procedure in the current of group servers.
	 *  including remoteServer, remoteJobs and remoteRely.
	 *  @parameter url - crashed server
	 *  @parameter remoteServerID - crashed server
	 *  @parameter currentJobs - crashed server
	 * */
	public void recoverFromCrash (String url,int remoteServerID, int currentJobs) throws RemoteException{
		try{
			if ((url.length()>0) && remoteServerID >0){
			// A letter from remote server to current server "please re-add me!"	
			remoteServer.put(remoteServerID, (SyncServerInterface)Naming.lookup(url));
			remoteJobs.put(remoteServerID,currentJobs);
			remoteReply.put(remoteServerID,0);
			logger.log(Level.INFO, this.ID+" is back and "+remoteServerID +" is re-added");
			}
			else logger.log(Level.SEVERE, "URL or remote server ID is not validated");
		}catch(Exception e){
			logger.log(Level.SEVERE, remoteServerID +" cannot be added - Exception: "+e);
		}
	}
	/**
	 *  heartbeat - update the status of sending node (alive or not); update it's current workloads
	 * */
	
	public void heartBeat (int remoteID, int currentWorkloads, List<Job> processJobQuese){
		// Test
		logger.log(Level.INFO,"receiverID =" +this.ID +" passing senderID= "+remoteID +". workload= "+currentWorkloads);
		// update the remote server with its current workloads
		remoteJobs.put(remoteID, currentWorkloads);
		remoteJobQueues.put(remoteID, processJobQuese);
		for (Map.Entry <Integer, List<Job>> entry : remoteJobQueues.entrySet()){
			System.out.println("Node ID:"+ this.ID + "remote Node ID:" + entry.getKey() + "job Queue:" + entry.getValue());
		}
	}
	
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
	
	/**
	 * 	Update job queue by job from client, RM or other remote server
	 * */
	public boolean updateJobQueue(Job comingJob){
		if(jobQueue != null){
			jobQueue.add(comingJob);
			logger.log(Level.INFO, this.ID + " add job "+comingJob.toString());
			return true;
		} else logger.log(Level.SEVERE,"Job Queue is not created");
		return false;
	}
	
	/**
	 * 	Get a job from Job Queue before sending to RM or offload to other remote servers
	 * */
	public Job getJobFromQueue (){
		if(jobQueue.isEmpty()!=true){
			Job j = jobQueue.poll();
			logger.log(Level.INFO, this.ID + " retrieve a job "+j.toString());
			return j;
		} else logger.log(Level.SEVERE,"Job Queue is empty");
		return null;
	}
	/**
	 * 	Get a total of Jobs in queue + RMs. 
	 * */
	public int getTotalofJobs (){
		int total = 0;
		if (RMJobs != null){
		for (Map.Entry<Integer, Integer> entry :RMJobs.entrySet()){
			total+= entry.getValue();
		}
		total+= jobQueue.size();
		logger.log(Level.INFO, "total jobs in queue + RMs ="+total);
		return total;
		} else logger.log(Level.SEVERE, "RMJobs is null");
			return 0;
	}
	
	/**
	 * 	Client connect to server
	 * */
	public void connectToServer(int clientID, String ClientName) throws RemoteException{
		// add client to List
		if ((clientID >0) && (ClientName.length()>0)){
		clientList.put(clientID, ClientName);
		logger.log(Level.INFO, "add client with ID = " + clientID);
		}
		else logger.log(Level.SEVERE, "something wrong with client ID = " + clientID);

	}
	
	/**
	 * 	Client disconnect
	 * */
	public void disconnect(int clientID)throws RemoteException{
		// remove a client
		try {
			if (clientID>0)
				 clientList.remove(clientID);
		logger.log(Level.INFO, "remove client with ID = "+clientID);
		} catch (Exception e) {
			// TODO: handle exception
		logger.log(Level.SEVERE, "cannot remove client with ID = "+clientID);
		}	
	}
	
	/**
	 * 	Client submit a job
	 * */
	public void addJob(Job job) throws RemoteException{
		// add a job to Queue
		if (job!=null){
			job.assigendGSNode = this.ID;
			waitingJobQueue.add(job);
			// Add this job to clusterManager queue job.
			clusterManager.addJob(job);
			logger.log(Level.INFO, "add job with ID = " +job.getId());
		} else 
			logger.log(Level.SEVERE, "cannot add job with ID = "+job.getId());
	}
	/**
	 * 	Remove a finished job from queue
	 * */
	public void removeJob (Job job){
		//The job send back from cluster are not the same object anymore
		//Have to check id and remove base on that
		for (Job j: waitingJobQueue) {
			if (j.getId() == job.getId()) {
				waitingJobQueue.remove(j);
				break;
			}
		}
		System.out.println("Node ID:"+ this.ID + "waiting job Queue:" + waitingJobQueue);
	}
}

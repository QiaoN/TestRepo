package dcs.gridscheduler.model;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class should be a property of gs node. 
 * This class help gs node to manage and communicate with its clusters.
 */
public class ClusterManager implements ClusterManagerInterface, Runnable {
	//TODO: handle multiple clusters
	private List <Cluster> clusterList;
	public ConcurrentLinkedQueue<Job> jobProcessQueue;
	public ConcurrentLinkedQueue<Job> jobFinishedQueue;
	private static String clusterPrefix = "cluster";
	private String gsURL;
	private DistributedServer gsNode;
	private Thread pushingThread;
	private boolean running;
	
	/*gsURL here = Address of Scheduler + "-cl" to distinguish ClusterManager of each Scheduler*/
	public ClusterManager(int clusterNumber, int nodeNumber, String gsURL, DistributedServer gsNode) {
		
		assert(clusterNumber > 0);
		assert(nodeNumber > 0);
		assert(gsURL.length() > 0);
		
		this.gsURL= gsURL;
		this.gsNode = gsNode;
		
		//init job queue
		this.jobProcessQueue = new ConcurrentLinkedQueue<Job>();
		this.jobFinishedQueue = new ConcurrentLinkedQueue<Job>();
		
		//init cluster list
		clusterList = new ArrayList<Cluster>(clusterNumber);
		for (int i = 0; i < clusterNumber; i++) {
			// cluster name = gsURL + clusterPrefix + i to differentiate with cluster of other Cluster Manager of Scheuduler
			String clusterName = this.gsURL+"-"+clusterPrefix + i;
			Cluster c = new Cluster(clusterName, nodeNumber, gsURL);
			clusterList.add(c);
		}
		
		//registry self
		ClusterManagerInterface gsstub;
		try {
			gsstub = (ClusterManagerInterface) UnicastRemoteObject.exportObject(this, 0);
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind(gsURL, gsstub);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Start the pushing thread
		running = true;
		pushingThread = new Thread(this);
		pushingThread.start();
	}
	
	public void addJob(Job job) {
		assert(job != null);
		jobProcessQueue.add(job);
		//pushJob();
	}
	
	//TODO: think about that this method should run automatically or logic triggered 
	public void pushJob() {
		Registry registry;
		String clusterName = mostFreeCluster();
		try {
			registry = LocateRegistry.getRegistry();
		    RMServerInterface clusterstub = (RMServerInterface) registry.lookup(clusterName);	
		    //NO, first in first out -> should use poll
		    Job job = jobProcessQueue.poll();
		    clusterstub.loadJob(job);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void rmOffLoadJob(Job job) throws RemoteException {
		assert(job != null);
		jobProcessQueue.add(job);
		System.out.println("off load job from RM:"+ job.getId());
	}
	
	@Override
	public void rmFinishJob(Job job) throws RemoteException {
		assert(job != null);
		jobFinishedQueue.add(job);
		this.gsNode.removeJob(job);
		System.out.println("rm Job Done: "+ job.getId() );
	    System.out.println("Job status: "+ job.getStatus());

	}
	
	//Helper
	private String mostFreeCluster() {
		Cluster mostFreeCluster = null;
		
		//job management logic inside gs
		//TODO: change the logic if needed.
		for(Cluster c : clusterList) {
			if (mostFreeCluster == null) {
				mostFreeCluster = c;
			} else {
				if (c.getResourceManager().jobQueue.size() < mostFreeCluster.getResourceManager().jobQueue.size() ) {
					mostFreeCluster = c;
				}
			}
		}
		
		return mostFreeCluster.getName();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (running) {
			// push the job
			if (jobProcessQueue.size() > 0) {
				pushJob();
			}
			
			// sleep 1s
			try {
				Thread.sleep(5);
			} catch (InterruptedException ex) {
				assert(false) : "Cluster poll thread was interrupted";
			}
			
		}
	}
	
}

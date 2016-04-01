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
public class ClusterManager implements RMOffLoadInterface {
	//TODO: handle multiple clusters
	private List <Cluster> clusterList;
	public Queue<Job> jobQueue;
	private static String clusterPrefix = "cluster";
	private String gsURL;
	
	/*gsURL here = Address of Scheduler + "-cl" to distinguish ClusterManager of each Scheduler*/
	public ClusterManager(int clusterNumber, int nodeNumber, String gsURL) {
		
		assert(clusterNumber > 0);
		assert(nodeNumber > 0);
		assert(gsURL.length() > 0);
		
		this.gsURL= gsURL;
		
		//init job queue
		this.jobQueue = new ConcurrentLinkedQueue<Job>();
		
		//init cluster list
		clusterList = new ArrayList<Cluster>(clusterNumber);
		for (int i = 0; i < clusterNumber; i++) {
			// cluster name = gsURL + clusterPrefix + i to differentiate with cluster of other Cluster Manager of Scheuduler
			String clusterName = this.gsURL+"-"+clusterPrefix + i;
			Cluster c = new Cluster(clusterName, nodeNumber, gsURL);
			clusterList.add(c);
		}
		
		//registry self
		RMOffLoadInterface gsstub;
		try {
			gsstub = (RMOffLoadInterface) UnicastRemoteObject.exportObject(this, 0);
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind(gsURL, gsstub);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addJob(Job job) {
		assert(job != null);
		jobQueue.add(job);
		pushJob();
	}
	
	//TODO: think about that this method should run automatically or logic triggered 
	public void pushJob() {
		Registry registry;
		String clusterName = mostFreeCluster();
		try {
			registry = LocateRegistry.getRegistry();
		    RMServerInterface clusterstub = (RMServerInterface) registry.lookup(clusterName);	
		    //NO, first in first out -> should use poll
		    Job job = jobQueue.poll();
		    clusterstub.loadJob(job);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void offLoadJob(Job job) throws RemoteException {
		assert(job != null);
		jobQueue.add(job);
		System.out.println("off load job from RM:"+ job.getId());
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
	
}

package dcs.gridscheduler.model;

import java.rmi.RemoteException;

import dcs.gridscheduler.model.Job;
import dcs.gridscheduler.model.ClusterManager;

/**
 * This is a fake gs class for testing cluster manager
 * including job generator and one cluster manager init
 */

public class FakeGridScheduler {
    public static void main(String[] args) {
	  	int jobCount = 15;
    	Job[] jobs = new Job[jobCount]; 
    	for(int i = 0; i < jobCount ; i++)
    	{
	    	long jobDuration = i * 500;
	    	long jobID = Long.valueOf(i);
			try {
				jobs[i] = new Job(jobDuration, jobID);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	ClusterManager cm = new ClusterManager(2,5,"gsURL");
    	
    	for(Job job: jobs) {
    		cm.addJob(job);
    	}
    }
}

package dcs.gridscheduler.model;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 	This class is used for storing the information of replicas
 * */
public class ReplicatedObject {
	private int id;
	private ServerStatus state;
	private ConcurrentLinkedQueue<Job> waitingTaskQueue;
	private ConcurrentLinkedQueue<Job> finishedTaskQueue;
	
	public ReplicatedObject(int serverID, ServerStatus status,ConcurrentLinkedQueue<Job> waitingQueue,ConcurrentLinkedQueue<Job> finishedQueue){
		this.id = serverID;
		this.state = status;
		waitingTaskQueue = waitingQueue;
		finishedTaskQueue = finishedQueue;
	}
	
	public int getID(){
		return this.id;
	}
	
	public ServerStatus getState(){
		return this.state;
	}
	
	public void setStatus(ServerStatus status){
		this.state = status;
	}
	
	public void addJobToWaitingList(Job job){
		waitingTaskQueue.add(job);
	} 
	
	public void removeJobToWaitingList(Job job){
		waitingTaskQueue.remove(job);
	}
	
	public void addJobToFinishedList(Job job){
		finishedTaskQueue.add(job);
	}
	
	public void removeJobToFinishedList(Job job){
		finishedTaskQueue.remove(job);
	}
	
	public void updateWaitingList (Job headOfWaitingQueue,ConcurrentLinkedQueue<Job> waitingQueue){
		
		if(waitingTaskQueue.contains(headOfWaitingQueue)==false){
			waitingTaskQueue = waitingQueue;
		} else {		
		while (true){
	        if(waitingTaskQueue.peek()!=headOfWaitingQueue){
			waitingTaskQueue.poll();
			} else
				break;
		}
		waitingTaskQueue.addAll(waitingQueue);
	  }
	}
	
	public void updateFinishedList (ConcurrentLinkedQueue<Job> finishedQueue){
		finishedTaskQueue.addAll(finishedQueue);
	}
	/*
	 private int id;
	private SyncServerInterface remoteReplica;
	private ServerStatus state;
	private ConcurrentLinkedQueue<Job> waitingTaskQueue;
	private ConcurrentLinkedQueue<Job> finishedTaskQueue;
	
	public ReplicatedObject(int serverID,SyncServerInterface remoteServer, ServerStatus status){
		this.id = serverID;
		this.remoteReplica = remoteServer;
		this.state = status;
		waitingTaskQueue = new ConcurrentLinkedQueue<Job>();
		finishedTaskQueue = new ConcurrentLinkedQueue<Job>();
	}
	
	public int getID(){
		return this.id;
	}
	
	public ServerStatus getState(){
		return this.state;
	}
	
	public void setStatus(ServerStatus status){
		this.state = status;
	}
	
	public void addJobToWaitingList(Job job){
		waitingTaskQueue.add(job);
	} 
	
	public void removeJobToWaitingList(Job job){
		waitingTaskQueue.remove(job);
	}
	
	public void addJobToFinishedList(Job job){
		finishedTaskQueue.add(job);
	}
	
	public void removeJobToFinishedList(Job job){
		finishedTaskQueue.remove(job);
	} */
}

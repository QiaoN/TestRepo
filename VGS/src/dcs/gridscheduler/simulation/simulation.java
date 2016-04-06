package dcs.gridscheduler.simulation;

import java.rmi.RemoteException;

import dcs.gridscheduler.model.*;

public class simulation {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ServerURL[] serverURLList = new ServerURL[]{new ServerURL(101,"192.168.1.1"), new ServerURL(102,"192.168.1.2"),new ServerURL(103,"192.168.1.3"),new ServerURL(104,"192.168.1.4")};
		DistributedServer[] serverArray = new DistributedServer[serverURLList.length];
		for (int i =0; i <serverURLList.length; i++){ 
			 try {
				serverArray[i] = new DistributedServer(serverURLList[i].getID(), serverURLList);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/*Starting - binding with url*/
		for (DistributedServer object : serverArray){
			try {
				// test fault tolerant - 103 will start from new thread.
				//if (object.getID()!=103)
				object.starting();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/*After creating list of server --> connect them*/
		for (DistributedServer object : serverArray){
			try {
				// test fault tolerant - 103 will start from new thread.
				//if (object.getID()!=103)
				object.connectToRemoteServers(serverURLList);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Here");
	}
 
}

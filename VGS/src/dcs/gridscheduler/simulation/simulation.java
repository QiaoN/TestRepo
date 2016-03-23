package dcs.gridscheduler.simulation;

import java.rmi.RemoteException;

import dcs.gridscheduler.model.*;

public class simulation {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ServerURL[] serverURLList = new ServerURL[]{new ServerURL(101,"192.168.1.1"), new ServerURL(102,"192.168.1.2")};
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
				object.starting();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/*After creating list of server --> connect them*/
		for (DistributedServer object : serverArray){
			try {
				object.connectToRemoteServers(serverURLList);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			serverArray[0].sendHeartBeat(serverArray[1].getID(),serverArray[0].getID());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Here");
	}
 
}

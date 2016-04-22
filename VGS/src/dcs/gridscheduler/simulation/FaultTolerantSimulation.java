package dcs.gridscheduler.simulation;

import java.rmi.RemoteException;

import dcs.gridscheduler.model.DistributedServer;
import dcs.gridscheduler.model.ServerURL;

public class FaultTolerantSimulation {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ServerURL[] serverURLList = new ServerURL[]{new ServerURL(101,"192.168.1.1"), new ServerURL(102,"192.168.1.2"),new ServerURL(103,"192.168.1.3")};
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
				// Only start remote server with id =103
				if(object.getID()==103)
				object.starting();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/*After creating list of server --> connect them*/
		for (DistributedServer object : serverArray){
			try {
				// only run someone having id = 103
				if(object.getID()==103){
				object.connectToRemoteServers(serverURLList);
				object.reRegisterInRemoteServers();
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}

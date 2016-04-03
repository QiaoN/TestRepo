package dcs.gridscheduler.simulation;

import java.rmi.RemoteException;
import java.util.logging.Level;

import com.sun.media.jfxmedia.logging.Logger;

import dcs.gridscheduler.model.Client;
import dcs.gridscheduler.model.Job;
import dcs.gridscheduler.model.ServerURL;

public class ClientSimulation {

	public static void main(String[] args) {
		// ServerList
		ServerURL[] serverURLList = new ServerURL[]{new ServerURL(101,"192.168.1.1"), new ServerURL(102,"192.168.1.2"),new ServerURL(103,"192.168.1.3")};

		Client aClient = new Client(202,"Aloha");
		boolean connect = false;
		int loop = 0;
		while (true){
		connect = aClient.connectToServer(serverURLList);
		// connected --> break the while loop
		if (connect ==true) break;
		else loop++;
		// the number of loop = 3 --> break the while loop
		if (loop == 3) break;
		}
		if (loop == 3) System.out.println("Cannot connect to server."); 
		
		// Send job to server. A job with id = 1234, duration = 1000 ms
		//Job aNewJob = new Job(1000,1234);
		//if (connect == true) aClient.submitAJobToServer(aNewJob);
		
		// Send a thousand of workloads to test...
		for (int i= 0; i<1; i++){
			Job bNewJob=null;
			try {
				bNewJob = new Job(1000,i);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (connect == true) aClient.submitAJobToServer(bNewJob);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}

package dcs.gridscheduler.simulation;

import java.rmi.RemoteException;
import java.util.Random;
import java.util.logging.Level;

import com.sun.media.jfxmedia.logging.Logger;

import dcs.gridscheduler.model.Client;
import dcs.gridscheduler.model.ConfigurationReader;
import dcs.gridscheduler.model.Job;
import dcs.gridscheduler.model.ServerURL;

public class ClientSimulation {

	public static void main(String[] args) {
		
		//get id from console id = 110, 120 or 130
		int id = (args.length < 1)? 0 :Integer.valueOf(args[0]);
		int totalJob = (args.length < 2)? 0 :Integer.valueOf(args[1]);
		// ServerList
		//Configuration file 
		String path = "C:\\ec2\\deployment-vdo\\Github\\VGS\\ServerList.csv";
		ConfigurationReader config = new ConfigurationReader(path);
		ServerURL[] serverURLList = config.URLparsing();

		Client aClient = new Client(id,"Client."+Integer.toString(id));
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
		System.out.println("Total number of jobs is "+totalJob);
		for (int i= 0; i<totalJob; i++){
			Job bNewJob=null;
			try {
				//Random job from 300 to 360 seconds
				int duration = new Random().nextInt(360 - 300 + 1) + 300;
				int durationTime = duration*1000;
				//int durationTime = 15000;
				// job id = 1100000 - enough for x*10000 jobs per client
				bNewJob = new Job(durationTime,id*1000+i);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (connect == true) aClient.submitAJobToServer(bNewJob);
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}

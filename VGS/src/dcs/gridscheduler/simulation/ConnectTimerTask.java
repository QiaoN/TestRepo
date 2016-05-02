package dcs.gridscheduler.simulation;

import java.rmi.RemoteException;
import java.util.TimerTask;

import dcs.gridscheduler.model.DistributedServer;
import dcs.gridscheduler.model.ServerURL;

public class ConnectTimerTask extends TimerTask {
    private final DistributedServer server;
    private final ServerURL[] urlList;

    ConnectTimerTask ( DistributedServer gsNode,ServerURL[] urlNodeList)
    {
      this.server = gsNode;
      this.urlList = urlNodeList;
    }

    public void run() {
      //Do stuff
    	try {
			server.connectToRemoteServers(urlList);
			// server crashed in last attempt
			if (server.getPreviousState()==false){
				server.reRegisterInRemoteServers();
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}


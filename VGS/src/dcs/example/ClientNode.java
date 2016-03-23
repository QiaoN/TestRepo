package dcs.example;

import java.rmi.Naming;

public class ClientNode {

	/**proxy object execute remote service */
	protected static SyncInterface remoteStatus = null;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			remoteStatus = (SyncInterface)Naming.lookup(SyncInterface.rmiKeyword);
			int remoteQueueSize = remoteStatus.getStatus();
			if (remoteQueueSize < 0){
				// When remoteQueuSize = -1, it means that there is an internal error happened in remote site
				System.out.println("An error happended. Keep calm and wait");
			}
			else{
				// Storage the value of queue in remote node
				System.out.println("Queue Size is "+remoteQueueSize);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println(e);
			//System.exit(1);
		}
		
	}

}

package dcs.example;

import java.rmi.Naming;

public class ServerNode {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			// Create an instance of the server object
			SyncInterfaceImpl si = new SyncInterfaceImpl();
			System.out.println("Data Server starting ...");
			
			// Tag the server object with rmiKeyword
			Naming.rebind(SyncInterface.rmiKeyword, si);
			System.out.println("Data Server is ready ...");
			} catch (Exception e) {
			// TODO: handle exception
				System.err.println(e);
				//System.exit(1);
		}
	}

}

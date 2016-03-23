package dcs.gridscheduler.model;

public class ServerURL {
	int id;
	String url;
	
	public ServerURL(int serverID,String serverAddress){
		id = serverID;
		url = serverAddress;
	}
	public int getID(){
		return id;
	}
	public String getURL(){
		return url;
	}
}

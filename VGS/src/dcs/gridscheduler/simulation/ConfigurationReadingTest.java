package dcs.gridscheduler.simulation;

import dcs.gridscheduler.model.ConfigurationReader;
import dcs.gridscheduler.model.ServerURL;

public class ConfigurationReadingTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path = "C:\\ec2\\deployment-vdo\\Github\\VGS\\ServerList.csv";
		ConfigurationReader config = new ConfigurationReader(path);
		ServerURL[] array = config.URLparsing();
		
		for (ServerURL entry: array){
			System.out.println(entry.getID()+" "+entry.getURL());
		}
	}

}

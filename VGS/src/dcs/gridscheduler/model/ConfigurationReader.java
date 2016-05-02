package dcs.gridscheduler.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ConfigurationReader {
	private String path;
	private ServerURL[] addressArray;
	
	public ConfigurationReader(String configurationPath){
		//C:\ec2\deployment-vdo\Github\VGS\ServerList.csv
		path = configurationPath;
		addressArray = new ServerURL[5];
	}
	
	public ServerURL[] URLparsing(){
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {
			br = new BufferedReader(new FileReader(path));
			int i =0;
			while ((line = br.readLine()) != null) {

			    // use comma as separator
				String[] address = line.split(cvsSplitBy);

				// id 
				int id = Integer.parseInt(address[0]);
				// address
				String url = address[1];
				addressArray[i] = new ServerURL(id, url);
				
				// increase index
				i++;
				//System.out.println("id = "+ id +" url = "+ url);	
			}
			if (addressArray!=null){
			return addressArray;
			} else return null;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	  }
	}

package dcs.gridscheduler.model;

import java.awt.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

public class SortingAlgorithm {

	/**
	 * 	Return 2 elements are next to the current ID
	 * */
	public ArrayList<Integer> sorting (ArrayList<Integer> list,int currentID) throws Exception{
		Collections.sort(list);
		ArrayList<Integer> smallSublist = new ArrayList<Integer>();
		ArrayList<Integer> bigSublist = new ArrayList<Integer>();
		
        for (Integer i: list){
        	if (i < currentID) 
        	{
        	smallSublist.add(i);
        	} else bigSublist.add(i);		     
        }
        bigSublist.addAll(smallSublist);
        ArrayList<Integer> outcomeList = new ArrayList<Integer>();
        outcomeList.add(bigSublist.get(1));
        outcomeList.add(bigSublist.get(2));
        return outcomeList;
}
	/*public static void main(String[] args) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		        list.add(5);
		        list.add(4);
		        list.add(3);
		        list.add(7);
		        list.add(2);
		        list.add(1);
		        Collections.sort(list);
				ArrayList<Integer> smallSublist = new ArrayList<Integer>();
				ArrayList<Integer> bigSublist = new ArrayList<Integer>();
				
		        for (Integer i: list){
		        	if (i < 7) 
	            	{
	            	smallSublist.add(i);
	            	} else bigSublist.add(i);		     
		        }
		        bigSublist.addAll(smallSublist);
		        for (Integer a : bigSublist){
		        	System.out.println(a);
		        }
			}*/	
	}

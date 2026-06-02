package ga;

import java.util.ArrayList;
import java.util.List;

/**
 * Node class for network
 * 
 * @author Shohag Barman
 * @since October 22, 2021
 */
public class NetNode {
	
	private List<Integer> data;
	
	/**
	 * Space or tab seperated values
	 * 
	 */
	public NetNode(String row){
		String arr[] = row.split(" ");
		data = new ArrayList<Integer>();
		for (String string : arr) {
			data.add(Integer.parseInt(string.trim()));
		}
	}
	/**
	 * Space or tab seperated values
	 * 
	 */
	public NetNode(List<Integer> data){
		this.data = data;
	}

	public List<Integer> getData() {
		return data;
	}
	
	public void negate(){
		List<Integer> newData = new ArrayList<Integer>();
		for(Integer d: data){
			newData.add(d^1);
		}
		
		this.data.clear();
		this.data.addAll(newData);
	}
	
	public void and(NetNode node){
		List<Integer> newData = new ArrayList<Integer>();
		for(int i=0;i<data.size();i++){
			newData.add(this.data.get(i) & node.getData().get(i) );
		}
		
		this.data.clear();
		this.data.addAll(newData);
	}
	
	public void or(NetNode node){
		List<Integer> newData = new ArrayList<Integer>();
		for(int i=0;i<data.size();i++){
			newData.add(this.data.get(i) | node.getData().get(i) );
		}
		
		this.data.clear();
		this.data.addAll(newData);
	}
	
	public NetNode cloneMe(){
		ArrayList<Integer> newData = new ArrayList<Integer>();
		newData.addAll(this.data);
		NetNode netNode = new NetNode(newData);
		
		return netNode;
	}
	
	/**
	 * target = this
	 * @param node, src
	 * @return
	 */
	public int compareAsTarget(NetNode node){
		int diff = 0;
		for(int i=1;i<data.size();i++){
			if(this.data.get(i) != node.data.get(i-1))
				diff++;
		}
		
		return diff;
	}
	
}

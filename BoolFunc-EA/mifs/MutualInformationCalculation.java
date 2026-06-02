package mifs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import mifs.Atribute;
import mifs.AtributeInteger;


/**************************************************************************************************
 * Mutual information with the target gene                                                        *
 *                                                                                                *
 *                                                                                                *
 * @author Shohag Barman                                                                          *
 * Assistant Professor, Dept. of Computer Science and Engineering                                 *                                                              *
 * Pirojpur Science and Technology University                                                     *
 *                                                                                                *                                                                                            *
 **************************************************************************************************/      


public class MutualInformationCalculation {
	
	protected int quantidadeDefeatures;
	private static List<Integer[]> nodes;	
	private static List<Atribute> features;
	private List<AtributeInteger> featuresInteger;
	private static int nodeLength;
	public static String DATA_FILE= "NetworkTransition.txt";
	private static List<String[]> data;
	protected static int nodeSize;

	public List<AtributeInteger> getfeaturesInteger() {
		return featuresInteger;
	}

	public void setNodeSize(int nodeSize) {
		MutualInformationCalculation.nodeSize = nodeSize;
	}
	public List<Integer[]> getNodes() {
		return MutualInformationCalculation.nodes;
	}

	public int getNodeSize(){
		if(nodes!=null)
			return nodes.size();
		else return -1;
	}


	public int getNodeLength() {
		return nodeLength;
	}


	public void setNodeLength(int nodeLength) {
		MutualInformationCalculation.nodeLength = nodeLength;
	}
	public static List<String[]> getData() {
		return data;
	}

	public static void setData(List<String[]> data) {
		MutualInformationCalculation.data = data;
	}

	public static List<Atribute> getfeatures() {
		return features;
	}

	public static void setfeatures(List<Atribute> features) {
		MutualInformationCalculation.features = features;
	}

	public MutualInformationCalculation() throws IOException{
		setfeatures(new ArrayList<Atribute>());
		featuresInteger = new ArrayList<AtributeInteger>();	

		nodes = new ArrayList<Integer[]>();

		addNodes();
		nodeSize = getNodeSize();
		
	}

	public void addNodes() throws FileNotFoundException{


		List<String[]> data = new ArrayList<String[]>();
		Scanner scn = new Scanner(new File(DATA_FILE)); 

		String line = null;
		while(scn.hasNextLine()){
			line = scn.nextLine();
			String[] arr = line.trim().split(" ");
			data.add(arr);
		}
		scn.close();

		nodeLength = data.size();
		int noOfNodes = data.get(0).length;

		for(int i=0;i<noOfNodes;i++){
			Integer nodevals[] = new Integer[nodeLength];
			nodes.add(nodevals);
		}

		for(int i=0;i<nodeLength;i++){
			for(int j=0;j<noOfNodes;j++){
				Integer nodevals[] = nodes.get(j);
				nodevals[i] = Integer.parseInt(data.get(i)[j]);
			}
		}

	}

	public double[][] calculateMI(){
		double[][] tabuaMI = new double[nodeSize][nodeSize];
				
		for(int i = 0; i < nodeSize; i++){
			for(int j = 0; j < nodeSize; j++){
				if(i != j){
					tabuaMI[i][j] = calculatePairwiseMutualInformation(i, j);
					
				}
			}
				
		}
		
		return tabuaMI;
	}
	
	public double entropyCheckingOfATargetgene(int x){
		double entropy =0;
		int m = x;
		Integer[] s = nodes.get(m);
		ArrayList<Integer> firstVector = new ArrayList<Integer>(nodeLength-1);
		List<ArrayList<Integer>> tmp = new ArrayList<ArrayList<Integer>>();
		for(int i=0;i<nodeLength-1;i++)
		{
			firstVector.add(s[i+1]);

		}
		tmp.add(firstVector);
		entropy = getEntropy(tmp);

		return entropy;				
	}
		
	// Pairwise mutual information calculation
	public static double calculatePairwiseMutualInformation(int x, int y){

		double mutualInformation = 0;
		Integer[] s = nodes.get(x);
		Integer[] q = nodes.get(y);

		//double[] firstVector = new double[nodeLength-1];
		ArrayList<Integer> firstVector = new ArrayList<Integer>(nodeLength-1);
		ArrayList<Integer> secondVector = new ArrayList<Integer>(nodeLength-1);
		//double[] secondVector = new double[nodeLength-1];
		List<ArrayList<Integer>> tmp = new ArrayList<ArrayList<Integer>>();
		for(int i=0;i<nodeLength-1;i++)
		{
			firstVector.add(s[i+1]);
			secondVector.add(q[i]);

		}
		tmp.add(firstVector);
		tmp.add(secondVector);

		//double targetEntropy = getEntropy(tmp);
		mutualInformation = getMutualInformation(tmp);

		return mutualInformation;

	}

	
	// Entropy calculation
	public static double getEntropy(List<ArrayList<Integer>> value){
		List<String> perm = binaryPermutation(value.size());

		Hashtable<String, Integer> occurence = new Hashtable<String, Integer>();

		for (String s : perm) {
			occurence.put(s, 0);
		}

		ArrayList<String> data = new ArrayList<String>();

		ArrayList<Integer> rvar = value.get(0);

		for (Integer v : rvar) {
			data.add(""+v);
		}

		for(int i=1;i<value.size();i++){
			rvar = value.get(i);

			for(int j=0;j<rvar.size();j++){
				String t = data.get(j);
				data.remove(j);
				t += rvar.get(j);
				data.add(j,t);
			}
		}

		for (String str : data) {
			occurence.put(str, occurence.get(str)+1);
		}

		double result = 0;
		for (String s : perm) {

			double p = (double)occurence.get(s)/data.size();

			result -= (occurence.get(s) == 0) ?0 :(p* Math.log(p) / Math.log(2));
		}
		return result;

	}
	// Mutual information calculation
	public static double getMutualInformation(List<ArrayList<Integer>> value){
		double mutualInformation = 0.0;
		mutualInformation -= getEntropy(value);
		mutualInformation += getEntropy(value.subList(0, 1));
		mutualInformation += getEntropy(value.subList(1, value.size()));
		return mutualInformation;
	}

	public static List<String> binaryPermutation(int dim){

		List<String> perms = new ArrayList<String>();
		List<String> perms1 = new ArrayList<String>();


		perms.add("0");
		perms.add("1");

		for(int i=1;i<dim;i++){
			for(String str:perms){
				perms1.add(str+"0");
				perms1.add(str+"1");
			}

			perms.clear();
			perms.addAll(perms1);
			perms1.clear();
		}


		return perms;

	}


	


}

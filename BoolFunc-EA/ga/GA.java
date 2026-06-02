package ga;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;



import mifs.MutualInformationCalculation;


public class GA {
	public static String DATA_FILE= "NetworkTransition.txt";
	private static ArrayList<ArrayList<Integer>> solution =  new ArrayList<ArrayList<Integer>>();
	private List<NetNode> nodes = new ArrayList<NetNode>();
	private List<Chromosome> solutions = new ArrayList<Chromosome>();
	private int generations = 0;
	private static int noOfNodes;
	private static int size;
	private static double mis[][];
	
	private Random rnd;
	
	public void initialize() throws IOException{
		MutualInformationCalculation mfs = new MutualInformationCalculation();
		mis = mfs.calculateMI();
		initializeNodes();		
		rnd = new Random();
	}
	
	private void initializeNodes() throws FileNotFoundException{
		Scanner scn = null;
		nodes.clear();
		try {
			scn = new Scanner(new File(DATA_FILE));
			ArrayList<String[]> rawData = new ArrayList<String[]>();
			String line = null;


			while(scn.hasNextLine()){
				line = scn.nextLine();
				rawData.add(line.trim().split(" +"));
			}
			
			noOfNodes = rawData.get(0).length;
			size = rawData.size();
			
			for(int i=0;i<noOfNodes;i++){
				List<Integer> nodeData = new ArrayList<Integer>();
				for(String[] arr: rawData){
					nodeData.add(Integer.parseInt(arr[i].trim()));
				}
				
				nodes.add(new NetNode(nodeData));
			}
		} catch (FileNotFoundException e) {
			throw e;
		}finally{
			if(scn != null)
				scn.close();
		}
	}
	private void initializeSolutions(int index){
		solutions.clear();
		for(int i=0;i<noOfNodes;i++){
			Chromosome c = Chromosome.createCromosome(index, noOfNodes, mis[index]);
			c.setFitness(calculateFitness(c));
			solutions.add(c);
		}
	}
	
	private void applyGA(int index) throws IOException{
		generations = 0;
		initializeSolutions(index);		
		double K = Math.sqrt(noOfNodes);
		int cnt = 0;

		float previousFitness = 999999999;
		float currentFitness = getTotalFitness();
		Collections.sort(solutions);
		System.out.print("Gene "+(index+1)+" : GA is applying because Boolean rules failed to find optimal solutions\n");
		while (solutions.get(0).getFitness() > 0 && cnt <1000) {
			System.out.print("\t ["+generations+"]");
			if(previousFitness == currentFitness){
				cnt++;

			}
			else{
				cnt = 0;

			}
			generations++;
			if(generations==500) {
				break;
			}
		    System.out.println("Adjusted Fitness = "+(previousFitness)+"\t"+ (currentFitness));
			List<Chromosome> offsprings = new ArrayList<Chromosome>();

			for(int k = 0;k<K;k++){

				List<Chromosome> parents = RouletteWheelSelection.select(solutions, false, 2, rnd);

				Chromosome offspring[] = parents.get(0).crossover(parents.get(1));

				offspring[0].setFitness(calculateFitness(offspring[0]));
				offspring[1].setFitness(calculateFitness(offspring[1]));

				offspring[0].mutate();
				offspring[1].mutate();

				offsprings.add(offspring[0]);
				offsprings.add(offspring[1]);

				Collections.sort(offsprings);
			}
			for(Chromosome offspring : offsprings){
				if(solutions.get(solutions.size() - 1).getFitness() > offspring.getFitness() ){
					solutions.remove(solutions.size() - 1);
					solutions.add(0, offspring);
				}else{
					break;
				}
			}

			previousFitness = currentFitness;
			currentFitness = getTotalFitness();
			Collections.sort(solutions);

		}
		System.out.println();
		solution.add(solutions.get(0).getSources());
	}
	
	public void optimizeByGA() throws IOException{

		for (int i =0; i<noOfNodes;i++) {	
			HashMap<Integer, Integer> e = applyBooleanRules(i);
			ArrayList<Integer> sol = new ArrayList<Integer>();
			Map.Entry<Integer,Integer> entry = e.entrySet().iterator().next();
			Integer key= entry.getKey();
			Integer value=entry.getValue();
			sol.add(value);
			if (key==0) {
				System.out.println("Gene "+(i+1) + ": Got optimal solutions by Boolean rules!");
				solution.add(i, sol);
			}
			
			else {
				applyGA(i);		
			}
		}
	}

	private HashMap<Integer, Integer> applyBooleanRules(int i) {
		int minDiff = 999999999;
		int misMatch = 999999999;
		int minFeatures = 0;
		int targetIndex = i;
		HashMap<Integer, Integer> ErrorAndFeature = new HashMap<Integer, Integer>();
		int [] target = new int[size-1];		
		for(int j=0;j<size-1;j++){
			target[j] = nodes.get(targetIndex).getData().get(j+1);
		}
		List<Integer> src = new ArrayList<Integer>();

		for(int k=0;k<noOfNodes; k++) {
			if (k==i) {
				continue;
			}
			if(k!=i) {
				src.clear();
				src.add(k);
				
				int regulators[][] = new int[src.size()][size-1];

				for(int l= 0;l<src.size();l++){
					int t = src.get(l);
					for(int j=0;j<size-1;j++){
						regulators[l][j] = nodes.get(t).getData().get(j);
					}
				}
				//int rec[] = test(target, regulators);
				int rec[] = boolFunctions(target, regulators);
				misMatch = matchAmount(target, rec);
				if(misMatch < minDiff){
				   minDiff = misMatch;		
				   minFeatures = k;
				}				
							
			}
			 
		}
		ErrorAndFeature.put(minDiff, minFeatures);
		return ErrorAndFeature;

	}

	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();	
		GA ga = new GA();
		ga.initialize();
		ga.optimizeByGA();
		System.out.println();
		System.out.println("Inferred a Boolean Network by Bool-GA");
		System.out.println("==========================================");
		for(int i=0;i<solution.size();i++){
			System.out.print((i+1)+"\t");
			System.out.print("\t");
			ArrayList<Integer> srcs = solution.get(i);

			for(int j=0;j<srcs.size();j++){
				System.out.print((srcs.get(j) + 1)+"\t ");


			}
			System.out.println();
		}
		long estimatedTime = System.currentTimeMillis() - startTime;
       System.out.println("The running time of Bool-GA is: "+estimatedTime+" ms");
	}
	
	private static List<String> binaryPermutation(int dim){

		List<String> perms = new ArrayList<String>();
		List<String> perms1 = new ArrayList<String>();

		if(dim == 0)
			return perms;

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
	
	public static int[] ConjunctionDisjuctionFunctions(int target[], int solution[][]){

		List<String> perms = binaryPermutation(solution.length);

		int ands[][] = new int[perms.size()][target.length];
		int ors[][] = new int[perms.size()][target.length];

		int cAnds[] = new int[perms.size()];
		int cOrs[] = new int[perms.size()];

		for(int i=0;i<target.length;i++){
			for(int j=0;j<perms.size();j++){
				String str = perms.get(j);
				
				for(int k=0;k<str.length();k++){
					if(k==0){
						if(str.charAt(k)=='0'){
							ands[j][i] = solution[k][i];
							ors[j][i] = solution[k][i];
							
						}
						else{
							ands[j][i] = solution[k][i]^1;
							ors[j][i] = solution[k][i]^1;
						}
					}else{
						if(str.charAt(k)=='0'){
							ands[j][i] &= solution[k][i];
							ors[j][i] |= solution[k][i];
						}
						else{
							ands[j][i] &= solution[k][i]^1;
							ors[j][i] |= solution[k][i]^1;
							
						}
					}
				}

				if(target[i] == ands[j][i])
				{
					cAnds[j]++;
				}

				if(target[i] == ors[j][i])
				{
					cOrs[j]++;
					
				}
			}

		}

		int ret[] = null;

		int max = 0;

		for(int i=0;i<cAnds.length;i++){
			if(cAnds[i] > max){
				max = cAnds[i];
				ret = ands[i];
				
			}
		}

		for(int i=0;i<cOrs.length;i++){
			if(cOrs[i] > max){
				max = cOrs[i];
				ret = ors[i];
				
			}
		}
		
		//	System.out.println("[2^|S+1|]  AND-OR UPDATE RULES:" + perms);
		

		return ret;

	}
	
	int calculateFitness(Chromosome c) {

		ArrayList<Integer> srcs =  c.getSources();
		int [] target = new int[size-1];		
		for(int j=1;j<size-1;j++){
    	    target[j] = nodes.get(c.getTarget()).getData().get(j+1);
    	}
	    int solution[][] = new int[srcs.size()][size-1];
		for(int i= 0;i<srcs.size();i++){
			int t = srcs.get(i);
			for(int j=0;j<size-1;j++){
				  solution[i][j] = nodes.get(t).getData().get(j);;
			}
		}
		int rec[] = ConjunctionDisjuctionFunctions(target, solution);

		int e= matchAmount(target, rec);
		e = e << 8;
		e += srcs.size();

		return e;
	}
	public static int[] boolFunctions(int target[], int solution[][]){

		int temp[] = new int[target.length];		
		int activation=0;
		int negation=0;
		for(int i=0;i<target.length;i++){
			if(target[i]== solution[0][i]) {
				activation++;
			}
			if(target[i]==(solution[0][i]^1)){
				negation++;
			}

		}
		if(activation == target.length){
			for(int i=0;i<target.length;i++){
				temp[i]=solution[0][i];
			}
		}
		if(negation == target.length) {			
			for(int i=0;i<target.length;i++){
				temp[i]=(solution[0][i]^1);
			}
		}
		return temp;	


	}

	private int matchAmount(int a[], int b[]){
		int amt = 0;
		if(a ==null | b == null || a.length != b.length)
			return -1;

		List<Integer> ta = new ArrayList<Integer>();

		for(int i=0;i<a.length;i++){
			ta.add(a[i]);
		}

		List<Integer> tb = new ArrayList<Integer>();

		for(int i=0;i<b.length;i++){
			tb.add(b[i]);
		}
		
		for(int i=0;i<tb.size();i++){
			if(ta.get(i).intValue() != tb.get(i).intValue()){
				amt++;
			}
		}

		return amt;
	}	
		
	private int getTotalFitness(){
		int total = 0;
		
		for(Chromosome c: solutions){
			total += c.getFitness();
		}
		
		return total;
	}

}

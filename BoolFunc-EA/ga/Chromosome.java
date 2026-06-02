package ga;

import java.util.ArrayList;
import java.util.Random;


/**************************************************************************************************
 * Chromosome Representation                                                                      *
 *                                                                                                *
 *                                                                                                *
 * @author Shohag Barman                                                                          *
 * Assistant Professor, Dept. of Computer Science and Engineering                                 *                                                              *
 * Pirojpur Science and Technology University                                                     *
 *                                                                                                *                                                                                            *
 **************************************************************************************************/      


public class Chromosome implements Comparable<Chromosome> {
	
	private static double Kmax = 0.60;
	private static double MUTATION_RATE = 0.01;
	private static double MI_THRESHOLD = 0.05;
	private int data[];
	private int target;
	private int fitness;
	private float scaledFitness;
	private int maxNode;
	private static Random rand = new Random();
	private double mi[];
	
	
	private Chromosome(){
		
	}
	
	public static Chromosome createCromosome(int target, int noOfNodes, double mi[]){
		
		Chromosome chromosome = new Chromosome();
		chromosome.target = target;
		chromosome.maxNode = noOfNodes;
		chromosome.mi = mi;
		chromosome.data = new int[noOfNodes];
		int cnt = 0;
		while(cnt < 1){
			for(int i=0;i<noOfNodes;){
				double val = rand.nextDouble();
				
				if(val < Kmax && i != target && mi[i] > MI_THRESHOLD){
					chromosome.data[i++] = 1;
					cnt++;
				}
				else
					chromosome.data[i++] = 0;
			}
		}
		
		return chromosome;
	}

	public int[] getData() {
		return data;
	}

	public void setData(int[] data) {
		this.data = data;
	}
	public Chromosome[] crossover(Chromosome secondCromosome){
	    
	    Chromosome offspring[] = new Chromosome[2];
	    
	    offspring[0] = createCromosome(target, maxNode, mi);
	    offspring[1] = createCromosome(target, maxNode, mi);
	    
	    // number of blocks to swap
	    int blocks = rand.nextInt(3) + 1;   // swap 1–3 blocks randomly
	    
	    // initialize offspring with parents (default: copy original)
	    for(int i=0;i<maxNode;i++){
	        offspring[0].data[i] = this.data[i];
	        offspring[1].data[i] = secondCromosome.data[i];
	    }

	    // swap random blocks
	    for(int b = 0; b < blocks; b++){
	        int start = rand.nextInt(maxNode - 1);          // random start
	        int length = rand.nextInt(maxNode - start);     // random block size
	        
	        for(int i = start; i < start + length && i < maxNode; i++){
	            // swap genes in this block
	            int temp = offspring[0].data[i];
	            offspring[0].data[i] = offspring[1].data[i];
	            offspring[1].data[i] = temp;
	        }
	    }
	    
	    return offspring;
	}
	
		public float getOriginalFitness() {
		return scaledFitness;
	}

	public void setOriginalFitness(float scaledFitness) {
		this.scaledFitness = scaledFitness;
	}
	public int getFitness() {
		return fitness;
	}

	public void setFitness(int fitness) {
		this.fitness = fitness;
	}

	@Override
	public int compareTo(Chromosome c) {
		
		return this.fitness - c.getFitness();
	}
	
	
	public void mutate(){
		for(int i=0;i<maxNode;i++){
			if(i==this.target)
				continue;
			if(rand.nextDouble() <= MUTATION_RATE){
	
				if(data[i] ==1)
					data[i] = 0;
				else
					data[i] = 1;
			}
		}
		
	}
	
	public int getTarget(){
		return this.target;
	}
	
	private boolean alreadyExist(int t){
		for(int i:data)
			if(i==t)
				return true;
		
		return false;
	}
	
	public ArrayList<Integer> getSources(){
		ArrayList<Integer> sols = new ArrayList<Integer>();
		for(int i=0;i<this.maxNode;i++){
			if(data[i]==1)
				sols.add(i);
		}
		
		return sols;
	}

		
}

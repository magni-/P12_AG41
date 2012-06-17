package challenge;

import java.util.ArrayList;
import java.util.Random;

public class Taboo {
	protected ArrayList<Solution> tabooList;
	protected int tabooLength;
	protected Solution solution;
	protected Solution newSolution;
	protected Solution bestSolution;
	
	public Solution getBest() {
		return bestSolution;
	}
	
	public Solution randomSolution(Problem pb) {
		
		// n number of total jobs, r randomly chosen number of jobs for each batch
		
		int n = pb.getNp(), r;
		String solStr = "";
		Solution sol = new Solution(pb);
		Random rand = new Random();;
		
		// random selection of production batches
		
		while (n > 0) {
			r = rand.nextInt(n) + 1;
			solStr += r + " ";
			n -= r;
		}
		
		solStr = solStr.substring(0, solStr.length() - 1) + "/";
		n = pb.getNp();
		
		// random selection of transport batches
		
		int cap = pb.transporter.getCapacity();
		while (n > 0) {
			r = rand.nextInt(Math.min(n,cap)) + 1; // can't exceed transporter's capacity in a delivery batch
			solStr += r + " ";
			n -= r;
		}
		
		solStr = solStr.substring(0, solStr.length() - 1);
		sol.setFromString(solStr);
		
		return sol;
	}
	
	public Solution bestNeighbor(Problem pb, Solution sol, int sizeNL, int var) {		
		Solution tmp; 						// temporary neighbor solution
		Solution best = new Solution(pb); 	// best neighbor solution
		best.setWorst();
		
		int pS, dS; 				// solution's productionSequence and deliverySequence vector sizes
		int pSinit = sol.getProductionSequenceMT().size();
		int dSinit = sol.getDeliverySequenceMT().size();
		
		int bNvar, dWvar;
		int cap = pb.transporter.getCapacity();
		
		Random rand = new Random();
		int randBatch;
		
		// each time bestNeighbor() is called, the best neighbor from sizeNL randomly chosen neighbors is returned
		
		while (sizeNL > 0) {
			tmp = sol.clone(pb);	// tmp neighbors are initialized to the calling solution
			
			pS = pSinit;
			dS = dSinit;
			
			// productionSequenceMT modifying
			
			bNvar = rand.nextInt(var)+1;
			dWvar = bNvar;
			do {
				randBatch = rand.nextInt(pS);	// one prodbatch is randomly selected
				if (tmp.getProductionSequenceMT().elementAt(randBatch).getQuantity() <= dWvar) {	// if that prodbatch doesn't have enough jobs, we'll  
					dWvar = dWvar - tmp.getProductionSequenceMT().elementAt(randBatch).getQuantity(); // need to split the decrementation with another prodbatch
					tmp.getProductionSequenceMT().remove(randBatch);				// we remove it
					--pS;															// and decrement the amount of prodbatches
				} else {
					tmp.getProductionSequenceMT().elementAt(randBatch).decQuantity(dWvar);
					dWvar = 0;
				}
			} while (dWvar > 0);
			
			randBatch = rand.nextInt(2*pS);		// 2*pS instead of pS to give the possibility of creating a new prodbatch
			if (randBatch >= pS) {				// create new batch
				randBatch = rand.nextInt(pS+1);	// choose where to put it
				tmp.getProductionSequenceMT().add(randBatch, new Batch(0)); // and give it 0 jobs
			}
			tmp.getProductionSequenceMT().elementAt(randBatch).incQuantity(bNvar); // increment chosen batch 
			
			// deliverySequenceMT modifying
			// same as above, taking into account the transporter's capacity
			
			dWvar = bNvar;
			do {
				randBatch = rand.nextInt(dS);
				if (tmp.getDeliverySequenceMT().elementAt(randBatch).getQuantity() <= dWvar) {  
					dWvar = dWvar - tmp.getDeliverySequenceMT().elementAt(randBatch).getQuantity();
					tmp.getDeliverySequenceMT().remove(randBatch);
					--dS;
				} else {
					tmp.getDeliverySequenceMT().elementAt(randBatch).decQuantity(dWvar);
					dWvar = 0;
				}
			} while (dWvar > 0);
			
			boolean repeat;
			do {
				repeat = false;
				randBatch = rand.nextInt(2*dS);
				if (randBatch >= dS) {
					randBatch = rand.nextInt(dS+1);
					tmp.getDeliverySequenceMT().add(randBatch, new Batch(0));
				} else if (tmp.getDeliverySequenceMT().elementAt(randBatch).getQuantity() == cap)
					repeat = true;
			} while (repeat);
			tmp.getDeliverySequenceMT().elementAt(randBatch).incQuantity(bNvar);
			
			tmp.evaluate();
					
			if (tabooList.contains(tmp) == false) {		// only possible if neighbor not in taboo
				--sizeNL; 								// one less neighbor to go
				if (tmp.evaluation < best.evaluation)	// update best if needed
					best = tmp.clone(pb);
			}		
		}			
			
		return best;
	}
	
	public Taboo(Problem pb, int iter, int sizeTL, int sizeNL, int var) {
		tabooList = new ArrayList<Solution>();
		tabooLength = sizeTL;
		solution = randomSolution(pb);
		solution.evaluate();
		bestSolution = solution.clone(pb);
		
		tabooList.add(solution.clone(pb));
	
		int currIter = 0;
		while (currIter < iter) {
			System.out.print("\r"+(100*currIter)/iter+"%...");
			newSolution = bestNeighbor(pb, solution, sizeNL, var);
			
			if (newSolution.evaluation < bestSolution.evaluation)
				bestSolution = newSolution.clone(pb);
			
			if (tabooList.size() == tabooLength)
				tabooList.remove(0);
			
			tabooList.add(newSolution.clone(pb));
			solution = newSolution.clone(pb);
			
			++currIter;
		}
	}
	
}

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
	
	public Solution bestNeighbor(Problem pb, Solution sol, int sizeNL) {
		Solution tmp; // temporary neighbor solution
		Solution best = new Solution(pb); // best neighbor solution
		best.setWorst();
		int pS, dS, randBatch; // solution's productionSequence and deliverySequence vector sizes
		int cap = pb.transporter.getCapacity();
		Random rand = new Random();
		
		// each time bestNeighbor() is called, the best neighbor from sizeNL randomly chosen neighbors is returned
		
		while (sizeNL > 0) {
			tmp = sol.clone(pb);	// tmp neighbors are initialized to the calling solution
			
			pS = tmp.getProductionSequenceMT().size();
			dS = tmp.getDeliverySequenceMT().size();
			
			// productionSequenceMT modifying
			
			randBatch = rand.nextInt(pS);	// one prodbatch is randomly selected
			tmp.getProductionSequenceMT().elementAt(randBatch).decQuantity(); // its quantity decremented
			if (tmp.getProductionSequenceMT().elementAt(randBatch).getQuantity() == 0) {	// if that batch is now empty
				tmp.getProductionSequenceMT().remove(randBatch);	// we remove it
				--pS;										// and decrement the amount of prodbatches
			}
			randBatch = rand.nextInt(pS+1);	// pS+1 instead of pS to give the possibility of creating a new prodbatch
			if (randBatch == pS) {	// create new batch
				randBatch = rand.nextInt(pS+1);	// choose where to put it
				tmp.getProductionSequenceMT().add(randBatch, new Batch(0)); // and give it 0 jobs
				// ++pS; (not needed as pS is not reused)
			}
			tmp.getProductionSequenceMT().elementAt(randBatch).incQuantity(); // increment chosen batch 
			
			// deliverySequenceMT modifying
			// same as above, taking into account the transporter's capacity
			
			randBatch = rand.nextInt(dS);
			tmp.getDeliverySequenceMT().elementAt(randBatch).decQuantity();
			if (tmp.getDeliverySequenceMT().elementAt(randBatch).getQuantity() == 0) {
				tmp.getDeliverySequenceMT().remove(randBatch);
				--dS;
			}
			boolean repeat = false;
			do {
				randBatch = rand.nextInt(dS+1);
				if (randBatch == dS) {
					randBatch = rand.nextInt(dS+1);
					tmp.getDeliverySequenceMT().add(randBatch, new Batch(0));
					// ++dS;
				} else if (tmp.getDeliverySequenceMT().elementAt(randBatch).getQuantity() == cap)
					repeat = true;
			} while (repeat);
			tmp.getDeliverySequenceMT().elementAt(randBatch).incQuantity();
			
			tmp.evaluate();
			
/*			System.out.print(sizeNL + " : \n bestSol=" + best.evaluation + " " + best.getProductionSequenceMT().toString() + best.getDeliverySequenceMT().toString() + 
					"\n tmpSol= " + tmp.evaluation + " " + tmp.getProductionSequenceMT().toString() + tmp.getDeliverySequenceMT().toString() + 
					"\n Sol=    " + sol.evaluation + " " + sol.getProductionSequenceMT().toString() + sol.getDeliverySequenceMT().toString() +
					"\n\nTabooList=");
			
			for(Solution item : tabooList) {
				System.out.print(item.getProductionSequenceMT().toString() + item.getDeliverySequenceMT().toString() + "\n");
			}
*/			
			if (tabooList.contains(tmp) == false) {	// only possible if neighbor not in taboo
				--sizeNL; // one less neighbor to go
				if (tmp.evaluation < best.evaluation)	// update best if needed
					best = tmp.clone(pb);
			}
		}			
			
		return best;
	}
	
	public Taboo(Problem pb, int iter, int sizeTL, int sizeNL) {
		tabooList = new ArrayList<Solution>();
		tabooLength = sizeTL;
		solution = randomSolution(pb);
		solution.evaluate();
		bestSolution = solution.clone(pb);
		
		tabooList.add(solution.clone(pb));
	
		int currIter = 0;
		while (currIter < iter) {
			System.out.print("\r"+(100*currIter)/iter+"%");
			newSolution = bestNeighbor(pb, solution, sizeNL);
			
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

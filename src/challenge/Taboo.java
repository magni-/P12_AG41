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
		Random rand = new Random();
		
		// random selection of production batches
		
		while (n > 0) {
			r = rand.nextInt(n) + 1;
			solStr += r + " ";
			n -= r;
		}
		
		solStr = solStr.substring(0, solStr.length() - 1) + "/";
		n = pb.getNp();
		
		// random selection of transport batches
		
		while (n> 0) {
			r = rand.nextInt(n) + 1;
			solStr += r + " ";
			n -= r;
		}
		
		solStr = solStr.substring(0, solStr.length() - 1);
		sol.setFromString(solStr);
		
		return sol;
	}
	
	public Taboo(Problem pb, int length) {
		tabooList = null;
		tabooLength = length;
		solution = randomSolution(pb);
		bestSolution = solution;
	// to do - sol = randomly generated solution
	//			best = sol
	//			tabou.add(sol)
	
	// while loop
	
	// to do - newSol = modify(sol)
	
	// to do - newSol.possible() ? (sh.be true)
	//			tabou.include(newSol) ? (sh.be false)
	//			sol.cost - newSol.cost > maxWorsen ? (sh.be false)
	
	// to do - add to taboo
	//			sol = newSol
	//			better than best ? => best = sol
	
	// end while
	}
	
}

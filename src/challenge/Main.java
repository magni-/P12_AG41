package challenge;

import java.util.Vector;

/**
 * 
 *     This file is part of ag41-print12-challenge.
 *     
 *     ag41-print12-challenge is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *     
 *     ag41-print12-challenge is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *     
 *     You should have received a copy of the GNU General Public License
 *     along with ag41-print12-challenge.  If not, see <http://www.gnu.org/licenses/>.
 *     
 */

/**
 * 
 * @author Olivier Grunder
 * @version 0.02
 * @date 23 mai 2012
 * 
 */
public class Main {

	
	// Static method to avoid the shallow copy effects of Vector.clone()
	public static Vector<Batch> cloneVB(Vector<Batch> v) {
		Vector<Batch> nv = new Vector<Batch>();
		for(Batch b : v)
			nv.add(b.clone());
		return nv;
	}
	
	// ----------------------------------------
	public static void main(String[] args) {
		Problem pb = new Problem("data/problem003-050.txt");
		//System.out.println("problem=" + pb.toString() + "\n");

		//Solution sol = new Solution(pb);
		//sol.setFromString("25 25/10 15 15 10");
		
		int iter = 1000;	// number of taboo iterations
		int sizeTL = 5;		// taboo length
		int sizeNL = 10;	// number of neighbors considered per taboo iteration
		
		if (args.length == 3) {
			try {
				iter = Integer.parseInt(args[0]);
				sizeTL = Integer.parseInt(args[1]);
				sizeNL = Integer.parseInt(args[2]);
	        } catch (NumberFormatException nfe) {
	            System.out.println("If user-defined parameters, they must be three integers.");
	            System.exit(1);
	        }
		}
		
		System.out.print("Taboo Parameters: \n " +
				iter + " iterations\n " +
				sizeTL + " size of taboo list\n " +
				sizeNL + " neighbors considered per iteration\n\n");
		
		Taboo tab = new Taboo(pb, iter, sizeTL, sizeNL);	
		Solution sol = tab.getBest();
		
		sol.evaluate();
		System.out.println("\r100%...done\n\nsolution= (eval) " + sol.evaluation + 
				"\n   (prodBatchSeq) " + sol.productionSequenceMT.toString() +
				"\n   (deliBatchSeq) " + sol.deliverySequenceMT.toString() + "\n");
	}
}

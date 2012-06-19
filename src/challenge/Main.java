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
		Problem pb = new Problem("data/problem-011-200.txt");
		int n = pb.getNp();
		
		long length = 200 * n;	// length of the taboo search in milliseconds
		int sizeTL = 1000;		// taboo length
		int sizeNL = 1000;		// number of neighbors considered per taboo iteration
		int var = (int) (0.75 * n);	// max variation between neighbor solutions
		
		if (args.length == 4) {
			try {
				length = Long.parseLong(args[0]);
				sizeTL = Integer.parseInt(args[1]);
				sizeNL = Integer.parseInt(args[2]);
				var = Integer.parseInt(args[3]);
	        } catch (NumberFormatException nfe) {
	            System.out.println("If user-defined parameters, they must be three integers.");
	            System.exit(1);
	        }
		}
		
		System.out.print("Taboo Parameters: \n " +
				length + " milliseconds\n " +
				sizeTL + " size of taboo list\n " +
				sizeNL + " neighbors considered per iteration\n " +
				var + " max variation between neighbor solutions\n\n");
		
		Taboo tab = new Taboo(pb, length, sizeTL, sizeNL, var);	
		Solution sol = tab.getBest();
		
		sol.evaluate();
		System.out.println("\r100%...done\n\nsolution= (eval) " + sol.evaluation + 
				"\n   (prodBatchSeq) " + sol.productionSequenceMT.toString() +
				"\n   (deliBatchSeq) " + sol.deliverySequenceMT.toString() +
				"\n found after " + (tab.timeBestFound / 1000) + "." + (tab.timeBestFound % 1000) + " seconds.\n");
	}
}

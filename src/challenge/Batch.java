package challenge;

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
 * @author Paul Mollet-Padier, Olivier Grunder
 * @version 0.02
 * @date 19 June 2012
 * 
 * added int incQuantity(int)
 *       int decQuantity(int)
 *       Batch clone()
 */
public class Batch {

	/**
	 * Quantity of product
	 */
	protected int quantity;

	/**
	 * 
	 */
	public Batch(int q) {
		quantity = q;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	// increments a batch's quantity
	
	public void incQuantity(int inc) {
		quantity += inc;
	}
	
	// decrements a batch's quantity
	
	public void decQuantity(int dec) {
		quantity -= dec;
	}

	public String toString() {
		return new String("" + quantity);
	}
	
	// called by the static method Main.cloneVB
	// (since v.clone() and new Vector(v) do not clone the vector element values but their references instead)
	
	public Batch clone() {
		return new Batch(quantity);
	}

}

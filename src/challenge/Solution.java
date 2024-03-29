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
 * @version 0.03
 * @date 19 June 2012
 * 
 * added Solution clone(Problem)
 *       void setWorst()
 *
 */

import java.util.*;

public class Solution {

	protected Problem slpb;
	protected double evaluation;

	/*
	 * Array of the production dates, index is job
	 */
	protected double productionStartingDates[];
	public double[] getProductionStartingDates() {
		return productionStartingDates;
	}

	public double productionCompletionTimes[];
	public double[] getProductionCompletionTimes() {
		return productionCompletionTimes;
	}

	public double transportationCompletionTimes[];

	public double[] getTransportationCompletionTimes() {
		return transportationCompletionTimes;
	}

	protected double evaluationManufacturer;
	protected double evaluationTransporter;
	protected double evaluationCustomer;

	/*
	 * Array of the first transporter departure, index is a transporter
	 */
	protected double dateTransporterDeparture;
	protected double dateProductionDeparture;

	public static final double NEG_INF = -1000000.0;

	/**
	 * multi transporter loading sequence : a solution is composed of different
	 * batches, each batch is defined by the identifier of the transporter and
	 * the number of transported parts.
	 */
	protected Vector<Batch> deliverySequenceMT;
	public Vector<Batch> getDeliverySequenceMT() {
		return deliverySequenceMT;
	}

	/**
	 * production sequence :
	 */
	protected Vector<Batch> productionSequenceMT;
	public Vector<Batch> getProductionSequenceMT() {
		return productionSequenceMT;
	}

	// ----------------------------------------
	public Solution(Problem pb_) {
		slpb = pb_;
		productionCompletionTimes = new double[slpb.getNp()];
		transportationCompletionTimes = new double[slpb.getNp()];
		productionStartingDates = new double[slpb.getNp()];

		deliverySequenceMT = new Vector<Batch>();
		productionSequenceMT = new Vector<Batch>();
	}

	public Solution clone(Problem pb) {
		Solution cloneSol = new Solution(pb);
		cloneSol.productionCompletionTimes = productionCompletionTimes;			// primitive types
		cloneSol.transportationCompletionTimes = transportationCompletionTimes;
		cloneSol.productionStartingDates = productionStartingDates;
		
		cloneSol.deliverySequenceMT = Main.cloneVB(deliverySequenceMT);	// object types (Vector<Batch>), need special implementation
		cloneSol.productionSequenceMT = Main.cloneVB(productionSequenceMT);
		
		cloneSol.evaluate();
		
		return cloneSol;
	}
	
	public void reset() {
		deliverySequenceMT = new Vector<Batch>();
		productionSequenceMT = new Vector<Batch>();
		productionCompletionTimes = new double[slpb.getNp()];
		productionStartingDates = new double[slpb.getNp()];
		System.gc();
	}

	public void setDeliverySequenceMT(Vector<Batch> newListChargt1) {
		deliverySequenceMT = newListChargt1;
	}

	// ----------------------------------------
	public int getNumberOfDeliveredBatches() {
		return deliverySequenceMT.size();
	}

	public int getNumberOfProducedBatches() {
		return productionSequenceMT.size();
	}

	// ----------------------------------------
	public int getNumberOfDeliveredParts() {
		int nc = getNumberOfDeliveredBatches();
		int nbrParts = 0;
		for (int i = 0; i < nc; i++) {
			nbrParts += getDeliveryBatchSize(i);
		}
		return nbrParts;
	}

	public int getNumberOfProducedParts() {
		int nc = getNumberOfProducedBatches();
		int nbrParts = 0;
		for (int i = 0; i < nc; i++) {
			nbrParts += getProductionBatch(i).getQuantity();
		}
		return nbrParts;
	}

	// ----------------------------------------
	public Batch getDeliveryBatch(int i) {
		return (Batch) deliverySequenceMT.get(i);
	}

	// ----------------------------------------
	public Batch getProductionBatch(int i) {
		return (Batch) productionSequenceMT.get(i);
	}

	// ----------------------------------------
	@SuppressWarnings("unused")
	private void setDeliveryBatch(int i, Batch b) {
		deliverySequenceMT.set(i, b);
	}

	@SuppressWarnings("unused")
	private void insertDeliveryBatch(int i5, Batch batch) {
		deliverySequenceMT.insertElementAt(batch, i5);
	}

	// ----------------------------------------
	public int getDeliveryBatchSize(int i) {
		Batch chargt = getDeliveryBatch(i);
		if (chargt != null)
			return chargt.getQuantity();
		return -1;
	}

	// ----------------------------------------
	public int getProductionBatchSize(int i) {
		Batch chargt = getProductionBatch(i);
		if (chargt != null)
			return chargt.getQuantity();
		return -1;
	}

	public void setProductionBatchSize(int i, int batchsize) {
		productionSequenceMT.set(i, new Batch(batchsize));
	}

	/**
	 * @param lot1
	 * @param Delivery
	 */
	public void setDelivery(int lot1, Batch Delivery) {
		deliverySequenceMT.set(lot1, Delivery);
	}

	/**
	 * @param i
	 * @param qteAEclater
	 * @param newtr
	 */
	public void addDelivery(int i, int qte) {
		deliverySequenceMT.insertElementAt(new Batch(qte), i);

	}

	// ----------------------------------------
	/**
	 * @param j
	 *            = quantity of delivered parts
	 * @param tr
	 *            = transporter
	 */
	public void addDeliveryFirst(int j) {
		deliverySequenceMT.add(0, new Batch(j));
	}

	public void addProductionFirst(int j) {
		productionSequenceMT.add(0, new Batch(j));
	}

	public void addProductionLast(int j) {
		productionSequenceMT.add(new Batch(j));
	}

	public void addDeliveryLast(int i) {
		deliverySequenceMT.add(new Batch(i));
	}

	public void setDelivery(int i, int j) {
		deliverySequenceMT.set(i, new Batch(j));
	}

	public void delDelivery(int lot1) {
		deliverySequenceMT.remove(lot1);
	}

	// ----------------------------------------
	public void delFirstDelivery() {
		deliverySequenceMT.remove(0);
	}

	public void delFirstProduction() {
		productionSequenceMT.remove(0);
	}

	public void delAllProduction() {
		productionSequenceMT.removeAllElements();
	}

	/**
	 * check the sequence
	 */
	protected boolean check() {
		int i = 0;
		while (i < getNumberOfDeliveredBatches()) {
			if (getDeliveryBatchSize(i) <= 0) {
				System.out
						.println("WARNING GSupplyLinkSolution.check : getDelivery("
								+ i
								+ ")="
								+ getDeliveryBatch(i)
								+ " <= 0 => removing it !");
				deliverySequenceMT.remove(i);
			} else
				i++;
		}

		if (getNumberOfDeliveredParts() > slpb.getNp()) {
			System.out
					.println("ERROR GSupplyLinkSolution.check : getNbrProduit()="
							+ getNumberOfDeliveredParts()
							+ " > slpb.getNp()="
							+ slpb.getNp() + " => don't know what to do !");
			return false;
		}
		return true;
	}

	private class Evaluator {
		// Supplier/Transporter/Customer cost
		int job;
		double completiontime;
		double[] transportationStartingTimes;

		public Evaluator() {
			transportationStartingTimes = new double[slpb.getNp()];
		}

		public double evaluate() {
			evaluation = 0;
			evaluationManufacturer = 0;
			evaluationTransporter = 0;
			evaluationCustomer = 0;

			job = slpb.getNp() - 1;
			completiontime = slpb.getDueDate(job);

			evaluateTransporterCustomerCosts();
			evaluateSupplierCosts();

			return evaluation;
		}

		/**
		 * ================================ Customer & Transporter cost
		 * ================================
		 */
		private void evaluateTransporterCustomerCosts() {
			int indiceDelivBatch = getNumberOfDeliveredBatches() - 1;
			while (job >= 0 && indiceDelivBatch >= 0) {
				int delivbatch = getDeliveryBatchSize(indiceDelivBatch);
				Transporter transp = slpb.getTransporter();

				// Transporter cost
				evaluationTransporter += transp.getBatchDeliveryCost();

				// Computation of the arrival date of the transporter
				for (int i = job; i >= job - delivbatch + 1; i--) {
					try {
						if (slpb.getDueDate(i) < completiontime)
							completiontime = slpb.getDueDate(i);
					} catch (Exception e) {
						System.out
								.println("Exception GSupplyLinkSolution.evaluate:"
										+ e.getMessage());
						System.out.println("i=" + i);
						System.out.println("slpb.getNp()=" + slpb.getNp());
						System.out.println("job=" + job);
						System.out.println("np_chargt=" + (delivbatch));
						System.out.println("job-np_chargt+1="
								+ (job - delivbatch + 1));
						System.out.println("solution=" + toString());
						System.out.println("numberOfDeliveredParts="
								+ getNumberOfDeliveredParts());
						check();

						System.exit(0);
					}
				}

				for (int i = job; i >= job - delivbatch + 1; i--) {
					// Customer cost
					transportationCompletionTimes[i] = completiontime;
					transportationStartingTimes[i] = transportationCompletionTimes[i]
							- transp.getBatchDeliveryTimes();
					evaluationCustomer += slpb.getCustomerHoldingCost(i,
							completiontime);
				}
				job -= delivbatch;

				// Transportation of the parts
				completiontime -= transp.getBatchDeliveryTimes();

				// dateTransportationStart[indiceDelivBatch] = completiontime ;
				dateTransporterDeparture = completiontime;

				// Empty transporter which come back from the customer to the
				// supplier
				completiontime -= transp.getBatchDeliveryTimes();

				indiceDelivBatch--;
			}

			evaluation += evaluationTransporter + evaluationCustomer;
		}

		/**
		 * ================================ Supplier Cost
		 * ================================
		 */
		private void evaluateSupplierCosts() {
			int indiceProdBatch = getNumberOfProducedBatches() - 1;
			@SuppressWarnings("unused")
			int sumProd = 0;

			job = slpb.getNp() - 1;
			double completionTimeBatch = transportationStartingTimes[job];
			dateProductionDeparture = completionTimeBatch;

			while (job >= 0 && indiceProdBatch >= 0) {
				int prodbatch = getProductionBatchSize(indiceProdBatch);

				// Production cost and WIP holding cost (1st holding cost) for
				// supplier
				evaluationManufacturer += slpb
						.getBatchProductionHoldingCost(prodbatch);

				double prodCompletionTime = dateProductionDeparture;
				int jobstart = job;
				for (int job2 = 0; job2 < prodbatch; job2++) {
					if (transportationStartingTimes[job] < prodCompletionTime)
						prodCompletionTime = transportationStartingTimes[job];
					job--;
				}

				job = jobstart;
				for (int job2 = 0; job2 < prodbatch; job2++) {
					productionCompletionTimes[job] = prodCompletionTime;
					dateProductionDeparture = productionCompletionTimes[job]
							- slpb.getSetupTime() - slpb.getProductionTime()
							* prodbatch;

					// Second holding cost for supplier
					evaluationManufacturer += (transportationStartingTimes[job] - productionCompletionTimes[job])
							* slpb.getSupplierUnitHoldingCost();

					job--;
				} // end for
				indiceProdBatch--;
			} // end while

			evaluation += evaluationManufacturer;

		}

	}

	/**
	 * Evaluation of the diff batch solution
	 */

	public double evaluate() {
		if (slpb == null)
			return -1;

		// The number of produced parts has to be greater or equal to the
		// number of delivered parts
		if (getNumberOfDeliveredBatches() <= 0) {
			return -1;
		}

		Evaluator ev = new Evaluator();
		return ev.evaluate();
	}
	
	// called by Taboo::bestNeighbor() to initialize the first bestNeighbor
	
	public void setWorst() {
		evaluation = Double.POSITIVE_INFINITY;
	}
	
	@Override
	public boolean equals(Object o) {
		Solution s = (Solution) o;
		return (productionSequenceMT == s.productionSequenceMT && deliverySequenceMT == s.deliverySequenceMT);
	}

	/**
	 * 
	 * @param solstring
	 *            : "production sequence / delivery sequence", e.g.
	 *            "24 26/5 3 6 5 5 5 4 4 6 7"
	 */
	public void setFromString(String solstring) {
		StringTokenizer st = new StringTokenizer(solstring, new String("/"));
		String prodst = st.nextToken().trim();
		String delivst = st.nextToken().trim();

		st = new StringTokenizer(prodst, new String(" "));
		while (st.hasMoreTokens()) {
			String nxtst = st.nextToken().trim();
			addProductionLast(Integer.parseInt(nxtst));
		}

		st = new StringTokenizer(delivst, new String(" "));
		while (st.hasMoreTokens()) {
			String nxtst = st.nextToken().trim();
			addDeliveryLast(Integer.parseInt(nxtst));
		}
	}

	@Override
	public String toString() {
		return "Solution [evaluation=" + evaluation
				+ ", productionStartingDates="
				+ Arrays.toString(productionStartingDates)
				+ ", productionCompletionTimes="
				+ Arrays.toString(productionCompletionTimes)
				+ ", transportationCompletionTimes="
				+ Arrays.toString(transportationCompletionTimes)
				+ ", evaluationManufacturer=" + evaluationManufacturer
				+ ", evaluationTransporter=" + evaluationTransporter
				+ ", evaluationCustomer=" + evaluationCustomer
				+ ", dateTransporterDeparture=" + dateTransporterDeparture
				+ ", dateProductionDeparture=" + dateProductionDeparture
				+ ", deliverySequenceMT=" + deliverySequenceMT
				+ ", productionSequenceMT=" + productionSequenceMT + "]";
	}

}

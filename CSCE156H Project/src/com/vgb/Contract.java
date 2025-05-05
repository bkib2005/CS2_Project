/**
 * Author: Barrett Kiburz
 * Date: February 2025
 */
package com.vgb;

import java.util.UUID;

/**
 * A subclass of item that holds the company which the contract is for
 */
public class Contract extends Item {
	private Company servicer;
	private double cost;
	
	/**
	 * A constructor for the contract subclass
	 * @param uuid
	 * @param name
	 * @param servicer
	 */
	public Contract(UUID uuid, String name, Company servicer, double cost) {
		super(uuid, name);
		this.servicer = servicer;
		this.cost = cost;
	}
	
	/**
	 * Returns the total amount of the contract cost.
	 * @return
	 */
	public double getCost() {
		return cost;
	}
	
	public double getTax() {
		return 0.0;
	}
	
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	@Override
	public String toString() {
		return super.toString()
				+ "   Servicer: \n" + servicer +"\n"
				+ "   Cost: $" + cost;
	}

	public Company getServicer() {
		return servicer;
	}

}

/**
 * Author: Barrett Kiburz
 * Date: February 2025
 */
package com.vgb;

import java.util.UUID;

/**
 * An item subclass which contains a material's unit and cost per unit
 */
public class Material extends Item {
	private String unit;
	private double costPerUnit;
	private int numOfUnits;
	
	private static final double TAX_RATE = 0.0715;

	/**
	 * A constructor method for the Material subclass
	 * @param uuid
	 * @param name
	 * @param unit
	 * @param costPerUnit
	 */
	public Material(UUID uuid, String name, String unit, double costPerUnit) {
		super(uuid, name);
		this.unit = unit;
		this.costPerUnit = costPerUnit;
	}
	
	public void setNumOfUnits(int numOfUnits) {
		this.numOfUnits = numOfUnits;
	}
	
	/**
	 * Returns the cost of buying a given number of units of a material
	 * @return
	 */
	public double getCost() {
		return numOfUnits * costPerUnit;
	}
	
	/**
	 * Returns how much is added to the cost in taxes
	 * @return
	 */
	public double getTax() {
		return Math.round(getCost() * TAX_RATE * 100.0) / 100.0;
	}

	@Override
	public String toString() {
		return super.toString()
				+ "   Unit: " + this.unit + "\n"
				+ "   Cost Per Unit: $" + String.format("%.2f", this.costPerUnit) + "\n"
				+ "   Number of Units: " + this.numOfUnits + "\n"
				+ "   Cost: " + String.format("%.2f", getCost());
	}

}

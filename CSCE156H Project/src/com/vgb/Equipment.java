/**
 * Author: Barrett Kiburz
 * Date: February 2025
 */
package com.vgb;

import java.util.UUID;

/**
 * An item subclass which contains a piece of equipment's model number and price
 */
public class Equipment extends Item {
	private String modelNumber;
	private double price;
	
	private static final double TAX_RATE = 0.0525;

	/**
	 * A constructor method for the equipment subclass
	 * @param uuid
	 * @param name
	 * @param modelNumber
	 * @param price
	 */
	public Equipment(UUID uuid, String name, String modelNumber, double price) {
		super(uuid, name);
		this.modelNumber = modelNumber;
		this.price = price;
	}

	/**
	 * A getter method for the price of the equipment
	 * @return
	 */
	public double getPrice() {
		return this.price;
	}

	public double getCost() {
		return Math.round(getPrice());
	}
	
	public double getTax() {
		return Math.round(getCost() * TAX_RATE * 100.0) / 100.0;
	}
	
	public String getModelNumber() {
		return this.modelNumber;
	}
	
	@Override
	public String toString() {
		return super.toString()
		+ "   Model Number: " + this.modelNumber + "\n"
		+ "   Price: $" + String.format("%.2f", this.price) + "\n";
	}

}

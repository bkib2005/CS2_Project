package com.vgb;

import java.util.UUID;

public class Rental extends Equipment {
	private double rentalHours;
	
	private static final double TAX_RATE = 0.0438;

	public Rental(UUID uuid, String name, String modelNumber, double price, double rentalHours) {
		super(uuid, name, modelNumber, price);
		this.rentalHours = rentalHours;
	}

	@Override
	public double getCost() {
		return Math.round(((getPrice() * 0.001) * rentalHours) * 100.0) / 100.0;
	}

	@Override
	public double getTax() {
		return Math.round((getCost() * TAX_RATE) * 100.0) / 100.0;
	}

	@Override
	public String toString() {
		return super.toString()
			+ "   Rental Hours: " + this.rentalHours + "\n"
			+ "   Rental Cost: " + String.format("$%.2f", getCost());
	}

	public double getRentalHours() {
		return rentalHours;
	}

	public static double getTaxRate() {
		return TAX_RATE;
	}

}

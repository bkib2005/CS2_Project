package com.vgb;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class Lease extends Equipment {
	private LocalDate start;
	private LocalDate end;

	public Lease(UUID uuid, String name, String modelNumber, double price, LocalDate start, LocalDate end) {
		super(uuid, name, modelNumber, price);
		this.start = start;
		this.end = end;
	}

	/**
	 * Returns the cost of leasing the equipment
	 */
	@Override
	public double getCost() {
		int days = (int) (ChronoUnit.DAYS.between(start, end) + 1);
		double years = days / 365.0;
		return Math.round((years / 5.0) * getPrice() * 1.5 * 100.0) / 100.0;
	}

	/**
	 * Returns how much is added to the cost in taxes
	 */
	@Override
	public double getTax() {
		if(getPrice() < 5000) {
			return 0;
		} else if(getPrice() < 12500) {
			return 500;
		} else {
			return 1500;
		}
	}

	@Override
	public String toString() {
		return super.toString() +
				"   Start Date: " + start + "\n"
				+ "   End Date: " + end + "\n"
				+ "   Lease Cost: $" + getCost();
	}

}

package com.vgb.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.vgb.Address;
import com.vgb.Company;
import com.vgb.Contract;
import com.vgb.Equipment;
import com.vgb.Lease;
import com.vgb.Material;
import com.vgb.Person;
import com.vgb.Rental;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JUnit test suite for VGB invoice system.
 */
public class EntityTests {

	public static final double TOLERANCE = 0.001;

	/**
	 * Creates an instance of a piece of equipment and tests if its cost and tax
	 * calculations are correct.
	 *
	 * TODO: finish implementation
	 */
	@Test
	public void testEquipment() {

		// data values
		UUID uuid = UUID.randomUUID();
		String name = "Backhoe 3000";
		String model = "BH30X2";
		double cost = 95125.00;

		// 1. TODO: Create an instance of equipment with the data values
		Equipment equipment = new Equipment(uuid, name, model, cost);
		// 2. Establish the expected cost and tax (rounded to nearest cent)
		double expectedCost = 95125.00;
		double expectedTax = 4994.06;

		// 3. TODO: Invoke methods to determine the cost/tax:
		double actualCost = equipment.getCost();
		double actualTax = equipment.getTax();

		// 4. Use assertEquals with the TOLERANCE to compare:
		assertEquals(expectedCost, actualCost, TOLERANCE);
		assertEquals(expectedTax, actualTax, TOLERANCE);
		// ensure that the string representation contains necessary elements
		String s = equipment.toString();
		System.out.println(s);
		assertTrue(s.contains("Backhoe 3000"));
		assertTrue(s.contains("BH30X2"));
		assertTrue(s.contains("95125.00"));

	}

	@Test
	public void testLease() {
		// TODO
		// data values
		UUID uuid = UUID.randomUUID();
		String name = "Bulldozer";
		String model = "RT67M1";
		double price = 100000;
		LocalDate start = LocalDate.parse("2025-03-01");
		LocalDate end = LocalDate.parse("2025-08-15");

		// 1. TODO: Create an instance of equipment with the data values
		Equipment equipment = new Lease(uuid, name, model, price, start, end);
		// 2. Establish the expected cost and tax (rounded to nearest cent)
		double expectedCost = 13808.22;
		double expectedTax = 1500;

		// 3. TODO: Invoke methods to determine the cost/tax:
		double actualCost = equipment.getCost();
		double actualTax = equipment.getTax();

		// 4. Use assertEquals with the TOLERANCE to compare:
		assertEquals(expectedCost, actualCost, TOLERANCE);
		assertEquals(expectedTax, actualTax, TOLERANCE);
		// ensure that the string representation contains necessary elements
		String s = equipment.toString();
		assertTrue(s.contains("Bulldozer"));
		assertTrue(s.contains("RT67M1"));
		assertTrue(s.contains("13808.22"));
		assertTrue(s.contains("2025-03-01"));
		assertTrue(s.contains("2025-08-15"));
	}

	@Test
	public void testRental() {
		// TODO
		// data values
		UUID uuid = UUID.randomUUID();
		String name = "Jackhammer";
		String model = "YU10L8";
		double cost = 10000;
		int hours = 6;

		// 1. TODO: Create an instance of equipment with the data values
		Equipment equipment = new Rental(uuid, name, model, cost, hours);
		// 2. Establish the expected cost and tax (rounded to nearest cent)
		double expectedCost = 600.0;
		double expectedTax = 26.28;

		// 3. TODO: Invoke methods to determine the cost/tax:
		double actualCost = equipment.getCost();
		double actualTax = equipment.getTax();

		// 4. Use assertEquals with the TOLERANCE to compare:
		assertEquals(expectedCost, actualCost, TOLERANCE);
		assertEquals(expectedTax, actualTax, TOLERANCE);
		// ensure that the string representation contains necessary elements
		String s = equipment.toString();
		assertTrue(s.contains("Jackhammer"));
		assertTrue(s.contains("YU10L8"));
		assertTrue(s.contains("600.00"));
		assertTrue(s.contains("6.0"));
	}

	@Test
	public void testMaterial() {
		// TODO
		// data values
		UUID uuid = UUID.randomUUID();
		String name = "Concrete";
		String unit = "Bags";
		double costPerUnit = 9.99;
		int numOfUnits = 11;

		// 1. TODO: Create an instance of equipment with the data values
		Material material = new Material(uuid, name, unit, costPerUnit, 0);
		material.setNumOfUnits(numOfUnits);
		// 2. Establish the expected cost and tax (rounded to nearest cent)
		double expectedCost = 109.89;
		double expectedTax = 7.86;

		// 3. TODO: Invoke methods to determine the cost/tax:
		double actualCost = material.getCost();
		double actualTax = material.getTax();

		// 4. Use assertEquals with the TOLERANCE to compare:
		assertEquals(expectedCost, actualCost, TOLERANCE);
		assertEquals(expectedTax, actualTax, TOLERANCE);
		// ensure that the string representation contains necessary elements
		String s = material.toString();
		assertTrue(s.contains("Concrete"));
		assertTrue(s.contains("Bags"));
		assertTrue(s.contains("109.89"));
		assertTrue(s.contains("9.99"));
		assertTrue(s.contains("109.89"));
		assertTrue(s.contains("11"));
	}

	@Test
	public void testContract() {
		// TODO
		// data values
		UUID uuid = UUID.randomUUID();
		String name = "Cement Pouring";
		List<String> emails = new ArrayList<String>();
		emails.add("jsmith@gmail.com");
		emails.add("johnsmith@outlook.com");
		Person person = new Person(UUID.randomUUID(), "John", "Smith", "1234567890", emails);
		Address address = new Address("123 Sesame Street", "Chicago", "IL", "12345");
		Company company = new Company(UUID.randomUUID(), "Company Co.", person, address);

		// 1. TODO: Create an instance of equipment with the data values
		Contract contract = new Contract(uuid, name, company, 0);
		contract.setCost(15000);
		// 2. Establish the expected cost and tax (rounded to nearest cent)
		double expectedCost = 15000;

		// 3. TODO: Invoke methods to determine the cost/tax:
		double actualCost = contract.getCost();

		// 4. Use assertEquals with the TOLERANCE to compare:
		assertEquals(expectedCost, actualCost, TOLERANCE);
		// ensure that the string representation contains necessary elements
		String s = contract.toString();
		assertTrue(s.contains("Cement Pouring"));
		assertTrue(s.contains("15000"));
		assertTrue(s.contains("Company Co."));
	}

}
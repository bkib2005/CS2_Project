/**
 * Author: Barrett Kiburz
 * Date: February 2025
 */
package com.vgb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * A company class that holds data for the compay's uuid, its primary contact, its name, and the address of the company.
 */
public class Company {
	private UUID companyUuid;
	private String name;
	private Person contact;
	private Address address;
	
	/**
	 * A constructor method for the company class
	 * @param companyUuid
	 * @param name
	 * @param contact
	 * @param address
	 */
	public Company(UUID companyUuid, String name, Person contact, Address address) {
		this.companyUuid = companyUuid;
		this.contact = contact;
		this.name = name;
		this.address = address;
	}
	
	/**
	 * Loads company data from a csv file into an array of companies
	 * @param fileName
	 * @param contactList
	 * @return
	 */
	public static List<Company> loadCompanyData(String fileName, List<Person> contactList) {
		List<Company> result = new ArrayList<Company>();
		String line = null;
		Person contact = null;
		try (Scanner s = new Scanner(new File(fileName))) {
			if (s.hasNext()) {
				s.nextLine();
			}
			while (s.hasNext()) {
				line = s.nextLine().trim();
				if (!line.isEmpty()) {
					String[] tokens = line.split(",");
					Address address = new Address(tokens[3], tokens[4], tokens[5], tokens[6]);
					for (Person x : contactList) {
						if(x.getUuid().equals(tokens[1]));
						contact = x;
					}
					Company company = new Company(UUID.fromString(tokens[0]), tokens[2], contact, address);
					result.add(company);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Encountered error on line " + line, e);
		}
		return result;
	}
	
	/**
	 * A getter method for the company's uuid
	 * @return
	 */
	public UUID getCompanyUuid() {
		return companyUuid;
	}
	
	/**
	 * A getter method for the company's name
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * A getter method for the main contact of the company
	 * @return
	 */
	public Person getContact() {
		return contact;
	}
	
	/**
	 * A getter method for the company address
	 * @return
	 */
	public Address getAddress() {
		return address;
	}

	@Override
	public String toString() {
		return "   Company UUID: " + this.companyUuid + "\n"
				+ "   Name: " + this.name + "\n"
				+ "   Contact: \n" + contact
				+ "\n   Address: " + address;
	}

}

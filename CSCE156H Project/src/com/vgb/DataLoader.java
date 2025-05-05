package com.vgb;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * Loads Invoice, Company, Person, and Item data from csv files.
 */
public class DataLoader {

	/**
	 * Loads persons data from a csv file and puts it into an array
	 * 
	 * @param fileName
	 * @return
	 */
	public static List<Person> loadPersonData(String fileName) {
		List<Person> result = new ArrayList<Person>();
		String line = null;
		try (Scanner s = new Scanner(new File(fileName))) {
			if (s.hasNext()) {
				s.nextLine();
			}
			while (s.hasNext()) {
				line = s.nextLine().trim();
				if (!line.isEmpty()) {
					String[] tokens = line.split(",");
					List<String> emailTokens = new ArrayList<String>();
					if (tokens.length >= 4) {
						for (int i = 4; i < tokens.length; i++) {
							emailTokens.add(tokens[i]);
						}
						Person person = new Person(UUID.fromString(tokens[0]), tokens[1], tokens[2], tokens[3],
								emailTokens);
						result.add(person);
					} else {
						Person person = new Person(UUID.fromString(tokens[0]), tokens[1], tokens[2], tokens[3], null);
						result.add(person);
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Encountered error on line " + line, e);
		}
		return result;
	}

	/**
	 * Loads company data from a csv file into an array of companies
	 * 
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
						if (x.getUuid().equals(tokens[1]))
							;
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
	 * Loads item data from a csv file into an array.
	 * 
	 * @param fileName
	 * @param servicerList
	 * @return
	 */
	public static List<Item> loadItemData(String fileName, List<Company> servicerList) {
		List<Item> result = new ArrayList<Item>();
		String line = null;
		Company servicer = null;
		try (Scanner s = new Scanner(new File(fileName))) {
			if (s.hasNext()) {
				s.nextLine();
			}
			while (s.hasNext()) {
				line = s.nextLine().trim();
				if (!line.isEmpty()) {
					String[] tokens = line.split(",");
					if (tokens[1].equals("E")) {
						Equipment equipment = new Equipment(UUID.fromString(tokens[0]), tokens[2], tokens[3],
								Double.parseDouble(tokens[4]));
						result.add(equipment);
					} else if (tokens[1].equals("M")) {
						Material material = new Material(UUID.fromString(tokens[0]), tokens[2], tokens[3],
								Double.parseDouble(tokens[4]), 0);
						result.add(material);
					} else if (tokens[1].equals("C")) {
						for (Company x : servicerList) {
							if (x.getCompanyUuid().toString().equals(tokens[3])) {
								servicer = x;
								Contract contract = new Contract(UUID.fromString(tokens[0]), tokens[2], servicer, 0);
								result.add(contract);
								break;
							}
						}
					}

				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Encountered error on line " + line, e);
		}
		return result;
	}

	public static List<Invoice> loadInvoiceData(String invoiceFileName, String invoiceItemFileName,
			List<Company> customerList, List<Person> salespersonList, List<Item> itemList) {
		List<Invoice> result = new ArrayList<Invoice>();
		String line = null;
		Company findCustomer = null;
		Person findSalesperson = null;
		String[] tokens = null;
		try (Scanner s1 = new Scanner(new File(invoiceFileName))) {
			if (s1.hasNext()) {
				s1.nextLine();
			}
			while (s1.hasNext()) {
				line = s1.nextLine().trim();
				if (!line.isEmpty()) {
					tokens = line.split(",");
				}
				for (Company c : customerList) {
					if (c.getCompanyUuid().equals(UUID.fromString(tokens[1]))) {
						findCustomer = c;
					}
				}
				for (Person sp : salespersonList) {
					if (sp.getUuid().equals(UUID.fromString(tokens[2]))) {
						findSalesperson = sp;
					}
				}
				Invoice invoice = new Invoice(UUID.fromString(tokens[0]), findCustomer, findSalesperson,
						LocalDate.parse(tokens[3]));
				result.add(invoice);
			}
		} catch (Exception e) {
			throw new RuntimeException("Encountered error on line " + line, e);
		}

		line = null;
		tokens = null;
		Item findItem = null;
		try (Scanner s2 = new Scanner(new File(invoiceItemFileName))) {
			if (s2.hasNext()) {
				s2.nextLine();
			}
			while (s2.hasNext()) {
				line = s2.nextLine().trim();
				if (!line.isEmpty()) {
					tokens = line.split(",");
				}
				for (Invoice x : result) {
					if (x.getInvoiceUuid().equals(UUID.fromString(tokens[0]))) {
						for (Item y : itemList) {
							if (y.getUuid().equals(UUID.fromString(tokens[1]))) {
								if (y instanceof Equipment) {
									if (tokens[2].equals("R")) {
										findItem = new Rental(y.getUuid(), y.getName(),
												((Equipment) y).getModelNumber(), ((Equipment) y).getPrice(),
												Double.parseDouble(tokens[3]));
									} else if (tokens[2].equals("L")) {
										findItem = new Lease(y.getUuid(), y.getName(), ((Equipment) y).getModelNumber(),
												((Equipment) y).getPrice(), LocalDate.parse(tokens[3]),
												LocalDate.parse(tokens[4]));
									} else if (tokens[2].equals("P")) {
										findItem = y;
									}
								} else if (y instanceof Material) {
									((Material) y).setNumOfUnits(Integer.parseInt(tokens[2]));
									findItem = y;
								} else {
									((Contract) y).setCost(Integer.parseInt(tokens[2]));
									findItem = y;
								}
								if (findItem != null) {
									x.addItem(findItem);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Encountered error on line " + line, e);
		}
		return result;
	}
}

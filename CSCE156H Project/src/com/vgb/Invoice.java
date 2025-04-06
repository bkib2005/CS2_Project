package com.vgb;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Invoice {
	private UUID invoiceUuid;
	private Company customer;
	private Person salesperson;
	private LocalDate invoiceDate;
	private List<Item> items;

	public Invoice(UUID invoiceUuid, Company customer, Person salesperson, LocalDate invoiceDate) {
		this.invoiceUuid = invoiceUuid;
		this.customer = customer;
		this.salesperson = salesperson;
		this.invoiceDate = invoiceDate;
		this.items = new ArrayList<>();
	}

	public double getSubtotal() {
		double subtotal = 0;
		for (Item x : items) {
			subtotal += x.getCost();
		}
		return subtotal;
	}

	public double getTaxTotal() {
		double taxTotal = 0;
		for (Item x : items) {
			taxTotal += x.getTax();
		}
		return taxTotal;
	}

	public double getGrandTotal() {
		return getSubtotal() + getTaxTotal();
	}
	
	public void addItem(Item newItem) {
		this.items.add(newItem);
	}
	
	@Override
	public String toString() {
		String itemsString = "";
		for(Item x : items) {
			itemsString = itemsString + x + "\n";
		}
		return "Invoice UUID: " + this.invoiceUuid + "\n"
				+ "Customer: \n" + this.customer
				+ "\n" + "Salesperson: \n" + this.salesperson + "\n"
				+ "Invoice Date: " + this.invoiceDate + "\n"
				+ "Items: \n" + itemsString
				+ "\nSubtotal: $" + String.format("%.2f", getSubtotal()) + "\n"
				+ "Tax Total: $" + String.format("%.2f", getTaxTotal()) + "\n"
				+ "Grand Total: $" + String.format("%.2f", getGrandTotal());
	}

	public static List<Invoice> loadInvoiceData(String invoiceFileName, String invoiceItemFileName, List<Company> customerList,
			List<Person> salespersonList, List<Item> itemList) {
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
					if (x.invoiceUuid.equals(UUID.fromString(tokens[0]))) {
						for (Item y : itemList) {
							if (y.getUuid().equals(UUID.fromString(tokens[1]))) {
								if (y instanceof Equipment) {
									if (tokens[2].equals("R")) {
										findItem = new Rental(y.getUuid(), y.getName(), ((Equipment) y).getModelNumber(),
												((Equipment) y).getPrice(), Double.parseDouble(tokens[3]));
									} else if (tokens[2].equals("L")) {
										findItem = new Lease(y.getUuid(), y.getName(), ((Equipment) y).getModelNumber(),
												((Equipment) y).getPrice(), LocalDate.parse(tokens[3]),
												LocalDate.parse(tokens[4]));
									} else if (tokens[2].equals("P")){
										findItem = y;
									}
								} else if (y instanceof Material) {
									((Material) y).setNumOfUnits(Integer.parseInt(tokens[2]));
									findItem = y;
								} else {
									((Contract) y).setCost(Integer.parseInt(tokens[2]));
									findItem = y;
								}
								if(findItem != null) {
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

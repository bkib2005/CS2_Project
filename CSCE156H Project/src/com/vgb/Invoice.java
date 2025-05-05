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
		for (Item x : items) {
			itemsString = itemsString + x + "\n";
		}
		return "Invoice UUID: " + this.invoiceUuid + "\n" + "Customer: \n" + this.customer + "\n" + "Salesperson: \n"
				+ this.salesperson + "\n" + "Invoice Date: " + this.invoiceDate + "\n" + "Items: \n" + itemsString
				+ "\nSubtotal: $" + String.format("%.2f", getSubtotal()) + "\n" + "Tax Total: $"
				+ String.format("%.2f", getTaxTotal()) + "\n" + "Grand Total: $"
				+ String.format("%.2f", getGrandTotal());
	}

	public UUID getInvoiceUuid() {
		return invoiceUuid;
	}

	public Company getCustomer() {
		return customer;
	}

	public Person getSalesperson() {
		return salesperson;
	}

	public LocalDate getInvoiceDate() {
		return invoiceDate;
	}

	public List<Item> getItems() {
		return items;
	}

}

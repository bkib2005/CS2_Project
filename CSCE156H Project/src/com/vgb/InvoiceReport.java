package com.vgb;

import java.util.List;

public class InvoiceReport {

	public static void main(String[] args) {
		List<Person> personData = Person.loadPersonData("data/Persons.csv");
		List<Company> companyData = Company.loadCompanyData("data/Companies.csv", personData);
		List<Item> itemData = Item.loadItemData("data/Items.csv", companyData);
		List<Invoice> invoiceData = Invoice.loadInvoiceData("data/Invoices.csv", "data/InvoiceItems.csv", companyData, personData, itemData);
		
		for(Invoice x : invoiceData) {
			System.out.print(x + "\n\n\n");
		}
	}

}

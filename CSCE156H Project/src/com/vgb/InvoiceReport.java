package com.vgb;

import java.util.List;

public class InvoiceReport {

	public static void printInvoiceList(SortedList<Invoice> invoices) {
		System.out.println("====================================");
		System.out.println("Invoices by Total");
		System.out.println("====================================");
		System.out.printf("%-36s %-25s %10s%n", "Invoice", "Customer", "Total");
		for (Invoice invoice : invoices) {
			System.out.printf("%-36s %-25s $%10.2f%n",
				invoice.getInvoiceUuid(),
				invoice.getCustomer().getName(),
				invoice.getGrandTotal()
			);
		}
		System.out.println();
	}

	public static void printCustomerList(SortedList<Company> companies, List<Invoice> invoices) {
		System.out.println("====================================");
		System.out.println("Customer Invoice Totals");
		System.out.println("====================================");
		System.out.printf("%-25s %-22s %10s%n", "Customer", "Number of Invoices", "Total");

		for (Company company : companies) {
			int count = 0;
			double total = 0;
			for (Invoice invoice : invoices) {
				if (invoice.getCustomer().getCompanyUuid().equals(company.getCompanyUuid())) {
					count++;
					total += invoice.getGrandTotal();
				}
			}
			System.out.printf("%-25s %-22d $%10.2f%n", company.getName(), count, total);
		}
		System.out.println();
	}

	public static void main(String[] args) {
		List<Invoice> invoiceData = InvoiceLoader.getInvoices();
		List<Company> companyData = InvoiceLoader.getAllCompanies();

		SortedList<Invoice> sortedByGrandTotalReversed = new SortedList<Invoice>(ListComparators.compareByTotalReverse);
		SortedList<Invoice> sortedByCustomer = new SortedList<Invoice>(ListComparators.compareByCustomer);
		SortedList<Company> sortedByGrandTotalNormal = new SortedList<Company>(
				ListComparators.compareCompanyByTotal(invoiceData));
		
		for(Invoice invoice : invoiceData) {
			sortedByGrandTotalReversed.insert(invoice);
			sortedByCustomer.insert(invoice);
		}
		
		for(Company company : companyData) {
			sortedByGrandTotalNormal.insert(company);
		}
		
		printInvoiceList(sortedByGrandTotalReversed);
		printInvoiceList(sortedByCustomer);
		printCustomerList(sortedByGrandTotalNormal, invoiceData);
	}

}
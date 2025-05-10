package com.vgb;

import java.util.Comparator;
import java.util.List;

/**
 * Comparators to be used in the sorted list.
 */
public class ListComparators {
	/**
	 * Sorts by the Invoice grand total, returns -1 if less than and 1 if more than.
	 * Ties are broken by the Invoice UUID.
	 */
	public static final Comparator<Invoice> compareByTotalReverse = new Comparator<Invoice>() {
		public int compare(Invoice invoice1, Invoice invoice2) {
			if (invoice1.getGrandTotal() > invoice2.getGrandTotal()) {
				return -1;
			} else if (invoice1.getGrandTotal() == invoice2.getGrandTotal()) {
				return invoice1.getInvoiceUuid().compareTo(invoice2.getInvoiceUuid());
			} else {
				return 1;
			}
		}
	};

	/**
	 * Sorts by the Invoice grand total, returns -1 if less than and 1 if more than.
	 * Ties are broken by the Invoice UUID.
	 */
	public static Comparator<Company> compareCompanyByTotal(List<Invoice> invoices) {
		return new Comparator<Company>() {
			@Override
			public int compare(Company company1, Company company2) {
				double total1 = 0;
				double total2 = 0;
				for (Invoice invoice : invoices) {
					if (invoice.getCustomer().getCompanyUuid().equals(company1.getCompanyUuid())) {
						total1 += invoice.getGrandTotal();
					} else if (invoice.getCustomer().getCompanyUuid().equals(company2.getCompanyUuid())) {
						total2 += invoice.getGrandTotal();
					}
				}
				if (total1 > total2) {
					return 1;
				} else if (total1 == total2) {
					return company1.getCompanyUuid().compareTo(company2.getCompanyUuid());
				} else {
					return -1;
				}
			}
		};
	}

	/**
	 * Sorts by the Invoice customer name alphabetically, returns -1 if before and 1
	 * if after. Ties are broken by the Invoice UUID.
	 */
	public static final Comparator<Invoice> compareByCustomer = new Comparator<Invoice>() {
		public int compare(Invoice invoice1, Invoice invoice2) {
			int result = invoice1.getCustomer().getName().compareTo(invoice2.getCustomer().getName());
			if (result != 0) {
				return result;
			} else {
				return invoice1.getInvoiceUuid().compareTo(invoice2.getInvoiceUuid());
			}
		}
	};
}
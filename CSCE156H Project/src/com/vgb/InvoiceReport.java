package com.vgb;

import java.util.List;

public class InvoiceReport {

	public static void main(String[] args) {
		List<Invoice> invoiceData = InvoiceLoader.getInvoices();
		
		for(Invoice x : invoiceData) {
			System.out.print(x + "\n\n\n");
		}
	}

}

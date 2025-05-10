package com.vgb.tests;

import com.vgb.Company;
import com.vgb.Equipment;
import com.vgb.Invoice;
import com.vgb.InvoiceLoader;
import com.vgb.ListComparators;
import com.vgb.Material;
import com.vgb.SortedList;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SortedListTests {

	private List<Invoice> invoiceData;
	private List<Company> companyData;
	private SortedList<Invoice> sortedByGrandTotalReversed;
	private SortedList<Invoice> sortedByCustomer;
	private SortedList<Company> sortedByGrandTotalNormal;

	@BeforeEach
	public void setUp() {
		invoiceData = InvoiceLoader.getInvoices();
		companyData = InvoiceLoader.getAllCompanies();
		sortedByGrandTotalReversed = new SortedList<>(ListComparators.compareByTotalReverse);
		sortedByCustomer = new SortedList<>(ListComparators.compareByCustomer);
		sortedByGrandTotalNormal = new SortedList<>(ListComparators.compareCompanyByTotal(invoiceData));
	}

	@Test
	public void testInsertInvoicesSortedByTotalReverse() {
		Invoice low = new Invoice(UUID.randomUUID(), null, null, null);
		low.addItem(new Equipment(UUID.randomUUID(), null, null, 100.0));

		Invoice high = new Invoice(UUID.randomUUID(), null, null, null);
		high.addItem(new Equipment(UUID.randomUUID(), null, null, 100000.0));

		sortedByGrandTotalReversed.insert(low);
		sortedByGrandTotalReversed.insert(high);

		assertEquals(high, sortedByGrandTotalReversed.get(0));
		assertEquals(low, sortedByGrandTotalReversed.get(1));
	}

	@Test
	public void testInsertInvoicesSortedByCustomer() {
		Company a = new Company(UUID.randomUUID(), "AAA", null, null);
		Company z = new Company(UUID.randomUUID(), "ZZZ", null, null);

		Invoice first = new Invoice(UUID.randomUUID(), a, null, null);
		Invoice second = new Invoice(UUID.randomUUID(), z, null, null);

		sortedByCustomer.insert(second);
		sortedByCustomer.insert(first);

		assertEquals(first, sortedByCustomer.get(0));
		assertEquals(second, sortedByCustomer.get(1));
	}

	@Test
	public void testInsertCompaniesSortedByInvoiceTotal() {
		Company low = new Company(UUID.randomUUID(), null, null, null);
		Company high = new Company(UUID.randomUUID(), null, null, null);

		Invoice lowInv = new Invoice(UUID.randomUUID(), low, null, null);
		lowInv.addItem(new Material(UUID.randomUUID(), null, null, 100.0, 1));
		invoiceData.add(lowInv);

		Invoice highInv = new Invoice(UUID.randomUUID(), high, null, null);
		highInv.addItem(new Equipment(UUID.randomUUID(), null, null, 500000.0));
		invoiceData.add(highInv);

		sortedByGrandTotalNormal = new SortedList<>(ListComparators.compareCompanyByTotal(invoiceData));

		sortedByGrandTotalNormal.insert(high);
		sortedByGrandTotalNormal.insert(low);

		assertEquals(low, sortedByGrandTotalNormal.get(0));
		assertEquals(high, sortedByGrandTotalNormal.get(1));
	}

	@Test
	public void testRemoveInvoiceByTotalReverse() {
		Invoice inv1 = invoiceData.get(0);
		Invoice inv2 = invoiceData.get(1);

		sortedByGrandTotalReversed.insert(inv1);
		sortedByGrandTotalReversed.insert(inv2);

		sortedByGrandTotalReversed.remove(0);
		sortedByGrandTotalReversed.remove(0);

		assertFalse(sortedByGrandTotalReversed.contains(inv1));
		assertFalse(sortedByGrandTotalReversed.contains(inv2));
	}

	@Test
	public void testRemoveInvoiceByCustomer() {
		Invoice inv1 = invoiceData.get(0);
		Invoice inv2 = invoiceData.get(1);

		sortedByCustomer.insert(inv1);
		sortedByCustomer.insert(inv2);

		sortedByCustomer.remove(0);
		sortedByCustomer.remove(0);

		assertFalse(sortedByCustomer.contains(inv1));
		assertFalse(sortedByCustomer.contains(inv2));
	}

	@Test
	public void testRemoveCompanyByTotal() {
		Company c1 = companyData.get(0);
		Company c2 = companyData.get(1);

		sortedByGrandTotalNormal.insert(c1);
		sortedByGrandTotalNormal.insert(c2);

		sortedByGrandTotalNormal.remove(0);
		sortedByGrandTotalNormal.remove(0);

		assertFalse(sortedByGrandTotalNormal.contains(c1));
		assertFalse(sortedByGrandTotalNormal.contains(c2));
	}

	@Test
	public void testRetrieveSortedByGrandTotalReversed() {
		SortedList<Invoice> sortedList = new SortedList<>(ListComparators.compareByTotalReverse);

		Invoice inv1 = new Invoice(UUID.randomUUID(), companyData.get(0), null, null);
		inv1.addItem(new Equipment(UUID.randomUUID(), "Item1", "Model1", 5000));

		Invoice inv2 = new Invoice(UUID.randomUUID(), companyData.get(1), null, null);
		inv2.addItem(new Equipment(UUID.randomUUID(), "Item2", "Model2", 3000));

		Invoice inv3 = new Invoice(UUID.randomUUID(), companyData.get(0), null, null);
		inv3.addItem(new Equipment(UUID.randomUUID(), "Item3", "Model3", 4000));

		sortedList.insert(inv1);
		sortedList.insert(inv2);
		sortedList.insert(inv3);

		assertEquals(inv1, sortedList.get(0));
		assertEquals(inv3, sortedList.get(1));
		assertEquals(inv2, sortedList.get(2));
	}

	@Test
	public void testRetrieveSortedByCustomer() {
		SortedList<Invoice> sortedList = new SortedList<>(ListComparators.compareByCustomer);

		Company c1 = new Company(UUID.randomUUID(), "A", null, null);
	    Company c2 = new Company(UUID.randomUUID(), "B", null, null);
	    Company c3 = new Company(UUID.randomUUID(), "C", null, null);
		
		Invoice inv1 = new Invoice(UUID.randomUUID(), c2, null, null);
		inv1.addItem(new Equipment(UUID.randomUUID(), "Item1", "Model1", 5000));

		Invoice inv2 = new Invoice(UUID.randomUUID(), c3, null, null);
		inv2.addItem(new Equipment(UUID.randomUUID(), "Item2", "Model2", 3000));

		Invoice inv3 = new Invoice(UUID.randomUUID(), c1, null, null);
		inv3.addItem(new Equipment(UUID.randomUUID(), "Item3", "Model3", 4000));

		sortedList.insert(inv1);
		sortedList.insert(inv2);
		sortedList.insert(inv3);

		assertEquals(inv3, sortedList.get(0));
		assertEquals(inv1, sortedList.get(1));
		assertEquals(inv2, sortedList.get(2));
	}

	@Test
	public void testRetrieveSortedByCompanyTotal() {
		SortedList<Company> sortedList = new SortedList<>(ListComparators.compareCompanyByTotal(invoiceData));

		Company c1 = new Company(UUID.randomUUID(), "A", null, null);
	    Company c2 = new Company(UUID.randomUUID(), "B", null, null);
	    Company c3 = new Company(UUID.randomUUID(), "C", null, null);
		
		Invoice inv1 = new Invoice(UUID.randomUUID(), c2, null, null);
		inv1.addItem(new Equipment(UUID.randomUUID(), "Item1", "Model1", 5000));
		invoiceData.add(inv1);

		Invoice inv2 = new Invoice(UUID.randomUUID(), c3, null, null);
		inv2.addItem(new Equipment(UUID.randomUUID(), "Item2", "Model2", 3000));
		invoiceData.add(inv2);
		
		Invoice inv3 = new Invoice(UUID.randomUUID(), c1, null, null);
		inv3.addItem(new Equipment(UUID.randomUUID(), "Item3", "Model3", 4000));
		invoiceData.add(inv3);

		sortedList.insert(c1);
		sortedList.insert(c2);
		sortedList.insert(c3);

		assertEquals(c3, sortedList.get(0));
		assertEquals(c1, sortedList.get(1));
		assertEquals(c2, sortedList.get(2));
	}

}

package com.vgb.tests;

import org.junit.jupiter.api.Test;

import com.vgb.Contract;
import com.vgb.Equipment;
import com.vgb.Invoice;
import com.vgb.InvoiceLoader;
import com.vgb.Item;
import com.vgb.Lease;
import com.vgb.Material;
import com.vgb.Rental;

import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class InvoiceLoaderTests {

    @Test
    public void testInvoiceLoading() {
        List<Invoice> invoices = InvoiceLoader.getInvoices();
        assertEquals(2, invoices.size());

        Invoice invoice1 = invoices.get(0);
        assertEquals(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0001"), invoice1.getInvoiceUuid());
        assertEquals(LocalDate.of(2024, 1, 15), invoice1.getInvoiceDate());
        assertEquals("Acme Corp", invoice1.getCustomer().getName());
        assertEquals("Bob", invoice1.getSalesperson().getFirstName());

        List<Item> items1 = invoice1.getItems();
        assertEquals(3, items1.size());

        Item item1 = items1.get(0);
        assertTrue(item1 instanceof Contract);
        Contract contract = (Contract) item1;
        assertEquals("HVAC Service", contract.getName());
        assertEquals("Global Tech", contract.getServicer().getName());
        assertEquals(1250.00, contract.getCost());
        
        Item item2 = items1.get(1);
        assertTrue(item2 instanceof Material);
        Material material = (Material) item2;
        assertEquals("Concrete", material.getName());
        assertEquals("cubic meter", material.getUnit());
        assertEquals(75.00, material.getCostPerUnit());
        assertEquals(40, material.getNumOfUnits());
        
        Item item3 = items1.get(2);
        assertTrue(item3 instanceof Equipment);
        Equipment equipment = (Equipment) item3;
        assertEquals("Excavator", equipment.getName());
        assertEquals("CAT-EX200", equipment.getModelNumber());
        assertEquals(95000.00, equipment.getPrice());

        Invoice invoice2 = invoices.get(1);
        assertEquals(UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbb0002"), invoice2.getInvoiceUuid());
        assertEquals(LocalDate.of(2024, 2, 10), invoice2.getInvoiceDate());
        assertEquals("Global Tech", invoice2.getCustomer().getName());
        assertEquals("Alice", invoice2.getSalesperson().getFirstName());

        List<Item> items2 = invoice2.getItems();
        assertEquals(2, items2.size());

        Item item4 = items2.get(0);
        assertTrue(item4 instanceof Contract);
        Contract leaseContract = (Contract) item4;
        assertEquals("Wiring Contract", leaseContract.getName());
        assertEquals("Acme Corp", leaseContract.getServicer().getName());
        assertEquals(2100.00, leaseContract.getCost());

        Item item5 = items2.get(1);
        assertTrue(item5 instanceof Equipment);
        Equipment leaseEquipment = (Equipment) item5;
        assertEquals("Forklift Lease", leaseEquipment.getName());
        assertEquals("FORK-X100", leaseEquipment.getModelNumber());
        assertEquals(32000.00, leaseEquipment.getPrice());
    }
}


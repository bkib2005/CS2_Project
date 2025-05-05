package com.vgb;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;

import com.vgb.database.ConnectionFactory;

public class InvoiceLoader {

	private static final Logger LOGGER = LogManager.getLogger(ConnectionFactory.class);

	static {
		Configurator.initialize(new DefaultConfiguration());
		Configurator.setRootLevel(Level.DEBUG);
		LOGGER.info("Started...");
	}

	public static List<String> getEmailsByPersonId(int personId, Connection conn) throws SQLException {
		List<String> emails = new ArrayList<>();
		String query = "SELECT emailAddress FROM Email WHERE personId = ?";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, personId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					emails.add(rs.getString("emailAddress"));
				}
			}
		}
		return emails;
	}

	public static Person getPersonById(int id, Connection conn) throws SQLException {
		String query = "SELECT uuid, firstName, lastName, phone FROM Person WHERE personId = ?";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					UUID uuid = UUID.fromString(rs.getString("uuid"));
					String firstName = rs.getString("firstName");
					String lastName = rs.getString("lastName");
					String phone = rs.getString("phone");

					List<String> emails = getEmailsByPersonId(id, conn);

					return new Person(uuid, firstName, lastName, phone, emails);
				}
			}
		} catch (SQLException e) {
			LOGGER.error("Failed to load person", e);
			throw new RuntimeException(e);
		}
		return null;
	}

	public static Address getAddressById(int id, Connection conn) throws SQLException {
		String query = "SELECT a.street, a.city, s.state, z.zip " + "FROM Address a "
				+ "JOIN State s ON a.stateId = s.stateId " + "JOIN ZipCode z ON a.zipId = z.zipId "
				+ "WHERE a.addressId = ?";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return new Address(rs.getString("street"), rs.getString("city"), rs.getString("state"),
							rs.getString("zip"));
				}
			}
		} catch (SQLException e) {
			LOGGER.error("Failed to load Address", e);
			throw new RuntimeException(e);
		}
		return null;
	}

	public static Company getCompanyById(int id, Connection conn) throws SQLException {
		String query = "SELECT uuid, name, addressId, contactId FROM Company WHERE companyId = ?";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					UUID uuid = UUID.fromString(rs.getString("uuid"));
					String name = rs.getString("name");
					int addressId = rs.getInt("addressId");
					int contactId = rs.getInt("contactId");

					Address address = getAddressById(addressId, conn);
					Person contact = getPersonById(contactId, conn);

					return new Company(uuid, name, contact, address);
				}
			}
		} catch (SQLException e) {
			LOGGER.error("Failed to load Company", e);
			throw new RuntimeException(e);
		}
		return null;
	}

	public static List<Item> getItemsByInvoiceId(int invoiceId, Connection conn) {
		List<Item> itemList = new ArrayList<>();

		String query = "SELECT i.itemId, i.uuid, i.name, " + "e.modelNumber, e.price, " + "m.unit, m.costPerUnit, "
				+ "c.servicerId, " + "ii.startDate, ii.endDate, ii.rentalHours, ii.numOfUnits, ii.cost "
				+ "FROM InvoiceItem ii " + "JOIN Item i ON ii.itemId = i.itemId "
				+ "LEFT JOIN Equipment e ON i.itemId = e.itemId " + "LEFT JOIN Material m ON i.itemId = m.itemId "
				+ "LEFT JOIN Contract c ON i.itemId = c.itemId " + "WHERE ii.invoiceId = ?";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, invoiceId);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					UUID itemUuid = UUID.fromString(rs.getString("uuid"));
					String name = rs.getString("name");
					Item item = null;

					String modelNumber = rs.getString("modelNumber");
					if (modelNumber != null) {
						double price = rs.getDouble("price");
						item = new Equipment(itemUuid, name, modelNumber, price);

						Date start = rs.getDate("startDate");
						if (start != null) {
							LocalDate startDate = start.toLocalDate();
							LocalDate endDate = rs.getDate("endDate").toLocalDate();
							item = new Lease(itemUuid, name, modelNumber, price, startDate, endDate);
						} else if (rs.getObject("rentalHours") != null) {
							double rentalHours = rs.getDouble("rentalHours");
							item = new Rental(itemUuid, name, modelNumber, price, rentalHours);
						}
					}

					else if (rs.getString("unit") != null) {
						String unit = rs.getString("unit");
						double costPerUnit = rs.getDouble("costPerUnit");
						int numOfUnits = rs.getInt("numOfUnits");
						item = new Material(itemUuid, name, unit, costPerUnit, numOfUnits);
					}

					else if (rs.getObject("servicerId") != null) {
						int servicerId = rs.getInt("servicerId");
						double cost = rs.getDouble("cost");
						Company servicer = getCompanyById(servicerId, conn);
						item = new Contract(itemUuid, name, servicer, cost);
					}

					if (item != null) {
						itemList.add(item);
					}
				}
			}
		} catch (SQLException e) {
			LOGGER.error("Failed to load items", e);
			throw new RuntimeException(e);
		}

		return itemList;
	}

	public static List<Invoice> getInvoices() {
		List<Invoice> invoiceList = new ArrayList<>();
		String query = "SELECT * FROM Invoice";

		try (Connection conn = ConnectionFactory.getConnection();
				PreparedStatement ps = conn.prepareStatement(query);
				ResultSet rs = ps.executeQuery()) {

			LOGGER.info("Executing query to fetch invoices: " + query);

			while (rs.next()) {
				LOGGER.info("Processing row: " + rs.getString("uuid") + ", " + rs.getInt("customerId") + ", "
						+ rs.getInt("salespersonId"));

				int invoiceId = rs.getInt("invoiceId");
				UUID uuid = UUID.fromString(rs.getString("uuid"));
				LocalDate invoiceDate = rs.getDate("invoiceDate").toLocalDate();
				int customerId = rs.getInt("customerId");
				int salespersonId = rs.getInt("salespersonId");
				Company customer = getCompanyById(customerId, conn);
				Person salesperson = getPersonById(salespersonId, conn);

				if (customer == null) {
					LOGGER.warn("Customer not found for ID: " + customerId);
				}
				if (salesperson == null) {
					LOGGER.warn("Salesperson not found for ID: " + salespersonId);
				}

				Invoice invoice = new Invoice(uuid, customer, salesperson, invoiceDate);

				List<Item> items = getItemsByInvoiceId(invoiceId, conn);
				for (Item item : items) {
					invoice.addItem(item);
				}

				LOGGER.info("Adding invoice: " + invoice.getInvoiceUuid());

				invoiceList.add(invoice);
			}
		} catch (SQLException e) {
			LOGGER.error("Failed to load invoices due to SQLException: ", e);
			throw new RuntimeException(e);
		}

		LOGGER.info("Finished loading invoices. Total invoices: " + invoiceList.size());

		return invoiceList;
	}

}

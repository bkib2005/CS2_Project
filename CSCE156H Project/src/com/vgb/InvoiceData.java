package com.vgb;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.UUID;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;

import com.vgb.database.ConnectionFactory;

/**
 * This is a collection of utility methods that define a general API for
 * interacting with the database supporting this application.
 *
 */
public class InvoiceData {

	private static final Logger LOGGER = LogManager.getLogger(ConnectionFactory.class);

	static {
		Configurator.initialize(new DefaultConfiguration());
		Configurator.setRootLevel(Level.DEBUG);
		LOGGER.info("Started...");
	}

	/**
	 * Removes all records from all tables in the database.
	 */
	public static void clearDatabase() {
		String[] deleteStatements = { "DELETE FROM InvoiceItem", "DELETE FROM Contract", "DELETE FROM Material",
				"DELETE FROM Equipment", "DELETE FROM Item", "DELETE FROM Invoice", "DELETE FROM Company",
				"DELETE FROM Email", "DELETE FROM Person", "DELETE FROM Address", "DELETE FROM State",
				"DELETE FROM ZipCode" };

		try (Connection conn = ConnectionFactory.getConnection(); Statement stmt = conn.createStatement()) {

			for (String sql : deleteStatements) {
				stmt.executeUpdate(sql);
			}

		} catch (SQLException e) {
			LOGGER.error("Failed to clear database", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Method to add a person record to the database with the provided data.
	 *
	 * @param personUuid
	 * @param firstName
	 * @param lastName
	 * @param phone
	 */
	public static void addPerson(UUID personUuid, String firstName, String lastName, String phone) {
		String query = "INSERT INTO Person (uuid, firstName, lastName, phone) VALUES (?, ?, ?, ?)";
		try (Connection conn = ConnectionFactory.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, personUuid.toString());
			ps.setString(2, firstName);
			ps.setString(3, lastName);
			ps.setString(4, phone);
			ps.executeUpdate();
		} catch (SQLException e) {
			LOGGER.error("Failed to insert person into the database", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Adds an email record corresponding person record corresponding to the
	 * provided <code>personUuid</code>
	 *
	 * @param personUuid
	 * @param email
	 */
	public static void addEmail(UUID personUuid, String email) {
		String insertEmail = "INSERT INTO Email (emailAddress, personId) VALUES (?, ?)";
		String selectPersonId = "SELECT personId FROM Person WHERE uuid = ?";
		try (Connection conn = ConnectionFactory.getConnection();
				PreparedStatement ps = conn.prepareStatement(insertEmail)) {
			int personId = -1;
			try (PreparedStatement psSelect = conn.prepareStatement(selectPersonId)) {
				psSelect.setString(1, personUuid.toString());
				try (ResultSet rs = psSelect.executeQuery()) {
					if (rs.next()) {
						personId = rs.getInt("personId");
					}
				}
			}
			if (personId != -1) {
				ps.setString(1, email);
				ps.setInt(2, personId);
				ps.executeUpdate();
			} else {
				throw new SQLException("Person not found for the given UUID.");
			}
		} catch (SQLException e) {
			LOGGER.error("Failed to insert email into the database", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Adds a company record to the database with the primary contact person
	 * identified by the given code.
	 *
	 * @param companyUuid
	 * @param name
	 * @param contactUuid
	 * @param street
	 * @param city
	 * @param state
	 * @param zip
	 */
	public static void addCompany(UUID companyUuid, UUID contactUuid, String name, String street, String city,
			String state, String zip) {
		String zipQuery = "INSERT INTO ZipCode (zip) VALUES (?)";
		String stateQuery = "INSERT INTO State (state) VALUES (?)";
		String addressQuery = "INSERT INTO Address (street, city, stateId, zipId) VALUES (?, ?, ?, ?)";
		String companyQuery = "INSERT INTO Company (uuid, name, contactId, addressId) VALUES (?, ?, ?, ?)";
		String selectPersonIdQuery = "SELECT personId FROM Person WHERE uuid = ?";
		String selectZipIdQuery = "SELECT zipId FROM ZipCode WHERE zip = ?";
		String selectStateIdQuery = "SELECT stateId FROM State WHERE state = ?";

		try (Connection conn = ConnectionFactory.getConnection()) {
			int zipId = 0;
			try (PreparedStatement zipPs = conn.prepareStatement(zipQuery, Statement.RETURN_GENERATED_KEYS)) {
				zipPs.setString(1, zip);
				zipPs.executeUpdate();
				try (ResultSet rs = zipPs.getGeneratedKeys()) {
					if (rs.next()) {
						zipId = rs.getInt(1);
					}
				}
			} catch (SQLIntegrityConstraintViolationException e) {
				try (PreparedStatement selectZipPs = conn.prepareStatement(selectZipIdQuery)) {
					selectZipPs.setString(1, zip);
					try (ResultSet rs = selectZipPs.executeQuery()) {
						if (rs.next()) {
							zipId = rs.getInt("zipId");
						}
					}
				}
			}
			int stateId = 0;
			try (PreparedStatement statePs = conn.prepareStatement(stateQuery, Statement.RETURN_GENERATED_KEYS)) {
				statePs.setString(1, state);
				statePs.executeUpdate();
				try (ResultSet rs = statePs.getGeneratedKeys()) {
					if (rs.next()) {
						stateId = rs.getInt(1);
					}
				}
			} catch (SQLIntegrityConstraintViolationException e) {
				try (PreparedStatement selectStatePs = conn.prepareStatement(selectStateIdQuery)) {
					selectStatePs.setString(1, state);
					try (ResultSet rs = selectStatePs.executeQuery()) {
						if (rs.next()) {
							stateId = rs.getInt("stateId");
						}
					}
				}
			}
			int addressId = 0;
			try (PreparedStatement addressPs = conn.prepareStatement(addressQuery, Statement.RETURN_GENERATED_KEYS)) {
				addressPs.setString(1, street);
				addressPs.setString(2, city);
				addressPs.setInt(3, stateId);
				addressPs.setInt(4, zipId);
				addressPs.executeUpdate();
				try (ResultSet rs = addressPs.getGeneratedKeys()) {
					if (rs.next()) {
						addressId = rs.getInt(1);
					}
				}
			}
			int contactId = 0;
			try (PreparedStatement selectContactPs = conn.prepareStatement(selectPersonIdQuery)) {
				selectContactPs.setString(1, contactUuid.toString());
				try (ResultSet rs = selectContactPs.executeQuery()) {
					if (rs.next()) {
						contactId = rs.getInt("personId");
					}
				}
			}
			try (PreparedStatement companyPs = conn.prepareStatement(companyQuery)) {
				companyPs.setString(1, companyUuid.toString());
				companyPs.setString(2, name);
				companyPs.setInt(3, contactId);
				companyPs.setInt(4, addressId);
				companyPs.executeUpdate();
			}
		} catch (SQLException e) {
			LOGGER.error("Failed to insert company into the database", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Adds an equipment record to the database of the given values.
	 *
	 * @param equipmentUuid
	 * @param name
	 * @param modelNumber
	 * @param retailPrice
	 */
	public static void addEquipment(UUID equipmentUuid, String name, String modelNumber, double retailPrice) {
		String itemQuery = "INSERT INTO Item (uuid, name) VALUES (?, ?)";
		String equipmentQuery = "INSERT INTO Equipment (itemId, modelNumber, price) VALUES (?, ?, ?)";

		try (Connection conn = ConnectionFactory.getConnection()) {
			try (PreparedStatement itemPs = conn.prepareStatement(itemQuery, Statement.RETURN_GENERATED_KEYS)) {
				itemPs.setString(1, equipmentUuid.toString());
				itemPs.setString(2, name);
				itemPs.executeUpdate();
				try (ResultSet generatedKeys = itemPs.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						int itemId = generatedKeys.getInt(1);
						try (PreparedStatement equipmentPs = conn.prepareStatement(equipmentQuery)) {
							equipmentPs.setInt(1, itemId);
							equipmentPs.setString(2, modelNumber);
							equipmentPs.setDouble(3, retailPrice);
							equipmentPs.executeUpdate();
						}
					} else {
						throw new SQLException("Failed to retrieve itemId for the new equipment.");
					}
				}
			}
		} catch (SQLException e) {
			LOGGER.error("Failed to insert equipment into the database", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Adds an material record to the database of the given values.
	 *
	 * @param materialUuid
	 * @param name
	 * @param unit
	 * @param pricePerUnit
	 */
	public static void addMaterial(UUID materialUuid, String name, String unit, double pricePerUnit) {
		String itemQuery = "INSERT INTO Item (uuid, name) VALUES (?, ?)";
		String materialQuery = "INSERT INTO Material (itemId, unit, costPerUnit) VALUES (?, ?, ?)";

		try (Connection conn = ConnectionFactory.getConnection()) {
			try (PreparedStatement itemPs = conn.prepareStatement(itemQuery, Statement.RETURN_GENERATED_KEYS)) {
				itemPs.setString(1, materialUuid.toString());
				itemPs.setString(2, name);
				itemPs.executeUpdate();
				try (ResultSet generatedKeys = itemPs.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						int itemId = generatedKeys.getInt(1);
						try (PreparedStatement materialPs = conn.prepareStatement(materialQuery)) {
							materialPs.setInt(1, itemId);
							materialPs.setString(2, unit);
							materialPs.setDouble(3, pricePerUnit);
							materialPs.executeUpdate();
						}
					} else {
						throw new SQLException("Failed to retrieve itemId for the new equipment.");
					}
				}
			}
		} catch (SQLException e) {
			LOGGER.error("Failed to insert equipment into the database", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Adds an contract record to the database of the given values.
	 *
	 * @param contractUuid
	 * @param name
	 * @param unit
	 * @param pricePerUnit
	 */
	public static void addContract(UUID contractUuid, String name, UUID servicerUuid) {
		String itemQuery = "INSERT INTO Item (uuid, name) VALUES (?, ?)";
		String servicerQuery = "SELECT companyId FROM Company WHERE uuid = ?";
		String contractQuery = "INSERT INTO Contract (itemId, servicerId) VALUES (?, ?)";

		try (Connection conn = ConnectionFactory.getConnection()) {
			try (PreparedStatement itemPs = conn.prepareStatement(itemQuery, Statement.RETURN_GENERATED_KEYS)) {
				itemPs.setString(1, contractUuid.toString());
				itemPs.setString(2, name);
				itemPs.executeUpdate();
				try (ResultSet generatedKeys = itemPs.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						int itemId = generatedKeys.getInt(1);
						int servicerId = -1;
						try (PreparedStatement servicerPs = conn.prepareStatement(servicerQuery)) {
							servicerPs.setString(1, servicerUuid.toString());
							try (ResultSet servicerRs = servicerPs.executeQuery()) {
								if (servicerRs.next()) {
									servicerId = servicerRs.getInt("companyId");
								}
							}
						}
						if (servicerId != -1) {
							try (PreparedStatement contractPs = conn.prepareStatement(contractQuery)) {
								contractPs.setInt(1, itemId);
								contractPs.setInt(2, servicerId);
								contractPs.executeUpdate();
							}
						} else {
							throw new SQLException("Servicer not found for the provided servicerUuid.");
						}
					} else {
						throw new SQLException("Failed to retrieve itemId for the new equipment.");
					}
				}
			}
		} catch (SQLException e) {
			LOGGER.error("Failed to insert equipment into the database", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Adds an Invoice record to the database with the given data.
	 *
	 * @param invoiceUuid
	 * @param customerUuid
	 * @param salesPersonUuid
	 * @param date
	 */
	public static void addInvoice(UUID invoiceUuid, UUID customerUuid, UUID salesPersonUuid, LocalDate date) {
		String query = "INSERT INTO Invoice (uuid, customerId, salespersonId, invoiceDate) VALUES (?, ?, ?, ?)";
		String getCustomerId = "SELECT companyId FROM Company WHERE uuid = ?";
		String getSalespersonId = "SELECT personId FROM Person WHERE uuid = ?";
		try (Connection conn = ConnectionFactory.getConnection()) {
			int customerId = -1;
			int salespersonId = -1;
			try (PreparedStatement customerPs = conn.prepareStatement(getCustomerId)) {
				customerPs.setString(1, customerUuid.toString());
				try (ResultSet rs = customerPs.executeQuery()) {
					if (rs.next()) {
						customerId = rs.getInt("companyId");
					}
				}
			}
			try (PreparedStatement salespersonPs = conn.prepareStatement(getSalespersonId)) {
				salespersonPs.setString(1, salesPersonUuid.toString());
				try (ResultSet rs = salespersonPs.executeQuery()) {
					if (rs.next()) {
						salespersonId = rs.getInt("personId");
					}
				}
			}
			if (customerId != -1 && salespersonId != -1) {
				try (PreparedStatement ps = conn.prepareStatement(query)) {
					ps.setString(1, invoiceUuid.toString());
					ps.setInt(2, customerId);
					ps.setInt(3, salespersonId);
					ps.setDate(4, Date.valueOf(date));
					ps.executeUpdate();
				}
			} else {
				throw new SQLException("Invalid customer or salesperson UUID");
			}
		} catch (SQLException e) {
			LOGGER.error("Failed to insert invoice into the database", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Adds an Equipment purchase record to the given invoice.
	 *
	 * @param invoiceUuid
	 * @param itemUuid
	 */
	public static void addEquipmentPurchaseToInvoice(UUID invoiceUuid, UUID itemUuid) {
		String getInvoiceId = "SELECT invoiceId FROM Invoice WHERE uuid = ?";
		String getItemId = "SELECT itemId FROM Item WHERE uuid = ?";
		String insertInvoiceItem = "INSERT INTO InvoiceItem (invoiceId, itemId) VALUES (?, ?)";
		try (Connection conn = ConnectionFactory.getConnection()) {
			int invoiceId = -1;
			int itemId = -1;
			try (PreparedStatement invoicePs = conn.prepareStatement(getInvoiceId)) {
				invoicePs.setString(1, invoiceUuid.toString());
				try (ResultSet rs = invoicePs.executeQuery()) {
					if (rs.next()) {
						invoiceId = rs.getInt("invoiceId");
					}
				}
			}
			try (PreparedStatement itemPs = conn.prepareStatement(getItemId)) {
				itemPs.setString(1, itemUuid.toString());
				try (ResultSet rs = itemPs.executeQuery()) {
					if (rs.next()) {
						itemId = rs.getInt("itemId");
					}
				}
			}
			if (invoiceId != -1 && itemId != -1) {
				try (PreparedStatement invoiceItemPs = conn.prepareStatement(insertInvoiceItem)) {
					invoiceItemPs.setInt(1, invoiceId);
					invoiceItemPs.setInt(2, itemId);
					invoiceItemPs.executeUpdate();
				}
			} else {
				throw new SQLException("Invalid invoice or item UUID");
			}
		} catch (SQLException e) {
			LOGGER.error("Failed to link equipment purchase to invoice", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Adds an Equipment lease record to the given invoice.
	 *
	 * @param invoiceUuid
	 * @param itemUuid
	 * @param start
	 * @param end
	 */
	public static void addEquipmentLeaseToInvoice(UUID invoiceUuid, UUID itemUuid, LocalDate start, LocalDate end) {
		String getInvoiceId = "SELECT invoiceId FROM Invoice WHERE uuid = ?";
		String getItemId = "SELECT itemId FROM Item WHERE uuid = ?";
		String insertInvoiceItem = "INSERT INTO InvoiceItem (invoiceId, itemId, startDate, endDate) VALUES (?, ?, ?, ?)";
		try (Connection conn = ConnectionFactory.getConnection()) {
			int invoiceId = -1;
			int itemId = -1;
			try (PreparedStatement invoicePs = conn.prepareStatement(getInvoiceId)) {
				invoicePs.setString(1, invoiceUuid.toString());
				try (ResultSet rs = invoicePs.executeQuery()) {
					if (rs.next()) {
						invoiceId = rs.getInt("invoiceId");
					}
				}
			}
			try (PreparedStatement itemPs = conn.prepareStatement(getItemId)) {
				itemPs.setString(1, itemUuid.toString());
				try (ResultSet rs = itemPs.executeQuery()) {
					if (rs.next()) {
						itemId = rs.getInt("itemId");
					}
				}
			}
			if (invoiceId != -1 && itemId != -1) {
				try (PreparedStatement invoiceItemPs = conn.prepareStatement(insertInvoiceItem)) {
					invoiceItemPs.setInt(1, invoiceId);
					invoiceItemPs.setInt(2, itemId);
					invoiceItemPs.setDate(3, java.sql.Date.valueOf(start));
					invoiceItemPs.setDate(4, java.sql.Date.valueOf(end));
					invoiceItemPs.executeUpdate();
				}
			} else {
				throw new SQLException("Invalid invoice or item UUID");
			}
		} catch (SQLException e) {
			LOGGER.error("Failed to link equipment purchase to invoice", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Adds an Equipment rental record to the given invoice.
	 *
	 * @param invoiceUuid
	 * @param itemUuid
	 * @param numberOfHours
	 */
	public static void addEquipmentRentalToInvoice(UUID invoiceUuid, UUID itemUuid, double numberOfHours) {
		String getInvoiceId = "SELECT invoiceId FROM Invoice WHERE uuid = ?";
		String getItemId = "SELECT itemId FROM Item WHERE uuid = ?";
		String insertInvoiceItem = "INSERT INTO InvoiceItem (invoiceId, itemId, rentalHours) VALUES (?, ?, ?)";
		try (Connection conn = ConnectionFactory.getConnection()) {
			int invoiceId = -1;
			int itemId = -1;
			try (PreparedStatement invoicePs = conn.prepareStatement(getInvoiceId)) {
				invoicePs.setString(1, invoiceUuid.toString());
				try (ResultSet rs = invoicePs.executeQuery()) {
					if (rs.next()) {
						invoiceId = rs.getInt("invoiceId");
					}
				}
			}
			try (PreparedStatement itemPs = conn.prepareStatement(getItemId)) {
				itemPs.setString(1, itemUuid.toString());
				try (ResultSet rs = itemPs.executeQuery()) {
					if (rs.next()) {
						itemId = rs.getInt("itemId");
					}
				}
			}
			if (invoiceId != -1 && itemId != -1) {
				try (PreparedStatement invoiceItemPs = conn.prepareStatement(insertInvoiceItem)) {
					invoiceItemPs.setInt(1, invoiceId);
					invoiceItemPs.setInt(2, itemId);
					invoiceItemPs.setDouble(3, numberOfHours);
					invoiceItemPs.executeUpdate();
				}
			} else {
				throw new SQLException("Invalid invoice or item UUID");
			}
		} catch (SQLException e) {
			LOGGER.error("Failed to link equipment purchase to invoice", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Adds a material record to the given invoice.
	 *
	 * @param invoiceUuid
	 * @param itemUuid
	 * @param numberOfUnits
	 */
	public static void addMaterialToInvoice(UUID invoiceUuid, UUID itemUuid, int numberOfUnits) {
		String getInvoiceId = "SELECT invoiceId FROM Invoice WHERE uuid = ?";
		String getItemId = "SELECT itemId FROM Item WHERE uuid = ?";
		String insertInvoiceItem = "INSERT INTO InvoiceItem (invoiceId, itemId, numOfUnits) VALUES (?, ?, ?)";
		try (Connection conn = ConnectionFactory.getConnection()) {
			int invoiceId = -1;
			int itemId = -1;
			try (PreparedStatement invoicePs = conn.prepareStatement(getInvoiceId)) {
				invoicePs.setString(1, invoiceUuid.toString());
				try (ResultSet rs = invoicePs.executeQuery()) {
					if (rs.next()) {
						invoiceId = rs.getInt("invoiceId");
					}
				}
			}
			try (PreparedStatement itemPs = conn.prepareStatement(getItemId)) {
				itemPs.setString(1, itemUuid.toString());
				try (ResultSet rs = itemPs.executeQuery()) {
					if (rs.next()) {
						itemId = rs.getInt("itemId");
					}
				}
			}
			if (invoiceId != -1 && itemId != -1) {
				try (PreparedStatement invoiceItemPs = conn.prepareStatement(insertInvoiceItem)) {
					invoiceItemPs.setInt(1, invoiceId);
					invoiceItemPs.setInt(2, itemId);
					invoiceItemPs.setInt(3, numberOfUnits);
					invoiceItemPs.executeUpdate();
				}
			} else {
				throw new SQLException("Invalid invoice or item UUID");
			}
		} catch (SQLException e) {
			LOGGER.error("Failed to link equipment purchase to invoice", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Adds a contract record to the given invoice.
	 *
	 * @param invoiceUuid
	 * @param itemUuid
	 * @param amount
	 */
	public static void addContractToInvoice(UUID invoiceUuid, UUID itemUuid, double amount) {
		String getInvoiceId = "SELECT invoiceId FROM Invoice WHERE uuid = ?";
		String getItemId = "SELECT itemId FROM Item WHERE uuid = ?";
		String insertInvoiceItem = "INSERT INTO InvoiceItem (invoiceId, itemId, cost) VALUES (?, ?, ?)";
		try (Connection conn = ConnectionFactory.getConnection()) {
			int invoiceId = -1;
			int itemId = -1;
			try (PreparedStatement invoicePs = conn.prepareStatement(getInvoiceId)) {
				invoicePs.setString(1, invoiceUuid.toString());
				try (ResultSet rs = invoicePs.executeQuery()) {
					if (rs.next()) {
						invoiceId = rs.getInt("invoiceId");
					}
				}
			}
			try (PreparedStatement itemPs = conn.prepareStatement(getItemId)) {
				itemPs.setString(1, itemUuid.toString());
				try (ResultSet rs = itemPs.executeQuery()) {
					if (rs.next()) {
						itemId = rs.getInt("itemId");
					}
				}
			}
			if (invoiceId != -1 && itemId != -1) {
				try (PreparedStatement invoiceItemPs = conn.prepareStatement(insertInvoiceItem)) {
					invoiceItemPs.setInt(1, invoiceId);
					invoiceItemPs.setInt(2, itemId);
					invoiceItemPs.setDouble(3, amount);
					invoiceItemPs.executeUpdate();
				}
			} else {
				throw new SQLException("Invalid invoice or item UUID");
			}
		} catch (SQLException e) {
			LOGGER.error("Failed to link equipment purchase to invoice", e);
			throw new RuntimeException(e);
		}
	}

}
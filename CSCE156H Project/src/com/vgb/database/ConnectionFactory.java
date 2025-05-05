package com.vgb.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection configuration
 */
public class ConnectionFactory {

	/**
	 * User name used to connect to the SQL server
	 */
	private static final String USERNAME = "bkiburz3";

	/**
	 * Password used to connect to the SQL server
	 */
	private static final String PASSWORD = "ooFahXajah6k";

	/**
	 * Connection parameters that may be necessary for server configuration
	 * 
	 */
	private static final String PARAMETERS = "";

	/**
	 * SQL server to connect to
	 */
	private static final String SERVER = "cse-linux-01.unl.edu";

	/**
	 * Fully formatted URL for a JDBC connection
	 */
	private static final String URL = String.format("jdbc:mysql://%s/%s?%s", SERVER, USERNAME, PARAMETERS);
	
	public static Connection getConnection() {
		try {
			return DriverManager.getConnection(URL, USERNAME, PASSWORD);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}

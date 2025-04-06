/**
 * Author: Barrett Kiburz
 * Date: February 2025
 */
package com.vgb;

/**
 * A class which contains the street, city, state, and zip code of an address
 */
public class Address {
	private String street;
	private String city;
	private String state;
	private String zip;
	
	/**
	 * A constructor method for the address class
	 * @param street
	 * @param city
	 * @param state
	 * @param zip
	 */
	public Address(String street, String city, String state, String zip) {
		this.street = street;
		this.city = city;
		this.state = state;
		this.zip = zip;
	}

	@Override
	public String toString() {
		return this.street + ", " + this.city + ", " + this.state + ", " + this.zip;
	}
}

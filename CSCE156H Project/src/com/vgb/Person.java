/**
 * Author: Barrett Kiburz
 * Date: February 2025
 */
package com.vgb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * A class which holds data for a person's uuid, name, phone number, and any
 * emails they have
 */
public class Person {
	private UUID personUuid;
	private String firstName;
	private String lastName;
	private String phone;
	private List<String> emails;

	/**
	 * A constructor for the person class
	 * 
	 * @param uuid
	 * @param firstName
	 * @param lastName
	 * @param phone
	 * @param emails
	 */
	public Person(UUID uuid, String firstName, String lastName, String phone, List<String> emails) {
		this.personUuid = uuid;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.emails = emails;
	}

	/**
	 * A getter method for the person's uuid
	 * 
	 * @return
	 */
	public UUID getUuid() {
		return personUuid;
	}

	/**
	 * A getter method for the person's first name
	 * 
	 * @return
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * A getter method for the person's last name
	 * 
	 * @return
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * A getter method for the person's phone number
	 * 
	 * @return
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * A getter method for the person's emails
	 * 
	 * @return
	 */
	public List<String> getEmails() {
		return emails;
	}

	@Override
	public String toString() {
		return "   Person UUID: " + this.personUuid + "\n" + "   Name: " + firstName + " " + lastName + "\n"
				+ "   Phone: " + phone + "\n" + "   Emails: " + emails;
	}

}

package com.vgb;

/**
 * Author: Barrett Kiburz
 * Date: February 2025
 * 
 * Takes in data from 3 csv files with data for people, companies, and items and outputs the data in an xml format
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import com.thoughtworks.xstream.XStream;

public class DataConverter {

	public static void main(String[] args) {
		XStream xstream = new XStream();
		List<Person> personData = Person.loadPersonData("data/Persons.csv");
		List<Company> companyData = Company.loadCompanyData("data/Companies.csv", personData);
		List<Item> itemData = Item.loadItemData("data/Items.csv", companyData);

		xstream.alias("person", Person.class);
		xstream.alias("email", String.class);
		xstream.alias("company", Company.class);
		xstream.alias("address", Address.class);
		xstream.alias("equipment", Equipment.class);
		xstream.alias("material", Material.class);
		xstream.alias("contract", Contract.class);

		try (PrintWriter personWriter = new PrintWriter(new File("data/Persons.xml"))) {
			personWriter.println("<?xml version=\"1.0\"?>");
			personWriter.println("<persons>");
			for (Person x : personData) {
				String prettyXML = xstream.toXML(x);
				personWriter.println(prettyXML);
			}
			personWriter.printf("</persons>");
		} catch (FileNotFoundException fnfe) {
			System.err.println("Error writing to file Persons.xml");
			throw new RuntimeException(fnfe);
		}
		
		try (PrintWriter companyWriter = new PrintWriter(new File("data/Companies.xml"))) {
			companyWriter.println("<?xml version=\"1.0\"?>");
			companyWriter.println("<companies>");
			for (Company x : companyData) {
				String prettyXML = xstream.toXML(x);
				companyWriter.println(prettyXML);
			}
			companyWriter.printf("</companies>");
		} catch (FileNotFoundException fnfe) {
			System.err.println("Error writing to file Companies.xml");
			throw new RuntimeException(fnfe);
		}

		try (PrintWriter itemWriter = new PrintWriter(new File("data/Items.xml"))) {
			itemWriter.println("<?xml version=\"1.0\"?>");
			itemWriter.println("<items>");
			for (Item x : itemData) {
				String prettyXML = xstream.toXML(x);
				itemWriter.println(prettyXML);
			}
			itemWriter.printf("</items>");
		} catch (FileNotFoundException fnfe) {
			System.err.println("Error writing to file Items.xml");
			throw new RuntimeException(fnfe);
		}
	}

}

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
 * An item superclass that contains the items uuid number and name
 */
public abstract class Item {
	private UUID uuid;
	private String name;

	/**
	 * A constructor for the Item superclass
	 * @param uuid
	 * @param name
	 */
	public Item(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
	}

	/**
	 * Loads item data from a csv file into an array.
	 * @param fileName
	 * @param servicerList
	 * @return
	 */
	public static List<Item> loadItemData(String fileName, List<Company> servicerList) {
		List<Item> result = new ArrayList<Item>();
		String line = null;
		Company servicer = null;
		try (Scanner s = new Scanner(new File(fileName))) {
			if (s.hasNext()) {
				s.nextLine();
			}
			while (s.hasNext()) {
				line = s.nextLine().trim();
				if (!line.isEmpty()) {
					String[] tokens = line.split(",");
					if (tokens[1].equals("E")) {
						Equipment equipment = new Equipment(UUID.fromString(tokens[0]), tokens[2], tokens[3], Double.parseDouble(tokens[4]));
						result.add(equipment);
					} else if (tokens[1].equals("M")) {
						Material material = new Material(UUID.fromString(tokens[0]), tokens[2], tokens[3], Double.parseDouble(tokens[4]));
						result.add(material);
					} else if (tokens[1].equals("C")) {
						for(Company x : servicerList) {
							if(x.getCompanyUuid().toString().equals(tokens[3])) {
								servicer = x;
								Contract contract = new Contract(UUID.fromString(tokens[0]), tokens[2], servicer);
								result.add(contract);
								break;
							}
						}
					}

				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Encountered error on line " + line, e);
		}
		return result;
	}

	public UUID getUuid() {
		return this.uuid;
	}
	
	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return "   Item UUID: " + getUuid() + "\n"
				+ "   Name: " + getName() + "\n";
	}
	
	public abstract double getCost();
	public abstract double getTax();

}

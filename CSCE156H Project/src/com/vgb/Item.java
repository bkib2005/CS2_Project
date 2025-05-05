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
	 * 
	 * @param uuid
	 * @param name
	 */
	public Item(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return "   Item UUID: " + getUuid() + "\n" + "   Name: " + getName() + "\n";
	}

	public abstract double getCost();

	public abstract double getTax();

}

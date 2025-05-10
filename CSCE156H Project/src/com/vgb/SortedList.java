package com.vgb;

import java.util.Comparator;
import java.util.Iterator;

/**
 * This is the ADT that sorts invoices or customers depending on the given
 * method of sorting them.
 * 
 * @param <T>
 */
public class SortedList<T> implements Iterable<T> {
	private Node<T> head;
	private int size;
	private final Comparator<T> comparator;

	public SortedList(Comparator<T> comparator) {
		this.head = null;
		this.size = 0;
		this.comparator = comparator;
	}

	/**
	 * Inserts an element at an index according to how the list is sorted.
	 * 
	 * @param element
	 */
	public void insert(T element) {
		Node<T> newNode = new Node<>(element);

		if (head == null || comparator.compare(element, head.getElement()) <= 0) {
			newNode.setNext(head);
			head = newNode;
		} else {
			Node<T> current = head;
			while (current.getNext() != null && comparator.compare(element, current.getNext().getElement()) > 0) {
				current = current.getNext();
			}
			newNode.setNext(current.getNext());
			current.setNext(newNode);
		}
		size++;
	}

	/**
	 * Retrieves the list element as the given index.
	 * 
	 * @param index
	 * @return
	 */
	public T get(int index) {
		if (0 > index || index >= size) {
			throw new IndexOutOfBoundsException("Invlaid position: " + index);
		}
		Node<T> current = head;
		for (int i = 0; i < index; i++) {
			current = current.getNext();
		}
		return current.getElement();
	}

	/**
	 * Removes an element at a given index.
	 * 
	 * @param element
	 */
	public void remove(int index) {
		if (index < 0 || index >= this.size) {
			throw new IndexOutOfBoundsException("Invlaid position: " + index);
		}
		if (index == 0) {
			head = head.getNext();
		} else {
			Node<T> current = head;
			for (int i = 0; i < index - 1; i++) {
				current = current.getNext();
			}
			current.setNext(current.getNext().getNext());
		}
		size--;
	}

	/**
	 * Checks if an element is in the Sorted List, used for testing the remove
	 * method.
	 * 
	 * @param element
	 * @return
	 */
	public boolean contains(T element) {
		Node<T> current = head;
		while (current != null) {
			if (comparator.compare(element, current.getElement()) == 0) {
				return true;
			}
			current = current.getNext();
		}
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		return new SortedListIterator<>(head);
	}
}

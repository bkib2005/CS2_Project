package com.vgb;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class SortedListIterator<T> implements Iterator<T> {
	private Node<T> current;

    public SortedListIterator(Node<T> head) {
        this.current = head;
    }

    @Override
    public boolean hasNext() {
        return current != null;
    }

    @Override
    public T next() {
        if (current == null) throw new NoSuchElementException();
        T element = current.getElement();
        current = current.getNext();
        return element;
    }
}

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
	private class Node{
		public Item data;
		public Node prev;
		public Node next;
		public Node(Item data){
			this.data = data;
			this.prev = null;
			this.next = null;
		}
	}
	private Node first = null;
	private Node last = null;
	private int size = 0;
	
	public Deque() { // construct an empty deque
		first = null;
		last = null;
		size = 0;
	}

	public boolean isEmpty() { // is the deque empty?
		return first == null;
	}

	public int size() { // return the number of items on the deque
		return size;
	}

	public void addFirst(Item item) { // insert the item at the front
		if(item == null)
			throw new NullPointerException();
		
		Node newNode = new Node(item);
		if(isEmpty()){
			first = newNode;
			last = newNode;
		}
		else{
			newNode.next = first;
			first.prev = newNode;
			first = newNode;
		}
		size += 1;
	}

	public void addLast(Item item) { // insert the item at the end
		if(item == null)
			throw new NullPointerException();
		
		Node newNode = new Node(item);
		if(isEmpty()){
			first = newNode;
			last = newNode;
		}
		else{
			newNode.prev = last;
			last.next = newNode;
			last = newNode;
		}
		size += 1;
	}

	public Item removeFirst() { // delete and return the item at the front
		if (isEmpty())
			throw new NoSuchElementException();
		
		Node result = first;
		if(size() == 1){
			first = null;
			last = null;
		}
		else{
			Node second = first.next;
			second.prev = null;
			first = second;
		}
		size -= 1;
		return result.data;
	}

	public Item removeLast() { // delete and return the item at the end
		if (isEmpty())
			throw new NoSuchElementException();
		
		Node result = last;
		if(size() == 1){
			first = null;
			last = null;
		}
		else{
			Node second = last.prev;
			second.next = null;
			last = second;
		}
		size -= 1;
		return result.data;
	}

	private class DequeIterator implements Iterator<Item>{
		private Node current = first;
		
		public boolean hasNext() {
			return current != null;
		}

		@Override
		public Item next() {
			if (current == null)
				throw new NoSuchElementException();
			
			Item item = current.data;
			current = current.next;
			return item;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	public Iterator<Item> iterator() { // return an iterator over items in order from front to end
		return new DequeIterator();
	}
}

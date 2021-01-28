package lib280.list;


import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import lib280.base.BilinearIterator280;
import lib280.base.CursorPosition280;
import lib280.base.Pair280;
import lib280.exception.*;

/**	This list class incorporates the functions of an iterated 
	dictionary such as has, obtain, search, goFirst, goForth, 
	deleteItem, etc.  It also has the capabilities to iterate backwards 
	in the list, goLast and goBack. */
public class BilinkedList280<I> extends LinkedList280<I> implements BilinearIterator280<I>
{
	/* 	Note that because firstRemainder() and remainder() should not cut links of the original list,
		the previous node reference of firstNode is not always correct.
		Also, the instance variable prev is generally kept up to date, but may not always be correct.  
		Use previousNode() instead! */

	/**	Construct an empty list.
		Analysis: Time = O(1) */
	public BilinkedList280()
	{
		super();
	}

	/**
	 * Create a BilinkedNode280 this Bilinked list.  This routine should be
	 * overridden for classes that extend this class that need a specialized node.
	 * @param item - element to store in the new node
	 * @return a new node containing item
	 */
	protected BilinkedNode280<I> createNewNode(I item)
	{
		return new BilinkedNode280<I>(item);
		//TODO
	}

	/**
	 * Insert element at the beginning of the list
	 * @param x item to be inserted at the beginning of the list 
	 */
	public void insertFirst(I x) 
	{
		BilinkedNode280<I> newNode = createNewNode(x);
		if(!isEmpty()){
			BilinkedNode280<I> prevNode = (BilinkedNode280<I>) this.head;
			prevNode.setPreviousNode(newNode);
			newNode.setNextNode(prevNode);
			if (position == head){
				prevPosition = newNode;
			}
		}
		else{
			this.tail = newNode;
		}
		this.head = newNode;
		//TODO
	}

	/**
	 * Insert element at the beginning of the list
	 * @param x item to be inserted at the beginning of the list 
	 */
	public void insert(I x) 
	{
		this.insertFirst(x);
	}

	/**
	 * Insert an item before the current position.
	 * @param x - The item to be inserted.
	 */
	public void insertBefore(I x) throws InvalidState280Exception {
		if( this.before() ) throw new InvalidState280Exception("Cannot insertBefore() when the cursor is already before the first element.");
		
		// If the item goes at the beginning or the end, handle those special cases.
		if( this.head == position ) {
			insertFirst(x);  // special case - inserting before first element
		}
		else if( this.after() ) {
			insertLast(x);   // special case - inserting at the end
		}
		else {
			// Otherwise, insert the node between the current position and the previous position.
			BilinkedNode280<I> newNode = createNewNode(x);
			newNode.setNextNode(position);
			newNode.setPreviousNode((BilinkedNode280<I>)this.prevPosition);
			prevPosition.setNextNode(newNode);
			((BilinkedNode280<I>)this.position).setPreviousNode(newNode);
			
			// since position didn't change, but we changed it's predecessor, prevPosition needs to be updated to be the new previous node.
			prevPosition = newNode;			
		}
	}
	
	
	/**	Insert x before the current position and make it current item. <br>
		Analysis: Time = O(1)
		@param x item to be inserted before the current position */
	public void insertPriorGo(I x) 
	{
		this.insertBefore(x);
		this.goBack();
	}

	/**	Insert x after the current item. <br>
		Analysis: Time = O(1) 
		@param x item to be inserted after the current position */
	public void insertNext(I x) 
	{
		if (isEmpty() || before())
			insertFirst(x); 
		else if (this.position==lastNode())
			insertLast(x); 
		else if (after()) // if after then have to deal with previous node
		{
			insertLast(x); 
			this.position = this.prevPosition.nextNode();
		}
		else // in the list, so create a node and set the pointers to the new node 
		{
			BilinkedNode280<I> temp = createNewNode(x);
			temp.setNextNode(this.position.nextNode());
			temp.setPreviousNode((BilinkedNode280<I>)this.position);
			((BilinkedNode280<I>) this.position.nextNode()).setPreviousNode(temp);
			this.position.setNextNode(temp);
		}

	}

	/**
	 * Insert a new element at the end of the list
	 * @param x item to be inserted at the end of the list 
	 */
	public void insertLast(I x) 
	{
		BilinkedNode280<I> newNode = createNewNode(x);
		newNode.setNextNode(null);

		if (!isEmpty() && this.after()){
			this.prevPosition = newNode;
		}
		if (isEmpty()){
			head = newNode;
		}
		else{
			newNode.setPreviousNode((BilinkedNode280<I>)this.tail);
			tail.setNextNode(newNode);
		}
		this.tail = newNode;
		//TODO
	}

	/**
	 * Delete the item at which the cursor is positioned
	 * @precond itemExists() must be true (the cursor must be positioned at some element)
	 */
	public void deleteItem() throws NoCurrentItem280Exception
	{
		if (!itemExists())
			throw new NoCurrentItem280Exception("No item at cursor to delete");
		if (position == head){
			deleteFirst();
			position = head;
		}
		else if (position == tail){
			deleteLast();
			position = tail;
		}
		else {
			BilinkedNode280<I> next = (BilinkedNode280<I>)position.nextNode();
			BilinkedNode280<I> prev = (BilinkedNode280<I>)prevPosition;
			next.setPreviousNode(prev);
			prev.setNextNode(next);
		}
		//TODO
	}

	
	@Override
	public void delete(I x) throws ItemNotFound280Exception {
		if( this.isEmpty() ) throw new ContainerEmpty280Exception("Cannot delete from an empty list.");

		// Save cursor position
		LinkedIterator280<I> savePos = this.currentPosition();
		
		// Find the item to be deleted.
		search(x);
		if( !this.itemExists() ) throw new ItemNotFound280Exception("Item to be deleted wasn't in the list.");

		// If we are about to delete the item that the cursor was pointing at,
		// advance the cursor in the saved position, but leave the predecessor where
		// it is because it will remain the predecessor.
		if( this.position == savePos.cur ) savePos.cur = savePos.cur.nextNode();
		
		// If we are about to delete the predecessor to the cursor, the predecessor 
		// must be moved back one item.
		if( this.position == savePos.prev ) {
			
			// If savePos.prev is the first node, then the first node is being deleted
			// and savePos.prev has to be null.
			if( savePos.prev == this.head ) savePos.prev = null;
			else {
				// Otherwise, Find the node preceding savePos.prev
				LinkedNode280<I> tmp = this.head;
				while(tmp.nextNode() != savePos.prev) tmp = tmp.nextNode();
				
				// Update the cursor position to be restored.
				savePos.prev = tmp;
			}
		}
				
		// Unlink the node to be deleted.
		if( this.prevPosition != null)
			// Set previous node to point to next node.
			// Only do this if the node we are deleting is not the first one.
			this.prevPosition.setNextNode(this.position.nextNode());
		
		if( this.position.nextNode() != null )
			// Set next node to point to previous node 
			// But only do this if we are not deleting the last node.
			((BilinkedNode280<I>)this.position.nextNode()).setPreviousNode(((BilinkedNode280<I>)this.position).previousNode());
		
		// If we deleted the first or last node (or both, in the case
		// that the list only contained one element), update head/tail.
		if( this.position == this.head ) this.head = this.head.nextNode();
		if( this.position == this.tail ) this.tail = this.prevPosition;
		
		// Clean up references in the node being deleted.
		this.position.setNextNode(null);
		((BilinkedNode280<I>)this.position).setPreviousNode(null);
		
		// Restore the old, possibly modified cursor.
		this.goPosition(savePos);
		
	}
	/**
	 * Remove the first item from the list.
	 * @precond !isEmpty() - the list cannot be empty
	 */
	public void deleteFirst() throws ContainerEmpty280Exception
	{
		if (isEmpty())
			throw new ContainerEmpty280Exception("Cannot delete from empty container");
		BilinkedNode280<I> nextNode = (BilinkedNode280<I>) head.nextNode();
		if (position == head) {
			position = nextNode;
		}
		if (nextNode != null){
			nextNode.setPreviousNode(null);
			head = nextNode;
		}
		else{
			head = null;
			tail = null;
		}
		//TODO
	}

	/**
	 * Remove the last item from the list.
	 * @precond !isEmpty() - the list cannot be empty
	 */
	public void deleteLast() throws ContainerEmpty280Exception
	{
		// Empty list
		if (isEmpty())
			throw new ContainerEmpty280Exception("Error: Cannot delete last on empty container");
		// Only 1 item in list so reset the list to empty
		if (tail == head){
			head = null;
			tail = null;
			position = null;
			prevPosition = null;
			return;
		}
		// At least 2 items
		BilinkedNode280<I> newLast = ((BilinkedNode280<I>)tail).previousNode;
		// Update cursor if it is on last node. Move it to previous node
		if (position == tail){
			position = newLast;
			prevPosition = newLast.previousNode;
		}
		tail = newLast;
		tail.setNextNode(null);
		//TODO
	}

	
	/**
	 * Move the cursor to the last item in the list.
	 * @precond The list is not empty.
	 */
	public void goLast() throws ContainerEmpty280Exception
	{
		if (isEmpty())
			throw new ContainerEmpty280Exception("Cannot goLast() on empty container");
		position = tail;
		prevPosition = ((BilinkedNode280<I>)tail).previousNode;
		// TODO

	}
  
	/**	Move back one item in the list. 
		Analysis: Time = O(1)
		@precond !before() 
	 */
	public void goBack() throws BeforeTheStart280Exception
	{
		if (before())
			throw new BeforeTheStart280Exception("Error: Cursor cannot move back from the before position");
		if (prevPosition == null){
			position = null;
			return;
		}
		position = prevPosition;
		prevPosition = ((BilinkedNode280<I>)prevPosition).previousNode;
		// TODO

	}

	/**	Iterator for list initialized to first item. 
		Analysis: Time = O(1) 
	*/
	public BilinkedIterator280<I> iterator()
	{
		return new BilinkedIterator280<I>(this);
	}

	/**	Go to the position in the list specified by c. <br>
		Analysis: Time = O(1) 
		@param c position to which to go */
	@SuppressWarnings("unchecked")
	public void goPosition(CursorPosition280 c)
	{
		if (!(c instanceof BilinkedIterator280))
			throw new InvalidArgument280Exception("The cursor position parameter" 
					    + " must be a BilinkedIterator280<I>");
		BilinkedIterator280<I> lc = (BilinkedIterator280<I>) c;
		this.position = lc.cur;
		this.prevPosition = lc.prev;
	}

	/**	The current position in this list. 
		Analysis: Time = O(1) */
	public BilinkedIterator280<I> currentPosition()
	{
		return  new BilinkedIterator280<I>(this, this.prevPosition, this.position);
	}

	
  
	/**	A shallow clone of this object. 
		Analysis: Time = O(1) */
	public BilinkedList280<I> clone() throws CloneNotSupportedException
	{
		return (BilinkedList280<I>) super.clone();
	}


	/* Regression test. */
	public static void main(String[] args) {
		BilinkedList280<Integer> l = new BilinkedList280<>();

		// insertFirst() testing
		l.insertFirst(7);
		if (l.isEmpty())
			System.out.println("Error 1: List should not be empty");
		if (l.firstItem() != 7)
			System.out.println("Error 2: Expected 7, got : " + l.firstItem());
		// List state: [7]
		l.insertFirst(6);
		if (l.isEmpty())
			System.out.println("Error 3: List should be empty");
		if (l.firstItem() != 6)
			System.out.println("Error 4: Expected 6, got : " + l.firstItem());

		// Test prev cursor position if position is at first element. Also test insertFirst again
		l.goFirst();
		l.insertFirst(5);
		if (l.firstItem() != 5)
			System.out.println("Error 5: Expected 5, got : " + l.firstItem());
		if (l.prevPosition.item() != 5)
			System.out.println("Error 6: Prev position. Expected 6, got : " + l.prevPosition.item());
		//Test references of populated list
		l.goFirst();
		l.goForth();
		if (l.item() != 6)
			System.out.println("Error 7: Expected 6, got: " + l.item());
		l.goForth();
		if (l.item() != 7)
			System.out.println("Error 8: Expected 7, got: " + l.item());


		//	 insertLast() Testing
		// On an empty list
		l = new BilinkedList280<Integer>();
		l.insertLast(2);
		if (l.firstItem()!=2)
			System.out.println("Error 8.1: Expected 2, got: " + l.firstItem());
		// On a list with one element
		l.insertLast(3);
		if (l.lastItem()!=3)
			System.out.println("Error 9: Expected 3, got: " + l.lastItem());
		if (l.tail.item()!=3)
			System.out.println("Error 10: Expected a tail of 3, got: " + l.tail.item());
		if (l.head.item()!=2)
			System.out.println("Error 11: Expected a head of 2, got: " + l.head.item());
		// On a list with 2 elements
		l.insertLast(4);
		if (l.lastItem()!=4)
			System.out.println("Error 12: Expected 4, got: " + l.lastItem());
		if (l.tail.item()!=4)
			System.out.println("Error 13: Expected a tail of 4, got: " + l.tail.item());
		// On a list with multiple elements
		l.insertLast(99);
		if (l.lastItem()!=99)
			System.out.println("Error 14: Expected 99, got: " + l.lastItem());

		// 	deleteItem() testing
		// Test deleting from a cursor position with null element. In this case empty list
		l = new BilinkedList280<>();
		// On an empty list
		try{
			l.deleteItem();
			System.out.println("Error 15: Can't call delete() on an empty list");
		}
		catch (NoCurrentItem280Exception e){
			// Expect this exception throws
		}
		// Insert element to empty list, move cursor there, delete element. Expect empty list
		l.insert(2);
		l.goFirst();
		l.deleteItem();
		if (!l.isEmpty())
			System.out.println("Error 16: Expected and empty list. List is not empty");
		// Add 3 elements, delete one and check against expected list. make sure cursor is still at first
		l.insert(3);
		l.insert(2);
		l.insert(1);
		l.goFirst();
		l.deleteItem();
		if (!l.toString().equals("2, 3, "))
			System.out.println("Error 17: Expected a list with elements: 2, 3, list but got: "+l.toString());
		if (l.position.item != 2)
			System.out.println("Error 17.1: Expected cursor to be at 2, list but got: "+l.item());
		// Delete middle element of list
		l.insert(1);
		l.deleteItem();
		if (!l.toString().equals("1, 3, "))
			System.out.println("Error 17.2: Expected a list with elements: 1, 3, list but got: "+l.toString());
		// Delete from end of list, make sure tail is updated
		l = new BilinkedList280<Integer>();
		l.insert(3);
		l.insert(2);
		l.insert(1);
		l.goFirst();
		l.goForth();
		l.goForth();
		l.deleteItem();
		if (!l.toString().equals("1, 2, "))
			System.out.println("Error 17.3: Expected a list with elements: 1, 2, list but got: "+l.toString());
		if (l.tail.item != 2 || l.item() != 2)
			System.out.println("Error 17.4: Expected a tail of: 2 but got: "+l.toString());

		// deleteLast() testing
		l.insertLast(4);
		// Multiple element list: 1,2,3 list
		l.deleteLast();
		if (!l.toString().equals("1, 2, "))
			System.out.println("Error 18.1: Expected a list with elements: 1, 2, list but got: "+l.toString());
		if (l.tail.item != 2 || l.item() != 2)
			System.out.println("Error 18.2: Expected a tail of: 2 but got: "+l.tail.item);
		// 2 element list: 1,2
		l.deleteLast();
		if (!l.toString().equals("1, "))
			System.out.println("Error 18.3: Expected a list with elements: 1, list but got: "+l.toString());
		if (l.tail.item != 1 || l.item() != 1)
			System.out.println("Error 18.4: Expected a tail of: 1 but got: "+l.tail.item);
		// 2 element list: 1,2
		l.deleteLast();
		if (!l.isEmpty())
			System.out.println("Error 18.5: Expected an empty list but got: "+l.toString());
		if (l.tail != null)
			System.out.println("Error 18.6: Expected a tail of: null but got: "+l.tail.item);

		// golast() testing
		// empty list goLast()
		try{
			l.goLast();
			System.out.println("Error 19.1: Should have thrown an container empty exception for goLast() on empty list");
		}
		catch (Container280Exception e){
			//do nothing. Wanted to test the exceptions being thrown
		}
		// goLast on single element list
		l.insert(1);
		l.goLast();
		if (l.tail.item != 1)
			System.out.println("Error 19.2: Expected a tail of 1, but got: " + l.tail.item);
		// goLast on two element list
		l.insertLast(2);
		l.goLast();
		if (l.tail.item != 2)
			System.out.println("Error 19.3: Expected a tail of 2, but got: " + l.tail.item);
		// goLast on three element list
		l.insertLast(3);
		l.goLast();
		if (l.tail.item != 3)
			System.out.println("Error 19.3: Expected a tail of 3, but got: " + l.tail.item);

		// goBack() testing
		l = new BilinkedList280<Integer>();
		// goBack on 3 element list
		l.insert(1);
		l.insert(2);
		l.insert(3);
		l.goLast();
		l.goBack();
		if (l.position.item != 2)
			System.out.println("Error 20.1: Expected a cursor position with item: 2, but got: " + l.position.item);
		// goBack while cursor is on first position
		l.goFirst();
		l.goBack();
		if (!l.before())
			System.out.println("Error 20.2: Cursor is not at before position");
		try{
			l.goBack();
			System.out.println("Error 20.2: Should have thrown an container empty exception for goBack() on empty list");
		}
		catch (BeforeTheStart280Exception e){
			//do nothing. Wanted to test the exceptions being thrown
		}

		//Bilinked Iterator unit testing for goBack
		// Empty list
		l = new BilinkedList280<>();
		BilinkedIterator280<Integer> ita = l.iterator();
		l.goBack();
		l.goForth();
		l.goLast();
		l.goBack();
		// Single element list
		l = new BilinkedList280<>();
		l.insert(1);
		// two element list
		l = new BilinkedList280<>();
		l.insert(1);
		l.insert(2);
		// Multiple element list
		l = new BilinkedList280<>();
		l.insert(1);
		l.insert(2);
		l.insert(3);
		l.insert(3);

	}
} 

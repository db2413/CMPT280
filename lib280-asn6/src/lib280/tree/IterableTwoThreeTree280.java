package lib280.tree;

import lib280.base.CursorPosition280;
import lib280.base.Keyed280;
import lib280.base.Pair280;
import lib280.dictionary.KeyedDict280;
import lib280.exception.*;
import lib280.list.BilinkedList280;
import lib280.list.LinkedList280;

public class IterableTwoThreeTree280<K extends Comparable<? super K>, I extends Keyed280<K>> extends TwoThreeTree280<K, I> implements KeyedDict280<K,I> {

	// References to the leaf nodes with the smallest and largest keys.
	LinkedLeafTwoThreeNode280<K,I> smallest, largest;
	
	// These next two variables represent the cursor which
	// the methods inherited from KeyedLinearIterator280 will
	// manipulate.  The cursor may only be positioned at leaf
	// nodes, never at internal nodes.
	
	// Reference to the leaf node at which the cursor is positioned.
	LinkedLeafTwoThreeNode280<K,I> cursor;
	
	// Reference to the predecessor of the node referred to by 'cursor' 
	// (or null if no such node exists).
	LinkedLeafTwoThreeNode280<K,I> prev;
	
	
	protected LinkedLeafTwoThreeNode280<K,I> createNewLeafNode(I newItem) {
		return new LinkedLeafTwoThreeNode280<K,I>(newItem);
	}


	@Override
	public void insert(I newItem) {

		if( this.has(newItem.key()) ) 
			throw new DuplicateItems280Exception("Key already exists in the tree.");

		// If the tree is empty, just make a leaf node. 
		if( this.isEmpty() ) {
			this.rootNode = createNewLeafNode(newItem);
			// Set the smallest and largest nodes to be the one leaf node in the tree.
			this.smallest = (LinkedLeafTwoThreeNode280<K, I>) this.rootNode;
			this.largest = (LinkedLeafTwoThreeNode280<K, I>) this.rootNode;
		}
		// If the tree has one node, make an internal node, and make it the parent
		// of both the existing leaf node and the new leaf node.
		else if( !this.rootNode.isInternal() ) {
			LinkedLeafTwoThreeNode280<K,I> newLeaf = createNewLeafNode(newItem);
			LinkedLeafTwoThreeNode280<K,I> oldRoot = (LinkedLeafTwoThreeNode280<K,I>)rootNode;
			InternalTwoThreeNode280<K,I> newRoot;
			if( newItem.key().compareTo(oldRoot.getKey1()) < 0) {
				// New item's key is smaller than the existing item's key...
				newRoot = createNewInternalNode(newLeaf, oldRoot.getKey1(), oldRoot, null, null);	
				newLeaf.setNext(oldRoot);
				oldRoot.setPrev(newLeaf);
				
				// There was one leaf node, now there's two.  Update smallest and largest nodes.
				this.smallest = newLeaf;
				this.largest = oldRoot;
			}
			else {
				// New item's key is larger than the existing item's key. 
				newRoot = createNewInternalNode(oldRoot, newItem.key(), newLeaf, null, null);
				oldRoot.setNext(newLeaf);
				newLeaf.setPrev(oldRoot);
				
				// There was one leaf node, now there's two.  Update smallest and largest nodes.
				this.smallest = oldRoot;
				this.largest = newLeaf;
			}
			this.rootNode = newRoot;
		}
		else {
			Pair280<TwoThreeNode280<K,I>, K> extra = this.insert((InternalTwoThreeNode280<K,I>)this.rootNode, newItem);

			// If extra returns non-null, then the root was split and we need
			// to make a new root.
			if( extra != null ) {
				InternalTwoThreeNode280<K,I> oldRoot = (InternalTwoThreeNode280<K,I>)rootNode;

				// extra always contains larger keys than its sibling.
				this.rootNode = createNewInternalNode(oldRoot, extra.secondItem(), extra.firstItem(), null, null);				
			}
		}
	}


	/**
	 * Recursive helper for the public insert() method.
	 * @param root Root of the (sub)tree into which we are inserting.
	 * @param newItem The item to be inserted.
	 */
	protected Pair280<TwoThreeNode280<K,I>, K> insert(TwoThreeNode280<K,I> root,
                                                      I newItem) {

		if( !root.isInternal() ) {
			// If root is a leaf node, then it's time to create a new
			// leaf node for our new element and return it so it gets linked
			// into root's parent.
			Pair280<TwoThreeNode280<K,I>, K> extraNode;
			LinkedLeafTwoThreeNode280<K,I> oldLeaf = (LinkedLeafTwoThreeNode280<K, I>) root;

			// If the new element is smaller than root, copy root's element to
			// a new leaf node, put new element in existing leaf node, and
			// return new leaf node.
			if( newItem.key().compareTo(root.getKey1()) < 0) {
				extraNode = new Pair280<TwoThreeNode280<K,I>, K>(createNewLeafNode(root.getData()), root.getKey1());
				((LeafTwoThreeNode280<K,I>)root).setData(newItem);
			}
			else {
				// Otherwise, just put the new element in a new leaf node
				// and return it.
				extraNode = new Pair280<TwoThreeNode280<K,I>, K>(createNewLeafNode(newItem), newItem.key());
			}
			
			LinkedLeafTwoThreeNode280<K,I> newLeaf= (LinkedLeafTwoThreeNode280<K, I>) extraNode.firstItem();
		
			// No matter what happens above, the node 'newLeaf' is a new leaf node that is 
			// immediately to the right of the node 'oldLeaf'.

			//  Link newLeaf to its proper successor/predecessor nodes and
			//  adjust links of successor/predecessor nodes accordingly.

			// Also adjust this.largest if necessary.
			newLeaf.setPrev(oldLeaf);
			newLeaf.setNext(oldLeaf.next);
			if(newLeaf.next != null) newLeaf.next.setPrev(newLeaf);
			else largest = newLeaf;
			oldLeaf.setNext(newLeaf);


			// (this.smallest will never need adjustment because if a new
			//  smallest element is inserted, it gets put in the existing 
			//  leaf node, and the old smallest element is copied to a  
			//  new node -- this is "true" case for the previous if/else.)
			
		
			return extraNode;
		}
		else { // Otherwise, recurse! 
			Pair280<TwoThreeNode280<K,I>, K> extra;
			TwoThreeNode280<K,I> insertSubtree;

			if( newItem.key().compareTo(root.getKey1()) < 0 ) {
				// decide to recurse left
				insertSubtree = root.getLeftSubtree();
			}
			else if(!root.isRightChild() || newItem.key().compareTo(root.getKey2()) < 0 ) {
				// decide to recurse middle
				insertSubtree = root.getMiddleSubtree();
			}
			else {
				// decide to recurse right
				insertSubtree = root.getRightSubtree();
			}

			// Actually recurse where we decided to go.
			extra = insert(insertSubtree, newItem);

			// If recursion resulted in a new node needs to be linked in as a child
			// of root ...
			if( extra != null ) {
				// Otherwise, extra.firstItem() is an internal node... 
				if( !root.isRightChild() ) {
					// if root has only two children.  
					if( insertSubtree == root.getLeftSubtree() ) {
						// if we inserted in the left subtree...
						root.setRightSubtree(root.getMiddleSubtree());
						root.setMiddleSubtree(extra.firstItem());
						root.setKey2(root.getKey1());
						root.setKey1(extra.secondItem());
						return null;
					}
					else {
						// if we inserted in the right subtree...
						root.setRightSubtree(extra.firstItem());
						root.setKey2(extra.secondItem());
						return null;
					}
				}
				else {
					// otherwise root has three children
					TwoThreeNode280<K, I> extraNode;
					if( insertSubtree == root.getLeftSubtree()) {
						// if we inserted in the left subtree
						extraNode = createNewInternalNode(root.getMiddleSubtree(), root.getKey2(), root.getRightSubtree(), null, null);
						root.setMiddleSubtree(extra.firstItem());
						root.setRightSubtree(null);
						K k1 = root.getKey1();
						root.setKey1(extra.secondItem());
						return new Pair280<TwoThreeNode280<K,I>, K>(extraNode, k1);
					}
					else if( insertSubtree == root.getMiddleSubtree()) {
						// if we inserted in the middle subtree
						extraNode = createNewInternalNode(extra.firstItem(), root.getKey2(), root.getRightSubtree(), null, null);
						root.setKey2(null);
						root.setRightSubtree(null);
						return new Pair280<TwoThreeNode280<K,I>, K>(extraNode, extra.secondItem());
					}
					else {
						// we inserted in the right subtree
						extraNode = createNewInternalNode(root.getRightSubtree(), extra.secondItem(), extra.firstItem(), null, null);
						K k2 = root.getKey2();
						root.setKey2(null);
						root.setRightSubtree(null);
						return new Pair280<TwoThreeNode280<K,I>, K>(extraNode, k2);
					}
				}
			}
			// Otherwise no new node was returned, so there is nothing extra to link in.
			else return null;
		}		
	}


	@Override
	public void delete(K keyToDelete) {
		if( this.isEmpty() ) return;

		if( !this.rootNode.isInternal()) {
			if( this.rootNode.getKey1() == keyToDelete ) {
				this.rootNode = null;
				this.smallest = null;
				this.largest = null;
			}
		}
		else {
			delete(this.rootNode, keyToDelete);	
			// If the root only has one child, replace the root with its
			// child.
			if( this.rootNode.getMiddleSubtree() == null) {
				this.rootNode = this.rootNode.getLeftSubtree();
				if( !this.rootNode.isInternal() ) {
					this.smallest = (LinkedLeafTwoThreeNode280<K, I>) this.rootNode;
					this.largest = (LinkedLeafTwoThreeNode280<K, I>) this.rootNode;
				}
			}
		}
	}


	/**
	 * Given a key, delete the corresponding key-item pair from the tree.
	 * @param root root of the current tree
	 * @param keyToDelete The key to be deleted, if it exists.
	 */
	protected void delete(TwoThreeNode280<K, I> root, K keyToDelete ) {
		if( root.getLeftSubtree().isInternal() ) {
			// root is internal, so recurse.
			TwoThreeNode280<K,I> deletionSubtree;
			if( keyToDelete.compareTo(root.getKey1()) < 0){
				// recurse left
				deletionSubtree = root.getLeftSubtree();
			}
			else if( root.getRightSubtree() == null || keyToDelete.compareTo(root.getKey2()) < 0 ){
				// recurse middle
				deletionSubtree = root.getMiddleSubtree();
			}
			else {
				// recurse right
				deletionSubtree = root.getRightSubtree();
			}

			delete(deletionSubtree, keyToDelete);

			// Do the first possible of:
			// steal left, steal right, merge left, merge right
			if( deletionSubtree.getMiddleSubtree() == null)  
				if(!stealLeft(root, deletionSubtree))
					if(!stealRight(root, deletionSubtree))
						if(!giveLeft(root, deletionSubtree))
							if(!giveRight(root, deletionSubtree))
								throw new InvalidState280Exception("This should never happen!");

		}
		else {
			// children of root are leaf nodes
			if( root.getLeftSubtree().getKey1().compareTo(keyToDelete) == 0 ) {
				// leaf to delete is on left

				//  Unlink leaf from it's linear successor/predecessor

				// A: prev of B,
				// B: leaf to delete,
				// C: next of B
				LinkedLeafTwoThreeNode280<K,I> B = (LinkedLeafTwoThreeNode280<K,I>) root.getLeftSubtree();
				LinkedLeafTwoThreeNode280<K,I> A = B.prev;
				LinkedLeafTwoThreeNode280<K,I> C = B.next;

				if(A!=null){
					A.setNext(C);
				}
				if (C!=null){
					C.setPrev(A);
				}

				if (smallest == B) this.smallest = C;
				if (largest == B) this.largest = A;

				// Proceed with deletion of leaf from the 2-3 tree.
				root.setLeftSubtree(root.getMiddleSubtree());
				root.setMiddleSubtree(root.getRightSubtree());
				if(root.getMiddleSubtree() == null)
					root.setKey1(null);
				else 
					root.setKey1(root.getKey2());
				if( root.getRightSubtree() != null) root.setKey2(null);
				root.setRightSubtree(null);					
			}
			else if( root.getMiddleSubtree().getKey1().compareTo(keyToDelete) == 0 ) {
				// leaf to delete is in middle

				// Unlink leaf from it's linear successor/predecessor

				// A: prev of B,
				// B: leaf to delete,
				// C: next of B
				LinkedLeafTwoThreeNode280<K,I> B = (LinkedLeafTwoThreeNode280<K,I>) root.getMiddleSubtree();
				LinkedLeafTwoThreeNode280<K,I> A = B.prev;
				LinkedLeafTwoThreeNode280<K,I> C = B.next;

				if(A!=null){
					A.setNext(C);
				}
				if (C!=null){
					C.setPrev(A);
				}

				if (smallest == B) this.smallest = C;
				if (largest == B) this.largest = A;
				
				
				// Proceed with deletion from the 2-3 tree.
				root.setMiddleSubtree(root.getRightSubtree());				
				if(root.getMiddleSubtree() == null)
					root.setKey1(null);
				else 
					root.setKey1(root.getKey2());

				if( root.getRightSubtree() != null) {
					root.setKey2(null);
					root.setRightSubtree(null);
				}
			}
			else if( root.getRightSubtree() != null && root.getRightSubtree().getKey1().compareTo(keyToDelete) == 0 ) {
				// leaf to delete is on the right

				// Unlink leaf from it's linear successor/predecessor

				// A: prev of B,
				// B: leaf to delete,
				// C: next of B
				LinkedLeafTwoThreeNode280<K,I> B = (LinkedLeafTwoThreeNode280<K,I>) root.getRightSubtree();
				LinkedLeafTwoThreeNode280<K,I> A = B.prev;
				LinkedLeafTwoThreeNode280<K,I> C = B.next;

				if(A!=null){
					A.setNext(C);
				}
				if (C!=null){
					C.setPrev(A);
				}

				if (smallest == B) this.smallest = C;
				if (largest == B) this.largest = A;
				
				
				// Proceed with deletion of the node from the 2-3 tree.
				root.setKey2(null);
				root.setRightSubtree(null);
			}
			else {
				// key to delete does not exist in tree.
			}
		}		
	}	
	
	
	@Override
	public K itemKey() throws NoCurrentItem280Exception {
		// REVIEW Return the key of the item in the node on which the cursor is positioned.
		
		// This is just a placeholder to avoid compile errors. Remove it when ready.
		return cursor.getKey1();
	}


	@Override
	public Pair280<K, I> keyItemPair() throws NoCurrentItem280Exception {
		// Return a pair consisting of the key of the item
		// at which the cursor is positioned, and the entire
		// item in the node at which the cursor is positioned.
		if( !itemExists() ) 
			throw new NoCurrentItem280Exception("There is no current item from which to obtain its key.");
		return new Pair280<K, I>(this.itemKey(), this.item());
	}


	@Override
	public I item() throws NoCurrentItem280Exception {
		if(!itemExists()) throw new NoCurrentItem280Exception("No item exists at the cursor");
		return this.cursor.data;
	}


	@Override
	public boolean itemExists() {
		return this.cursor != null;
	}


	@Override
	public boolean before() {
		return this.cursor == null && this.prev == null;
	}


	@Override
	public boolean after() {
		return this.cursor == null && this.prev != null || this.isEmpty();
	}


	@Override
	public void goForth() throws AfterTheEnd280Exception {
		if( this.after() ) throw new AfterTheEnd280Exception("Cannot advance the cursor past the end.");
		if( this.before() ) this.goFirst();
		else {
			this.prev = this.cursor;
			this.cursor = this.cursor.next();
		}
	}


	@Override
	public void goFirst() throws ContainerEmpty280Exception {
		if(this.isEmpty()) throw new ContainerEmpty280Exception("Attempted to move linear iterator to first element of an empty tree.");
		this.prev = null;
		this.cursor = this.smallest;
	}


	@Override
	public void goBefore() {
		this.prev = null;
		this.cursor = null;
	}


	@Override
	public void goAfter() {
		this.prev = this.largest;
		this.cursor = null;
	}


	@Override
	public CursorPosition280 currentPosition() {
		return new TwoThreeTreePosition280<K,I>(this.cursor, this.prev);
	}


	@SuppressWarnings("unchecked")
	@Override
	public void goPosition(CursorPosition280 c) {
		if(c instanceof TwoThreeTreePosition280 ) {
			this.cursor = ((TwoThreeTreePosition280<K,I>) c).cursor;
			this.prev = ((TwoThreeTreePosition280<K,I>) c).prev;
		}
		else {
			throw new InvalidArgument280Exception("The provided position was not a TwoThreeTreePosition280 object.");
		}
	}


	/**
	 * Position the cursor at the item with key k (if such an item exists).
	 * If no item with key k can be found leave the cursor in the after position.
	 * @time O(logN)
	 * @param k key being sought
	 */
	public void search(K k) {
		cursor = (LinkedLeafTwoThreeNode280<K, I>) super.find(rootNode,k);
	}


	@Override
	public void searchCeilingOf(K k) {
		// Position the cursor at the smallest item that
		// has key at least as large as 'k', if such an
		// item exists.  If no such item exists, leave 
		// the cursor in the after position.
		
		// This one is easier to do with a linear search.
		// Could make it potentially faster but the solution is
		// not obvious -- just use linear search via the cursor.
		
		// If it's empty, do nothing; itemExists() will be false.
		if( this.isEmpty() ) 
			return;
		
		// Find first item item >= k.  If there is no such item,
		// cursor will end up in after position, and that's fine
		// since itemExists() will be false.
		this.goFirst();
		while(this.itemExists() && this.itemKey().compareTo(k) < 0) {
			this.goForth();
		}
		
	}

	@Override
	public void setItem(I x) throws NoCurrentItem280Exception,
            InvalidArgument280Exception {
		if(!itemExists()) throw new NoCurrentItem280Exception("No item exists to swap");
		if (!x.key().equals(cursor.data.key())) throw new InvalidArgument280Exception("Cursor's key value does not match the value of the item to set");
		cursor.data = x;
	}


	@Override
	public void deleteItem() throws NoCurrentItem280Exception {
		// Leave the cursor on the successor of the deleted item.
		if (cursor==null) throw new NoCurrentItem280Exception("No item at cursor to delete");
		delete(cursor.data.key());
		cursor = cursor.next();
	}


	
	
	
    @Override
    public String toStringByLevel() {
        String s = super.toStringByLevel();
        
        s += "\nThe Linear Ordering is: ";
        CursorPosition280 savedPos = this.currentPosition();
        this.goFirst();
        while(this.itemExists()) {
            s += this.itemKey() + ", ";
            this.goForth();
        }
        this.goPosition(savedPos);
        
        if( smallest != null)
            s += "\nSmallest: " + this.smallest.getKey1();
        if( largest != null ) {
            s += "\nLargest: " + this.largest.getKey1();
        }
        return s;
    }

	public static void main(String args[]) {

		// A class for an item that is compatible with our 2-3 Tree class.  It has to implement Keyed280
		// as required by the class header of the 2-3 tree.  Keyed280 just requires that the item have a method
		// called key() that returns its key.  You *must* test your tree using Loot objects.

		class Loot implements Keyed280<String> {
			protected int goldValue;
			protected String key;

			@Override
			public String key() {
				return key;
			}
			
			@SuppressWarnings("unused")
			public int itemValue() {
				return this.goldValue;
			}

			Loot(String key, int i) {
				this.goldValue = i;
				this.key = key;
			}

		}

		// Create a tree to test with.
		IterableTwoThreeTree280<String, Loot> T =
				new IterableTwoThreeTree280<String, Loot>();

		// Get all the test loot
		Loot bag_of_holding = new Loot("Bag of Holding",1500);
		Loot blue_ioun_stone = new Loot("Blue Ioun Stone",200000);
		Loot plus_one_mace = new Loot("+1 Mace",2000);
		Loot hideous_halberd = new Loot("Hideous Halberd",600);
		Loot kite_shield = new Loot("Kite Shield",795);
		Loot leather_armor = new Loot("Leather Armor", 10);
		Loot light_flail = new Loot("Light Flail",200);
		Loot master_sword = new Loot("Master Sword",300);
		Loot opal = new Loot("Opal Gem",50);
		Loot plate_armor = new Loot("Plate Armor",450);
		Loot potion_of_healing = new Loot("Potion of Healing", 100);
		Loot ring_of_jumping = new Loot("Ring of Jumping", 1000);
		Loot scroll_of_bane = new Loot("Scroll of Bane", 250);
		Loot vampiric_blade = new Loot("Vampiric Blade", 21050);
		Loot wave_blade = new Loot("Wave Blade", 16000);

		// Testing after and before, goForth, goFirst,goAfter,goBefore
		IterableTwoThreeTree280<String, Loot> S =
				new IterableTwoThreeTree280<String, Loot>();
		if (!S.before()) System.out.println("New list should be in before position");
		if (!S.after()) System.out.println("New list should be in after position");
		S.insert(master_sword);
		if (!S.before()) System.out.println("New list with sword added should STILL be in before position");
		if (!S.before()) System.out.println("New list with sword added should NOT be in after position");
		S.goForth();
		if (S.before()) System.out.println(" Should NOT be in before position after goForth");
		if (S.after()) System.out.println(" Should NOT be in after position after goForth");
		if (S.cursor.data != master_sword) System.out.println("Cursor should be at master Sword node");
		S.goForth();
		if (S.before()) System.out.println(" Should NOT be in before position after goForth");
		if (!S.after()) System.out.println(" Should BE in after position after goForth");
		if (S.itemExists()) System.out.println("Cursor should NOT be at master Sword node");
		S.insert(kite_shield);
		S.insert(ring_of_jumping);
		S.goFirst();
		// Test on multi item tree
		if (S.cursor.data != kite_shield) System.out.println("Cursor should be at Kite Shield node");
		S.goForth();
		if (S.cursor.data != master_sword) System.out.println("Cursor should be at master sword node");
		S.goForth();
		if (S.cursor.data != ring_of_jumping) System.out.println("Cursor should be at ring of jumping node");
		try { // Go one step too far
			S.goForth();
		}
		catch (AfterTheEnd280Exception e){
			// Good we got here, continue testing
		}
		if(!S.after()) System.out.println("Should be in after position");
		S.goBefore();
		if (!S.before()) System.out.println("Should be in before position after goBefore()");

		// Testing insert(key)
		// Add first item
		T.insert(plus_one_mace);

		if (T.height() != 1) System.out.println("1.Height should be 1 after adding to fresh tree");
		T.goFirst();
		if(!T.item().key.equals("+1 Mace"))System.out.println("2.First item should be a +1 mace");
		if(!(T.cursor.next == null || T.cursor.prev == null))System.out.println("prev or next for first item not set to null");

		// Add second item
		T.insert(light_flail);
		T.goFirst();
		if(!T.item().key.equals("+1 Mace"))System.out.println("3.First item should be a +1 mace");
		if(!T.toStringByLevel().equals(
				"\n     2:Light Flail\n" +
				"1:K1:Light Flail\n" +
				"     2:+1 Mace\n" +
				"The Linear Ordering is: +1 Mace, Light Flail, \n" +
				"Smallest: +1 Mace\n" +
				"Largest: Light Flail"))System.out.println("Resulting tree does not match expected");

		// Add multiple items causing split
		T.insert(scroll_of_bane);
		T.insert(plate_armor);
		if (!T.toStringByLevel().equals("\n" +
				"          3:Scroll of Bane\n" +
				"     2:K1:Scroll of Bane\n" +
				"          3:Plate Armor\n" +
				"1:K1:Plate Armor\n" +
				"          3:Light Flail\n" +
				"     2:K1:Light Flail\n" +
				"          3:+1 Mace\n" +
				"The Linear Ordering is: +1 Mace, Light Flail, Plate Armor, Scroll of Bane, \n" +
				"Smallest: +1 Mace\n" +
				"Largest: Scroll of Bane")) System.out.println("Multiple insertions with split. Resulting tree does not match expected");


		// Test delete(key)
		T.delete("+1 Mace");
		if (!T.toStringByLevel().equals("\n" +
				"     2:Scroll of Bane\n" +
				"1:K2:Scroll of Bane\n" +
				"     2:Plate Armor\n" +
				"1:K1:Plate Armor\n" +
				"     2:Light Flail\n" +
				"The Linear Ordering is: Light Flail, Plate Armor, Scroll of Bane, \n" +
				"Smallest: Light Flail\n" +
				"Largest: Scroll of Bane")) System.out.println("Delete +1 Mace causing a give. Resulting tree does not match expected");

		// Test item() and itemKey()
		T.goBefore();
		try{
			T.item();
			System.out.println("item() should have thrown exception");
		}
		catch (NoCurrentItem280Exception e){
			//"Good"
		}
		T.goFirst();
		T.goForth();
		if (T.item()!=plate_armor) System.out.println("item() should have returned 'Plate Armor'");
		if (!T.itemKey().equals("Plate Armor")) System.out.println("itemkey() should have returned 'Plate Armor'");
		T.goForth();
		T.goForth();
		try{
			T.item();
			System.out.println("item() should have thrown exception");
		}
		catch (NoCurrentItem280Exception e){
			//"Good"
		}

		// Test search() on larger tree
		T.insert(potion_of_healing);
		T.insert(master_sword);
		T.insert(blue_ioun_stone);
		T.insert(kite_shield);
		T.insert(wave_blade);
		T.search("MICKEY MOUSE");
		if (!T.after()) System.out.println("Cursor should be in after postion");
		T.search("Master Sword");
		if (T.item() != master_sword) System.out.println("Cursor should be on master sword, a middle item");
		T.search("Wave Blade");
		if (T.item()!=wave_blade)System.out.println("Cursor should be on Wave Blade, the last item");
		T.search("Blue Ioun Stone");
		if (T.item()!=blue_ioun_stone) System.out.println("Cursor should be on Blue Ioun Stone, the first item");

		// Test setItem()
		T.goFirst();
		Loot new_blue = new Loot("Blue Ioun Stone",3333);
		T.setItem(new_blue);
		if (T.item().goldValue!=3333)System.out.println("setItem() should have set the blue ioun stone's value to 3333");
		try {
			T.setItem(master_sword);
			System.out.println("Should have thrown exception when setting the ioun stone to the master sword");
		}
		catch (InvalidArgument280Exception e) {
			// Good we got here
		}

		// Test delete item()
		T.deleteItem();
		if (!T.toStringByLevel().equals("\n" +
				"          3:Wave Blade\n" +
				"     2:K2:Wave Blade\n" +
				"          3:Scroll of Bane\n" +
				"     2:K1:Scroll of Bane\n" +
				"          3:Potion of Healing\n" +
				"1:K2:Potion of Healing\n" +
				"          3:Plate Armor\n" +
				"     2:K1:Plate Armor\n" +
				"          3:Master Sword\n" +
				"1:K1:Master Sword\n" +
				"          3:Light Flail\n" +
				"     2:K1:Light Flail\n" +
				"          3:Kite Shield\n" +
				"The Linear Ordering is: Kite Shield, Light Flail, Master Sword, Plate Armor, Potion of Healing, Scroll of Bane, Wave Blade, \n" +
				"Smallest: Kite Shield\n" +
				"Largest: Wave Blade")) System.out.println("deleteItem() (deletes blue ioun stone)did not yield the expected result. Got: \n" + T.toStringByLevel());
		if (T.cursor.data != kite_shield) System.out.println("deleteItem() did not update the cursor to the successor node in tree. Got:" + T.cursor.data.toString());

		// Delete from before to throw exception
		T.goBefore();
		try {
			T.deleteItem();
			System.out.println("Shouldnt be able to delete form before position");
		}
		catch (NoCurrentItem280Exception e){
			// Good we got here, continue testing
		}

		// Delete from single entry tree
		IterableTwoThreeTree280<String,Loot> U = new IterableTwoThreeTree280<>();
		U.insert(master_sword);
		U.goFirst();
		U.deleteItem();
		if (U.height() != 0) System.out.println("Tree should have a height of 0");
		if (U.cursor != null) System.out.println("Cursor should be at null pos");
		//System.out.println(T.toStringByLevel());

		// Testing currentPosition() and goPosition
		U.insert(master_sword);
		U.insert(hideous_halberd);
		U.insert(bag_of_holding);
		U.goFirst();
		U.goForth(); // should be at hideous halberd
		TwoThreeTreePosition280 pos =  (TwoThreeTreePosition280) U.currentPosition();
		if (pos.cursor.data != hideous_halberd) System.out.println("Stored cursor position should be hideous halberd");
		U.goForth();
		U.goPosition(pos);
		if (pos.cursor.data != hideous_halberd) System.out.println("Stored cursor position should be hideous halberd");
	}


	
}

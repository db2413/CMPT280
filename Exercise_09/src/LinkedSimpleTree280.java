// *** This is a solution to Exercise 3.  It might have some differences
// *** from what we came up with in class.  It also has some slight differences 
// *** from what actually appears in lib280 -- this is to make it successfully
// *** compile without the rest of the library.


/* LinkedSimpleTree280.java
 * ---------------------------------------------
 * Copyright (c) 2004 University of Saskatchewan
 * All Rights Reserved
 * --------------------------------------------- */


import lib280.list.LinkedList280;

/**	An implementation of the SimpleTree280 interface with functions to access and
	set the root node and the root subtrees.  It also has functions to 
	access the root item, test for empty, and to wipe out all the items. */
public class LinkedSimpleTree280<I> implements SimpleTree280<I>, Cloneable
{
	/**	Root node of the lib280.tree. */
	protected BinaryNode280<I> rootNode;

	/**	Create an empty lib280.tree. <br>
		Analysis: Time = O(1) */
	public LinkedSimpleTree280()
	{
		rootNode = null;
	}

	/**	Create a lib280.tree from a root and two subtrees. <
		Analysis: Time = O(1) 
		@param lt lib280.tree to initialize as the left subtree.  If null, the left subtree is empty.
		@param r item to initialize as the root item
		@param rt lib280.tree to initialize as the right subtree.  If null, the right subtree is empty. */
	public LinkedSimpleTree280(LinkedSimpleTree280<I> lt, I r, LinkedSimpleTree280<I> rt) 
	{
		rootNode = createNewNode(r);
		try {
			setRootLeftSubtree(lt);
			setRootRightSubtree(rt);
		}
		catch(Exception e) {
			// This should never happen because we add a root node
			// before setting the subtrees, so it can never be empty.
		}
	}

	/**	Create a new node that is appropriate to this lib280.tree.  This method should be
		overidden for classes that extend this class and need a specialized node,
		i.e., a descendant of BinaryNode280. 
		Analysis: Time = O(1) 
		@param item    The item to be placed in the new node */
	protected BinaryNode280<I> createNewNode(I item)
	{
		return new BinaryNode280<I>(item);
	}

	/**	Is the lib280.tree empty?.
		Analysis: Time = O(1)  */
	public boolean isEmpty()
	{
		return rootNode == null;
	}

	/**	Is the lib280.tree full?.
		Analysis: Time = O(1) */
	public boolean isFull()
	{
		return false;
	}

	/**	Return the root node. 
		Analysis: Time = O(1) */
	protected BinaryNode280<I> rootNode()
	{
		return rootNode;
	}

	/**	Set root node to new node.
		Analysis: Time = O(1) 
		@param newNode node to become the new root node */
	protected void setRootNode(BinaryNode280<I> newNode)
	{
		rootNode = newNode;
	}

	/**	Contents of the root item. 
		Analysis: Time = O(1) 
		@precond !isEmpty()
	 */
	public I rootItem() throws ContainerEmpty280Exception
	{
		if (isEmpty()) 
			throw new ContainerEmpty280Exception("Cannot access the root of an empty lib280.tree.");
		
		return rootNode.item();
	}

	/**	Set contents of the root to x. 
		Analysis: Time = O(1) 
		@precond !isEmpty() 
		@param x item to become the new root item 
	  */
	public void setRootItem(I x) throws ContainerEmpty280Exception
	{
		if (isEmpty())
			throw new ContainerEmpty280Exception("Cannot set the root of an empty lib280.tree.");
		
		rootNode.setItem(x);
	}

	/**	Left subtree of the root. 
		Analysis: Time = O(1) 
		@precond !isEmpty() 
	  */
	public LinkedSimpleTree280<I> rootLeftSubtree() throws ContainerEmpty280Exception
	{
		if (isEmpty())
			throw new ContainerEmpty280Exception("Cannot return a subtree of an empty lib280.tree.");
		
		LinkedSimpleTree280<I> result = this.clone();
		result.clear();
		result.setRootNode(rootNode.leftNode());
		return result;
	}

	/**	Right subtree of the root. 
		Analysis: Time = O(1) 
		@precond !isEmpty() 
	  */
	public LinkedSimpleTree280<I> rootRightSubtree() throws ContainerEmpty280Exception
	{
		if (isEmpty())
			throw new ContainerEmpty280Exception("Cannot return a subtree of an empty lib280.tree.");
		
		LinkedSimpleTree280<I> result = this.clone();
		result.clear();
		result.setRootNode(rootNode.rightNode());
		return result;
	}

	/**	Set the left subtree to t (set isEmpty if t == null). 
		Analysis: Time = O(1) 
		@precond !isEmpty() 
		@param t lib280.tree to become the rootLeftSubtree()
	  */
	public void setRootLeftSubtree(LinkedSimpleTree280<I> t) throws ContainerEmpty280Exception
	{
		if (isEmpty())
			throw new ContainerEmpty280Exception("Cannot set subtree of an empty lib280.tree.");
		
		if (t != null)
			rootNode.setLeftNode(t.rootNode);
		else
			rootNode.setLeftNode(null);
	}

	/**	Set the right subtree to t (set isEmpty if t == null). 
		Analysis: Time = O(1) 
		@precond !isEmpty() 
		@param t lib280.tree to become the rootRightSubtree()
	  */
	public void setRootRightSubtree(LinkedSimpleTree280<I> t) throws ContainerEmpty280Exception
	{
		if (isEmpty())
			throw new ContainerEmpty280Exception("Cannot set subtree of an empty lib280.tree.");
		
		if (t != null)
			rootNode.setRightNode(t.rootNode);
		else
			rootNode.setRightNode(null);
	}

	/**	Remove all items from the lib280.tree.
		Analysis: Time = O(1) */
	public void clear()
	{
		setRootNode(null);
	}



	/**	A shallow clone of this lib280.tree.
		Analysis: Time = O(1)
	  */
	@SuppressWarnings("unchecked")
	public LinkedSimpleTree280<I> clone()
	{
		try
		{
			return (LinkedSimpleTree280<I>) super.clone();
		} catch(CloneNotSupportedException e)
		{
			/*	Should not occur: SimpleList280 extends Cloneable and Object implements clone(). */
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	 * These next two methods are not part of any exercise, they are
	 * just here so that we can see that our solution to Exercise 4
	 * (below) works correctly by printing out the structure of the
	 * lib280.tree in an easy to read format.  We'll investigate why this works
	 * later.
	 */
	
	protected String toStringByLevel(int i)  
	{
		StringBuffer blanks = new StringBuffer((i - 1) * 5);
		for (int j = 0; j < i - 1; j++)
			blanks.append("     ");
	  
		String result = new String();
		if (!isEmpty() && (!rootLeftSubtree().isEmpty() || !rootRightSubtree().isEmpty()))
			result += rootRightSubtree().toStringByLevel(i+1);
		
		result += "\n" + blanks + i + ": " ;
		if (isEmpty())
			result += "-";
		else 
		{
			result += rootItem();
			if (!rootLeftSubtree().isEmpty() || !rootRightSubtree().isEmpty())
				result += rootLeftSubtree().toStringByLevel(i+1);
		}
		return result;
	}

	public void depthFirstPrintTree(){
		if (rootNode!= null){
			depthFirstPrintTreeHelper(rootNode);
		}
	}
	private void depthFirstPrintTreeHelper(BinaryNode280<I> root){
		System.out.println(root.item.toString());
		if (root.leftNode != null){
			depthFirstPrintTreeHelper(root.leftNode);
		}
		if (root.rightNode != null){
			depthFirstPrintTreeHelper(root.rightNode);
		}
	}

	public void inOrderPrintTree(){
		if (rootNode!= null){
			inOrderPrintTreeHelper(rootNode);
		}
	}

	private void inOrderPrintTreeHelper(BinaryNode280<I> root){
		if (root.leftNode != null){
			inOrderPrintTreeHelper(root.leftNode);
		}
		System.out.println(root.item.toString());
		if (root.rightNode != null){
			inOrderPrintTreeHelper(root.rightNode);
		}
	}

	public int treeSize(){
		if (rootNode!=null){
			return treeSizeHelper(rootNode);
		}
		else return 0;
	}

	private int treeSizeHelper(BinaryNode280<I> node){
		if (node == null){
			return 0;
		}
		return treeSizeHelper(node.leftNode)+
				treeSizeHelper(node.rightNode)+
				1;
	}


	public int treeHeight(){
		if (rootNode!=null){
			return treeHeightHelper(rootNode);
		}
		else return 0;
	}

	private int treeHeightHelper(BinaryNode280<I> node){
		if (node == null){
			return 0;
		}
		return  Integer.max(treeHeightHelper(node.leftNode),treeHeightHelper(node.rightNode)) +
				1;
	}

	public void breadthFirstPrintTree(){
		if (rootNode == null) throw new RuntimeException("Empty tree");
		LinkedList280<BinaryNode280<I>> nq = new LinkedList280<>();
		nq.insertFirst(rootNode);

		while (!nq.isEmpty())
		{
			// Visit Node
			BinaryNode280<I> p = nq.firstItem();
			nq.deleteFirst();
			System.out.println(p.item.toString());
			// Add children
			if (p.leftNode!=null){
				nq.insertLast(p.leftNode);
			}
			if (p.rightNode!=null){
				nq.insertLast(p.rightNode);
			}
		}

	}


	/**	String representation of the lib280.tree, level by level. <br>
		Analysis: Time = O(n), where n = number of items in the lib280.tree
	 * @throws ContainerEmpty280Exception 
	  */
	public String toStringByLevel() 
	{
		return toStringByLevel(1);
	}

	// *** Exercise 4 ***
	public static void main(String args[]) {
		// Build trees with a root and empty subtrees for leaf nodes 
		LinkedSimpleTree280<Integer> T1 = new LinkedSimpleTree280<Integer>(null, 1, null);
		LinkedSimpleTree280<Integer> T4 = new LinkedSimpleTree280<Integer>(null, 4, null);
		LinkedSimpleTree280<Integer> T7 = new LinkedSimpleTree280<Integer>(null, 7, null);
		LinkedSimpleTree280<Integer> T8 = new LinkedSimpleTree280<Integer>(null, 8, null);

		// Combine subtrees to build up the entire lib280.tree.
		LinkedSimpleTree280<Integer> T9 = new LinkedSimpleTree280<Integer>(T4, 9, T7);
		LinkedSimpleTree280<Integer> T5 = new LinkedSimpleTree280<Integer>(T1, 5, T9);
		LinkedSimpleTree280<Integer> T3 = new LinkedSimpleTree280<Integer>(T5, 3, T8);
		//System.out.println(T3.toStringByLevel());
		T3.breadthFirstPrintTree();
	}
	
}

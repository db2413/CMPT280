// *** This is a solution to Exercise 1, Demo 1 and Exercise 2.
// *** It might have some differences from what we came up with in class.
// *** It also has some slight differences from what actually appears in lib280 --
// *** this is to make it successfully compile without the rest of the library.

public class BinaryNode280<I> implements Cloneable
{
	/** Contents of the node. */
	protected I item;

	/** The left node. */
	protected BinaryNode280<I> leftNode;

	/** The right node. */
	protected BinaryNode280<I> rightNode;

		
	/** Construct a new node with item x. 
	 *  @timing Time = O(1)
	 *  @param x the item placed in the new node */	
	public BinaryNode280(I x)
	{
		this.item = x;
	}

	/** Contents of the node.
	 *  @timing Time = O(1) */
	public I item()
	{
		return item;
	}

	/** The left node.
	 *  @timing Time = O(1) */
	public BinaryNode280<I> leftNode()
	{
		return leftNode;
	}
	
	/** The left node.
	 *  @timing Time = O(1) */
	public BinaryNode280<I> rightNode()
	{
		return rightNode;
	}
		
	/**
	 * Set the item contained in the node.
	 * @param x The new item to store in the node.
	 * @timing Time = O(1)
	 * */	
	public void setItem(I x) {
		this.item = x;
	}
	
	/**
	 * Set the left child of this node
	 * @param n The new left child of this node.
	 */
	public void setLeftNode(BinaryNode280<I> n) {
		this.leftNode = n;
	}
	
	/**
	 * Set the right child of this node.
	 * @param n The new right child of this node.
	 */
	public void setRightNode(BinaryNode280<I> n) {
		this.rightNode = n;
	}
	
	/**
	 * Returns a string representation of the item contained within the node.
	 */
	public String toString() {
		return this.item.toString();
	}
	
	
	@SuppressWarnings("unchecked")
	public BinaryNode280<I> clone() throws CloneNotSupportedException {
		return (BinaryNode280<I>)super.clone();
	}
	
}
import lib280.tree.BinaryNode280;
import lib280.tree.OrderedSimpleTree280;

public class AvlNode<I extends Comparable<? super I>> extends BinaryNode280<I> {

    public int leftHeight;
    public int rightHeight;

    /**
     * Construct a new node with item x.
     *
     * @param x the item placed in the new node
     * @timing Time = O(1)
     */
    public AvlNode(I x) {
        super(x);
        leftHeight = 0;
        rightHeight = 0;
    }

}

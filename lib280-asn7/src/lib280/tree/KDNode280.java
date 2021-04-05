/**
 * Duncan Boyes
 * dhb021
 * 11084342
 */
package lib280.tree;

import lib280.base.NDPoint280;

public class KDNode280 extends BinaryNode280<NDPoint280> {


    /**
     * Construct a new node with item x.
     *
     * @param x the item placed in the new node
     * @timing Time = O(1)
     */
    public KDNode280(NDPoint280 x) {
        super(x);
    }

    public int dim(){
        return item().dim();
    }

    @Override
    public String toString() {
        return item().toString();
    }
}

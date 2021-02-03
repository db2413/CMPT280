public class BinaryNode280<I> implements Cloneable,Comparable<I>{

    I item;                         // stored item
    BinaryNode280<I> rn;            // left node
    BinaryNode280<I> ln;            // right node

    public BinaryNode280(I node_item){
        this.setItem(node_item);
        this.setLn(null);
        this.setRn(null);
    }

    public I item() {
        return item;
    }

    public BinaryNode280<I> leftNode(){
        return this.ln;
    }

    public BinaryNode280<I> rightNode(){
        return this.rn;
    }

    public void setItem(I item) {
        this.item = item;
    }

    public void setLn(BinaryNode280<I> ln) {
        this.ln = ln;
    }

    public void setRn(BinaryNode280<I> rn){
        this.rn = rn;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
        // TODO implement
    }

    @Override
    public int compareTo(I o) {
        return 0;
        // TODO implement
    }

    @Override
    public String toString() {
        return "BinaryNode280{" +
                "item=" + item.toString() +
                ", rn=" + rn.toString() +
                ", ln=" + ln.toString() +
                '}';
    }
}

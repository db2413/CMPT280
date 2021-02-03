public class ListNode<T> implements Cloneable{
    private ListNode<T> next;
    private T item;

    public ListNode(T item){
        setItem(item);
        setNextNode(null);
    }

    public void setNextNode(ListNode<T> next){
        this.next = next;
    }

    public ListNode<T> getNextNode(){
        return next;
    }

    public T getItem(){
        return item;
    }

    public void setItem(T item){
        this.item = item;
    }

    public ListNode<T> clone() throws CloneNotSupportedException {
        return (ListNode<T>) super.clone();
    }

    public ListNode<T> deepClone() throws CloneNotSupportedException{
        ListNode<T> dclone = (ListNode<T>) super.clone();
        if (dclone.next != null){
            dclone.setNextNode(this.next.deepClone());
        }
        return dclone;
    }
}

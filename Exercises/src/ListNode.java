public class ListNode<T> {
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
}

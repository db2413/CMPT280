package Exercise1_Lists;

public class LinkedList<T> implements ListADT<T>{
    private ListNode<T> head;
    private int numEl;

    public LinkedList(){
        head = null;
        numEl = 0;
    }

    public boolean isEmpty(){
        return numEl == 0;
    }

    public boolean isFull(){
        return false;
    }

    public void insertFirst(T item){
        ListNode<T> old = head;
        head = new ListNode<>(item);
        head.setNextNode(old);
        numEl++;
    }

    public void deleteFirst(){
        if (isEmpty()) throw new RuntimeException("Cannot delete from empty list");

        ListNode<T> old = head;
        head = old.getNextNode();
        old.setNextNode(null);
        numEl--;
    }

    public T firstItem(){
        if (isEmpty()) throw new RuntimeException("Cannot retrieve first item from an empty list");

        return head.getItem();
    }

    public static void main(String[] args) {
        //Test instance
        LinkedList<Double> l = new LinkedList<>();
        // Black box test for insertFirst(). 0,1,2,many elements
        // White box test for insertFirst(). Test a full list. Test when head is null/not null
        try {

            if (!l.isEmpty()) {
                System.out.println("Error: the newly created list is not empty");
            }
            l.insertFirst(2.54);
            if (l.isEmpty() || l.firstItem() != 2.54) {
                System.out.println("Error: list should be not empty and have a first value of 2.54, but got: " + l.firstItem());
            }
            l.insertFirst(0.0);
            if (l.firstItem() != 0) {
                System.out.println("Error: first item should be 0.0, but got: " + l.firstItem() +
                        ", list head should be 2.54");
            }
            l.insertFirst(1.1);
            l.insertFirst(2.2);
            if (l.firstItem() != 2.2) {
                System.out.println("Error: first item should be 2.2, but got: " + l.firstItem());
            }
        }
        catch (Exception e){
            System.out.println("Error: exception thrown when using insert first");
        }
    }
}

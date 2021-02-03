public class LinkedList<T> implements Cursor<T>, ListADT<T>, Cloneable {
    private ListNode<T> head;
    private int numEl;
    // Cursor position
    private ListNode<T> position;
    //Cursor previous position
    private ListNode<T> prevPosition;

    public LinkedList(){
        head = null;
        numEl = 0;
        position = null;
        prevPosition = null;
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

    @Override
    public boolean itemExists() {
        return position != null;
    }

    @Override
    public T item() {
        if (!itemExists()) {throw new RuntimeException("No element at position");}
        else return position.getItem();
    }

    @Override
    public void goFirst() {
        if (this.isEmpty()){
            throw new RuntimeException("Error: goFirst cannot move the cursor to the first element of an empty list");
        }
        this.prevPosition = null;
        this.position = head;
    }

    @Override
    public void goForth() {
        if (this.after()) throw new RuntimeException("Error: Cannot goForth. Cursor is at after end.");
        if (this.before()) {
            goFirst();
        }
        else{
            this.prevPosition = this.position;
            this.position = this.position.getNextNode();
        }
    }

    @Override
    public void goLast() {
        if (isEmpty()) throw new RuntimeException("Error: Cannot goLast on empty list");
        while(position.getNextNode()!=null){
            goForth();
        }
    }

    @Override
    public void goBefore() {
        prevPosition = null;
        position = null;
    }

    @Override
    public void goAfter() {
        goLast();
        prevPosition = position;
        position = null;
    }

    @Override
    public boolean before() {
        return position == null && prevPosition==null && !isEmpty();
    }

    @Override
    public boolean after() {
        return position == null && prevPosition != null && !isEmpty();
    }

    public LinkedList<T> clone() throws CloneNotSupportedException{
        return (LinkedList<T>)super.clone();
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

        /**
         * Test cursor functionality
         */
        l = new LinkedList<Double>();
        for (int i = 0; i < 5; i++) {
            l.insertFirst(Math.random());
        }
        l.goFirst();
        System.out.print("The numbers in the list are: ");
        while (l.itemExists()){
            System.out.print( l.item().toString()+" ");
            l.goForth();
        }
    }

}

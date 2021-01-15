package Exercise1_Lists;

public class ArrayedList<T> implements ListADT<T> {

    int listTail;
    private final int capacity;
    T[] listElements;

    public ArrayedList(int capacity){
        this.capacity = capacity;
        listTail = 0;
    }

    public boolean isEmpty(){
        return listTail == 0;
    }

    public boolean isFull(){
        return listTail == capacity;
    }

    public void insertFirst(T newitem){
        if (isFull()) throw  new RuntimeException("Arrayed List is full. Cannot insert");

        // index walker 'i'. Work from back to front. Shifting items up one index.
        for (int i = listTail; i >0 ; i--) {
            listElements[i+1] = listElements[i];
        }
        // Reassign first item
        listElements[0] = newitem;
        listTail++;
    }

    public void deleteFirst(){
        if (isEmpty()) throw new RuntimeException("Arrayedlist is empty. Cannot remove from empty list.");

        // index walker 'i'. Work from front to back. Shifting all items 1 towards the front
        for (int i = 0; i < listTail; i++) {
            this.listElements[i] = this.listElements[i+1];
        }
        listTail --;
    }

    public T firstItem(){
        if(isEmpty()) throw new RuntimeException("Cannot access first item in empty arrayed list");

        return listElements[0];
    }
}

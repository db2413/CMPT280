import lib280.base.Dispenser280;
import lib280.exception.ContainerFull280Exception;
import lib280.exception.DuplicateItems280Exception;
import lib280.exception.NoCurrentItem280Exception;
import lib280.tree.ArrayedBinaryTree280;

public class ArrayedHeap280<I extends Comparable<? super I>> extends ArrayedBinaryTree280<I> implements Dispenser280<I> {



    /**
     * Constructor.
     *
     * @param cap Maximum number of elements that can be in the lib280.tree.
     */
    public ArrayedHeap280(int cap) {
        super(cap);
        items = (I[]) new Comparable[capacity+1];
        currentNode = 1;
    }

    /**
     * Inserts the element x into the heap
     * @param x item to be inserted into the data structure
     * @throws ContainerFull280Exception Container cant be full
     * @throws DuplicateItems280Exception Container cannot contain duplicate items
     */
    @Override
    public void insert(I x) throws ContainerFull280Exception, DuplicateItems280Exception {
        if( this.isFull() ) throw new ContainerFull280Exception("Cannot add item to a tree that is full.");
        else {
            count ++;
            items[count] = x;
        }
        int i = count;
        int parent = findParent(i);
        while (parent!=0 && x.compareTo(items[parent])>0){
            items[i] = items[parent];
            items[parent] = x;
            i = parent;
            parent = findParent(i);
        }
    }


    /**
     * Remove the largest element from the heap
     * @throws NoCurrentItem280Exception Must be at least 1 item in heap
     */
    @Override
    public void deleteItem() throws NoCurrentItem280Exception {
        if(items[1] == null) {
            throw new NoCurrentItem280Exception("There is no current item to delete.");
        }
        items[1] = items[count];
        items[count] = null;
        count --;

        // If empty heap, set cursor to 0
        if (count == 0){
            currentNode = 0;
            return;
        }

        int i  = 1;
        boolean done = false;
        I el = items[1];
        I largestChild;
        int lNode;
        int rNode;

        while (!done ){
            lNode = findLeftChild(i);
            rNode = findRightChild(i);
            boolean leftHeavy = false;

            if (lNode > count){
                done = true;
                break;
            }
            else if (rNode > count){
                leftHeavy = true;
            }
            else{
                leftHeavy = items[lNode].compareTo(items[rNode])>0;
            }

            if (leftHeavy){
                largestChild = items[findLeftChild(i)];
                if (el.compareTo(largestChild)>=0){
                    done = true;
                }
                else{
                    items[i] = largestChild;
                    items[findLeftChild(i)] = el;
                    i = findLeftChild(i);
                }
            }
            else {
                largestChild = items[findRightChild(i)];
                if (el.compareTo(largestChild)>=0){
                    done = true;
                }
                else{
                    items[i] = largestChild;
                    items[findRightChild(i)] = el;
                    i = findRightChild(i);
                }
            }
        }
    }

    /**
     * Helper for the regression test.  Verifies the heap property for all nodes.
     */
    private boolean hasHeapProperty() {
        for(int i=1; i <= count; i++) {
            if( findRightChild(i) <= count ) {  // if i Has two children...
                // ... and i is smaller than either of them, , then the heap property is violated.
                if( items[i].compareTo(items[findRightChild(i)]) < 0 ) return false;
                if( items[i].compareTo(items[findLeftChild(i)]) < 0 ) return false;
            }
            else if( findLeftChild(i) <= count ) {  // if n has one child...
                // ... and i is smaller than it, then the heap property is violated.
                if( items[i].compareTo(items[findLeftChild(i)]) < 0 ) return false;
            }
            else break;  // Neither child exists.  So we're done.
        }
        return true;
    }

    /**
     * Regression test
     */
    public static void main(String[] args) {

        ArrayedHeap280<Integer> H = new ArrayedHeap280<Integer>(10);

        // Empty heap should have the heap property.
        if(!H.hasHeapProperty()) System.out.println("Does not have heap property.");

        // Insert items 1 through 10, checking after each insertion that
        // the heap property is retained, and that the top of the heap is correctly i.
        for(int i = 1; i <= 10; i++) {
            H.insert(i);
            if(H.item() != i) System.out.println("Expected current item to be " + i + ", got " + H.item());
            if(!H.hasHeapProperty()) System.out.println("Does not have heap property.");
        }

        // Remove the elements 10 through 1 from the heap, chekcing
        // after each deletion that the heap property is retained and that
        // the correct item is at the top of the heap.
        for(int i = 10; i >= 1; i--) {
            // Remove the element i.
            H.deleteItem();
            // If we've removed item 1, the heap should be empty.
            if(i==1) {
                if( !H.isEmpty() ) System.out.println("Expected the heap to be empty, but it wasn't.");
            }
            else {
                // Otherwise, the item left at the top of the heap should be equal to i-1.
                if(H.item() != i-1) System.out.println("Expected current item to be " + i + ", got " + H.item());
                if(!H.hasHeapProperty()) System.out.println("Does not have heap property.");
            }
        }
        System.out.println("Regression Test Complete.");

    }
}

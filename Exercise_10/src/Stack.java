import lib280.exception.Container280Exception;
import lib280.list.SimpleList280;

public abstract class Stack<I> {
    SimpleList280<I> list;
    public void push(I item){
        list.insertFirst(item);
    }
    public void pop()throws Container280Exception {
        list.deleteFirst();
    }
    public I top(){
        return list.firstItem();
    }

}

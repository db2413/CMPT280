import lib280.list.ArrayedList280;

public class ArrayedStack<I> extends Stack<I> {
    public ArrayedStack(int capacity){
        list = new ArrayedList280<I>(capacity);
    }
}

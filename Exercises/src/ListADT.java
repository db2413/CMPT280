public interface ListADT<T> {
    void insertFirst(T item);
    void deleteFirst();
    boolean isEmpty();
    boolean isFull();
    T firstItem();
}

public interface Cursor<I> {
    /**
     * Checks whether the cursor is positioned at an
     * element in the collection
     *
     * @return true if the cursor is positioned at an element,
     *          false otherwise
     */
    boolean itemExists();

    /**
     * Returns the element in the container at which the cursor is positioned
     */
    I item();

    /**
     * Move the cursor to the first element.
     */
    void goFirst();

    /**
     * Move the cursor to the next element.
     */
    void goForth();

    /**
     * Move the cursor to the last element.
     */
    void goLast();

    /**
     * Move the cursor to the position before the first element.
     */
    void goBefore();

    /**
     * Move the cursor to the positoin after the last element.
     */
    void goAfter();

    /**
     * Test whether the cursor is positioned before the first element.
     *
     * @return true if positioned before first element
     *          false otherwise
     */
    boolean before();

    /**
     * Test whether the cursor is positioned after the last element.
     *
     * @return true if positioned after the last element
     *          false otherwise
     */
    boolean after();

}

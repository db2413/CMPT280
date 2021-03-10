

public interface SimpleTree280<I> extends Container280   
{
	/** 
	 * Contents of the root item. 				
	 * @precond !isEmpty()  
     */
	public I rootItem() throws ContainerEmpty280Exception;
 
	/**
     * Right subtree of the root.
	 * @precond !isEmpty() 
	 */
		public SimpleTree280<I> rootRightSubtree() 
				throws ContainerEmpty280Exception;

	/**
	 * Left subtree of the root. 
     * @precond !isEmpty() 
	 */
	public SimpleTree280<I> rootLeftSubtree() 
				throws ContainerEmpty280Exception;
}

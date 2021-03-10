/* ContainerEmpty280Exception.java
 * ---------------------------------------------
 * Copyright (c) 2004,2010 University of Saskatchewan
 * All Rights Reserved
 * --------------------------------------------- */

/**	This exception is used whenever there is an attempt to do
	an operation which requires an item in the container, but
	the container is empty. */
public class ContainerEmpty280Exception extends RuntimeException
{
	/**	Create an exception with the specified message. <br> 
		Analysis: Time = O(1) */
	public ContainerEmpty280Exception(String message)
	{
		super(message);
	}

	/**	Create an exception with the default message. <br> 
		Analysis: Time = O(1) */
	public ContainerEmpty280Exception()
	{
		super("ContainerEmpty280Exception thrown!");
	}

	/**	A unique class identifier for serializing and deserializing. */
	static final long serialVersionUID = 1l;
} 

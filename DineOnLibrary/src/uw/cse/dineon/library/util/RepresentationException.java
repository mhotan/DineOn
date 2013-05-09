package uw.cse.dineon.library.util;

/**
 * Helper Exception for checking representation.
 * @author mhotan
 */
public class RepresentationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5271796675140963419L;

	/**
	 * Creates representation exception with message
	 * @param message
	 */
	public RepresentationException(String message){
		super("Representation Failure occured " + message);
	}
	
	/**
	 * Creates default representation exception.
	 * Doesn't provide any details
	 */
	public RepresentationException(){
		this("");
	}
	
}

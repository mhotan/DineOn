package uw.cse.dineon.library;

/**
 * 
 * @author zachr81
 *
 */
public class MenuItem extends Storable {

	private int productID;
	private double price;
	private String description;
	
	/**
	 * 
	 * @param productID
	 * @param price
	 * @param description
	 */
	public MenuItem(int productID, double price, String description) {
		super();
		this.productID = productID;
		this.price = price;
		this.description = description;
	}
	
	/**
	 * @return the productID
	 */
	public int getProductID() {
		return productID;
	}
	
	/**
	 * @param productID the productID to set
	 */
	public void setProductID(int productID) {
		this.productID = productID;
	}

	/**
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}

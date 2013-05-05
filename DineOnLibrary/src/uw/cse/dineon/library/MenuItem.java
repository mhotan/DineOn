package uw.cse.dineon.library;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseObject;

/**
 * MenuItem object representing an item on a restaurant's menu.
 * 
 * @author zachr81
 *
 */
public class MenuItem extends Storable implements Parcelable {

	// ID's used for easier parsing
	public static final String PRODUCT_ID = "productID";
	public static final String PRICE = "price";
	public static final String DESCRIPTION = "description";
	
	private int productID;		// ID of this product
	private double price;		// price of this product
	private String description;	// description of this product
	// TODO Image id;
	
	/**
	 * Creates a new MenuItem with the given parameters.
	 * 
	 * @param productID is an int ID representing the product
	 * @param price double value of this item
	 * @param description of this item in String form
	 */
	public MenuItem(int productID, double price, String description) {
		super();
		this.productID = productID;
		this.price = price;
		this.description = description;
	}
	
	/**
	 * Creates a new MenuItem in from the given Parcel.
	 * 
	 * @param source Parcel of information in:
	 * 		int, double, String
	 * 		order.
	 */
	public MenuItem(Parcel source) {
		readFromParcel(source);
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

	/**
	 * Packs this MenuItem into a ParseObject to be stored.
	 * 
	 * @return ParseObject containing saved/packed data
	 */
	@SuppressWarnings("static-access")
	@Override
	public ParseObject packObject() {
		ParseObject pobj = new ParseObject(this.getClass().getSimpleName());
		pobj.add(this.PRODUCT_ID, this.productID);
		pobj.add(this.PRICE, this.price);
		pobj.add(this.DESCRIPTION, this.description);
		//in case this storable is going to be used after the pack.
		this.setObjId(pobj.getObjectId());
		
		return pobj;
	}

	/**
	 * Unpacks the given ParseObject into this MenuItem setting
	 * field values to the given data.
	 * 
	 * @param pobj ParseObject to be unpacked into a MenuItem
	 */
	@SuppressWarnings("static-access")
	@Override
	public void unpackObject(ParseObject pobj) {
		this.setObjId(pobj.getObjectId());
		this.setProductID(pobj.getInt(this.PRODUCT_ID));
		this.setPrice(pobj.getDouble(this.PRICE));
		this.setDescription(pobj.getString(this.DESCRIPTION));
	}

	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * Writes this MenuItem to Parcel dest in the order:
	 * int, double, String to be retrieved at a later time.
	 * 
	 * @param dest Parcel to write MenuItem data to.
	 * @param flags int
	 */
	// NOTE: if you change the write order you must change the read order
	// below.
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(productID);
		dest.writeDouble(price);
		dest.writeString(description);
	}
	
	/**
	 * Helper method for updating MenuItem with the data from a Parcel.
	 * @param source Parcel containing data in the order: int, double, string
	 */
	private void readFromParcel(Parcel source) {
		productID = source.readInt();
		price = source.readDouble();
		description = source.readString();
	}
	
	/**
	 * Parcelable creator object of a MenuItem.
	 * Can create a MenuItem from a Parcel.
	 */
	public static final Parcelable.Creator<MenuItem> CREATOR = 
			new Parcelable.Creator<MenuItem>() {

				@Override
				public MenuItem createFromParcel(Parcel source) {
					return new MenuItem(source);
				}

				@Override
				public MenuItem[] newArray(int size) {
					return new MenuItem[size];
				}
			};
}

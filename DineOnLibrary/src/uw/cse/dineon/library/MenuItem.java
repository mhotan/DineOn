package uw.cse.dineon.library;

import com.parse.ParseObject;

/**
 * MenuItem object representing an item on a restaurant's menu.
 * 
 * @author zachr81, mhotan
 */
public class MenuItem extends Storable {

	// ID's used for easier parsing
	private static final String PRODUCT_ID = "productID";
	private static final String PRICE = "price";
	private static final String DESCRIPTION = "description";
	private static final String TITLE = "title";
	
	// We dont want 
	private final int mProductID;		// ID of this product
	private final double mPrice;
	private final String mTitle; // price of this product
	private final String mDescription;	// description of this product
	// TODO Image id;
	
	/**
	 * Creates a new MenuItem with the given parameters.
	 * 
	 * @param productID is an int ID representing the product
	 * @param price double value of this item
	 * @param description of this item in String form
	 */
	public MenuItem(int productID, double price, String title, String description) {
		super(MenuItem.class);
		this.mProductID = productID;
		this.mPrice = price;
		this.mDescription = description;
		this.mTitle = title;
	}
	
	/**
	 * Creates a new MenuItem in from the given Parcel.
	 * 
	 * @param po Parcel of information in:
	 * 		int, double, String
	 * 		order.
	 */
	public MenuItem(ParseObject po) {
		super(po);
		mProductID = po.getInt(PRODUCT_ID);
		mPrice = po.getDouble(PRICE);
		mDescription = po.getString(DESCRIPTION);
		mTitle = po.getString(TITLE);
	}

	/**
	 * @return the productID
	 */
	public int getProductID() {
		return mProductID;
	}
	
//	/**
//	 * @param productID the productID to set
//	 */
//	public void setProductID(int productID) {
//		this.mProductID = productID;
//	}

	/**
	 * @return the price
	 */
	public double getPrice() {
		return mPrice;
	}

//	/**
//	 * @param price the price to set
//	 */
//	public void setPrice(double price) {
//		this.mPrice = price;
//	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return mDescription;
	}

//	/**
//	 * @param description the description to set
//	 */
//	public void setDescription(String description) {
//		this.mDescription = description;
//	}

	/**
	 * Packs this MenuItem into a ParseObject to be stored.
	 * 
	 * @return ParseObject containing saved/packed data
	 */
	@Override
	public ParseObject packObject() {
		ParseObject pobj = super.packObject();
		pobj.put(PRODUCT_ID, mProductID);
		pobj.put(PRICE, mPrice);
		pobj.put(DESCRIPTION, mDescription);
		pobj.put(TITLE, mTitle);
		return pobj;
	}

//	/**
//	 * Unpacks the given ParseObject into this MenuItem setting
//	 * field values to the given data.
//	 * 
//	 * @param pobj ParseObject to be unpacked into a MenuItem
//	 */
//	@SuppressWarnings("static-access")
//	@Override
//	public void unpackObject(ParseObject pobj) {
//		this.setObjId(pobj.getObjectId());
//		this.setProductID(pobj.getInt(this.PRODUCT_ID));
//		this.setPrice(pobj.getDouble(this.PRICE));
//		this.setDescription(pobj.getString(this.DESCRIPTION));
//	}
//
//	@Override
//	public int describeContents() {
//		return 0;
//	}

//	/**
//	 * Writes this MenuItem to Parcel dest in the order:
//	 * int, double, String to be retrieved at a later time.
//	 * 
//	 * @param dest Parcel to write MenuItem data to.
//	 * @param flags int
//	 */
//	// NOTE: if you change the write order you must change the read order
//	// below.
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//		dest.writeInt(productID);
//		dest.writeDouble(price);
//		dest.writeString(description);
//	}
//	
//	/**
//	 * Helper method for updating MenuItem with the data from a Parcel.
//	 * @param source Parcel containing data in the order: int, double, string
//	 */
//	private void readFromParcel(Parcel source) {
//		productID = source.readInt();
//		price = source.readDouble();
//		description = source.readString();
//	}
//	
//	/**
//	 * Parcelable creator object of a MenuItem.
//	 * Can create a MenuItem from a Parcel.
//	 */
//	public static final Parcelable.Creator<MenuItem> CREATOR = 
//			new Parcelable.Creator<MenuItem>() {
//
//				@Override
//				public MenuItem createFromParcel(Parcel source) {
//					return new MenuItem(source);
//				}
//
//				@Override
//				public MenuItem[] newArray(int size) {
//					return new MenuItem[size];
//				}
//			};
}

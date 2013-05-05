
package uw.cse.dineon.library;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseObject;

/**
 * Class for storing information on Restaurants.
 * @author Espeo196
 *
 */
public class RestaurantInfo extends Storable implements Parcelable {
	public static final String NAME = "name";
	public static final String ADDR = "addr";
	public static final String PHONE = "phone";
	public static final String IMAGE_MAIN = "imageMain";
	public static final String IMAGE_LIST = "imageList";
	public static final String MENU = "menu";
	
	private String name;
	private String addr;
	private int phone;
	private int imageMain;
	private List<Integer> imageList;
	private Menu menu;

	/**
	 * Default constructor.
	 */
	public RestaurantInfo() {
		name = "";
		addr = "";
		phone = 0; // TODO inconsistent w/ UserInfo phone (String)
	}
	
	/**
	 * @param name String of Restaurant
	 * @param addr String address of Restaurant
	 * @param phone int phone number of Restaurant
	 * @param imageMain int image ID
	 * @param imageList List of images
	 * @param menu Restaurant's default menu
	 */
	public RestaurantInfo(String name, String addr, int phone, int imageMain, 
			List<Integer> imageList, Menu menu) {
		this.name = name;
		this.addr = addr;
		this.phone = phone;
		this.imageMain = imageMain;
		this.imageList = imageList;
		this.menu = menu;
	}
	
	/**
	 * Creates a RestaurantInfo object from the given Parcel.
	 * 
	 * @param source Parcel of information in:
	 * 		String (name), String (addr), Int (phone), Int (imageMain),
	 * 		imageList (ints), Menu, String (objID)
	 */
	public RestaurantInfo(Parcel source) {
		readFromParcel(source);
	}

	/**
	 * @return String Restaurant name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param n String name
	 */
	public void setName(String n) {
		this.name = n;
	}

	/**
	 * @return addr String Restaurant address
	 */
	public String getAddr() {
		return addr;
	}

	/**
	 * @param a String
	 */
	public void setAddr(String a) {
		this.addr = a;
	}

	/**
	 * @return phone number of Restaurant
	 */
	public int getPhone() {
		return phone;
	}

	/**
	 * @param number int phone number
	 */
	public void setPhone(int number) {
		this.phone = number;
	}

	/**
	 * @return imageMain
	 */
	public int getImageMain() {
		return imageMain;
	}

	/**
	 * @param imageM int
	 */
	public void setImageMain(int imageM) {
		this.imageMain = imageM;
	}

	/**
	 * @return list of integers
	 */
	public List<Integer> getImageList() {
		return imageList;
	}

	/**
	 * @param images list of Integers
	 */
	public void setImageList(List<Integer> images) {
		this.imageList = images;
	}

	/**
	 * @return Menu of Restaurant
	 */
	public Menu getMenu() {
		return menu;
	}

	/**
	 * @param m Menu
	 */
	public void setMenu(Menu m) {
		this.menu = m;
	}



	@Override
	public ParseObject packObject() {
		ParseObject pobj = new ParseObject(this.getClass().getSimpleName());
		pobj.add(RestaurantInfo.NAME, this.name);
		pobj.add(RestaurantInfo.ADDR, this.addr);
		pobj.add(RestaurantInfo.PHONE, this.phone);
		pobj.add(RestaurantInfo.IMAGE_MAIN, this.imageMain);
		pobj.add(RestaurantInfo.IMAGE_LIST, this.imageList);
		pobj.add(RestaurantInfo.MENU, this.menu.packObject());
		//in case this storable is going to be used after the pack.
		this.setObjId(pobj.getObjectId());
				
		return pobj;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void unpackObject(ParseObject pobj) {
		this.setObjId(pobj.getObjectId());
		this.setName(pobj.getString(RestaurantInfo.NAME));
		this.setAddr(pobj.getString(RestaurantInfo.ADDR));
		this.setPhone(pobj.getInt(RestaurantInfo.PHONE));
		this.setImageMain(pobj.getInt(RestaurantInfo.IMAGE_MAIN));
		this.setImageList((List<Integer>) pobj.get(RestaurantInfo.IMAGE_LIST));

		Menu menu = new Menu("", null);
		menu.unpackObject((ParseObject) pobj.get(RestaurantInfo.MENU));
		this.setMenu(menu);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(addr);
		dest.writeInt(phone);
		dest.writeInt(imageMain);
		dest.writeList(imageList);
		dest.writeParcelable(menu, flags);
		dest.writeString(this.getObjId());
		
	}
	
	/**
	 * Parcelable creator object of a RestaurantInfo.
	 * Can create a RestaurantInfo from a Parcel.
	 */
	public static final Parcelable.Creator<RestaurantInfo> CREATOR = 
			new Parcelable.Creator<RestaurantInfo>() {

				@Override
				public RestaurantInfo createFromParcel(Parcel source) {
					return new RestaurantInfo(source);
				}

				@Override
				public RestaurantInfo[] newArray(int size) {
					return new RestaurantInfo[size];
				}
	};
			
	/**
	 * Read an object back out of parcel.
	 * @param source parcel to read from.
	 */
	private void readFromParcel(Parcel source) {
		this.setName(source.readString());
		this.setAddr(source.readString());
		this.setPhone(source.readInt());
		this.setImageMain(source.readInt());
		source.readList(imageList, Integer.class.getClassLoader());
		this.setMenu((Menu)source.readParcelable(Menu.class.getClassLoader()));
		this.setObjId(source.readString());
	}
}

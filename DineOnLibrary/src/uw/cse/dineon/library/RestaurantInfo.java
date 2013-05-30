package uw.cse.dineon.library;

import java.util.ArrayList;
import java.util.List;

import uw.cse.dineon.library.image.DineOnImage;
import uw.cse.dineon.library.util.ParseUtil;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Class for storing information on Restaurants.
 * @author Espeo196, mhotan
 *
 */
public class RestaurantInfo extends LocatableStorable {

	private static final String TAG = RestaurantInfo.class.getSimpleName();

	public static final String PARSEUSER = "parseUser";
	public static final String NAME = "restaurantName";
	public static final String ADDR = "restaurantAddr";
	public static final String PHONE = "restaurantPhone";
	public static final String IMAGE_MAIN = "restaurantImageMain";
	public static final String IMAGE_LIST = "restaurantImageList";
	public static final String MENUS = "restaurantMenu";
	public static final String HOURS = "restaurantHours";

	private static final String UNDETERMINED = "Undetermined";

	private final ParseUser mUser;
	// Lets set the name of the restaurant once and only once.
	private final String mName;
	private String mAddress;
	private String mHours;
	private String mPhone;
	private int mMainImageIndex; // Index of Main image
	private final List<DineOnImage> mImageList; // Mapping of Parse Object IDs
	private final List<Menu> mMenus; // All menus
	//private String mRestaurantHours;

	/**
	 * Creates a bare restaurant info using the inputted name.
	 * @param name name of the restaurant
	 * @throws ParseException 
	 */
	public RestaurantInfo(ParseUser name) throws ParseException {
		super(RestaurantInfo.class);
		name.fetchIfNeeded();
		mUser = name;
		mName = mUser.getUsername();
		mAddress = UNDETERMINED;
		mPhone = UNDETERMINED;
		mHours = UNDETERMINED;
		mMainImageIndex = 0;
		mImageList = new ArrayList<DineOnImage>();
		mMenus = new ArrayList<Menu>();
	}

	/**
	 * Creates a RestaurantInfo object from the given ParseObject.
	 * 
	 * @param po Parse object to build from
	 * @throws ParseException 
	 */
	public RestaurantInfo(ParseObject po) throws ParseException {
		super(po);
		mUser = po.getParseUser(PARSEUSER).fetchIfNeeded();
		mName = mUser.getUsername();
		mAddress = po.getString(ADDR);
		mPhone = po.getString(PHONE);
		mHours = po.getString(HOURS);
		mMainImageIndex = po.getInt(IMAGE_MAIN);
		mImageList = ParseUtil.toListOfStorables(DineOnImage.class, po.getList(IMAGE_LIST));
		mMenus = ParseUtil.toListOfStorables(Menu.class, po.getList(MENUS));
	}



	@Override
	public ParseObject packObject() {
		ParseObject po = super.packObject();
		po.put(PARSEUSER, mUser);
		po.put(NAME, mName);
		po.put(ADDR, mAddress);
		po.put(PHONE, mPhone);
		po.put(IMAGE_MAIN, mMainImageIndex);
		po.put(IMAGE_LIST, ParseUtil.toListOfParseObjects(mImageList));
		po.put(MENUS, ParseUtil.toListOfParseObjects(mMenus));	
		po.put(HOURS, mHours);
		return po;
	}

	/**
	 * @return String Restaurant name
	 */
	public String getName() {
		return mName;
	}

	/**
	 * @return addr String Restaurant address
	 */
	public String getAddr() {
		return mAddress;
	}

	/**
	 * @param a String
	 */
	public void setAddr(String a) {
		this.mAddress = a;
	}

	/**
	 * @return hours of operation for restaurant
	 */
	public String getHours() {
		return mHours;
	}

	/**
	 * @param hours of operation to set
	 */
	public void setHours(String hours) {
		this.mHours = hours;
	}
	
	/**
	 * @return phone number of Restaurant
	 */
	public String getPhone() {
		return mPhone;
	}

	/**
	 * Must Adhere to some form of a number.
	 * (123)456-7890
	 * number cannot be null.
	 * @param number String representation of a phone number
	 */
	public void setPhone(String number) {
		this.mPhone = number;
	}

	/**
	 * Returns the main image if it exists.
	 * @return the main image if it exists, null if no image is set to default
	 */
	public DineOnImage getMainImage() {
		if (mImageList.isEmpty()) {
			return null;
		} else if (mMainImageIndex < 0 || mMainImageIndex >= mImageList.size()) {
			return mImageList.get(0);
		}
		return mImageList.get(mMainImageIndex);
	}

	/**
	 * @param pos int
	 */
	public void setMainImage(int pos) {
		// This can be -1;
		pos = Math.min(Math.max(0, pos), mImageList.size() - 1);
		this.mMainImageIndex = pos;
	}

	/**
	 * Returns a list of all the general images of this restaurant.
	 * @return List of images.
	 */
	public List<DineOnImage> getImageList() {
		return new ArrayList<DineOnImage>(mImageList);
	}
	
	/**
	 * Adds an image to the end of the group of images.
	 * @param image Image to add to the restaurant.
	 */
	void addImage(DineOnImage image) {
		if (image == null) {
			return;
		}
		mImageList.add(image);
	}

	/**
	 * For a zero based index it removes the image at index.
	 * @param index Index to remove
	 * @return True upon success, false on failure
	 */
	public boolean removeImageAt(int index) {
		if (index < 0 || index >= mImageList.size()) {
			return false;
		}
		mImageList.remove(index);
		return true;
	}
	
	/**
	 * Retrieves menu with associated name menuName.
	 * 
	 * @param menuName MenuName to search for
	 * @return Menu with argument name, null otherwise
	 */
	private Menu getMenu(String menuName) {
		if (menuName == null) {
			return null;
		}

		for (Menu m : mMenus) {
			if (m.getName().equals(menuName)) {
				return m;
			}
		}
		// Not found
		return null;
	}
	
	/**
	 * @return The restaurant's list of menus
	 */
	public List<Menu> getMenuList() {
		return this.mMenus;
	}

	/**
	 * Checks if this restaurant has the associated menu.
	 * If there already exists a menu with the same name then 
	 * true is returned.
	 * @param menu Menu to check existence
	 * @return true if restaurant has Menu already.
	 */
	public boolean hasMenu(Menu menu) {
		if (menu == null) {
			return false;
		}

		return getMenu(menu.getName()) != null;
	}

	/**
	 * If the menu with the same name does not exist already this menu
	 * will be added.
	 * @param newMenu Menu to add to the restaurant.
	 * @return true if the menu did not exists already, 
	 * false if the menu was not added because it already exists   
	 */
	public boolean addMenu(Menu newMenu) {
		if (!hasMenu(newMenu)) {
			return mMenus.add(newMenu);
		}
		return false;
	}

	/**
	 * Adds a new Item to associated Menu.
	 * @param menu Menu to add to.
	 * @param item Item to add to the menu
	 * @return false if Menu does not exist in the restaurant 
	 */
	public boolean addItemToMenu(Menu menu, MenuItem item) {
		if (!hasMenu(menu)) {
			return false;
		}

		for (Menu m : mMenus) {
			if (m.getName().equals(menu.getName())) {
				menu.addNewItem(item);
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes a menu from the restaurant.
	 * @param menu menu to remove
	 * @return true if menu was removed false other wise.
	 */
	public boolean removeMenu(Menu menu) {
		if (!hasMenu(menu)) {
			return false;
		}
		return mMenus.remove(getMenu(menu.getName()));
	}

	/**
	 * Creates Restaurant Info from a Parcel.
	 * @param source Source to use to build Restaurant Info.
	 */
	public RestaurantInfo(Parcel source) {
		super(source);
		mUser = new ParseUser();
		mUser.setObjectId(source.readString());
		mUser.fetchInBackground(new GetCallback() {
			
			@Override
			public void done(ParseObject o, ParseException e) {
				if (e != null) {
					Log.e(TAG, "Unable to fetch user");
				}
			}
		});
		mName = source.readString();
		mAddress = source.readString();
		mPhone = source.readString();
		mMainImageIndex = source.readInt();
		mImageList = new ArrayList<DineOnImage>();
		source.readList(mImageList, String.class.getClassLoader());
		mMenus = new ArrayList<Menu>();
		source.readTypedList(mMenus, Menu.CREATOR);
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(mUser.getObjectId());
		dest.writeString(mName);
		dest.writeString(mAddress);
		dest.writeString(mPhone);
		dest.writeInt(mMainImageIndex);
		dest.writeList(mImageList);
		dest.writeTypedList(mMenus);
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
}

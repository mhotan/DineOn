package uw.cse.dineon.library;

import java.util.ArrayList;
import java.util.List;

import uw.cse.dineon.library.util.ParseUtil;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Class for storing information on Restaurants.
 * @author Espeo196, mhotan
 *
 */
public class RestaurantInfo extends Storable {

	private static final String TAG = RestaurantInfo.class.getSimpleName();

	public static final String PARSEUSER = "parseUser";
	public static final String NAME = "restaurantName";
	public static final String ADDR = "restaurantAddr";
	public static final String PHONE = "restaurantPhone";
	public static final String IMAGE_MAIN = "restaurantImageMain";
	public static final String IMAGE_LIST = "restaurantImageList";
	public static final String MENUS = "restaurantMenu";

	private static final String UNDETERMINED = "Undetermined";

	private final ParseUser mUser;
	// Lets set the name of the restaurant once and only once.
	private final String mName;
	private String mAddress;
	private String mPhone;
	private int mMainImageIndex; // Index of Main image
	private final List<String> mImageList; // Mapping of Parse Object IDs
	private final List<Menu> mMenus; // All menus

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
		mMainImageIndex = 0;
		mImageList = new ArrayList<String>();
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
		mName = po.getString(NAME);
		mAddress = po.getString(ADDR);
		mPhone = po.getString(PHONE);
		mMainImageIndex = po.getInt(IMAGE_MAIN);
		mImageList = po.getList(IMAGE_LIST);
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
		po.put(IMAGE_LIST, mImageList);
		po.put(MENUS, ParseUtil.toListOfParseObjects(mMenus));	
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
	 * @return imageMain
	 */
	public int getImageMain() {
		return mMainImageIndex;
	}

	/**
	 * @param pos int
	 */
	public void setImageMain(int pos) {
		pos = Math.min(Math.max(0, pos), mImageList.size() - 1);
		if (pos == -1) {
			//TODO Handle no images
		}
		this.mMainImageIndex = pos;
	}

	/**
	 * @return list of integers
	 * TODO make copy
	 */
	public List<String> getImageList() {
		return mImageList;
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
	public List<Menu> getMenuList(){
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
		mImageList = new ArrayList<String>();
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


package uw.cse.dineon.library;

import java.util.ArrayList;
import java.util.List;

import uw.cse.dineon.library.util.ParseUtil;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Class for storing information on Restaurants.
 * @author Espeo196, mhotan
 *
 */
public class RestaurantInfo extends Storable {
	
	private static final String TAG = RestaurantInfo.class.getSimpleName();
	
	public static final String PARSEUSER = "parseUser";
	public static final String ADDR = "restaurantAddr";
	public static final String PHONE = "restaurantPhone";
	public static final String IMAGE_MAIN = "restaurantImageMain";
	public static final String IMAGE_LIST = "restaurantImageList";
	public static final String MENUS = "restaurantMenu";
	
	private static final String UNDETERMINED = "Undetermined";
	
	
	private final ParseUser mUser;
	private String mAddress;
	private String mPhone;
	private int mMainImageIndex; // Index of Main image
	private List<String> mImageList; // Mapping of Parse Object IDs
	private List<Menu> mMenus; // All menus

	/**
	 * Creates a bare restaurant info using the inputted name.
	 * @param name name of the restaurant
	 * @throws ParseException 
	 */
	public RestaurantInfo(ParseUser name) throws ParseException {
		super(RestaurantInfo.class);
		name.fetchIfNeeded();
		mUser = name;
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
		return mUser.getUsername();
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
	 * @param number int phone number
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

//	/**
//	 * @param images list of Integers
//	 */
//	public void setImageList(List<Integer> images) {
//		this.mImageList = images;
//	}

//	/**
//	 * @return Menu of Restaurant
//	 */
//	public Menu getMenu() {
//		return ;
//	}
//
//	/**
//	 * @param m Menu
//	 */
//	public void setMenu(Menu m) {
//		this.menu = m;
//	}




//
//	@SuppressWarnings("unchecked")
//	@Override
//	public void unpackObject(ParseObject pobj) {
//		this.setObjId(pobj.getObjectId());
//		this.setName(pobj.getString(RestaurantInfo.NAME));
//		this.setAddr(pobj.getString(RestaurantInfo.ADDR));
//		this.setPhone(pobj.getInt(RestaurantInfo.PHONE));
//		this.setImageMain(pobj.getInt(RestaurantInfo.IMAGE_MAIN));
//		this.setImageList((List<Integer>) pobj.get(RestaurantInfo.IMAGE_LIST));
//
//		Menu menu = new Menu("", null);
//		menu.unpackObject((ParseObject) pobj.get(RestaurantInfo.MENU));
//		this.setMenu(menu);
//	}
//
//	@Override
//	public int describeContents() {
//		return 0;
//	}
//
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//		dest.writeString(mName);
//		dest.writeString(mAddress);
//		dest.writeInt(mPhone);
//		dest.writeInt(mMainImageIndex);
//		dest.writeList(mImageList);
//		dest.writeParcelable(menu, flags);
//		dest.writeString(this.getObjId());
//		
//	}
//	
//	/**
//	 * Parcelable creator object of a RestaurantInfo.
//	 * Can create a RestaurantInfo from a Parcel.
//	 */
//	public static final Parcelable.Creator<RestaurantInfo> CREATOR = 
//			new Parcelable.Creator<RestaurantInfo>() {
//
//				@Override
//				public RestaurantInfo createFromParcel(Parcel source) {
//					return new RestaurantInfo(source);
//				}
//
//				@Override
//				public RestaurantInfo[] newArray(int size) {
//					return new RestaurantInfo[size];
//				}
//	};
//			
//	/**
//	 * Read an object back out of parcel.
//	 * @param source parcel to read from.
//	 */
//	private void readFromParcel(Parcel source) {
//		this.setName(source.readString());
//		this.setAddr(source.readString());
//		this.setPhone(source.readInt());
//		this.setImageMain(source.readInt());
//		source.readList(mImageList, Integer.class.getClassLoader());
//		this.setMenu((Menu)source.readParcelable(Menu.class.getClassLoader()));
//		this.setObjId(source.readString());
//	}
}

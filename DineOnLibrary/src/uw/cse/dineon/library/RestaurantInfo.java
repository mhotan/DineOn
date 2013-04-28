
package uw.cse.dineon.library;

import java.util.List;
import android.os.Bundle;
import com.parse.ParseObject;

/**
 * @author Espeo196
 *
 */
public class RestaurantInfo extends Storable {
	private String name;
	private String addr;
	private int phone;
	private int imageMain;
	private List<Integer> imageList;
	private Menu menu;

	/**
	 *
	 */
	public RestaurantInfo() {
		// TODO
	}

	/**
	 *
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 *
	 * @param n String name
	 */
	public void setName(String n) {
		this.name = n;
	}

	/**
	 *
	 * @return addr
	 */
	public String getAddr() {
		return addr;
	}

	/**
	 *
	 * @param a String
	 */
	public void setAddr(String a) {
		this.addr = a;
	}

	/**
	 *
	 * @return phone
	 */
	public int getPhone() {
		return phone;
	}

	/**
	 *
	 * @param number int phone number
	 */
	public void setPhone(int number) {
		this.phone = number;
	}

	/**
	 *
	 * @return imageMain
	 */
	public int getImageMain() {
		return imageMain;
	}

	/**
	 *
	 * @param imageM int
	 */
	public void setImageMain(int imageM) {
		this.imageMain = imageM;
	}

	/**
	 *
	 * @return list of integers
	 */
	public List<Integer> getImageList() {
		return imageList;
	}

	/**
	 *
	 * @param images list of Integers
	 */
	public void setImageList(List<Integer> images) {
		this.imageList = images;
	}

	/**
	 *
	 * @return Menu
	 */
	public Menu getMenu() {
		return menu;
	}

	/**
	 *
	 * @param m Menu
	 */
	public void setMenu(Menu m) {
		this.menu = m;
	}

	@Override
	public Bundle bundle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unbundle(Bundle b) {
		// TODO Auto-generated method stub
	}

	@Override
	public ParseObject packObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unpackObject(ParseObject pobj) {
		// TODO Auto-generated method stub
	}
}

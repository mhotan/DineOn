package uw.cse.dineon.library;

import java.util.List;

/**
 * 
 * @author zachr81
 *
 */
public class Order extends Storable {

	private int tableID;
	private int userID;
	private int restID;
	private int timestamp;
	private List<MenuItem> menuItems;
	
	/**
	 * 
	 * @param tableID
	 * @param userID
	 * @param restID
	 * @param timestamp
	 * @param menuItems
	 */
	public Order(int tableID, int userID, int restID, int timestamp, List<MenuItem> menuItems) {
		super();
		this.tableID = tableID;
		this.userID = userID;
		this.restID = restID;
		this.timestamp = timestamp;
		this.menuItems = menuItems;
	}
	
	/**
	 * @return the tableID
	 */
	public int getTableID() {
		return tableID;
	}
	
	/**
	 * @param tableID the tableID to set
	 */
	public void setTableID(int tableID) {
		this.tableID = tableID;
	}
	
	/**
	 * @return the userID
	 */
	public int getUserID() {
		return userID;
	}
	
	/**
	 * @param userID the userID to set
	 */
	public void setUserID(int userID) {
		this.userID = userID;
	}

	/**
	 * @return the restID
	 */
	public int getRestID() {
		return restID;
	}

	/**
	 * @param restID the restID to set
	 */
	public void setRestID(int restID) {
		this.restID = restID;
	}

	/**
	 * @return the timestamp
	 */
	public int getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the menuItems
	 */
	public List<MenuItem> getMenuItems() {
		return menuItems;
	}

	/**
	 * @param menuItems the menuItems to set
	 */
	public void setMenuItems(List<MenuItem> menuItems) {
		this.menuItems = menuItems;
	}
}

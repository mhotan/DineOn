package uw.cse.dineon.library;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import android.os.Bundle;

import com.parse.ParseObject;

/**
 * 
 * @author zachr81
 *
 */
public class DiningSession extends Storable {
	
	private List<User> users;
	private long startTime;
	private long endTime;
	private boolean isCheckedIn;
	private List<Order> orders;
	private int sessToken;
	private int tableID;
	RequestType waiterRequest;
	
	/**
	 * 
	 * @param orders
	 * @param sessToken
	 * @param tableID
	 */
	public DiningSession(List<Order> orders, int sessToken, int tableID) {
		super();
		this.sessToken = sessToken;
		this.orders = orders;
		this.tableID = tableID;
	}
	
	/**
	 * @return the orders
	 */
	public List<Order> getOrders() {
		List<Order> copy = new ArrayList<Order>(orders.size());
		Collections.copy(copy, orders);
		return copy;
	}
	/**
	 * @param orders the orders to set
	 */
	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}
	/**
	 * @return the sessToken
	 */
	public int getSessToken() {
		return sessToken;
	}
	/**
	 * @param sessToken the sessToken to set
	 */
	public void setSessToken(int sessToken) {
		this.sessToken = sessToken;
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
	 * 
	 * @param order to add
	 */
	public void addOrder(Order order) {
		orders.add(order);
	}
	
	/**
	 * 
	 * @param order to remove
	 */
	public void removeOrder(Order order) {
		orders.remove(order);
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
	
	/**
	 * @return the users
	 * 
	 */
	public List<User> getUsers() {
		List<User> copy = new ArrayList<User>(users.size());
		Collections.copy(copy, users);
		return copy;
	}

	/**
	 * @param users the users to set
	 */
	public void setUsers(List<User> users) {
		this.users = users;
	}

	/**
	 * @return the startTime
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the endTime
	 */
	public long getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return the isCheckedIn
	 */
	public boolean isCheckedIn() {
		return isCheckedIn;
	}

	/**
	 * @param isCheckedIn the isCheckedIn to set
	 */
	public void setCheckedIn(boolean isCheckedIn) {
		this.isCheckedIn = isCheckedIn;
	}
}

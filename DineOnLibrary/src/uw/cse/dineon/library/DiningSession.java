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
	
	public static final String USERS = "users";
	public static final String START_TIME = "startTime";
	public static final String END_TIME = "endTime";
	public static final String ORDERS = "orders";
	public static final String SESS_TOKEN = "sessToken";
	public static final String TABLE_ID = "tableID";
	public static final String WAITER_REQUEST = "waiterRequest";
	
	private List<UserInfo> users;
	private long startTime;
	private long endTime;
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
		this.users = new ArrayList<UserInfo>();
		this.sessToken = sessToken;
		this.startTime = System.currentTimeMillis();
		this.orders = orders;
		this.tableID = tableID;
		this.waiterRequest = RequestType.NONE;
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
		List<Order> lst = new ArrayList<Order>();
		lst.addAll(orders);
		this.orders = lst;
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
	public ParseObject packObject() {
		ParseObject pobj = new ParseObject(this.getClass().getSimpleName());
		pobj.add(this.USERS, this.users);
		pobj.add(this.START_TIME, new Long(this.startTime));
		pobj.add(this.END_TIME, new Long (this.endTime));
		pobj.add(this.ORDERS, this.orders);
		pobj.addUnique(this.SESS_TOKEN, new Integer(this.sessToken));
		pobj.add(this.TABLE_ID, new Integer(this.tableID));
		pobj.add(this.WAITER_REQUEST, this.waiterRequest);
		//in case this storable is going to be used after the pack.
		this.setObjId(pobj.getObjectId());
		
		return pobj;
	}

	@Override
	public void unpackObject(ParseObject pobj){
		this.setObjId(pobj.getObjectId());
		this.users.addAll((List<UserInfo>) pobj.get(this.USERS));
		this.setStartTime(pobj.getLong(START_TIME));
		this.setEndTime(pobj.getLong(this.END_TIME));
		this.setOrders((List<Order>) pobj.get(this.ORDERS));
		this.setSessToken(pobj.getInt(this.SESS_TOKEN));
		this.setTableID(pobj.getInt(this.TABLE_ID));
		this.setWaiterRequest((RequestType) pobj.get(this.WAITER_REQUEST));
	}
	
	public void setWaiterRequest(RequestType wreq){
		this.waiterRequest = wreq;
	}
	
	/**
	 * @return the users
	 * 
	 */
	public List<UserInfo> getUsers() {
		List<UserInfo> copy = new ArrayList<UserInfo>(this.users.size());
		Collections.copy(copy, this.users);
		return copy;
	}

	/**
	 * @param users the users to set
	 */
	public void addUser(UserInfo userInfo) {
		this.users.add(userInfo);
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

	@Override
	public Bundle bundle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unbundle(Bundle b) {
		// TODO Auto-generated method stub
		
	}
}

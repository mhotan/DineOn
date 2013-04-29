package uw.cse.dineon.library;

import java.util.List;

/**
 * 
 * @author zachr81
 *
 */
public class DiningSession extends Storable {

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
		return orders;
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
	
}

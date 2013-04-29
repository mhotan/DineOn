package uw.cse.dineon.library;

/**
 * 
 * @author zachr81
 *
 */
public class Restaurant extends Storable {

	private Menu menu;
	private List<Reservation> reservationList;
	private RestaurantInfo info;
	private List<Order> orders;
	private List<DiningSession> sessions;
	
	/**
	 * @param Menu
	 * @param reservationList
	 * @param info
	 * @param orders
	 * @Param sessions
	 */
	public Restaurant(Menu menu, List<Reservation> reservationList, RestaurantInfo info,
			List<Order> orders, List<DiningSession> sessions) {
		super();
		this.menu = menu;
		this.reservationList = reservationList;
		this.info = info;
		this.orders = orders;
		this.sessions = sessions;
	}
	
	/**
	 * @return Menu
	 */
	public Menu getMenu() {
		return menu;
	}
	
	/**
	 * @param menu: A Menu object to set as the new menu
	 * 
	 */
	public void setMenu(Menu menu) {
		this.menu = menu;
	}
	
	/**
	 * @return List<Reservation>
	 */
	public List<Reservation> getReservationList() {
		return reservationList;
	}
	
	/**
	 * @param reservationList: A List of Reservations to set as the new reservation list
	 * 
	 */
	public void setReservationList(List<ReservationList> newReservationList) {
		this.reservationList = newReservationList;
	}
	
	/**
	 * @return RestaurantInfo
	 */
	public RestaurantInfo getInfo() {
		return info;
	}
	
	/**
	 * Sets info to the param value
	 * @param RestaurantInfo
	 */
	public void setInfo(RestaurantInfo newInfo) {
		this.info = newInfo;
	}
	
	/**
	 * @return List<Order>
	 */
	public List<Order> getOrders() {
		return orders;
	}
	
	/**
	 * Sets orders to the parameter value
	 * @param List<Order>
	 */
	public void setOrders(List<Order> newOrders) {
		orders = newOrders;
	}
	
	/**
	 * @return List<DiningSession>
	 */
	public List<DiningSession> getSessions() {
		return sessions;
	}
	
	/**
	 * Sets sessions to the parameter value
	 * @param List<DiningSession>
	 */
	public void setSessions(List<DiningSessions> newSessions) {
		sessions = newSessions;
	}
	
	/**
	 * Adds the given reservation to the reservation list
	 * @param Reservation
	 */
	public void addReservation(Reservation newReservation) {
		reservationList.add(newReservation);
	}
	
	/**
	 * Remove the specified reservation
	 * @param Reservation
	 */
	public void removeReservation(Reservation removeReservation) {
		reservationList.remove(removeReservation);
	}
	
	/**
	 * Adds given order to orders
	 * @param Order
	 */
	public void addOrder(Order order) {
		orders.add(order);
	}
	
	/**
	 * Remove given order from orders
	 * @param Order
	 */
	public void removeOrder(Order order) {
		orders.remove(order);
	}
	
	/**
	 * Adds given DiningSession to sessions
	 * @param DiningSession
	 */
	public void addDiningSession(DiningSession session) {
		sessions.add(session);
	}
	
	/**
	 * Removes given DiningSession from sessions
	 * @param DiningSession
	 */
	public void removeDiningSession(DiningSession session) {
		sessions.remove(session);
	}
}

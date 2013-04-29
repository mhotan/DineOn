package uw.cse.dineon.library;

import java.util.List;


/**
 * 
 * @author zachr81
 *
 */
public class Menu extends Storable {

	private List<MenuItem> items;

	/**
	 * 
	 * @param items
	 */
	public Menu(List<MenuItem> items) {
		super();
		this.items = items;
		
	}
	
	/**
	 * @return the items
	 */
	public List<MenuItem> getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(List<MenuItem> items) {
		this.items = items;
	}
	
	/**
	 * 
	 * @param item
	 */
	public void addNewItem(MenuItem item) {
		items.add(item);
	}
	
	/**
	 * 
	 * @param item
	 */
	public void removeItem(MenuItem item) {
		items.remove(item);
	}
}

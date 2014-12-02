package dojo;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class Store {
	
	private Map<String, Integer> inventory = new HashMap<>();
	private Map<String, Float> prices = new HashMap<>();
	
	
	public SellReport sell(String id, int quantity) throws ItemNotFoundException, NoMoreItemException {

		int items = 0;
		for (int i=0; i<quantity; i++) {
			if (quantity >= 0) {
				if (inventory.get(id) != null ) {
					int total = inventory.get(id);
					if (total > 0) {
						items ++;
						total --;
						inventory.put(id, total);
					} else {
						throw new NoMoreItemException();
					}
				} else {
					throw new ItemNotFoundException();
				}
			} else {
				throw new NoMoreItemException();
			}
		}
		
		return new SellReport(Calendar.getInstance(), quantity, inventory.get(id), items, prices.get(id));
	}
	
	public void registerItemPrice(String itemId, Float price) {
		if (inventory.get(itemId) == null) {
			inventory.put(itemId, 0);
			prices.put(itemId, price);
		} 
		prices.put(itemId, price);
	}
	
	void add(String itemId, int nb) {
		Integer total = inventory.get(itemId);
		if (total == null) {
			total = 0;
		}
		inventory.put(itemId, total+nb);
	}
	
	public Integer getItems(String id) throws ItemNotFoundException {
		if (inventory.get(id) != null) {
			return inventory.get(id);
		}
		throw new ItemNotFoundException();
	}
	
	public String printInventory() {
		StringBuffer sb = new StringBuffer("Inventory :\n");
		for (String id : inventory.keySet()){
			sb.append(id).append("-").append(inventory.get(id)).append("\n");
		}
		return sb.toString();
	}

}

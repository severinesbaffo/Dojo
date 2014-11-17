package dojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Store {
	
	private Map<String, List<Item>> inventory = new HashMap<>();
	

	public List<Item> sell(String id, int quantity) throws ItemNotFoundException, NoMoreItemException {

		List<Item> items = new ArrayList<>();
		for (int i=0; i<quantity; i++) {
			if (quantity > 0) {
				if (inventory.get(id) != null && !inventory.get(id).isEmpty()) {
					items.add(inventory.get(id).remove(0));
				} else {
					throw new ItemNotFoundException();
				}
			} else {
				throw new NoMoreItemException();
			}
		}
		return items;
	}
	
	void add(Item item) {
		List<Item> itemList = inventory.get(item.getId());
		if (itemList == null) {
			itemList = new ArrayList<>();
			inventory.put(item.getId(), itemList);
		}
		itemList.add(item);
	}
	
	public List<Item> getItems(String id) throws ItemNotFoundException {
		if (inventory.get(id) != null) {
			return inventory.get(id);
		}
		throw new ItemNotFoundException();
	}

}

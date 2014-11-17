package dojo;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;


@SuppressWarnings("unused")
public class StoreTest {

	
	@Test
	public void testGetNotFoundItem() {

		Store store = new Store();
		Item item1 = new Item("id-2");
		Item item2 = new Item("id-2");
		store.add(item1);
		store.add(item2);

		checkGetItems(store, 0, true);
	}
	
	@Test
	public void testGetFindOneItem() {

		Store store = new Store();
		Item item1 = new Item("id-1");
		store.add(item1);
		

		checkGetItems(store, 1, false);
	}
	
	@Test
	public void testGetFindTwoItems() {

		Store store = new Store();
		Item item1 = new Item("id-1");
		Item item2 = new Item("id-1");
		store.add(item1);
		store.add(item2);
		
		checkGetItems(store, 2, false);
	}

	private void checkGetItems(Store store, int size, boolean isNotFound) {
		try {
			List<Item> items = store.getItems("id-1");
			if (!isNotFound) {
				assertNotNull(items);
				assertThat(items.size(), equalTo(size));
			}
		} catch (ItemNotFoundException e) {
			assertTrue(isNotFound);
		}
	}

	@Test
	public void testAdd() {

		Store store = new Store();
		Item item = new Item("id-1");
		store.add(item);
		
		try {
			assertThat(store.getItems("id-1").get(0), equalTo(item));
		} catch (ItemNotFoundException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void testSellOk() {
		Store store = new Store();
		Item item = new Item("id-1");
		store.add(item);
		
		try {
			List<Item> soldItems = store.sell("id-1", 1);
			assertNotNull(soldItems);
			assertThat(soldItems.size(), equalTo(1));
			assertThat(soldItems.get(0).getId(), equalTo("id-1"));
			assertThat(store.getItems("id-1").size(), equalTo(0));
		} catch (ItemNotFoundException e) {
			assertTrue(false);
		} catch (NoMoreItemException e) {
			assertTrue(false);
		}
	}
	

	@Test
	public void testSellTwoItems() {
		Store store = new Store();
		Item item1 = new Item("id-1");
		Item item2 = new Item("id-1");
		Item item3 = new Item("id-1");
		store.add(item1);
		store.add(item2);
		store.add(item3);
		
		try {
			List<Item> soldItems = store.sell("id-1", 2);
			assertNotNull(soldItems);
			assertThat(soldItems.size(), equalTo(2));
			assertThat(soldItems.get(0).getId(), equalTo("id-1"));
			assertThat(soldItems.get(1).getId(), equalTo("id-1"));
			assertThat(store.getItems("id-1").size(), equalTo(1));
		} catch (ItemNotFoundException e) {
			assertTrue(false);
		} catch (NoMoreItemException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void testSellNotFoundException() {
		Store store = new Store();
		
		try {
			store.sell(null, 1);
		} catch (ItemNotFoundException e) {
			assertTrue(true);
		} catch (NoMoreItemException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void testPrint() {
		Store store = new Store();
		Item item1 = new Item("id-1");
		Item item2 = new Item("id-1");
		Item item3 = new Item("id-1");
		Item item4 = new Item("id-2");
		Item item5 = new Item("id-2");
		Item item6 = new Item("id-3");
		store.add(item1);
		store.add(item2);
		store.add(item3);
		store.add(item4);
		store.add(item5);
		
		String result = store.printInventory();
		System.out.println(result);
		String[] lines = result.split("\n");
		for (String line : lines) {
			if (line.lastIndexOf("-") != -1 ) {
				String id = line.substring(0, line.lastIndexOf("-"));
				int quantity = Integer.valueOf(line.substring(line.lastIndexOf("-")+1, line.length()));
				if (id.equals("id-1")) {
					assertThat(quantity, equalTo(3));
				}
				if (id.equals("id-2")) {
					assertThat(quantity, equalTo(2));
				}
				if (id.equals("id-3")) {
					assertThat(quantity, equalTo(1));
				}
			}
		}
	}

}

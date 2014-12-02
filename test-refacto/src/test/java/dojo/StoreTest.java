package dojo;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.List;

import org.junit.Test;


@SuppressWarnings("unused")
public class StoreTest {

	
	@Test
	public void testGetNotFoundItem() {

		Store store = new Store();
		store.add("id-2", 2);

		checkGetItems(store, 0, true);
	}
	
	@Test
	public void testGetFindOneItem() {

		Store store = new Store();
		store.add("id-1", 1);

		checkGetItems(store, 1, false);
	}
	
	@Test
	public void testGetFindTwoItems() {

		Store store = new Store();
		store.add("id-1", 2);
		
		checkGetItems(store, 2, false);
	}

	private void checkGetItems(Store store, int size, boolean isNotFound) {
		try {
			int items = store.getItems("id-1");
			if (!isNotFound) {
				assertNotNull(items);
				assertThat(items, equalTo(size));
			}
		} catch (ItemNotFoundException e) {
			assertTrue(isNotFound);
		}
	}

	@Test
	public void testAdd() {

		Store store = new Store();
		Item item = new Item("id-1");
		store.add("id-1", 1);
		
		try {
			assertThat(store.getItems("id-1"), equalTo(1));
		} catch (ItemNotFoundException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void testSellOk() {
		Store store = new Store();
		store.add("id-1", 1);
		
		try {
			assertThat(store.getItems("id-1"), equalTo(1));
			SellReport sellReport = store.sell("id-1", 1);
			assertNotNull(sellReport);
			assertThat(sellReport.getQuantity(), equalTo(1));
			assertThat(sellReport.getRemainingItems(), equalTo(0));
			assertThat(sellReport.getDate(), equalTo(Calendar.getInstance()));
			int soldItems = sellReport.getSoldItems();
			assertThat(soldItems, equalTo(1));
			assertThat(sellReport.getTotalPrice(), equalTo("UNDEFINED"));
		} catch (ItemNotFoundException e) {
			assertTrue(false);
		} catch (NoMoreItemException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void testSellOk2() {
		Store store = new Store();
		store.registerItemPrice("id-1", 3f);
		store.add("id-1", 1);
		
		try {
			assertThat(store.getItems("id-1"), equalTo(1));
			SellReport sellReport = store.sell("id-1", 1);
			assertNotNull(sellReport);
			assertThat(sellReport.getQuantity(), equalTo(1));
			assertThat(sellReport.getRemainingItems(), equalTo(0));
			assertThat(sellReport.getDate(), equalTo(Calendar.getInstance()));
			int soldItems = sellReport.getSoldItems();
			assertThat(soldItems, equalTo(1));
			assertThat(sellReport.getTotalPrice(), equalTo("3.0€"));
		} catch (ItemNotFoundException e) {
			assertTrue(false);
		} catch (NoMoreItemException e) {
			assertTrue(false);
		}
	}
	

	@Test
	public void testSellTwoItems() {
		Store store = new Store();
		store.add("id-1", 3);
		
		try {
			assertThat(store.getItems("id-1"), equalTo(3));
			SellReport sellReport = store.sell("id-1", 2);
			assertThat(sellReport.getQuantity(), equalTo(2));
			assertThat(sellReport.getRemainingItems(), equalTo(1));
			assertThat(sellReport.getDate(), equalTo(Calendar.getInstance()));
			int soldItems = sellReport.getSoldItems();
			assertThat(soldItems, equalTo(2));
		} catch (ItemNotFoundException e) {
			assertTrue(false);
		} catch (NoMoreItemException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void testSellNotFoundException() {
		Store store = new Store();
		store.add("id-1", 1);
		
		try {
			store.sell("id-2", 1);
		} catch (ItemNotFoundException e) {
			assertTrue(true);
		} catch (NoMoreItemException e) {
			assertTrue(false);
		}
	}
	
	
	@Test
	public void testSellNoMore() {
		Store store = new Store();
		store.add("id-1", 1);
		
		try {
			store.sell("id-1", 2);
		} catch (ItemNotFoundException e) {
			assertTrue(false);
		} catch (NoMoreItemException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testNeedRefill() {
		Store store = new Store();
		store.add("id-1", 3);
		
		try {
			SellReport sellReport = store.sell("id-1", 2);
			assertTrue(sellReport.needRefill());
		} catch (ItemNotFoundException e) {
			assertTrue(false);
		} catch (NoMoreItemException e) {
			assertTrue(false);
		}
	}
	

	@Test
	public void testNeedRefill2() {
		Store store = new Store();
		store.add("id-1", 5);
		
		try {
			SellReport sellReport = store.sell("id-1", 2);
			int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
			if (day > 5 || day < 27) {
				assertTrue(sellReport.needRefill());
			} else {
				assertFalse(sellReport.needRefill());
			}
		} catch (ItemNotFoundException e) {
			assertTrue(false);
		} catch (NoMoreItemException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void testPrint() {
		Store store = new Store();
		store.add("id-1", 3);
		store.add("id-2", 2);
		store.add("id-3", 1);
		
		String result = store.printInventory();
		System.out.println(result);
	}

}

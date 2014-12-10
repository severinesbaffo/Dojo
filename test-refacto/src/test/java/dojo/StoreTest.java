package dojo;

import static org.hamcrest.Matchers.*;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;


import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;


public class StoreTest {

    private static final String ITEM_3 = "id-3";
	private static final String ITEM_2 = "id-2";
    private static final String ITEM_1 = "id-1";
    private Store store;

    @Before
    public void setUp() {
        store = new Store(true);
    }

    @Test(expected = ItemNotFoundException.class)
    public void testGetNotFoundItem() throws Exception {
        store.add(ITEM_2, 2);
        store.getNbItems(ITEM_1);
    }

    @Test
    public void testGetFindOneItem() throws Exception {
        int nbItemsToAdd = 10;
        store.add(ITEM_1, nbItemsToAdd);

        checkNbItem(ITEM_1, nbItemsToAdd);
    }

    @Test
    public void testGetAdd3Items() throws Exception {

        store.add(ITEM_1, 1);
        store.add(ITEM_1, 2);

        checkNbItem(ITEM_1, 3);
    }

    private void checkNbItem(String itemId, int expectedNbItems) throws ItemNotFoundException {
        int nbItems = store.getNbItems(itemId);
        assertThat(nbItems, equalTo(expectedNbItems));
    }

    @Test
    public void sellOneItemWithoutRegisteringPrice() throws Exception {
        store.add(ITEM_1, 1);
        SellReport sellReport = store.sell(ITEM_1, 1);
        checkSellReport(sellReport, 1, 0, Calendar.getInstance(), 1, "UNDEFINED");
    }

    @Test
    public void sellOneItemWithPrice() throws Exception {
        store.registerItemPrice(ITEM_1, 3f);
        store.add(ITEM_1, 1);
        SellReport sellReport = store.sell(ITEM_1, 1);
        checkSellReport(sellReport, 1, 0, Calendar.getInstance(), 1, "3.0€");
    }

    private void checkSellReport(SellReport sellReport, int quantity, int remainingItems, Calendar date,
            int expectedSoldItems, String expectedTotalPrice) {
        assertThat(sellReport.getQuantity(), equalTo(quantity));
        assertThat(sellReport.getRemainingItems(), equalTo(remainingItems));
        assertThat(sellReport.getDate(), equalTo(date));
        assertThat(sellReport.getSoldItems(), equalTo(expectedSoldItems));
        assertThat(sellReport.getTotalPrice(), equalTo(expectedTotalPrice));
    }

    @Test
    public void sellTwoItems() throws Exception {
        store.add(ITEM_1, 3);
        SellReport sellReport = store.sell(ITEM_1, 2);
        checkSellReport(sellReport, 2, 1, Calendar.getInstance(), 2, "UNDEFINED");
    }

    @Test(expected=ItemNotFoundException.class)
    public void testSellNotExistingItem() throws Exception {
        store.add(ITEM_1, 1);

        store.sell(ITEM_2, 1);
    }

    @Test(expected=NoMoreItemException.class)
    public void testSellNoMore() throws Exception {
        store.add(ITEM_1, 1);

        store.sell(ITEM_1, 2);
    }

    @Test
    public void testNeedRefillWhenNoMoreItemsToSell() throws Exception {
        store.add(ITEM_1, 3);
        SellReport sellReport = store.sell(ITEM_1, 3);
        assertTrue(sellReport.needRefill());
    }

    @Test
    public void testNeedRefillDependingOnCurrentDate()  {

        store.add(ITEM_1, 5);

        try {
            SellReport sellReport = store.sell(ITEM_1, 2);
            int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            if (day < 5 || day > 27) {

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
    public void testPrint() throws Exception {
    	Store storeSpy = spy(store);
    	store.registerItemPrice(ITEM_1, 3f);
    	store.registerItemPrice(ITEM_3, 1.5f);
        store.add(ITEM_1, 10);
        store.add(ITEM_2, 5);
        store.add(ITEM_3, 2);
        store.sell(ITEM_2, 3);
        store.sell(ITEM_3, 2);
        
        BufferedReader br = null;
		try {
	        storeSpy.printInventory();
			String itemId;
			String line;
			br = new BufferedReader(new InputStreamReader(new FileInputStream(Store.INVENTORY_FILENAME), "UTF-8"));
			while ((line = br.readLine()) != null) {
				if (line.startsWith(Store.ITEM)) {
					itemId = line.substring(Store.ITEM.length(),
							line.indexOf(":") - 1);
					if (itemId.equals(ITEM_1)) {
						assertThat(line, not("NEED REFILL !!"));
						if (line.contains("items remaining")) {
							assertThat(line, equalTo("8 items remaining"));
						}
						if (line.startsWith("Last total price :")) {
							assertThat(line,  equalTo("Last total price : 6.0€"));
						}
					}
					if (itemId.equals(ITEM_2)) {
						assertThat(line, not("NEED REFILL !!"));
						if (line.contains("items remaining")) {
							assertThat(line, equalTo("2 items remaining"));
						}
						if (line.startsWith("Last total price :")) {
							assertThat(line,  equalTo("Last total price : UNDEFINED"));
						}
					}
					if (itemId.equals(ITEM_3)) {
						if (line.contains("items remaining")) {
							assertThat(line, equalTo("0 items remaining"));
						}
						if (line.startsWith("Last total price :")) {
							assertThat(line,  equalTo("Last total price : 3.0€"));
						}
					}
				}
			}
		} finally {
			br.close();
		}
		verify(storeSpy).sendInventory();
		verify(storeSpy, never()).sendEmergencyRefillReports(anyList());
    }

    
    @Test
    public void testPrint2() throws Exception {
    	Store storeSpy = spy(store);
        
        Calendar calendar1 = new GregorianCalendar();
        calendar1.set(2014, 10, 25);
        SellReport sellReport1 = new SellReport(calendar1, 2, 0, 2, 5f);
        

        Calendar calendar2 = new GregorianCalendar();
        calendar2.set(2014, 11, 25);
        SellReport sellReport2 = new SellReport(calendar2, 2, 10, 2, 5f);
        Map<String, SellReport> sellReports = new HashMap<String, SellReport>();
        sellReports.put(ITEM_1, sellReport1);
        sellReports.put(ITEM_2, sellReport2);
        
        storeSpy.setLastSellReports(sellReports);
        
        store.add(ITEM_1, 0);
        store.add(ITEM_2, 10);
        
        storeSpy.printInventory();
        
		verify(storeSpy).sendInventory();
		List<SellReport> needRefillReports = new ArrayList<SellReport>();
		needRefillReports.add(sellReport1);
		
		verify(storeSpy).sendEmergencyRefillReports(needRefillReports);
        
    }

}

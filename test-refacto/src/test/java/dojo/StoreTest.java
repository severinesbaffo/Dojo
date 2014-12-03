package dojo;

import static org.hamcrest.Matchers.*;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

@SuppressWarnings("unused")
public class StoreTest {

    private static final String ITEM_2 = "id-2";
    private static final String ITEM_1 = "id-1";
    private Store store;

    @Before
    public void setUp() {
        store = new Store();
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
    public void testNeedRefillDependingOnCurrentDate() {
        store.add(ITEM_1, 5);

        try {
            SellReport sellReport = store.sell(ITEM_1, 2);
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
        store.add(ITEM_1, 3);
        store.add(ITEM_2, 2);
        store.add("id-3", 1);

        String result = store.printInventory();
        System.out.println(result);
    }

}

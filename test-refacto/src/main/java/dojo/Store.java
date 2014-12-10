package dojo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class Store {
	
	public static final String ITEM = "ITEM ";
	public static final String INVENTORY_FILENAME = "inventory.txt";
	private Map<String, Integer> inventory = new HashMap<String, Integer>();
	private Map<String, Float> prices = new HashMap<String, Float>();
	private Map<String, SellReport> lastSellReports = new HashMap<String, SellReport>();
	
	private boolean test = false;
	
	public Store(boolean test){
		this.test = test;
	}
	public Store(){
	}
	
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
		
		SellReport sellReport = new SellReport(Calendar.getInstance(), quantity, inventory.get(id), items, prices.get(id));
		lastSellReports.put(id, sellReport);
		return sellReport;
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
	
	public Integer getNbItems(String id) throws ItemNotFoundException {
		if (inventory.get(id) != null) {
			return inventory.get(id);
		}
		throw new ItemNotFoundException();
	}
	
	public void printInventory() throws IOException {
		
		Writer out = new OutputStreamWriter(new FileOutputStream(
				INVENTORY_FILENAME), "UTF-8");
		try {

			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
			StringBuffer sb = new StringBuffer("Inventory :\n\n");
			for (String id : inventory.keySet()) {
				sb.append(ITEM).append(id).append(" : \n").append(inventory.get(id))
						.append(" items remaining\n");
				SellReport sellReport = lastSellReports.get(id);
				if (sellReport != null) {
					sb.append("Last sell at ")
							.append(dateFormat.format(sellReport.getDate().getTime()))
									.append("\n");
					sb.append("Last total price : ")
							.append(sellReport.getTotalPrice()).append("\n");
					if (sellReport.needRefill()) {
						sb.append("NEED REFILL !!\n");
					}
				}
				sb.append("\n");
			}
			out.write(sb.toString());
		} finally {
			out.close();
		}
		sendInventory();
		List<SellReport> needRefillReports = new ArrayList<SellReport>();
		for (SellReport report : this.lastSellReports.values()) {
			if (report != null
					&& report.needRefill()
					&& ((Calendar.getInstance().getTimeInMillis() - report
							.getDate().getTimeInMillis())/ (60 * 60 * 1000 * 24) > 5)) {
				needRefillReports.add(report);
			}
		}
		if (!needRefillReports.isEmpty()) {
			sendEmergencyRefillReports(needRefillReports);
		}
	}
	
	
	public void sendInventory() {
		if (!test) {
			throw new RuntimeException("Unable to send email !!");
		}
	}

	public void sendEmergencyRefillReports(List<SellReport> needRefillReports) {
		if (!test) {
			throw new RuntimeException("Unable to send email !!");
		}
	}
	public void setLastSellReports(Map<String, SellReport> lastSellReports) {
		this.lastSellReports = lastSellReports;
	}
	
	

}

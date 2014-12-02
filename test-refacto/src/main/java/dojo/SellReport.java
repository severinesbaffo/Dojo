package dojo;

import java.util.Calendar;

public class SellReport {

	private Calendar date;
	private int quantity;
	private int remainingItems;
	private int soldItems;
	private Float price;
	
	public SellReport(Calendar date, int quantity, int remainingItems,
			int soldItems, Float price) {
		this.date = date;
		this.quantity = quantity;
		this.remainingItems = remainingItems;
		this.soldItems = soldItems;
		this.price = price;
	}

	public Calendar getDate() {
		return date;
	}

	public int getQuantity() {
		return quantity;
	}

	public int getRemainingItems() {
		return remainingItems;
	}


	public int getSoldItems() {
		return soldItems;
	}

	
	public boolean needRefill() {
		return ((remainingItems < 1) || 
				((date.get(Calendar.DAY_OF_MONTH) < 5 || date.get(Calendar.DAY_OF_MONTH)>27) && remainingItems<5));
	}
	
	public String getTotalPrice() {
		if (price == null) {
			return "UNDEFINED";
		}
		return (price*quantity)+"€";
	}
	
}

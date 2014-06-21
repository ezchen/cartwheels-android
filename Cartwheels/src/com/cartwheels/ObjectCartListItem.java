package com.cartwheels;

public class ObjectCartListItem {
	public int icon;
	public String cartName;
	public String zipcode;
	public String permit;
	
	public ObjectCartListItem() {
		icon = 0;
		cartName = null;
		zipcode = null;
		permit = null;
	}
	
	public ObjectCartListItem(int icon, String cartName, String zipcode, String permit) {
		this.icon = icon;
		this.cartName = cartName;
		this.zipcode = zipcode;
		this.permit = permit;
	}
}

package com.cartwheels;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class FoodMenuItem implements Parcelable {

	public int id;
	public int menu_id;
	public double price;
	public String[] notes;
	
	public String image_url;
	public String image_url_large;
	
	public String description;
	public String name;
	
	public FoodMenuItem() {
		id = 0;
		menu_id = 0;
		price = 0;
		notes = null;
		image_url = "unknown";
		image_url_large = "unknown";
		description = "unknown";
		name = "unknown";
	}
	public FoodMenuItem(Parcel parcel) {
		id = parcel.readInt();
		menu_id = parcel.readInt();
		price = parcel.readDouble();
		image_url = parcel.readString();
		image_url_large = parcel.readString();
		description = parcel.readString();
		name = parcel.readString();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		parcel.writeInt(id);
		parcel.writeInt(menu_id);
		parcel.writeDouble(price);
		parcel.writeString(image_url);
		parcel.writeString(image_url_large);
		parcel.writeString(description);
		parcel.writeString(name);
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMenu_id() {
		return menu_id;
	}

	public void setMenu_id(int menu_id) {
		this.menu_id = menu_id;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String[] getNotes() {
		return notes;
	}

	public void setNotes(String[] notes) {
		this.notes = notes;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public String getImage_url_large() {
		return image_url_large;
	}

	public void setImage_url_large(String image_url_large) {
		this.image_url_large = image_url_large;
	}

	public String getDescription() {
		if (description == null) {
			return "";
		}
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
}

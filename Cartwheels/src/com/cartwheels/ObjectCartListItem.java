package com.cartwheels;

import android.os.Parcel;
import android.os.Parcelable;

public class ObjectCartListItem implements Parcelable {
	public String bitmapUrl;
	public String cartName;
	public String zipcode;
	public String permit;
	
	public ObjectCartListItem() {
		bitmapUrl = null;
		cartName = null;
		zipcode = null;
		permit = null;
	}
	
	// Icon, cartName, zipCode, Permit
	public ObjectCartListItem(Parcel parcel) {
		bitmapUrl = parcel.readString();
		cartName = parcel.readString();
		zipcode = parcel.readString();
		permit = parcel.readString();
	}
	
	public ObjectCartListItem(String bitmapUrl, String cartName, String zipcode, String permit) {
		
		this.bitmapUrl = null;
		if (bitmapUrl != null)
			this.bitmapUrl = "cartwheels.us" + bitmapUrl;
		this.cartName = cartName;
		this.zipcode = zipcode;
		this.permit = permit;
	}
	
	public String toString() {
		String s = "";
		s += "icon: " + bitmapUrl + "\n";
		s += "cartName: " + cartName + "\n";
		s += "zipcode: " + zipcode + "\n";
		s += "permit: " + permit;
		
		return s;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	// icon, cartName, zipcode, permit
	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		parcel.writeString(bitmapUrl);
		parcel.writeString(cartName);
		parcel.writeString(zipcode);
		parcel.writeString(permit);
	}
	
    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<ObjectCartListItem> CREATOR = new Parcelable.Creator<ObjectCartListItem>() {
        public ObjectCartListItem createFromParcel(Parcel in) {
            return new ObjectCartListItem(in);
        }

        public ObjectCartListItem[] newArray(int size) {
            return new ObjectCartListItem[size];
        }
    };
}

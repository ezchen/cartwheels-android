package com.cartwheels.tasks;

import android.os.Parcel;
import android.os.Parcelable;

public class ReviewItem implements Parcelable {

	private String user;
	private String text;
	private int rating;
	
	public ReviewItem(String user, String text, int rating) {
		this.user = user;
		this.text = text;
		this.rating = rating;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}
	
}

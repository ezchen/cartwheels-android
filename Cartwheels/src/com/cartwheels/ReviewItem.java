package com.cartwheels;

import android.os.Parcel;
import android.os.Parcelable;

public class ReviewItem implements Parcelable {

	public String user;
	public String text;
	public int rating;
	public String reviewId;
	public String createdAt;
	public String updatedAt;
	
	public ReviewItem(String user, String text, int rating) {
		this.user = user;
		this.text = text;
		this.rating = rating;
	}
	
	public ReviewItem(Parcel parcel) {
		user = parcel.readString();
		text = parcel.readString();
		rating = parcel.readInt();
		createdAt = parcel.readString();
		updatedAt = parcel.readString();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		parcel.writeString(user);
		parcel.writeString(text);
		parcel.writeInt(rating);
		parcel.writeString(createdAt);
		parcel.writeString(updatedAt);
	}
	
	public static final Parcelable.Creator<ReviewItem> CREATOR = new Parcelable.Creator<ReviewItem>() {
		public ReviewItem createFromParcel(Parcel in) {
			return new ReviewItem(in);
		}
		
		public ReviewItem[] newArray(int size) {
			return new ReviewItem[size];
		}
	};
	
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
	
	public String toString() {
		String s = "";
		s += reviewId + "\n";
		s += user + "\n";
		s += text + "\n";
		s += rating + "\n";
		s += createdAt + "\n";
		s += updatedAt + "\n";
		
		return s;
	}
}

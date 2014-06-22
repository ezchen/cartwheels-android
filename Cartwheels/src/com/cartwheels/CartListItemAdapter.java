package com.cartwheels;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CartListItemAdapter extends ArrayAdapter<ObjectCartListItem> {
	Context context;
	int layoutResourceID;
	ObjectCartListItem[] items;
	
	
	public CartListItemAdapter(Context context, int layoutResourceID, ObjectCartListItem[] items) {
		super(context, layoutResourceID, items);
		
		this.context = context;
		this.layoutResourceID = layoutResourceID;
		this.items = items;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View listItem = convertView;
		
		
		Log.d("getView", "inflating");
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		listItem = inflater.inflate(layoutResourceID, parent, false);
		Log.d("getView", "inflate successful");
		
		Log.d("getView", "retrieving item");
		ObjectCartListItem item = items[position];
		Log.d("getView", "retrieve success");
		
		Log.d("getView", "retrieving picture");
		ImageView cartPicture = (ImageView) listItem.findViewById(R.id.cartPicture);
		Log.d("getView", "got view");
		cartPicture.setImageResource(item.icon);
		Log.d("getView", "picture retrieved");
		
		Log.d("getView", "retrieving cartName");
		TextView cartName = (TextView) listItem.findViewById(R.id.cartName);
		cartName.setText(item.cartName);
		Log.d("getView", "cartName retrieved");
		
		TextView cartZipcode = (TextView) listItem.findViewById(R.id.cartZipcode);
		Log.d("getView", "got zipcode textview");
		cartZipcode.setText(item.zipcode);
		Log.d("getView", "set zipCode");
		
		TextView cartPermit = (TextView) listItem.findViewById(R.id.cartPermit);
		cartPermit.setText(item.permit);
		
		Log.d("getView", "methodEnded");
		return listItem;
	}
}

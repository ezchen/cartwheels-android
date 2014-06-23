package com.cartwheels;

import android.app.Activity;
import android.content.Context;
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
		
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		listItem = inflater.inflate(layoutResourceID, parent, false);

		ObjectCartListItem item = items[position];

		ImageView cartPicture = (ImageView) listItem.findViewById(R.id.cartPicture);
		cartPicture.setImageResource(item.icon);
		
		TextView cartName = (TextView) listItem.findViewById(R.id.cartName);
		cartName.setText(item.cartName);
		
		TextView cartZipcode = (TextView) listItem.findViewById(R.id.cartZipcode);
		cartZipcode.setText(item.zipcode);
		
		TextView cartPermit = (TextView) listItem.findViewById(R.id.cartPermit);
		cartPermit.setText(item.permit);

		return listItem;
	}
}

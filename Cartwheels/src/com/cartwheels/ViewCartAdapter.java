package com.cartwheels;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewCartAdapter extends ArrayAdapter<ObjectViewCartItem> {
	
	Context context;
	int layoutResourceID;
	ObjectViewCartItem[] items;
	
	public ViewCartAdapter(Context context, int layoutResourceID, ObjectViewCartItem[] items) {
		super(context, layoutResourceID);
		
		this.context = context;
		this.layoutResourceID = layoutResourceID;
		this.items = items;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View listItem = null;
		
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		listItem = inflater.inflate(layoutResourceID, parent, false);
		
		ObjectViewCartItem item = items[position];
		ImageView icon = (ImageView) listItem.findViewById(R.id.viewCart_OptionIcon);
		icon.setImageResource(item.icon);
		
		TextView name = (TextView) listItem.findViewById(R.id.viewCart_OptionName);
		name.setText(item.name);
		
		ImageView carotIcon = (ImageView) listItem.findViewById(R.id.viewCart_CarotIcon);
		carotIcon.setImageResource(item.carotIcon);
		
		return listItem;
	}
}

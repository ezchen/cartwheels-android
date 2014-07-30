package com.cartwheels.adapters;


import com.cartwheels.ObjectDrawerItem;
import com.cartwheels.R;
import com.cartwheels.R.id;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerItemCustomAdapter extends ArrayAdapter<ObjectDrawerItem> {
	Context mContext;
	int layoutResourceID;
	ObjectDrawerItem items[];
	
	public DrawerItemCustomAdapter(Context mContext, int layoutResourceID, ObjectDrawerItem[] items) {
		super(mContext, layoutResourceID, items);
		
		this.layoutResourceID = layoutResourceID;
		this.mContext = mContext;
		this.items = items;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View listItem = convertView;
		
		LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
		listItem = inflater.inflate(layoutResourceID, parent, false);
		
		ImageView imageViewIcon = (ImageView) listItem.findViewById(R.id.imageViewIcon);
		TextView textViewName = (TextView) listItem.findViewById(R.id.textViewName);
		
		ObjectDrawerItem folder = items[position];
		
		imageViewIcon.setImageResource(folder.icon);
		textViewName.setText(folder.name);
		
		return listItem;
	}
}

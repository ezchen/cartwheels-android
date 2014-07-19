package com.cartwheels;

import java.util.ArrayList;

import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuItemAdapter extends ArrayAdapter<FoodMenuItem> {
	private int layoutResourceId;
	private Context context;
	private ArrayList<FoodMenuItem> items;
	
	public MenuItemAdapter(Context context, int layoutResourceId, ArrayList<FoodMenuItem> items) {
		super(context, layoutResourceId, items);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.items = items;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = ((Activity)context).getLayoutInflater();
		View listItem = inflater.inflate(layoutResourceId, parent, false);
		
		FoodMenuItem item = items.get(position);

		ImageView menuImageView = (ImageView) listItem.findViewById(R.id.menuItemImage);
		TextView menuItemNameView = (TextView) listItem.findViewById(R.id.menuItemName);
		TextView menuItemPriceView = (TextView) listItem.findViewById(R.id.menuItemPrice);
		TextView menuItemDescriptionView = (TextView) listItem.findViewById(R.id.menuItemDescription);
		
		String imageUrl = item.getImage_url();
		String name = item.getName();
		String price = "" + item.getPrice();
		String description = item.getDescription();
		
		Picasso.with(context).load(imageUrl).into(menuImageView);
		menuItemNameView.setText(name);
		menuItemPriceView.setText(price);
		menuItemDescriptionView.setText(description);
		return listItem;
	}
}

package com.cartwheels;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nhaarman.listviewanimations.ArrayAdapter;
import com.squareup.picasso.Picasso;

public class CartListItemAdapter extends ArrayAdapter<ObjectCartListItem> {
	private Context context;
	int layoutResourceID;
	
	public CartListItemAdapter(int layoutResourceID, Context context, ObjectCartListItem[] items) {
		super(Arrays.asList(items), false);
		
		this.context = context;
		this.layoutResourceID = layoutResourceID;
	}

	public CartListItemAdapter(int layoutResourceId, Activity context,
			ObjectCartListItem[] items) {
		super(Arrays.asList(items), false);
		
		this.context = context;
		this.layoutResourceID = layoutResourceID;
	}
	
	public CartListItemAdapter(int layoutResourceId, Context context,
				ArrayList<ObjectCartListItem> items) {
		super(items, false);
		
		this.context = context;
		this.layoutResourceID = layoutResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View listItem = convertView;
		/*
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		listItem = inflater.inflate(layoutResourceID, parent, false);
*/
		
		listItem = LayoutInflater.from(context).inflate(R.layout.listview_cart_row, parent, false);
		ObjectCartListItem item = getItem(position);

		ImageView cartPicture = (ImageView) listItem.findViewById(R.id.cartPicture);
		Picasso.with(context).load(item.bitmapUrl).into(cartPicture);
		
		RatingBar ratingBar = (RatingBar) listItem.findViewById(R.id.cartRating);
		ratingBar.setRating(item.rating);
		
		TextView cartName = (TextView) listItem.findViewById(R.id.cartName);
		cartName.setText(item.cartName);
		
		TextView cartZipcode = (TextView) listItem.findViewById(R.id.cartZipcode);
		cartZipcode.setText(item.zipcode);
		
		TextView cartPermit = (TextView) listItem.findViewById(R.id.cartPermit);
		cartPermit.setText(item.permit);

		return listItem;
	}
	
}

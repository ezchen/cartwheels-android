package com.cartwheels;

import com.cartwheels.tasks.ImageDownloaderTask;

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
		
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		listItem = inflater.inflate(layoutResourceID, parent, false);

		ObjectCartListItem item = items[position];

		ImageView cartPicture = (ImageView) listItem.findViewById(R.id.cartPicture);
		if (item.bitmapUrl != null)
			new ImageDownloaderTask(cartPicture).execute(item.bitmapUrl);
		else
			Log.d("bitmapUrl", "is null");
		
		TextView cartName = (TextView) listItem.findViewById(R.id.cartName);
		cartName.setText(item.cartName);
		
		TextView cartZipcode = (TextView) listItem.findViewById(R.id.cartZipcode);
		cartZipcode.setText(item.zipcode);
		
		TextView cartPermit = (TextView) listItem.findViewById(R.id.cartPermit);
		cartPermit.setText(item.permit);

		return listItem;
	}
}

package com.cartwheels;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CartListItemAdapter extends ArrayAdapter<ObjectCartListItem> {
	private Context context;
	int layoutResourceID;
	private ObjectCartListItem[] items;
	private LruCache<String, Bitmap> cache;
	
	
	public CartListItemAdapter(Context context, int layoutResourceID, ObjectCartListItem[] items, LruCache cache) {
		super(context, layoutResourceID, items);
		
		this.context = context;
		this.layoutResourceID = layoutResourceID;
		this.items = items;
		this.cache = cache;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View listItem = convertView;
		
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		listItem = inflater.inflate(layoutResourceID, parent, false);

		ObjectCartListItem item = items[position];

		ImageView cartPicture = (ImageView) listItem.findViewById(R.id.cartPicture);
		Bitmap bitmap = cache.get(item.bitmapUrl);
		if (bitmap != null) {
			cartPicture.setImageBitmap(bitmap);
		} else {
			Log.d("getView CartListItemAdapter", "bitmap is null");
		}
		
		TextView cartName = (TextView) listItem.findViewById(R.id.cartName);
		cartName.setText(item.cartName);
		
		TextView cartZipcode = (TextView) listItem.findViewById(R.id.cartZipcode);
		cartZipcode.setText(item.zipcode);
		
		TextView cartPermit = (TextView) listItem.findViewById(R.id.cartPermit);
		cartPermit.setText(item.permit);

		return listItem;
	}
}

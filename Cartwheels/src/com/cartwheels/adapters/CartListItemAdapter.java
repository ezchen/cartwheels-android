package com.cartwheels.adapters;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.cartwheels.ObjectCartListItem;
import com.cartwheels.R;
import com.cartwheels.RoundedTransform;
import com.nhaarman.listviewanimations.ArrayAdapter;
import com.squareup.picasso.Picasso;

public class CartListItemAdapter extends ArrayAdapter<ObjectCartListItem> {
	private Context context;
	private int layoutResourceID;
	
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
		
		Picasso.with(context).load(item.bitmapUrl).transform(new RoundedTransform(10, 3)).into(cartPicture);
		
		RatingBar ratingBar = (RatingBar) listItem.findViewById(R.id.cartRating);
		ratingBar.setRating(item.rating);
		
		TextView cartName = (TextView) listItem.findViewById(R.id.cartName);
		cartName.setText(item.cartName);
		
		TextView cartZipcode = (TextView) listItem.findViewById(R.id.cartZipcode);
		
		if (item.address != null && !item.address.equals("null"))
			cartZipcode.setText(item.address);
		else
			cartZipcode.setText("Address Unavailable");
		
		TextView cartDescription = (TextView) listItem.findViewById(R.id.cartDescription);
		if (item.description != null && !item.description.equals("null")) {
			cartDescription.setText(item.description);
		}

		return listItem;
	}
}

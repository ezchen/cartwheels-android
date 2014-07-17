package com.cartwheels;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nhaarman.listviewanimations.ArrayAdapter;
import com.squareup.picasso.Picasso;

public class DisplayOwnedCartsAdapter extends ArrayAdapter<ObjectCartListItem> {

	Context context;
	int layoutResourceId;

	public DisplayOwnedCartsAdapter(int layoutResourceId, Context context, ArrayList<ObjectCartListItem> items) {
		super(items, true);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		view = LayoutInflater.from(context).inflate(R.layout.listview_owned_carts, parent, false);
		
		ImageView cartPicture = (ImageView) view.findViewById(R.id.ownedCartPicture);
		TextView cartName = (TextView) view.findViewById(R.id.ownedCartName);
		RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ownedCartRating);
		TextView zipCode = (TextView) view.findViewById(R.id.ownedCartZipcode);
		TextView permit = (TextView) view.findViewById(R.id.ownedCartPermit);
		TextView description = (TextView) view.findViewById(R.id.ownedCartDescription);
		
		ObjectCartListItem item = getItem(position);
		Picasso.with(context).load(item.bitmapUrl).into(cartPicture);
		
		cartName.setText(item.cartName);
		ratingBar.setRating(item.rating);
		zipCode.setText(item.zipcode);
		permit.setText(item.permit);
		description.setText(item.description);
		
		return view;
	}
}

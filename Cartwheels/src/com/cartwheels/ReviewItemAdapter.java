package com.cartwheels;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cartwheels.custom_views.RatingView;

public class ReviewItemAdapter extends ArrayAdapter<ReviewItem> {

	private int layoutResourceId;
	private Context context;
	private ReviewItem[] items;
	
	public ReviewItemAdapter(Context context, int layoutResourceId, ReviewItem[] items) {
		super(context, layoutResourceId, items);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.items = items;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = ((Activity)context).getLayoutInflater();
		View listItem = inflater.inflate(layoutResourceId, parent, false);
		
		ReviewItem item = items[position];
		

		TextView userNameText = (TextView) listItem.findViewById(R.id.reviewUser);
		userNameText.setText(item.user);
		
		RatingView rating = (RatingView) listItem.findViewById(R.id.reviewRating);
		rating.setRating(item.rating);
		
		TextView text = (TextView) listItem.findViewById(R.id.reviewText);
		text.setText(item.text);
		
		return listItem;
	}
}
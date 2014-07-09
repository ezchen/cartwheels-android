package com.cartwheels.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cartwheels.R;

public class RatingView extends LinearLayout {
	
	ImageView[] views;
	int rating;
	int size;
	
	public RatingView(Context context) {
		super(context);
		
	}
	
	public RatingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RatingView);
		rating = array.getInt(R.styleable.RatingView_rating, 0);
		size = array.getInt(R.styleable.RatingView_size, 20);
		
		LayoutInflater inflater = (LayoutInflater)
										context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		inflater.inflate(R.layout.view_rating, this, true);
		// access all the individual circles and change them depending on the rating
		
		views = new ImageView[5];
		views[0] = (ImageView) findViewById(R.id.ratingCircle1);
		views[1] = (ImageView) findViewById(R.id.ratingCircle2);
		views[2] = (ImageView) findViewById(R.id.ratingCircle3);
		views[3] = (ImageView) findViewById(R.id.ratingCircle4);
		views[4] = (ImageView) findViewById(R.id.ratingCircle5);
		
		GradientDrawable shapeDrawable;
		for (int i = 0; i < views.length && i < rating; i++) {
			ImageView view = views[i];
			shapeDrawable = (GradientDrawable) view.getBackground();
			shapeDrawable.setColor(getResources().getColor(R.color.theme_color));
		}
		
	}
	
	public void setRating(int rating) {
		this.rating = rating;
		GradientDrawable shapeDrawable;
		for (int i = 0; i < views.length && i < rating; i++) {
			ImageView view = views[i];
			shapeDrawable = (GradientDrawable) view.getBackground();
			shapeDrawable.setColor(getResources().getColor(R.color.theme_color));
		}
		invalidate();
		requestLayout();
	}
	
}

package com.cartwheels.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SearchView extends RelativeLayout {

	private TextView textQuery;
	private TextView locationQuery;
	
	public SearchView(Context context) {
		super(context);
	}
	
	public SearchView(Context context, AttributeSet attributes) {
		super(context, attributes);
	}
	
	public SearchView(Context context, AttributeSet attributes, int integer) {
		super(context, attributes, integer);
	}
	
	

}

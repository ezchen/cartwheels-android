package com.cartwheels.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.cartwheels.R;

public class SearchView extends RelativeLayout implements OnClickListener {

	public final static String tq = "tq";
	public final static String lq = "lq";
	
	private EditText textQuery;
	private EditText locationQuery;
	
	private SearchListener searchListener;
	
	public SearchView(Context context) {
		super(context);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_search, this, true);
		
		Log.d("SearchView Constructor", "created");
		Button button = (Button) findViewById(R.id.search_button);
		
		button.setOnClickListener(this);
	}
	
	public SearchView(Context context, AttributeSet attributes) {
		super(context, attributes);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_search, this, true);
		
		Button button = (Button) findViewById(R.id.search_button);
		
		button.setOnClickListener(this);
	}
	
	public void search() {
		// access the EditText's view
		Log.d("search", "accessing data");
		textQuery = (EditText) findViewById(R.id.textQuery);
		locationQuery = (EditText) findViewById(R.id.locationQuery);
		Log.d("search", "data access successful");
		
		String textQueryData = textQuery.getText().toString();
		String locationQueryData = locationQuery.getText().toString();
		
		searchListener.search(textQueryData, locationQueryData);
	}
	
	public SearchListener setSearchListener(SearchListener listener) {
		this.searchListener = listener;
		return this.searchListener;
	}
	
	public interface SearchListener {
		
		void search(String textQueryData, String locationQueryData);
	}

	@Override
	public void onClick(View v) {
		Log.d("SearchButton", "clicked");
		search();
	}

}

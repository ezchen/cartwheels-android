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
	
	private boolean open;
	
	private SearchListener searchListener;
	
	public SearchView(Context context) {
		super(context);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.view_search, this, true);
		
		EditText editTextQuery = (EditText) view.findViewById(R.id.textQuery);
		EditText editTextLocationQuery = (EditText) view.findViewById(R.id.locationQuery);
		
		// hide until user presses the search button
		editTextQuery.setVisibility(View.INVISIBLE);
		editTextLocationQuery.setVisibility(View.INVISIBLE);
		Log.d("SearchView Constructor", "created");
		Button button = (Button) findViewById(R.id.search_button);
		
		button.setOnClickListener(this);
	}
	
	public SearchView(Context context, AttributeSet attributes) {
		super(context, attributes);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.view_search, this, true);
		
		EditText editTextQuery = (EditText) view.findViewById(R.id.textQuery);
		EditText editTextLocationQuery = (EditText) view.findViewById(R.id.locationQuery);
		
		open = true;
		Log.d("SearchView Constructor", "created");
		
		Button button = (Button) findViewById(R.id.search_button);
		
		button.setOnClickListener(this);
	}
	
	public void search() {
		// access the EditText's view
		Log.d("search", "accessing data");
		
		// open the view if it isn't open
		
		View view = findViewById(R.id.view_search);
		
		EditText editTextQuery = (EditText) view.findViewById(R.id.textQuery);
		EditText editTextLocationQuery = (EditText) view.findViewById(R.id.locationQuery);
		
		if (!open) {
			// add to view
			view.setVisibility(View.VISIBLE);
			editTextQuery.setVisibility(View.VISIBLE);
			editTextLocationQuery.setVisibility(View.VISIBLE);
			Button button = (Button) view.findViewById(R.id.search_button);
			button.setVisibility(View.VISIBLE);
			
			open = true;
		} else {
			editTextQuery = (EditText) findViewById(R.id.textQuery);
			editTextLocationQuery = (EditText) findViewById(R.id.locationQuery);
			Log.d("search", "data access successful");
			
			String textQueryData = editTextQuery.getText().toString();
			String locationQueryData = editTextLocationQuery.getText().toString();
			
			// only close the view if the user puts in a correct search sequence
			if (textQueryData.length() != 0 || locationQueryData.length() != 0) {
				open = false;
				
				editTextQuery.setVisibility(View.GONE);
				editTextLocationQuery.setVisibility(View.GONE);
			}
			
			searchListener.search(textQueryData, locationQueryData);	

		}
	}
	
	public SearchListener setSearchListener(SearchListener listener) {
		this.searchListener = listener;
		return this.searchListener;
	}
	
	public void hide() {
		View view = findViewById(R.id.view_search);
		EditText editTextQuery = (EditText) view.findViewById(R.id.textQuery);
		EditText editTextLocationQuery = (EditText) findViewById(R.id.locationQuery);
		
		editTextQuery.setVisibility(View.GONE);
		editTextLocationQuery.setVisibility(View.GONE);
		
		open=false;
	}
	
	public void show() {
		View view = findViewById(R.id.view_search);
		EditText editTextQuery = (EditText) view.findViewById(R.id.textQuery);
		EditText editTextLocationQuery = (EditText) view.findViewById(R.id.locationQuery);
		
		editTextQuery.setVisibility(View.VISIBLE);
		editTextLocationQuery.setVisibility(View.VISIBLE);
		open=true;
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

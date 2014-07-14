package com.cartwheels;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class WriteReviewActivity extends Activity {

	private SharedPreferences preferences;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_default);
		
		Intent intent = getIntent();
		Bundle arguments = intent.getExtras();

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, WriteReviewFragment.newInstance(arguments)).commit();
		}
		
		preferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
	}
}

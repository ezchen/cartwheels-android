package com.cartwheels;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.cartwheels.tasks.WriteReviewTask;
import com.cartwheels.tasks.WriteReviewTaskFragment;

public class WriteReviewFragment extends Fragment
											implements OnClickListener {
	
	ObjectCartListItem item;
	
	public static WriteReviewFragment newInstance(Bundle arguments) {
		WriteReviewFragment fragment = new WriteReviewFragment();
		fragment.setArguments(arguments);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		item = getArguments().getParcelable("ObjectCartListItem");
		FragmentManager fragmentManager = getFragmentManager();
		Resources resources = getResources();
		//String fragmentTag = resources.getString(R.string.write_review_task_fragment_string);
		//int fragmentId = resources.getInteger(R.integer.write_review_task_fragment);
		WriteReviewTaskFragment fragment = (WriteReviewTaskFragment) fragmentManager.findFragmentByTag("WriteReview");
		
		if (fragment != null) {
			fragment.setTargetFragment(this, 4);
		} else {
			Log.d("fragment", "null");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
								Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		View view = inflater.inflate(R.layout.fragment_write_review, container, false);
		
		//Button button = (Button) view.findViewById(R.id.writeReviewButton);
		//button.setOnClickListener(this);
		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null && data.hasExtra("success") && resultCode == Activity.RESULT_OK) {
			boolean success = data.getBooleanExtra("success", false);
			
			Intent intent = new Intent(getActivity(), ViewCartActivity.class);
			intent.putExtra("success", success);
			intent.putExtra("ObjectCartListItem", item);
			startActivity(intent);
		}
	}
	
	@Override
	public void onClick(View view) {
		RatingBar ratingBar;
		if (getView() != null) {
			ratingBar = (RatingBar) getView().findViewById(R.id.writeReviewRatingBar);
			int rating = (int) ratingBar.getRating();
			
			EditText editText = (EditText) getView().findViewById(R.id.writeReviewText);
			String text = editText.getText().toString();
			
			WriteReviewTask asyncTask = new WriteReviewTask(getActivity().getApplicationContext());
			SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", Activity.MODE_PRIVATE);
			String email = preferences.getString("email", "");
			String auth_token = preferences.getString("AuthToken", "");
			
			asyncTask.put("email", email);
			asyncTask.put("auth_token", auth_token);
			asyncTask.put("review[rating]", rating + "");
			asyncTask.put("review[text]", text);
			
			asyncTask.setCartId(item.cartId);
			
			WriteReviewTaskFragment fragment = new WriteReviewTaskFragment();
			fragment.setTask(asyncTask);
			fragment.setTargetFragment(this, 4);
			
			asyncTask.setFragment(fragment);
			
			fragment.execute();
			fragment.show(getFragmentManager(), "WriteReview");
			
		}
			
	}
}

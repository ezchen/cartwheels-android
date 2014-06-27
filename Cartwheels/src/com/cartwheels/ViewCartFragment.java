package com.cartwheels;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewCartFragment extends Fragment {

	ObjectCartListItem item;
	Bitmap bitmap;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_view_cart, container, false);
		
		TextView cartName = (TextView) rootView.findViewById(R.id.viewCart_Name);
		TextView reviews = (TextView) rootView.findViewById(R.id.viewCart_NumberOfReviews);
		ImageView rating = (ImageView) rootView.findViewById(R.id.viewCart_Rating);
		TextView zipcode = (TextView) rootView.findViewById(R.id.viewCart_Zipcode);
		TextView permit = (TextView) rootView.findViewById(R.id.viewCart_Permit);
		ImageView cartPicture = (ImageView) rootView.findViewById(R.id.viewCart_CartPicture);
		
		if (savedInstanceState == null) {
			item = getArguments().getParcelable("ObjectCartListItem");
			bitmap = (Bitmap) getArguments().getParcelable("bitmap");
			
			if (bitmap != null)
				cartPicture.setImageBitmap(bitmap);
			cartName.setText(item.cartName);
			zipcode.setText("Zipcode: " + item.zipcode);
			permit.setText("Permit: c" + item.permit);
			
			Toast.makeText(getActivity(), item.toString(), Toast.LENGTH_SHORT).show();
		} else {
			// restore the fragment's state
			// CartItem
			if (savedInstanceState.containsKey("CartItem")) {
				item = (ObjectCartListItem) savedInstanceState.get("CartItem");
			} else {
				Log.d("onCreateView", "cart item does not exist");
			}
			// Bitmap
			if (savedInstanceState.containsKey("bitmap")) {
				bitmap = (Bitmap) savedInstanceState.get("bitmap");
			} else {
				Log.d("onCreateView", "bitmap does not exist");
			}
			
			if (bitmap != null)
				cartPicture.setImageBitmap(bitmap);
			cartName.setText(item.cartName);
			zipcode.setText("Zipcode: " + item.zipcode);
			permit.setText("Permit: c" + item.permit);
		}
		
		Log.d("ViewCartFragment onCreateView", "view created");
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("onActivityCreated", "called");
		if (savedInstanceState == null) {
			
		} else {
			if (getActivity() != null) {
				//Toast.makeText(getActivity(), savedInstanceState.getString("test", "nope"), Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	public static ViewCartFragment newInstance(Bundle bundle) {
		Log.d("ViewCartFragment newInstance", "ViewCartFragment instance created");
		ViewCartFragment fragment = new ViewCartFragment();
		fragment.setArguments(bundle);
		return fragment;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("test", "test");
		outState.putParcelable("CartItem", item);
		outState.putParcelable("bitmap", bitmap);
		Log.d("onSaveInstanceState", "called");
	}
	
}

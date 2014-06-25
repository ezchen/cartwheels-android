package com.cartwheels;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class ViewCartFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_view_cart, container, false);
		
		Log.d("ViewCartFragment onCreateView", "view created");
		if (savedInstanceState == null) {
			Log.d("onCreateView", "savedInstanceState is null");
			ObjectCartListItem item = getArguments().getParcelable("ObjectCartListItem");
			Toast.makeText(getActivity(), item.toString(), Toast.LENGTH_SHORT).show();
			return rootView;
		} else {
			if (getActivity() != null)
			Toast.makeText(getActivity(), savedInstanceState.getString("test", "nope"), Toast.LENGTH_SHORT).show();
			Log.d("onCreateView", "" + savedInstanceState.getString("test"));
		}
		
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState == null) {
			
		} else {
			if (getActivity() != null)
			Toast.makeText(getActivity(), savedInstanceState.getString("test", "nope"), Toast.LENGTH_SHORT).show();
			Log.d("OnCreateView", "" + savedInstanceState.getString("test"));
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
		Log.d("onSaveInstanceState", "called");
	}
}

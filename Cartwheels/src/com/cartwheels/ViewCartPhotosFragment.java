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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.cartwheels.tasks.GetPhotosTaskFragment;
import com.cartwheels.tasks.PhotoUrlTask;

public class ViewCartPhotosFragment extends Fragment implements OnItemClickListener {


	
	private GridView cartPhotos;
	private String[] imageUrls;
	
	int offset;
	int limit;
	
	private FragmentManager fragmentManager;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		fragmentManager = getFragmentManager();
		
		Resources resources = getResources();
		String fragmentTag = "PhotoUrlTask";
		int fragmentId = 3;
		limit = 20;
		
		GetPhotosTaskFragment fragment = (GetPhotosTaskFragment) fragmentManager.findFragmentByTag(fragmentTag);
		
		if (fragment != null) {
			fragment.setTargetFragment(this, fragmentId);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
									Bundle savedInstanceState) {
		
		super.onCreateView(inflater, container, savedInstanceState);
		
		ObjectCartListItem item = getArguments().getParcelable("ObjectCartListItem");
		
		String cartId = item.cartId;
		PhotoUrlTask photoUrlTask = new PhotoUrlTask();
		SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", Activity.MODE_PRIVATE);
		
		Log.d("cartId", cartId);
		String email = preferences.getString("email", "");
		String auth_token = preferences.getString("AuthToken", "");
		
		photoUrlTask.put("email", email);
		photoUrlTask.put("AuthToken", auth_token);
		photoUrlTask.put("offset", "" + offset);
		photoUrlTask.put("limit", "" + limit);
		
		photoUrlTask.setCartId(cartId);
		
		GetPhotosTaskFragment fragment = new GetPhotosTaskFragment();
		fragment.setTask(photoUrlTask);
		fragment.setShowsDialog(false);
		photoUrlTask.setFragment(fragment);
		
		Resources resources = getResources();
		int fragmentId = resources.getInteger(R.integer.review_task_fragment);
		String fragmentTag = resources.getString(R.string.review_task_fragment_string);
		fragment.setTargetFragment(this, fragmentId);
		
		
		cartPhotos = (GridView) inflater.inflate(R.layout.fragment_display_cart_photos, container, false);
		
		fragment.show(getFragmentManager(), "PhotoUrlTask");
		fragment.execute();
		cartPhotos.setOnItemClickListener(this);
		
		return cartPhotos;
	}

	public static ViewCartPhotosFragment newInstance(Bundle arguments) {
		ViewCartPhotosFragment fragment = new ViewCartPhotosFragment();
		fragment.setArguments(arguments);
		return fragment;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		Resources resources = getResources();
		int fragmentId = 3;
		
		if (requestCode == fragmentId && resultCode == Activity.RESULT_OK) {
			imageUrls = data.getStringArrayExtra("ImageUrls");
			buildList(imageUrls);
			Log.d("onActivityResult ViewCartPhotosFragment", "" + imageUrls);
		}
	}
	
	private void buildList(String[] imageUrls) {
		
		ViewCartPhotosAdapter adapter = new ViewCartPhotosAdapter(getActivity(), imageUrls);
		
		cartPhotos.setAdapter(adapter);
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	}
}

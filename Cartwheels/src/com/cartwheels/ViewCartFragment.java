package com.cartwheels;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cartwheels.tasks.ImageDownloaderTask;
import com.cartwheels.tasks.StaticMapsTaskFragment;
import com.squareup.picasso.Picasso;

public class ViewCartFragment extends Fragment {

	private ObjectCartListItem item;
	private Bitmap mapBitmap;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FragmentManager manager = getFragmentManager();
		
		manager.findFragmentById(1);
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
		ImageView map = (ImageView) rootView.findViewById(R.id.viewCart_Map);
		
		if (savedInstanceState == null) {
			item = getArguments().getParcelable("ObjectCartListItem");
			Picasso.with(getActivity()).load(item.bitmapUrl).into(cartPicture);

			if (mapBitmap != null)
				map.setImageBitmap(mapBitmap);
			
			cartName.setText(item.cartName);
			zipcode.setText("Zipcode: " + item.zipcode);
			permit.setText("Permit: c" + item.permit);
			
			Toast.makeText(getActivity(), item.toString(), Toast.LENGTH_SHORT).show();
		} else {
			// restore the fragment's state
			// CartItem
			if (savedInstanceState.containsKey("CartItem")) {
				item = (ObjectCartListItem) savedInstanceState.get("CartItem");
				Picasso.with(getActivity()).load(item.bitmapUrl).into(cartPicture);
			} else {
				Log.d("onCreateView", "cart item does not exist");
			}
			
			if (savedInstanceState.containsKey("mapBitmap")) {
				mapBitmap = (Bitmap) savedInstanceState.get("mapBitmap");
			} else {
				Log.d("onCreateView", "mapBitmap does not exist");
			}

			
			if (mapBitmap != null)
				map.setImageBitmap(mapBitmap);
			cartName.setText(item.cartName);
			zipcode.setText("Zipcode: " + item.zipcode);
			permit.setText("Permit: c" + item.permit);
		}
		setupOptions(rootView);
		Log.d("ViewCartFragment onCreateView", "view created");
		return rootView;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			View view = getView();
			
			ImageView map = null;
			if (view != null) {
				map = (ImageView) view.findViewById(R.id.viewCart_Map);
			}
			
			mapBitmap = (Bitmap) data.getParcelableExtra("mapBitmap");
			Log.d("onActivityResult ViewCartFragment", mapBitmap + "");
			
			if (map != null)
				map.setImageBitmap(mapBitmap);
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("onActivityCreated", "called" + getArguments());
		if (savedInstanceState == null) {
			if (getArguments().containsKey("ObjectCartListItem")) {
				ObjectCartListItem item = getArguments().getParcelable("ObjectCartListItem");
				String lat = item.lat;
				String lon = item.lon;
				
				String url = "http://maps.googleapis.com/maps/api/staticmap?" 
							+ "center=" + lat + "," 
							+ lon + "&zoom=18&size=640x250&scale=2&maptype=roadmap&markers=" + lat + "," + lon;
				
				getMapBitmap(url);
			}
		} else {
			if (getActivity() != null) {
				
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
		outState.putParcelable("mapBitmap", mapBitmap);
		Log.d("onSaveInstanceState", "called");
	}
	
	public void getMapBitmap(String url) {
		ImageDownloaderTask task = new ImageDownloaderTask();
		StaticMapsTaskFragment fragment = new StaticMapsTaskFragment();
		
		task.setFragment(fragment);
		fragment.setTask(task);
		fragment.setTargetFragment(this, 1);
		fragment.execute(url);
	}
	
	private void setupOptions(View rootView) {
		ListView listView = (ListView) rootView.findViewById(R.id.viewCart_Options);
		
		ObjectViewCartItem[] options = new ObjectViewCartItem[4];
		options[0] = new ObjectViewCartItem(R.drawable.ic_action_directions, "Get Directions",
												R.drawable.ic_action_next_item);
		/* options[1] = new ObjectViewCartItem(R.drawable.ic_action_labels, "Menu",
												R.drawable.ic_action_next_item); */
		options[1] = new ObjectViewCartItem(R.drawable.ic_action_edit, "Write a Review",
												R.drawable.ic_action_next_item);
		options[2] = new ObjectViewCartItem(R.drawable.ic_action_new_picture, "Add Photo",
												R.drawable.ic_action_next_item);
		options[3] = new ObjectViewCartItem(R.drawable.ic_action_about, "More Info",
												R.drawable.ic_action_next_item);
		
		ViewCartAdapter adapter = new ViewCartAdapter(getActivity(), R.layout.listview_viewcart_row, options);
		listView.setAdapter(adapter);
		Log.d("setupOptions", "method completed");
	}
}

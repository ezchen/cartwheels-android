package com.cartwheels;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cartwheels.custom_views.RatingView;
import com.cartwheels.tasks.CheckinTask;
import com.cartwheels.tasks.DefaultGetJsonAsyncTask;
import com.cartwheels.tasks.DefaultPostJsonAsyncTask;
import com.cartwheels.tasks.DefaultTaskFragment;
import com.cartwheels.tasks.ImageDownloaderTask;
import com.cartwheels.tasks.StaticMapsTaskFragment;
import com.cartwheels.tasks.UploadPhotoTask;
import com.squareup.picasso.Picasso;

public class ViewCartFragment extends Fragment implements OnItemClickListener {

	private static final int REQUEST_IMAGE_CAPTURE = 0;
	private ObjectCartListItem item;
	private Bitmap mapBitmap;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FragmentManager manager = getFragmentManager();
		
		String[] tags = new String[2];
		tags[0] = "UploadPhoto";
		tags[1] = "Checkin";
		
		HashMap<String, Integer> fragmentInfo = new HashMap<String, Integer>();
		fragmentInfo.put("UploadPhoto", 6);
		fragmentInfo.put("Checkin", 7);
		
		for (String tag : fragmentInfo.keySet()) {
			Fragment fragment = manager.findFragmentByTag(tag);
			
			if (fragment != null) {
				fragment.setTargetFragment(this, fragmentInfo.get(tag));
			}
		}
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_view_cart, container, false);
		
		TextView cartName = (TextView) rootView.findViewById(R.id.viewCart_Name);
		TextView reviews = (TextView) rootView.findViewById(R.id.viewCart_NumberOfReviews);
		RatingView rating = (RatingView) rootView.findViewById(R.id.viewCart_Rating);
		TextView zipcode = (TextView) rootView.findViewById(R.id.viewCart_Zipcode);
		TextView permit = (TextView) rootView.findViewById(R.id.viewCart_Permit);
		ImageView cartPicture = (ImageView) rootView.findViewById(R.id.viewCart_CartPicture);
		ImageView map = (ImageView) rootView.findViewById(R.id.viewCart_Map);
		
		if (savedInstanceState == null) {
			Bundle arguments = getArguments();
			
			if (arguments != null)
				item = arguments.getParcelable("ObjectCartListItem");
			
			if (item != null) {
			Picasso.with(getActivity()).load(item.bitmapUrl).into(cartPicture);

			if (mapBitmap != null)
				map.setImageBitmap(mapBitmap);
			
			cartName.setText(item.cartName);
			zipcode.setText("Zipcode: " + item.zipcode);
			permit.setText("Permit: c" + item.permit);
			rating.setRating(item.rating);
			}
			
			//Toast.makeText(getActivity(), item.toString(), Toast.LENGTH_SHORT).show();
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
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
			if (data != null && data.hasExtra("data")) {
				Bundle extras = data.getExtras();
		        Bitmap imageBitmap = (Bitmap) extras.get("data");
		        Log.d("onActivityResult ViewCartFragment", "bitmap: " + imageBitmap);
		        sendBitmap(imageBitmap);
			}
		}
		if (data.hasExtra("mapBitmap") && resultCode == Activity.RESULT_OK) {
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
	
	@Override
	public void onAttach(Activity activity) {
		if (!(activity instanceof LocationActivity)) {
			throw new IllegalStateException("Activity must implement TaskCallbacks");
		}
		
		super.onAttach(activity);
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
		outState.putParcelable("CartItem", item);
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
		
		ObjectViewCartItem[] options = new ObjectViewCartItem[5];
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
		options[4] = new ObjectViewCartItem(R.drawable.ic_action_labels, "Claim Cart",
												R.drawable.ic_action_next_item);
		ViewCartAdapter adapter = new ViewCartAdapter(getActivity(), R.layout.listview_viewcart_row, options);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		Log.d("setupOptions", "method completed");
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		
		Intent intent;
		switch(position) {
			case 0:
				Location location = ((LocationActivity)getActivity()).getLastLocation();
				
				if (location != null) {
					String currentLat = location.getLatitude() + "";
					String currentLon = location.getLongitude() + "";
					
					String cartLat = item.lat;
					String cartLon = item.lon;
					
					String url = "http://maps.google.com/maps?saddr=" +
							currentLat + "," + currentLon + "&daddr=" + cartLat + "," + cartLon;
					intent = new Intent(android.content.Intent.ACTION_VIEW,
							Uri.parse(url));
					startActivity(intent);
				} else {
					
				}
				break;
			case 1:
				intent = new Intent(getActivity(), WriteReviewActivity.class);
				intent.putExtra("ObjectCartListItem", item);
				startActivity(intent);
				break;
			case 2:
				takePicture();
				break;
			case 3:
				checkin();
				break;
			case 4:
				claimCart();
				break;
		}
	}
	
	
	public void takePicture() {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
	        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	    }
	}
	
	private void sendBitmap(Bitmap bitmap) {
		DefaultTaskFragment<UploadPhotoTask, ViewCartFragment, Boolean> fragment =
				new DefaultTaskFragment<UploadPhotoTask, ViewCartFragment, Boolean>(6);
		
		UploadPhotoTask asyncTask = new UploadPhotoTask();
		
		// set up query parameters
		SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", Activity.MODE_PRIVATE);
		String auth_token = preferences.getString("AuthToken", "");
		String email = preferences.getString("email", "");
		String target_id = item.cartId + "";
		String target_type = "Cart";
		// encode bitmap
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		Log.d("sendBitmap", bitmap + "");
		byte[] bitmapdata = stream.toByteArray();
		String encoded_image = Base64.encodeToString(bitmapdata, Base64.DEFAULT);
		
		Log.d("sendBitmap", encoded_image);
		// put values in asynctask
		asyncTask.put("auth_token", auth_token);
		asyncTask.put("email", email);
		
		asyncTask.setInnerKey("photo");
		asyncTask.putInner("encoded_image", encoded_image);
		
		fragment.setTask(asyncTask);
		asyncTask.setFragment(fragment);
		fragment.show(getFragmentManager(), "UploadPhoto");
		fragment.setTargetFragment(this, 6);
		
		String url;
		url = "http://cartwheels.us/carts/" + target_id + "/photos";
		fragment.execute(url);
	}
	
	private void checkin() {
		DefaultTaskFragment<CheckinTask, ViewCartFragment, Boolean> fragment =
				new DefaultTaskFragment<CheckinTask, ViewCartFragment, Boolean>(7);
		
		CheckinTask asyncTask = new CheckinTask();
		
		// set up query parameters
		SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", Activity.MODE_PRIVATE);
		String auth_token = preferences.getString("AuthToken", "");
		String email = preferences.getString("email", "");
		String target_id = item.cartId + "";
		
		String innerKey = "checkin";
		
		// location query parameters
		Location location = ((LocationActivity)getActivity()).getLastLocation();
		String lat = location.getLatitude() + "";
		String lon = location.getLongitude() + "";
		
		asyncTask.put("auth_token", auth_token);
		asyncTask.put("email", email);
		
		asyncTask.setInnerKey(innerKey);
		asyncTask.putInner("lat", lat);
		asyncTask.putInner("lon", lon);
		
		fragment.setTask(asyncTask);
		asyncTask.setFragment(fragment);
		fragment.show(getFragmentManager(), "Checkin");
		
		fragment.setTargetFragment(this, 7);
		
		String url;
		url = "http://cartwheels.us/carts/" + target_id + "/checkins";
		fragment.execute(url);
	}
	
	private void claimCart() {
		
		SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", Activity.MODE_PRIVATE);
		String email = preferences.getString("email", "");
		String auth_token = preferences.getString("AuthToken", "");
		
		String permit_number = item.permit;
		
		String cartId = item.cartId;
		
		String[] path = new String[3];
		path[0] = "carts";
		path[1] = cartId;
		path[2] = "claim";
		
		DefaultGetJsonAsyncTask asyncTask = new DefaultGetJsonAsyncTask("http", "cartwheels.us", path);
		
		asyncTask.put("email", email);
		asyncTask.put("auth_token", auth_token);
		asyncTask.put("permit_number", permit_number);
		
		DefaultTaskFragment<DefaultGetJsonAsyncTask, ViewCartFragment, Boolean> fragment =
				new DefaultTaskFragment<DefaultGetJsonAsyncTask, ViewCartFragment, Boolean>(10);
		fragment.setTask(asyncTask);
		asyncTask.setFragment(fragment);
		
		fragment.execute();
	}
}

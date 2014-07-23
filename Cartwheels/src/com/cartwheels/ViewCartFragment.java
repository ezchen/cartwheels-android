package com.cartwheels;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cartwheels.tasks.CheckinTask;
import com.cartwheels.tasks.DefaultGetJsonAsyncTask;
import com.cartwheels.tasks.DefaultPostJsonAsyncTask;
import com.cartwheels.tasks.DefaultTaskFragment;
import com.cartwheels.tasks.UploadPhotoTask;
import com.cartwheels.tasks.WriteReviewTask;
import com.cartwheels.tasks.WriteReviewTaskFragment;
import com.squareup.picasso.Picasso;

public class ViewCartFragment extends Fragment implements OnItemClickListener, OnClickListener {

	private static final int REQUEST_IMAGE_CAPTURE = 0;
	private static final int REQUEST_IMAGE_CAPTURE_MENU = 1;
	private ObjectCartListItem item;
	private Bitmap mapBitmap;
	
	private AlertDialog alert;
	private Bitmap dialogBitmap;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
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
		RatingBar rating = (RatingBar) rootView.findViewById(R.id.viewCart_CartRating);
		TextView zipcode = (TextView) rootView.findViewById(R.id.viewCart_Zipcode);
		ImageView cartPicture = (ImageView) rootView.findViewById(R.id.viewCart_CartPicture);
		ImageView map = (ImageView) rootView.findViewById(R.id.viewCart_Map);
		
		map.setOnClickListener(this);
		
		if (savedInstanceState == null) {
			Bundle arguments = getArguments();
			
			if (arguments != null)
				item = arguments.getParcelable("ObjectCartListItem");
			
			if (item != null) {
				Picasso.with(getActivity()).load(item.bitmapUrl).transform(new RoundedTransform(20, 3)).into(cartPicture);
			
				map.setImageBitmap(mapBitmap);
				String lat = item.lat;
				String lon = item.lon;
				
				String url = "https://maps.googleapis.com/maps/api/staticmap?" 
							+ "center=" + lat + "," 
							+ lon + "&zoom=18&size=640x250&scale=2&maptype=roadmap&markers=" + lat + "," + lon;
				Picasso.with(getActivity()).load(url).into(map);
			
				cartName.setText(item.cartName);
				zipcode.setText(item.address);
				rating.setRating(item.rating);
			}
			
			//Toast.makeText(getActivity(), item.toString(), Toast.LENGTH_SHORT).show();
		} else {
			// restore the fragment's state
			// CartItem
			if (savedInstanceState.containsKey("CartItem")) {
				item = (ObjectCartListItem) savedInstanceState.get("CartItem");
				Picasso.with(getActivity()).load(item.bitmapUrl).transform(new RoundedTransform(20, 3)).into(cartPicture);
				
				String lat = item.lat;
				String lon = item.lon;
				
				String url = "https://maps.googleapis.com/maps/api/staticmap?" 
							+ "center=" + lat + "," 
							+ lon + "&zoom=18&size=640x250&scale=2&maptype=roadmap&markers=" + lat + "," + lon;
				Picasso.with(getActivity()).load(url).into(map);
				Picasso.with(getActivity()).load(url).into(map);
			}
			
			cartName.setText(item.cartName);
			rating.setRating(item.rating);
			zipcode.setText(item.address);
		}
		setupOptions(rootView);
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
		} else if (requestCode == REQUEST_IMAGE_CAPTURE_MENU && resultCode == Activity.RESULT_OK) {
			if (data != null && data.hasExtra("data")) {
				Bundle extras = data.getExtras();
				Bitmap imageBitmap = (Bitmap) extras.get("data");
				
				ImageView imageView = (ImageView) alert.findViewById(R.id.menuItemImage);
				imageView.setImageBitmap(imageBitmap);
				dialogBitmap = imageBitmap;
			}
		} else if (requestCode == 41 && resultCode == Activity.RESULT_OK) {
			if (data != null) {
				if (data.getBooleanExtra("result", false)) {
					Toast.makeText(getActivity(), "Menu Updated", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity(), "Updating Menu Was Unsuccessful", Toast.LENGTH_SHORT).show();
				}
			}
		} else if (requestCode == 4 && resultCode == Activity.RESULT_OK) {
			if (data != null) {
				if (data.getBooleanExtra("success", false)) {
					Toast.makeText(getActivity(), "Review Uploaded", Toast.LENGTH_SHORT).show();
				} else {
					SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", Activity.MODE_PRIVATE);
					String userType = preferences.getString("LoginType", "");
					if ("owner".equals(userType)) {
						Toast.makeText(getActivity(), "You cannot make a review, you're an owner", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getActivity(), "Review Unsuccessful", Toast.LENGTH_SHORT).show();
					}
				}
			}
		} else if (requestCode == 7 && resultCode == Activity.RESULT_OK) {
			if (data != null) {
				if (data.getBooleanExtra("success", false)) {
					Toast.makeText(getActivity(), "Checkin Successful", Toast.LENGTH_SHORT).show();
					
					} else {
						SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", Activity.MODE_PRIVATE);
						String userType = preferences.getString("LoginType", "");
					if ("owner".equals(userType)) {
						Toast.makeText(getActivity(), "Owners cannot checkin", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getActivity(), "Checkin Unsuccessful", Toast.LENGTH_SHORT).show();
					}
				}
			}
		} else if (requestCode == 10 && resultCode == Activity.RESULT_OK) {
			if (data.getBooleanExtra("result", false)) {
				Toast.makeText(getActivity(), "Cart Claimed", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity(), "Wrong Permit Number. Stop Stealing", Toast.LENGTH_SHORT).show();
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
	
	private void setupOptions(View rootView) {
		ListView listView = (ListView) rootView.findViewById(R.id.viewCart_Options);
		
		ObjectViewCartItem[] options = new ObjectViewCartItem[6];
		options[0] = new ObjectViewCartItem(R.drawable.ic_action_directions, "Get Directions",
												R.drawable.ic_action_next_item);
		/* options[1] = new ObjectViewCartItem(R.drawable.ic_action_labels, "Menu",
												R.drawable.ic_action_next_item); */
		options[1] = new ObjectViewCartItem(R.drawable.ic_action_edit, "Write a Review",
												R.drawable.ic_action_next_item);
		options[2] = new ObjectViewCartItem(R.drawable.ic_action_new_picture, "Add Photo",
												R.drawable.ic_action_next_item);
		options[3] = new ObjectViewCartItem(R.drawable.ic_action_location_searching, "Check In",
												R.drawable.ic_action_next_item);
		options[4] = new ObjectViewCartItem(R.drawable.ic_action_labels, "Add Menu Item",
												R.drawable.ic_action_next_item);
		options[5] = new ObjectViewCartItem(R.drawable.ic_action_cart, "Claim This Cart", R.drawable.ic_action_next_item);
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
					
					String url = "https://maps.google.com/maps?saddr=" +
							currentLat + "," + currentLon + "&daddr=" + cartLat + "," + cartLon;
					intent = new Intent(android.content.Intent.ACTION_VIEW,
							Uri.parse(url));
					startActivity(intent);
				} else {
					Toast.makeText(getActivity(), "Location Services Are Off. Please Turn Them On.", Toast.LENGTH_SHORT).show();
				}
				break;
			case 1:
				showWriteReviewDialog();
				break;
			case 2:
				takePicture(REQUEST_IMAGE_CAPTURE);
				break;
			case 3:
				showCheckInDialog(item.cartName);
				break;
			case 4:
				showUpdateMenuDialog();
				break;
			case 5:
				showClaimDialog();
				break;
		}
	}
	
	private void showClaimDialog() {

		final EditText textView = new EditText(getActivity());
		textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		textView.setInputType(InputType.TYPE_CLASS_NUMBER);
		textView.setHint("Permit Number");
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(textView).setTitle("Claim Cart")
		       .setCancelable(false)
		       .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   String permit = textView.getText().toString();
		        	   claimCart(permit);
		           }
		       })
		       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void takePicture(int requestCode) {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
	        startActivityForResult(takePictureIntent, requestCode);
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
		url = "https://cartwheels.us/carts/" + target_id + "/photos";
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
		
		if (location == null) {
			Toast.makeText(getActivity(),
					"Location Services Are Off. You Must Have Location Services On To Checkin", Toast.LENGTH_SHORT).show();
			return;
		}
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
		url = "https://cartwheels.us/carts/" + target_id + "/checkins";
		fragment.execute(url);
	}
	
	private void claimCart(String permit_number) {
		
		SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", Activity.MODE_PRIVATE);
		String email = preferences.getString("email", "");
		String auth_token = preferences.getString("AuthToken", "");
		
		String cartId = item.cartId;
		
		String[] path = new String[3];
		path[0] = "carts";
		path[1] = cartId;
		path[2] = "claim";
		
		DefaultGetJsonAsyncTask asyncTask = new DefaultGetJsonAsyncTask("https", "cartwheels.us", path);
		
		asyncTask.put("email", email);
		asyncTask.put("auth_token", auth_token);
		asyncTask.put("permit_number", permit_number);
		
		DefaultTaskFragment<DefaultGetJsonAsyncTask, ViewCartFragment, Boolean> fragment =
				new DefaultTaskFragment<DefaultGetJsonAsyncTask, ViewCartFragment, Boolean>(10);
		fragment.setTask(asyncTask);
		asyncTask.setFragment(fragment);
		fragment.setTargetFragment(this, 10);
		fragment.show(getFragmentManager(), "Claim Cart");
		
		fragment.execute();
	}

	private void updateMenu(Bitmap bitmap, String name, String price) {
		SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", Activity.MODE_PRIVATE);
		String email = preferences.getString("email", "");
		String auth_token = preferences.getString("AuthToken", "");
		
		String cartId = item.cartId;
		String url = "https://cartwheels.us/carts/" + cartId + "/menu/items";
		
		String encodedImage = null;
		
		if (bitmap != null) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			Log.d("sendBitmap", bitmap + "");
			byte[] bitmapdata = stream.toByteArray();
			encodedImage = Base64.encodeToString(bitmapdata, Base64.DEFAULT);
		}
		
		DefaultPostJsonAsyncTask asyncTask = new DefaultPostJsonAsyncTask();
		DefaultTaskFragment<DefaultPostJsonAsyncTask, ViewCartFragment, Boolean> fragment =
				new DefaultTaskFragment<DefaultPostJsonAsyncTask, ViewCartFragment, Boolean>(41);
		
		asyncTask.put("email", email);
		asyncTask.put("auth_token", auth_token);
		
		asyncTask.setInnerKey("menu_item");
		asyncTask.putInner("price", price);
		asyncTask.putInner("name", name);
		asyncTask.putInner("encoded_image", encodedImage);
		
		fragment.setTask(asyncTask);
		asyncTask.setFragment(fragment);
		fragment.setTargetFragment(this, 41);
		fragment.show(getFragmentManager(), "UpdateMenu");
		fragment.execute(url);
	}
	
	private void showUpdateMenuDialog() {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edit_menu_item, null, false);
		
		final ImageView foodImage = (ImageView) view.findViewById(R.id.menuItemImage);
		final TextView foodName = (TextView) view.findViewById(R.id.menuItemName);
		final TextView price = (TextView) view.findViewById(R.id.menuItemPrice);
		foodImage.setOnClickListener(this);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(view)
		       .setCancelable(false)
		       .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   updateMenu(dialogBitmap, foodName.getText().toString(), price.getText().toString());
		           }
		       })
		       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		alert = builder.create();
		alert.show();
	}
	
	private void showCheckInDialog(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(message)
		       .setCancelable(false)
		       .setPositiveButton("Check In", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   checkin();
		           }
		       })
		       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void showWriteReviewDialog() {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_write_review, null, false);
		
		final RatingBar ratingBar = (RatingBar) view.findViewById(R.id.writeReviewRatingBar);
		final EditText editText = (EditText) view.findViewById(R.id.writeReviewText);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(view).setTitle("Write Review")
		       .setCancelable(false)
		       .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   float rating = ratingBar.getRating();
		        	   String text = editText.getText().toString();
		        	   writeReview(rating, text);
		           }
		       })
		       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		alert = builder.create();
		alert.show();
	}
	
	protected void writeReview(float rating, String text) {
		WriteReviewTask asyncTask = new WriteReviewTask();
		SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", Activity.MODE_PRIVATE);
		String email = preferences.getString("email", "");
		String auth_token = preferences.getString("AuthToken", "");

		asyncTask.put("email", email);
		asyncTask.put("auth_token", auth_token);
		asyncTask.put("review[rating]", ((int)rating) + "");
		asyncTask.put("review[text]", text);
		
		asyncTask.setCartId(item.cartId);
		
		WriteReviewTaskFragment fragment = new WriteReviewTaskFragment();
		fragment.setTask(asyncTask);
		fragment.setTargetFragment(this, 4);
		
		asyncTask.setFragment(fragment);
		
		fragment.execute();
		fragment.show(getFragmentManager(), "WriteReview");
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

	    menu.clear();
	    super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		if (id == R.id.viewCart_Map) {
			ArrayList<ObjectCartListItem> items = new ArrayList<ObjectCartListItem>();
			items.add(item);
			Intent intent = new Intent(getActivity(), MarkerActivity.class);
			intent.putExtra("ObjectCartListItems", items);
			startActivity(intent);
		} else if (id == R.id.menuItemImage) {
			takePicture(REQUEST_IMAGE_CAPTURE_MENU);
		}
	}
}

package com.cartwheels;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cartwheels.tasks.DefaultPostJsonAsyncTask;
import com.cartwheels.tasks.DefaultPutJsonAsyncTask;
import com.cartwheels.tasks.DefaultTaskFragment;
import com.cartwheels.tasks.GetOwnedCartsInfoTask;
import com.cartwheels.tasks.UpdateOwnedCartTask;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

public class DisplayOwnedCartsFragment extends ListFragment implements OnClickListener {

	ArrayList<ObjectCartListItem> items;
	
	int updatePosition;
	String updateName;
	String updatePermit;
	String updateDescription;
	AlertDialog alert;
	private Bitmap dialogBitmap;
	private ObjectCartListItem focusedItem;
	
	DisplayOwnedCartsExpandableAdapter adapter;
	SwingBottomInAnimationAdapter swingBottomInAnimation;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onAttach(Activity activity) {
		if (!(activity instanceof LocationActivity)) {
			throw new IllegalStateException("Activity must extend LocationActivity");
		}
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_display_owned_carts, container, false);
		updateUserInfo();
		return view;
	}
	
	private void updateUserInfo() {
		String[] path = new String[2];
		path[0] = "owners";
		path[1] = "data";
		
		UpdateOwnedCartTask asyncTask = new UpdateOwnedCartTask("https", "cartwheels.us", path,
												getActivity().getApplicationContext());
		DefaultTaskFragment<UpdateOwnedCartTask, DisplayOwnedCartsFragment, ArrayList<String>> fragment = 
				new DefaultTaskFragment<UpdateOwnedCartTask, DisplayOwnedCartsFragment, ArrayList<String>>(12) {
			@Override
			protected Intent getIntent(ArrayList<String> items) {
				Intent intent = new Intent();
				intent.putStringArrayListExtra("cartId", items);
				return intent;
			}
		};
		
		SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", Activity.MODE_PRIVATE);
		String email = preferences.getString("email", "");
		asyncTask.put("email", email);
		
		String auth_token = preferences.getString("AuthToken","");
		asyncTask.put("auth_token", auth_token);
		
		String offset = "0";
		asyncTask.put("offset", offset);
		
		String limit = "1";
		asyncTask.put("limit", limit);
		
		asyncTask.put("owner[email]", email);
		
		asyncTask.setFragment(fragment);
		fragment.setTask(asyncTask);
		
		fragment.setTargetFragment(this, 12);
		fragment.show(getFragmentManager(), "getOwnedCarts");
		fragment.execute();
	}
	
	@Override
	public void onActivityResult(int resultCode, int requestCode, Intent data) {
		Log.d("onActivityResult", resultCode + "");
		if (resultCode == 12 && requestCode == Activity.RESULT_OK) {
			ArrayList<String> cartId = data.getStringArrayListExtra("cartId");
			
			// start another async task that retrieves more info about each cart
			String[] path = new String[2];
			path[0] = "carts";
			path[1] = "data";
			
			GetOwnedCartsInfoTask asyncTask = new GetOwnedCartsInfoTask("https", "cartwheels.us", path, cartId,
													getActivity().getApplicationContext());
			
			
			SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", Activity.MODE_PRIVATE);
			String email = preferences.getString("email", "");
			String auth_token = preferences.getString("AuthToken", "");
			asyncTask.put("email", email);
			asyncTask.put("auth_token", auth_token);
			asyncTask.put("offset", "0");
			asyncTask.put("limit", "1");
			
			DefaultTaskFragment<GetOwnedCartsInfoTask, DisplayOwnedCartsFragment, ArrayList<ObjectCartListItem>> fragment =
					new DefaultTaskFragment<GetOwnedCartsInfoTask, DisplayOwnedCartsFragment, ArrayList<ObjectCartListItem>>(13) {
				@Override
				protected Intent getIntent(ArrayList<ObjectCartListItem> items) {
					Intent intent = new Intent();
					intent.putParcelableArrayListExtra("ObjectCartListItems", items);
					return intent;
				}
			};
			
			fragment.setTask(asyncTask);
			asyncTask.setFragment(fragment);
			fragment.setTargetFragment(this, 13);
			
			fragment.show(getFragmentManager(), "getOwnedCartsInfo");
			fragment.execute();
		} else if (resultCode == 13 && requestCode == Activity.RESULT_OK) {
			if (data != null) {
				ArrayList<ObjectCartListItem> items = data.getParcelableArrayListExtra("ObjectCartListItems");
				
				buildList(items);
			}
		} else if (resultCode == 20 && requestCode == Activity.RESULT_OK) {
	    	
    		SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", Activity.MODE_PRIVATE);
    		String email = preferences.getString("email", "");
    		String auth_token = preferences.getString("AuthToken", "");
    		DefaultTaskFragment<DefaultPutJsonAsyncTask, Fragment, Boolean> taskFragment =
    				new DefaultTaskFragment<DefaultPutJsonAsyncTask, Fragment, Boolean>(21);
    		
    		DefaultPutJsonAsyncTask asyncTask = new DefaultPutJsonAsyncTask(getActivity().getApplicationContext());
    		asyncTask.put("email", email);
    		asyncTask.put("auth_token", auth_token);
    		
    		int position = data.getIntExtra("position", -1);
    		
    		if (position >= 0 && position < items.size()) {
    			Toast.makeText(getActivity(), "Position: " + position, Toast.LENGTH_SHORT).show();
    			ObjectCartListItem item = items.get(position);
    			
    			Location location = ((LocationActivity)getActivity()).getLastLocation();
    			
    			if (location == null) {
    				Toast.makeText(getActivity(), "Location Services Are Off. Please Turn Them On.", Toast.LENGTH_SHORT).show();
    				return;
    			}
    			
    			String lat = location.getLatitude() + "";
    			String lon = location.getLongitude() + "";
    			
    			asyncTask.setInnerKey("cart");
    			asyncTask.putInner("name", item.cartName);
    			asyncTask.putInner("lat", lat);
    			asyncTask.putInner("lon", lon);
    			
        		
        		taskFragment.setTargetFragment(this, 21);
        		taskFragment.setTask(asyncTask);
        		asyncTask.setFragment(taskFragment);
        		
        		taskFragment.show(getFragmentManager(), "updateCartLocation");
        		taskFragment.execute("https://cartwheels.us/carts/" + item.cartId);
    		}
		} else if (resultCode == 21 && requestCode == Activity.RESULT_OK) {
			if (data.getBooleanExtra("result", false)) {
				Toast.makeText(getActivity(), "Cart Successfuly Updated", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
			}
		} else if (resultCode == 30 && requestCode == Activity.RESULT_OK) {
			
	    	
    		SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", Activity.MODE_PRIVATE);
    		String email = preferences.getString("email", "");
    		String auth_token = preferences.getString("AuthToken", "");
    		DefaultTaskFragment<DefaultPutJsonAsyncTask, Fragment, Boolean> taskFragment =
    				new DefaultTaskFragment<DefaultPutJsonAsyncTask, Fragment, Boolean>(31);
    		
    		DefaultPutJsonAsyncTask asyncTask = new DefaultPutJsonAsyncTask(getActivity().getApplicationContext());
    		asyncTask.put("email", email);
    		asyncTask.put("auth_token", auth_token);
    		
    		updatePosition = data.getIntExtra("position", -1);
    		
    		updateName = data.getStringExtra("CartName");
    		updatePermit = data.getStringExtra("CartPermit");
    		updateDescription = data.getStringExtra("CartDescription");
    		if (updatePosition >= 0 && updatePosition < items.size()) {
    			
    			ObjectCartListItem item = items.get(updatePosition);
    			asyncTask.setInnerKey("cart");
    			asyncTask.putInner("name", updateName);
    			asyncTask.putInner("permit", updatePermit);
    			asyncTask.putInner("description", updateDescription);
    			
        		taskFragment.setTargetFragment(this, 31);
        		taskFragment.setTask(asyncTask);
        		asyncTask.setFragment(taskFragment);
        		
        		taskFragment.show(getFragmentManager(), "updateCartLocation");
        		taskFragment.execute("https://cartwheels.us/carts/" + item.cartId);
    		}
		} else if (resultCode == 31 && requestCode == Activity.RESULT_OK) {
			if (data.getBooleanExtra("result", false)) {
				Toast.makeText(getActivity(), "Cart Successfuly Updated", Toast.LENGTH_SHORT).show();
				ObjectCartListItem item = items.get(updatePosition);
				item.cartName = updateName;
				item.permit = updatePermit;
				item.description = updateDescription;
				adapter.notifyDataSetChanged();
				swingBottomInAnimation.notifyDataSetChanged();
			} else {
				Toast.makeText(getActivity(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
			}
		} else if (resultCode == 50 && requestCode == Activity.RESULT_OK) {
			if (data != null) {
				focusedItem = data.getParcelableExtra("ObjectCartListItem");
				if (focusedItem != null)
					showUpdateMenuDialog();
			}
		} else if (resultCode == 60 && requestCode == Activity.RESULT_OK) {
			Bundle extras = data.getExtras();
			Bitmap imageBitmap = (Bitmap) extras.get("data");
			
			ImageView imageView = (ImageView) alert.findViewById(R.id.menuItemImage);
			imageView.setImageBitmap(imageBitmap);
			dialogBitmap = imageBitmap;
		}
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

	protected void updateMenu(Bitmap bitmap, String name,
			String price) {
		SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", Activity.MODE_PRIVATE);
		String email = preferences.getString("email", "");
		String auth_token = preferences.getString("AuthToken", "");
		
		String cartId = focusedItem.cartId;
		String url = "https://cartwheels.us/carts/" + cartId + "/menu/items";
		
		String encodedImage = null;
		
		if (bitmap != null) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			Log.d("sendBitmap", bitmap + "");
			byte[] bitmapdata = stream.toByteArray();
			encodedImage = Base64.encodeToString(bitmapdata, Base64.DEFAULT);
		}
		
		DefaultPostJsonAsyncTask asyncTask = new DefaultPostJsonAsyncTask(getActivity().getApplicationContext());
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

	private void buildList(ArrayList<ObjectCartListItem> items) {
		this.items = items;
		adapter = 
				new DisplayOwnedCartsExpandableAdapter(getActivity(), R.layout.listview_expandable_owned_carts,
						R.id.activity_expandablelistitem_card_title, R.id.activity_expandablelistitem_card_content, items);
		swingBottomInAnimation = new SwingBottomInAnimationAdapter(adapter);
		swingBottomInAnimation.setInitialDelayMillis(300);
		
		ListView listView = (ListView) getActivity().findViewById(android.R.id.list);
		swingBottomInAnimation.setAbsListView(listView);
		setListAdapter(swingBottomInAnimation);
	}

	@Override
	public void onClick(View arg0) {
		takePicture(60);
	}
	
	public void takePicture(int requestCode) {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
	        startActivityForResult(takePictureIntent, requestCode);
	    }
	}
	
	@Override
	public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

	    switch (item.getItemId()) {
	    case R.id.action_refresh:
	    	updateUserInfo();
	    	return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
}

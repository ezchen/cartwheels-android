package com.cartwheels;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * A placeholder fragment containing a simple view.
 */
public class DisplayCartsFragment extends Fragment
											implements OnItemClickListener {

	private ListView displayCarts;
	private ObjectCartListItem[] items;
	//ObjectCartListItem items[];
	
	public DisplayCartsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		displayCarts = (ListView) inflater.inflate(R.layout.fragment_search,
				container, false);
		
		displayCarts.setOnItemClickListener(this);
		
		Log.d("DisplayCartsFragment onCreateView", "fragment created");
		
		
		return displayCarts;
	}
	
	public void buildList(JSONArray jsonArray) {
		try {
			Log.d("length", jsonArray.length() + "");
			items = new ObjectCartListItem[jsonArray.length()];
			
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				
				String cartName = json.getString(getResources().getString(R.string.TAGS_NAME));
				String cartZipcode = json.getString(getResources().getString(R.string.TAGS_ZIP_CODE));
				String cartPermit = json.getString(getResources().getString(R.string.TAGS_PERMIT_NUMBER));
				
				JSONArray arrayBitmapUrl = json.getJSONArray(getResources().getString(R.string.TAGS_PHOTOS));
				
				String bitmapUrl = null;
				if (arrayBitmapUrl.length() > 0) {
					JSONObject jsonBitmapUrl = arrayBitmapUrl.getJSONObject(0);
					bitmapUrl = jsonBitmapUrl.getString(getResources().getString(R.string.TAGS_URL_THUMB));
				}
				
				ObjectCartListItem cartListItem = new ObjectCartListItem(bitmapUrl, cartName,
														cartZipcode, cartPermit);
				
				Log.d("cart list item", cartListItem.toString());
				items[i] = cartListItem;
			}
			
			Log.d("All cart list items", Arrays.toString(items));
			Log.d("BuildList", "objects added");
			
			if (getActivity() == null) {
				Log.d("getActivity", "null");
			}
			ArrayAdapter<ObjectCartListItem> adapter = new CartListItemAdapter(getActivity(), R.layout.listview_cart_row, items);
			
			displayCarts.setAdapter(adapter);
			
			
		} catch (NullPointerException e) {
			e.printStackTrace();
			Log.e("NullPointerException", e.toString());
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("JSONException", e.toString());
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("Exception", e.toString());
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(getActivity(), ViewCartActivity.class);
		intent.putExtra("ObjectCartListItem", items[position]);
		startActivity(intent);
	}
}
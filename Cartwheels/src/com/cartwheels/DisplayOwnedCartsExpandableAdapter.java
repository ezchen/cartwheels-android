package com.cartwheels;

import java.io.ObjectInputStream.GetField;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.itemmanipulation.ExpandableListItemAdapter;
import com.squareup.picasso.Picasso;

public class DisplayOwnedCartsExpandableAdapter extends
		ExpandableListItemAdapter<ObjectCartListItem> implements OnClickListener {

	private final Context context;
	
	public DisplayOwnedCartsExpandableAdapter(Context context, int layoutResId,
			int titleParentResId, int contentParentResId,
			List<ObjectCartListItem> items) {
		super(context, layoutResId, titleParentResId, contentParentResId, items);
		setActionViewResId(titleParentResId);
		setLimit(1);
		this.context = context;
	}
	
	@Override
	public View getTitleView(final int position, final View convertView, final ViewGroup parent) {
		View view = convertView;
		
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.listview_title_owned_carts, parent, false);
		}
		
		ImageView cartPicture = (ImageView) view.findViewById(R.id.ownedCartPicture);
		TextView cartName = (TextView) view.findViewById(R.id.ownedCartName);
		
		ObjectCartListItem item = getItem(position);
		Picasso.with(context).load(item.bitmapUrl).into(cartPicture);
		cartName.setText(item.cartName);
		
		return view;
	}
	
	@Override
	public View getContentView(final int position, final View convertView, final ViewGroup parent) {
		View view = convertView;
		
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.listview_content_owned_carts, parent, false);
		}
		
		ObjectCartListItem item = getItem(position);
		
		RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ownedCartRating);
		TextView zipCode = (TextView) view.findViewById(R.id.ownedCartZipcode);
		TextView permit = (TextView) view.findViewById(R.id.ownedCartPermit);
		TextView description = (TextView) view.findViewById(R.id.ownedCartDescription);
		
		ratingBar.setRating(item.rating);
		zipCode.setText(item.zipcode);
		permit.setText(item.permit);
		description.setText(item.description);
		
		// Inflate options view
		View updateLocationView = view.findViewById(R.id.ownedCartUpdateLocationOption);
		
		
		
		ImageView updateLocationImageView =
				(ImageView) updateLocationView.findViewById(R.id.viewCart_OptionIcon);
		
		TextView updateLocationTextView =
				(TextView) updateLocationView.findViewById(R.id.viewCart_OptionName);
		updateLocationImageView.setImageResource(R.drawable.ic_action_location_found);
		updateLocationTextView.setText("Update Location");
		updateLocationView.setTag(position);
		updateLocationView.setOnClickListener(this);
		
		View editCartView = view.findViewById(R.id.ownedCartEditOption);
		editCartView.setTag(position);
		ImageView editImageView =
				(ImageView) editCartView.findViewById(R.id.viewCart_OptionIcon);
		TextView editTextView =
				(TextView) editCartView.findViewById(R.id.viewCart_OptionName);
		editImageView.setImageResource(R.drawable.ic_action_edit);
		editTextView.setText("Edit Cart");
		editCartView.setOnClickListener(this);
		
		return view;
	}

	@Override
	public void onClick(View v) {
		Object obj = v.getTag();
		Integer position = (Integer) obj;
		
		ObjectCartListItem item = getItem(position);
		
		switch (v.getId()) {
			case R.id.ownedCartUpdateLocationOption:
				showDialog("Do you want to update location for \n" + item.cartName, position);
				break;
			case R.id.ownedCartEditOption:
				showDialog("Change this wtf", 0);
				break;
		}
	}
	
	private void updateLocation(final int position) {
		Intent intent = new Intent("start.fragment.action");
		intent.putExtra("position", position);
		context.sendBroadcast(intent);
	}
	
	public void showDialog(String message, final int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message)
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   updateLocation(position);
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
}

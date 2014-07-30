package com.cartwheels;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nhaarman.listviewanimations.itemmanipulation.ExpandableListItemAdapter;
import com.squareup.picasso.Picasso;

public class DisplayOwnedCartsExpandableAdapter extends
		ExpandableListItemAdapter<ObjectCartListItem> implements OnClickListener {

	private final Context context;
	private Picasso picasso;
	
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
		
		if (picasso == null) {
			TrustedPicassoBuilder builder = new TrustedPicassoBuilder(context);
			picasso = builder.buildDefault();
		}
		picasso.load(item.bitmapUrl).centerCrop().into(cartPicture);
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
		if (item.address != null && !item.address.equals("null"))
			zipCode.setText(item.address);
		else
			zipCode.setText("item unavailable");
		
		if (item.permit != null)
		permit.setText(item.permit);
		
		if (item.description != null && !item.description.equals("null")){
			description.setText(item.description);
		}
		
		// Inflate options view
		View viewCart = view.findViewById(R.id.ownedCartViewCartOption);
		ImageView viewCartImage = (ImageView) viewCart.findViewById(R.id.viewCart_OptionIcon);
		viewCartImage.setImageResource(R.drawable.ic_action_about);
		
		TextView viewCartText =
				(TextView) viewCart.findViewById(R.id.viewCart_OptionName);
		viewCartText.setText("More Info");
		viewCart.setTag(position);
		viewCart.setOnClickListener(this);
		
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
		
		View addMenuView = view.findViewById(R.id.ownedCartUpdateMenuOption);
		ImageView addMenuImageView =
				(ImageView) addMenuView.findViewById(R.id.viewCart_OptionIcon);
		TextView addMenuTextView =
				(TextView) addMenuView.findViewById(R.id.viewCart_OptionName);
		addMenuImageView.setImageResource(R.drawable.ic_action_labels);
		addMenuTextView.setText("Add Menu Item");
		addMenuView.setTag(position);
		addMenuView.setOnClickListener(this);
		
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
				showEditCartDialog("Edit Cart", position);
				break;
			case R.id.ownedCartUpdateMenuOption:
				updateMenu(item);
				break;
			case R.id.ownedCartViewCartOption:
				Intent intent = new Intent("start.fragment.viewCart");
				intent.putExtra("ObjectCartListItem", item);
				context.sendBroadcast(intent);
				break;
		}
	}

	
	public void showEditCartDialog(String message, final int position) {
		View view = LayoutInflater.from(context).inflate(R.layout.edit_cart_dialog_body, null, false);
		
		final EditText editCartName = (EditText) view.findViewById(R.id.editCartNameText);
		final EditText editPermit = (EditText) view.findViewById(R.id.editCartPermitText);
		final EditText editDescription = (EditText) view.findViewById(R.id.editCartDescription);
		
		ObjectCartListItem item = getItem(position);
		editCartName.setText(item.cartName);
		editPermit.setText(item.permit);
		editDescription.setText(item.description);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view).setTitle(message)
		       .setCancelable(false)
		       .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   String cartName = editCartName.getText().toString();
		        	   String cartPermit = editPermit.getText().toString();
		        	   String cartDescription = editDescription.getText().toString();
		        	   editCart(cartName, cartPermit, cartDescription, position);
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
	
	protected void editCart(String cartName, String cartPermit,
			String cartDescription, int position) {
		Intent intent = new Intent("start.fragment.editCart");
		intent.putExtra("CartName", cartName);
		intent.putExtra("CartPermit", cartPermit);
		intent.putExtra("CartDescription", cartDescription);
		intent.putExtra("position", position);
		context.sendBroadcast(intent);
	}
	
	protected void updateMenu(ObjectCartListItem item) {
		Intent intent = new Intent("start.fragment.updateMenu");
		intent.putExtra("ObjectCartListItem", item);
		context.sendBroadcast(intent);
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

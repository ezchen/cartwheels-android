package com.cartwheels;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ViewCartPhotosAdapter extends BaseAdapter {

	Context context;
	int count;
	String[] imageUrls;
	
	public ViewCartPhotosAdapter(Context context, String[] imageUrls) {
		this.context = context;
		this.imageUrls = imageUrls;
		this.count = imageUrls.length;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String imageUrl = imageUrls[position];
		
		Log.d("imageUrl", "" + imageUrl);
		ImageView imageView;
		if (convertView == null) {
			imageView = new ImageView(context);
			imageView.setLayoutParams(new GridView.LayoutParams(210, 210));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		} else {
			imageView = (ImageView) convertView;
		}
		Picasso.with(context).load(imageUrl).into(imageView);
		
		return imageView;
	}

	@Override
	public int getCount() {
		return imageUrls.length;
	}

	@Override
	public Object getItem(int position) {
		return imageUrls[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	
}

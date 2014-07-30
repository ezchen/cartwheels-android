package com.cartwheels;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nhaarman.listviewanimations.ArrayAdapter;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class CartListItemAdapter extends ArrayAdapter<ObjectCartListItem> {
	private Context context;
	private int layoutResourceID;
	private Picasso picasso;
	
	public CartListItemAdapter(int layoutResourceID, Context context, ObjectCartListItem[] items) {
		super(Arrays.asList(items), false);
		
		
		this.context = context;
		this.layoutResourceID = layoutResourceID;
		picasso = buildPicasso();
	}

	public CartListItemAdapter(int layoutResourceId, Activity context,
			ObjectCartListItem[] items) {
		super(Arrays.asList(items), false);
		
		
		this.context = context;
		this.layoutResourceID = layoutResourceID;
		picasso = buildPicasso();
	}
	
	public CartListItemAdapter(int layoutResourceId, Context context,
				ArrayList<ObjectCartListItem> items) {
		super(items, false);
		
		
		this.context = context;
		this.layoutResourceID = layoutResourceId;
		picasso = buildPicasso();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View listItem = convertView;
		/*
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		listItem = inflater.inflate(layoutResourceID, parent, false);
*/
		
		listItem = LayoutInflater.from(context).inflate(R.layout.listview_cart_row, parent, false);
		ObjectCartListItem item = getItem(position);

		ImageView cartPicture = (ImageView) listItem.findViewById(R.id.cartPicture);

		if (picasso != null) {
			picasso.load(item.bitmapUrl).transform(new RoundedTransform(10, 3)).into(cartPicture);
		} else {
			picasso = buildPicasso();
			picasso.load(item.bitmapUrl).transform(new RoundedTransform(10, 3)).into(cartPicture);
		}
		
		RatingBar ratingBar = (RatingBar) listItem.findViewById(R.id.cartRating);
		ratingBar.setRating(item.rating);
		
		TextView cartName = (TextView) listItem.findViewById(R.id.cartName);
		cartName.setText(item.cartName);
		
		TextView cartZipcode = (TextView) listItem.findViewById(R.id.cartZipcode);
		
		if (item.address != null && !item.address.equals("null"))
			cartZipcode.setText(item.address);
		else
			cartZipcode.setText("Address Unavailable");
		
		TextView cartDescription = (TextView) listItem.findViewById(R.id.cartDescription);
		if (item.description != null && !item.description.equals("null")) {
			cartDescription.setText(item.description);
		}

		return listItem;
	}
	
	private Picasso buildPicasso() {
		TrustedPicassoBuilder builder = new TrustedPicassoBuilder(context);
		return builder.build();
	}

	private OkHttpClient createOkHttpClient() {
		OkHttpClient client = new OkHttpClient();
		KeyStore keyStore = readKeyStore(); //your method to obtain KeyStore

		try {
			SSLContext sslContext = SSLContext.getInstance("SSL");
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(keyStore);
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keyStore, "ct-weel7t6Axa#u".toCharArray());
			sslContext.init(keyManagerFactory.getKeyManagers(),trustManagerFactory.getTrustManagers(), new SecureRandom());
			client.setSslSocketFactory(sslContext.getSocketFactory());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		
		return client;
	}

	private KeyStore readKeyStore() {
	    KeyStore ks = null;
		try {
			ks = KeyStore.getInstance(KeyStore.getDefaultType());
			InputStream in = context.getResources().openRawResource(R.raw.cartwheels_keystore);
			try {
				ks.load(in, "ct-weel7t6Axa#u".toCharArray());
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CertificateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    return ks;
	}
}

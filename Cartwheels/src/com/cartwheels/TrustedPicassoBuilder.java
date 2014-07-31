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

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.Builder;

public class TrustedPicassoBuilder extends Builder {

	private Context context;
	
	public TrustedPicassoBuilder(Context context) {
		super(context);
		this.context = context;
	}

	public Picasso buildDefault() {
		Picasso.Builder builder = new Picasso.Builder(context);
		
		// Create the OkHttpClient to accept the keystore
		OkHttpClient client = createOkHttpClient();
		OkHttpDownloader downloader = new OkHttpDownloader(client);
		builder.downloader(downloader);
		
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
			Log.d("Keystore Type", KeyStore.getDefaultType());
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

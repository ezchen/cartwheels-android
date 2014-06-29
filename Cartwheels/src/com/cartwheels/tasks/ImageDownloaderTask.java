package com.cartwheels.tasks;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;


public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
	
	StaticMapsTaskFragment fragment;
	
	@Override
	protected void onPreExecute(){}
	
	@Override
	protected Bitmap doInBackground(String... params) {
		return downloadBitmap(params[0]);
	}
	
	@Override
	protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
            Log.d("onPostExecute", "cancelled");
        }
        Log.d("onPostExecute", "entered");
        
        fragment.onTaskFinished(bitmap);
        super.onPostExecute(bitmap);
	}
	
	public static Bitmap downloadBitmap(String url) {
        final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        final HttpGet getRequest = new HttpGet(url);
        
        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("ImageDownloader", "Error " + statusCode
                        + " while retrieving bitmap from " + url);
                return null;
            }
 
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            // Could provide a more explicit error message for IOException or
            // IllegalStateException
            getRequest.abort();
            Log.w("ImageDownloader", "Error while retrieving bitmap from " + url);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return null;
    }
	
	
	public void setFragment(StaticMapsTaskFragment fragment) {
		this.fragment = fragment;
	}

}

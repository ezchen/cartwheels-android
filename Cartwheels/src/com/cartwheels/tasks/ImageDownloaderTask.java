package com.cartwheels.tasks;

import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.cartwheels.R;


public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {

	private final WeakReference imageViewReference;
	
	public ImageDownloaderTask(ImageView imageView) {
		imageViewReference = new WeakReference(imageView);
	}
	
	@Override
	protected Bitmap doInBackground(String... params) {
		return downloadBitmap("http://png-1.findicons.com/files/icons/1579/devine/256/cart.png");
	}
	
	@Override
	protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
            Log.d("onPostExecute", "cancelled");
        }
        Log.d("onPostExecute", "entered");
 
        if (imageViewReference != null) {
            ImageView imageView = (ImageView) imageViewReference.get();
            if (imageView != null) {
 
                if (bitmap != null) {
                	Log.d("onPostExecute", "bitmap set");
                    imageView.setImageBitmap(bitmap);
                } else {
                    imageView.setImageDrawable(imageView.getContext().getResources()
                            .getDrawable(R.drawable.profile));
                }
            }
 
        }
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

}

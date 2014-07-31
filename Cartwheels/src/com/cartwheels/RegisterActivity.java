package com.cartwheels;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cartwheels.tasks.DefaultTaskFragment;
import com.cartwheels.tasks.RegisterTask;

public class RegisterActivity extends Activity {

	
	
	private SharedPreferences preferences;
	private String pictureName;
	private String userEmail;
	private String userFirstName;
	private String userLastName;
	private String userPassword;
	private String userPasswordConfirmation;
	private String userZipcode;
	
	/* Photo capturing variables */
	static final int REQUEST_IMAGE_CAPTURE = 1;
	String currentPhotoPath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		Intent intent = getIntent();
		if (savedInstanceState == null) {
			if (intent.getBooleanExtra("user", false)) {
					getFragmentManager().beginTransaction()
							.add(R.id.container, new registerUserFragment()).commit();
			} else {
				getFragmentManager().beginTransaction()
							.add(R.id.container, new registerOwnerFragment()).commit();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/*
	 * Methods used to handle the picture capture
	 */
	public void takePicture(View view) {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	    }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	}
	
	/*
	 * Create the image
	 */
	private File createImageFile(String username) throws IOException {
	    // Create an image file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "com_cartwheels_" + timeStamp + "_" + username;
	    
	    pictureName = imageFileName;
	    
	    File storageDir = Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_PICTURES);
	    File image = File.createTempFile(
	        imageFileName,  /* prefix */
	        ".jpg",         /* suffix */
	        storageDir      /* directory */
	    );

	    // Save a file: path for use with ACTION_VIEW intents
	    currentPhotoPath = "file:" + image.getAbsolutePath();
	    return image;
	}
	

	/*
	 * fragment used for people who want to register as a user
	 */
	public static class registerUserFragment extends AbstractRegisterFragment {

		private final static String REGISTER_API_ENDPOINT_URL = "https://cartwheels.us/mobile/registrations";
		
		public registerUserFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_register,
					container, false);
			
			Button button = (Button) rootView.findViewById(R.id.registerButton);
			button.setOnClickListener(this);
			return rootView;
		}
		
		@Override
		protected void execute(
				DefaultTaskFragment<RegisterTask, AbstractRegisterFragment, String> fragment) {
			fragment.execute(REGISTER_API_ENDPOINT_URL);
		}
	}
	
	/*
	 * fragment used for people who want to register as a cart
	 */
	public static class registerOwnerFragment extends AbstractRegisterFragment {
		
		private final static String REGISTER_API_ENDPOINT_URL = "https://cartwheels.us/mobile/owners/registrations";
		
		public registerOwnerFragment() {
			
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_register_owner,
					container, false);
			
			Button button = (Button) rootView.findViewById(R.id.registerOwnerButton);
			button.setOnClickListener(this);
			return rootView;
		}
		
		@Override
		protected void execute(
				DefaultTaskFragment<RegisterTask, AbstractRegisterFragment, String> fragment) {
			fragment.execute(REGISTER_API_ENDPOINT_URL);
		}
	}

}

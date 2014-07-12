package com.cartwheels;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cartwheels.tasks.DefaultTaskFragment;
import com.cartwheels.tasks.LoginTask;
import com.savagelook.android.UrlJsonAsyncTask;

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
	public void register(View view) {
	    EditText userEmailField = (EditText) findViewById(R.id.userEmail);
	    userEmail = userEmailField.getText().toString();
	    
	    EditText userNameField = (EditText) findViewById(R.id.firstName);
	    userFirstName = userNameField.getText().toString();
	    
	    EditText userLastNameField = (EditText) findViewById(R.id.lastName);
	    userLastName = userLastNameField.getText().toString();
	    
	    EditText userPasswordField = (EditText) findViewById(R.id.userPassword);
	    userPassword = userPasswordField.getText().toString();
	    
	    EditText userPasswordConfirmationField = (EditText) findViewById (R.id.userPasswordConfirmation);
	    userPasswordConfirmation = userPasswordConfirmationField.getText().toString();
	    
	    EditText userZipcodeField = (EditText) findViewById(R.id.userZipcode);
	    userZipcode = userZipcodeField.getText().toString();

	    if (userEmail.length() == 0 || userFirstName.length() == 0 ||
	    		userLastName.length() == 0 || userPassword.length() == 0 ||
	    		userPasswordConfirmationField.length() == 0 || userZipcodeField.length() == 0) {
	    	
	        // input fields are empty
	        Toast.makeText(this, "Please complete all the fields",
	            Toast.LENGTH_LONG).show();
	        return;
	        
	    } else {
	            // everything is ok!
	            RegisterTask registerTask = new RegisterTask(RegisterActivity.this);
	            registerTask.setMessageLoading("Registering new account...");
	            registerTask.execute(REGISTER_API_ENDPOINT_URL);
	    }
	}
	*/
	
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
	    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
	        Bundle extras = data.getExtras();
	        Bitmap imageBitmap = (Bitmap) extras.get("data");
	        ImageView imageView = (ImageView) findViewById(R.id.defaultProfilePicture);
	        imageView.setImageBitmap(Bitmap.createScaledBitmap(imageBitmap, 125, 125, false));
	    }
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
	private class RegisterTask extends UrlJsonAsyncTask {
		public RegisterTask(Context context) {
			super(context);
		}
		
		@Override
		protected JSONObject doInBackground(String... urls) {
	        DefaultHttpClient client = new DefaultHttpClient();
	        HttpPost post = new HttpPost(urls[0]);
	        JSONObject holder = new JSONObject();
	        JSONObject userObj = new JSONObject();
	        String response = null;
	        JSONObject json = new JSONObject();
	        
	        try {
	        	try {
	        		// default returned values in case something goes wrong
	        		json.put("success", false);
	        		json.put("info", "Something went wrong. Retry!");
	        		
	        		// add username, email, zip_code, password, password confirmation
	        		userObj.put("name", userFirstName + " " + userLastName);
	        		userObj.put("email", userEmail);
	        		userObj.put("zip_code", userZipcode);
	        		userObj.put("password", userPassword);
	        		userObj.put("password_confirmation", userPasswordConfirmation);
	        		
	        		holder.put("user", userObj);
	        		Log.d("holder", holder.toString());
	        		StringEntity se = new StringEntity(holder.toString());
	        		post.setEntity(se);
	        		
	        		post.setHeader("Accept", "application/json");
	        		post.setHeader("Content-Type", "application/json");
	        		
	        		ResponseHandler<String> responseHandler = new BasicResponseHandler();
	        		response = client.execute(post, responseHandler);
	        		
	        		json = new JSONObject(response);
	        	} catch (HttpResponseException e) {
	        		e.printStackTrace();
	        		Log.e("ClientProtocol", e.toString());
	        	} catch (IOException e) {
	        		e.printStackTrace();
	        		Log.e("IO", e.toString());
	        	}
	        } catch (JSONException e) {
	        	e.printStackTrace();
	        	Log.e("JSON", e.toString());
	        }
	        
	        return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
	        try {
	            if (json.getBoolean("success")) {
	                // everything is ok
	                SharedPreferences.Editor editor = preferences.edit();
	                // save the returned auth_token into
	                // the SharedPreferences
	                editor.putString("AuthToken", json.getJSONObject("data").getString("auth_token"));
	                editor.commit();

	                // launch the HomeActivity and close this one
	                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
	                startActivity(intent);
	                finish();
	            }
	            Toast.makeText(context, json.getString("info"), Toast.LENGTH_LONG).show();
	        } catch (Exception e) {
	            // something went wrong: show a Toast
	            // with the exception message
	            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
	        } finally {
	            super.onPostExecute(json);
	        }
	    }
	}

*/
	/*
	 * fragment used for people who want to register as a user
	 */
	public static class registerUserFragment extends AbstractRegisterFragment {

		private final static String REGISTER_API_ENDPOINT_URL = "http://cartwheels.us/mobile/registrations";
		
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
				DefaultTaskFragment<LoginTask, AbstractRegisterFragment, String> fragment) {
			fragment.execute(REGISTER_API_ENDPOINT_URL);
		}
	}
	
	/*
	 * fragment used for people who want to register as a cart
	 */
	public static class registerOwnerFragment extends AbstractRegisterFragment {
		
		private final static String REGISTER_API_ENDPOINT_URL = "http://cartwheels.us/mobile/owners/registrations";
		
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
				DefaultTaskFragment<LoginTask, AbstractRegisterFragment, String> fragment) {
			fragment.execute(REGISTER_API_ENDPOINT_URL);
		}
	}

}

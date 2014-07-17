package com.cartwheels;

import java.io.IOException;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.cartwheels.tasks.DefaultDeleteAsyncTask;
import com.cartwheels.tasks.DefaultTaskFragment;
import com.savagelook.android.UrlJsonAsyncTask;

public class LoginActivity extends Activity {

	private SharedPreferences preferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		
		preferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		Intent intent = getIntent();
		if (intent.getBooleanExtra("logout", false)) {
			if (intent.hasExtra("logout"))
				intent.removeExtra("logout");
			logout();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
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
	 * Have back pressed do nothing if the user is not logged in
	 */
	@Override
	public void onBackPressed() {
		Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
	}
	
	public void logout() {
		Fragment fragment = getFragmentManager().findFragmentById(R.id.loginFragment);
		
		SharedPreferences preferences = getSharedPreferences("CurrentUser", Activity.MODE_PRIVATE);
		String loginType = preferences.getString("LoginType", "user");
		
		String[] path;
		if (loginType.equals("user")) {
			path = new String[2];
			path[0] = "mobile";
			path[1] = "sessions";
		} else {
			path = new String[3];
			path[0] = "mobile";
			path[1] = "owners";
			path[2] = "sessions";
		}

		String email = preferences.getString("email", "");
		String auth_token = preferences.getString("AuthToken", "");
		
		DefaultDeleteAsyncTask asyncTask = new DefaultDeleteAsyncTask("http", "cartwheels.us", path);
		asyncTask.put("email", email);
		asyncTask.put("auth_token", auth_token);
		
		DefaultTaskFragment<DefaultDeleteAsyncTask, LoginFragment, Boolean> taskFragment =
				new DefaultTaskFragment<DefaultDeleteAsyncTask, LoginFragment, Boolean>(11);
		
		taskFragment.setTask(asyncTask);
		asyncTask.setFragment(taskFragment);
		taskFragment.setTargetFragment(fragment, 11);
		taskFragment.show(getFragmentManager(), "logoutFragment");
		
		taskFragment.execute();
	}

	public void switchRegister(View view) {
		Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
		intent.putExtra("user", true);
		startActivity(intent);
	}
	
	public void switchRegisterOwner(View view) {
		Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
		intent.putExtra("user", false);
		startActivity(intent);
	}

}

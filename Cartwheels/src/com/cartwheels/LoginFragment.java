package com.cartwheels;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.cartwheels.tasks.DefaultTaskFragment;
import com.cartwheels.tasks.LoginTask;

/*
 * Login fragment
 */
public class LoginFragment extends Fragment implements OnClickListener {

	private final static String LOGIN_API_ENDPOINT_URL = "http://cartwheels.us/mobile/sessions";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// find the taskfragment and set it to this
		FragmentManager manager = getFragmentManager();
		
		Fragment fragment = null;
		try {
			fragment = manager.findFragmentByTag("loginFragment");
		} catch(ClassCastException e) {
			e.printStackTrace();
		}
		if (fragment != null)
			fragment.setTargetFragment(this, 8);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_login,
				container, false);
		Button button = (Button) rootView.findViewById(R.id.loginButton);
		Button loginAsOwner = (Button) rootView.findViewById(R.id.loginAsOwnerButton);
		loginAsOwner.setOnClickListener(this);
		button.setOnClickListener(this);
		return rootView;
	}
	
	public void login(String innerKey) {
		EditText userEmailField = (EditText) getActivity().findViewById(R.id.userEmail);
	    String userEmail = userEmailField.getText().toString();
	    EditText userPasswordField = (EditText) getActivity().findViewById(R.id.userPassword);
	    String userPassword = userPasswordField.getText().toString();
	    
	    // put the email into the sharedpreferences
	    SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", Activity.MODE_PRIVATE);
	    SharedPreferences.Editor editor = preferences.edit();
	    editor.putString("email", userEmail);
	    editor.commit();
	    
	    LoginTask asyncTask = new LoginTask();
	    
	    DefaultTaskFragment<LoginTask, LoginFragment, String> fragment =
	    		new DefaultTaskFragment<LoginTask, LoginFragment, String>(8);
	    
	    asyncTask.setInnerKey(innerKey);
	    
	    asyncTask.putInner("email", userEmail);
	    asyncTask.putInner("password", userPassword);
	    
	    fragment.setTask(asyncTask);
	    asyncTask.setFragment(fragment);
	    
	    fragment.setTargetFragment(this, 8);
	    fragment.show(getFragmentManager(), "loginFragment");
	    
	    fragment.execute(LOGIN_API_ENDPOINT_URL);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 8 && resultCode == Activity.RESULT_OK) {
			boolean completed = false;
			if (data != null) {
				if (data.hasExtra("result")) {
					SharedPreferences preferences = 
							getActivity().getSharedPreferences("CurrentUser", Activity.MODE_PRIVATE);
					
					SharedPreferences.Editor editor = preferences.edit();
					String auth_token = data.getStringExtra("result");
					editor.putString("AuthToken", auth_token);
					editor.commit();
					
					// start the main activity
					Intent intent = new Intent(getActivity(), MainActivity.class);
					startActivity(intent);
					completed = true;
				} else {
					Log.e("onActivityResult loginActivity", "auth_token is null");
				}
			} else {
				Log.e("onActivityResult loginActivity", "Intent data is null");
			}
			
			// remove the email from the sharedpreferences
			if (!completed) {
				SharedPreferences preferences = 
						getActivity().getSharedPreferences("CurrentUser", Activity.MODE_PRIVATE);
				
				SharedPreferences.Editor editor = preferences.edit();
				editor.remove("email");
				editor.commit();
			}
		}
	}

	@Override
	public void onClick(View view) {
		
		switch (view.getId()) {
		case R.id.loginButton:
			login("user");
		case R.id.loginAsOwnerButton:
			login("owner");
		}
	}
}
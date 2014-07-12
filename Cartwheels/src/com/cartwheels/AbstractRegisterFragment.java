package com.cartwheels;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.cartwheels.tasks.DefaultTaskFragment;
import com.cartwheels.tasks.LoginTask;

public abstract class AbstractRegisterFragment extends Fragment
												implements OnClickListener {

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.registerButton:
			register("user");
			break;
		case R.id.registerOwnerButton:
			register("owner");
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 9 && resultCode == Activity.RESULT_OK) {
			if (data != null && data.hasExtra("result")) {
				String auth_token = data.getStringExtra("result");
				SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = preferences.edit();
				
				editor.putString("AuthToken", auth_token);
				editor.commit();
				
				Intent intent = new Intent(getActivity(), MainActivity.class);
				startActivity(intent);
			} else {
				Log.d("onActivityResult", "AuthToken is null");
			}
		}
	}
	// register as an owner
	private void register(String innerKey) {
		
		EditText editTextEmail = (EditText) getActivity().findViewById(R.id.userEmail);
		String email = editTextEmail.getText().toString();
		EditText editTextFirstName = (EditText) getActivity().findViewById(R.id.firstName);
		String firstName = editTextFirstName.getText().toString();
		EditText editTextLastName = (EditText) getActivity().findViewById(R.id.lastName);
		String lastName = editTextLastName.getText().toString();
		EditText editTextPassword = (EditText) getActivity().findViewById(R.id.userPassword);
		String password = editTextPassword.getText().toString();
		EditText editTextPasswordConfirmation =
				(EditText) getActivity().findViewById(R.id.userPasswordConfirmation);
		String passwordConfirmation = editTextPasswordConfirmation.getText().toString();
		
		String name = firstName + " " + lastName;
		String zipcode = null;
		if (innerKey.equals("user")) {
			EditText editTextZipcode = (EditText) getActivity().findViewById(R.id.userZipcode);
			zipcode = editTextZipcode.getText().toString();
		}

		SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		
		editor.putString("email", email);
		editor.commit();
		// basic fields that both users and owners have
		LoginTask asyncTask = new LoginTask();
		
		asyncTask.setInnerKey(innerKey);
		asyncTask.putInner("email", email);
		asyncTask.putInner("name", name);
		asyncTask.putInner("password", password);
		asyncTask.putInner("password_confirmation", passwordConfirmation);
		
		if (zipcode != null) {
			asyncTask.putInner("zip_code", zipcode);
		}
		
		DefaultTaskFragment<LoginTask, AbstractRegisterFragment, String> fragment =
				new DefaultTaskFragment<LoginTask, AbstractRegisterFragment, String>(9);
		
		fragment.setTask(asyncTask);
		asyncTask.setFragment(fragment);
		
		fragment.setTargetFragment(this, 9);
		fragment.show(getFragmentManager(), "registerFragment");
		
		execute(fragment);
	}
	
	protected abstract void execute(DefaultTaskFragment<LoginTask, AbstractRegisterFragment, String> fragment);
}

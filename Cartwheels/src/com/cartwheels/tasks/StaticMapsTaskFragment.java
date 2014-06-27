package com.cartwheels.tasks;

import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.cartwheels.R;

public class StaticMapsTaskFragment extends DialogFragment {
	
	private AsyncTask asyncTask;
	private Bitmap bitmap;
	ProgressBar progressBar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
								Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_task, container);
		
		progressBar = (ProgressBar) view.findViewById(R.id.progressTaskFragment);
		getDialog().setTitle("Progress Dialog");
		
		getDialog().setCanceledOnTouchOutside(false);
		
		return view;
	}
}

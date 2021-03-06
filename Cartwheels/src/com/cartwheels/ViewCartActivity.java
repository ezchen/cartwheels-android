package com.cartwheels;

import java.util.Locale;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

public class ViewCartActivity extends LocationActivity {

	private static final int REQUEST_IMAGE_CAPTURE = 0;

	private final Handler handler = new Handler();
	
	private PagerSlidingTabStrip tabs;
	private ViewPager viewPager;
	private SectionsPagerAdapter adapter;
	
	private Drawable oldBackground = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_cart);

		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		viewPager = (ViewPager) findViewById(R.id.pager);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		adapter = new SectionsPagerAdapter(getFragmentManager(), bundle);

		viewPager.setOffscreenPageLimit(3);
		viewPager.setAdapter(adapter);

		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
				.getDisplayMetrics());
		viewPager.setPageMargin(pageMargin);

		tabs.setViewPager(viewPager);
	}
	
	public void viewMap() {
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_cart, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return false;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d("onSaveInstanceState", "called");
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		private Bundle bundle;
		
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		public SectionsPagerAdapter(FragmentManager fm, Bundle bundle) {
			this(fm);
			this.bundle = bundle;
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			Log.d("getItem", position + "");
			switch (position) {
				case 0:
					return ViewCartFragment.newInstance(bundle);
				case 1:
					return ViewReviewFragment.newInstance(bundle);
				case 2:
					return ViewCartPhotosFragment.newInstance(bundle);
				case 3:
					return ViewMenuFragment.newInstance(bundle);
			}
			return ViewCartFragment.newInstance(bundle);
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.tab_section1).toUpperCase(l);
			case 1:
				return "REVIEW";
			case 2:
				return getString(R.string.tab_section3).toUpperCase(l);
			case 3:
				return "MENU";
			}
			return null;
		}
	}

}

package com.cartwheels.custom_views;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

public class ListFragmentSwipeRefresh extends SwipeRefreshLayout {

	private ListView listView;
	
	public ListFragmentSwipeRefresh(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public ListFragmentSwipeRefresh(Context context, AttributeSet attr) {
		super(context, attr);
	}
	
	@Override
    public boolean canChildScrollUp() {
        final ListView listView = getListView();
        if (listView.getVisibility() == View.VISIBLE) {
            return canListViewScrollUp(listView);
        } else {
            return false;
        }
    }

	/**
	 * Utility method to check whether a {@link ListView} can scroll up from it's current position.
	 * Handles platform version differences, providing backwards compatible functionality where
	 * needed.
	 */
	private static boolean canListViewScrollUp(ListView listView) {
	    if (android.os.Build.VERSION.SDK_INT >= 14) {
	        // For ICS and above we can call canScrollVertically() to determine this
	        return ViewCompat.canScrollVertically(listView, -1);
	    } else {
	        // Pre-ICS we need to manually check the first visible item and the child view's top
	        // value
	        return listView.getChildCount() > 0 &&
	                (listView.getFirstVisiblePosition() > 0
	                        || listView.getChildAt(0).getTop() < listView.getPaddingTop());
	    }
	}
	
	public ListView getListView() {
		return listView;
	}
	
	public void setListView(ListView listView) {
		this.listView = listView;
	}

}

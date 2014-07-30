package com.cartwheels.custom_views;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.widget.GridView;

public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {

	private GridView view;
	
	public CustomSwipeRefreshLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setGridView(GridView view) {
		this.view = view;
	}
	
	@Override
	public boolean canChildScrollUp() {
		if (view != null) {
			return (view.getChildCount() > 0) &&
					(view.getFirstVisiblePosition() > 0);
		} else {
			return super.canChildScrollUp();
		}
	}
}

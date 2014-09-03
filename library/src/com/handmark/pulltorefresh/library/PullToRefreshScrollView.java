/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.handmark.pulltorefresh.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PointF;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ScrollView;

public class PullToRefreshScrollView extends PullToRefreshBase<ScrollView> {

	public PullToRefreshScrollView(Context context) {
		super(context);
	}

	public PullToRefreshScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullToRefreshScrollView(Context context, Mode mode) {
		super(context, mode);
	}

	public PullToRefreshScrollView(Context context, Mode mode, AnimationStyle style) {
		super(context, mode, style);
	}

	@Override
	public final Orientation getPullToRefreshScrollDirection() {
		return Orientation.VERTICAL;
	}

	@Override
	protected ScrollView createRefreshableView(Context context, AttributeSet attrs) {
		ScrollView scrollView;
		if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
			scrollView = new InternalScrollViewSDK9(context, attrs);
		} else {
			scrollView = new CustomScrollView(context, attrs);
		}

		scrollView.setId(R.id.scrollview);
		return scrollView;
	}

	@Override
	protected boolean isReadyForPullStart() {
		return mRefreshableView.getScrollY() == 0;
	}

	@Override
	protected boolean isReadyForPullEnd() {
		View scrollViewChild = mRefreshableView.getChildAt(0);
		if (null != scrollViewChild) {
			return mRefreshableView.getScrollY() >= (scrollViewChild.getHeight() - getHeight());
		}
		return false;
	}
	
	class VerticalScrollView extends ScrollView {
		private boolean mIsScrolling =false;
		private int mTouchSlop;
		private PointF downPoint = new PointF();
		public VerticalScrollView(Context context, AttributeSet attrs) {
			super(context, attrs);
		    ViewConfiguration vc = ViewConfiguration.get(context);
		    mTouchSlop = vc.getScaledTouchSlop();
		}
		public boolean onInterceptTouchEvent(MotionEvent ev) {
			final int action = ev.getAction();
	        switch (action) {
				case MotionEvent.ACTION_DOWN:
					downPoint.x = ev.getX();
					downPoint.y = ev.getY();
					break;
	            case MotionEvent.ACTION_MOVE: {
	                final int xDiff = (int)Math.abs(downPoint.x - ev.getX());
	                final int yDiff = (int)Math.abs(downPoint.y - ev.getY());
	                if (yDiff > xDiff && yDiff > mTouchSlop) { 
	                    mIsScrolling = true;
	                }
	                break;
	            }
	        }
	        return super.onInterceptTouchEvent(ev) && mIsScrolling;
		}
	}
	
	class CustomScrollView extends ScrollView {
		private GestureDetector mGestureDetector;
		View.OnTouchListener mGestureListener;

		public CustomScrollView(Context context, AttributeSet attrs) {
		    super(context, attrs);
		    mGestureDetector = new GestureDetector(context, new YScrollDetector());
		    setFadingEdgeLength(0);
		}

		@Override
		public boolean onInterceptTouchEvent(MotionEvent ev) {
		    return super.onInterceptTouchEvent(ev)
		            && mGestureDetector.onTouchEvent(ev);
		}

		// Return false if we're scrolling in the x direction
		class YScrollDetector extends SimpleOnGestureListener {
		    @Override
		    public boolean onScroll(MotionEvent e1, MotionEvent e2,
		            float distanceX, float distanceY) {
		        if (Math.abs(distanceY) > Math.abs(distanceX)) {
		            return true;
		        }
		        return false;
		    }
		}
	}

	@TargetApi(9)
	final class InternalScrollViewSDK9 extends CustomScrollView {

		public InternalScrollViewSDK9(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
				int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

			final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
					scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

			// Does all of the hard work...
			OverscrollHelper.overScrollBy(PullToRefreshScrollView.this, deltaX, scrollX, deltaY, scrollY,
					getScrollRange(), isTouchEvent);

			return returnValue;
		}

		/**
		 * Taken from the AOSP ScrollView source
		 */
		private int getScrollRange() {
			int scrollRange = 0;
			if (getChildCount() > 0) {
				View child = getChildAt(0);
				scrollRange = Math.max(0, child.getHeight() - (getHeight() - getPaddingBottom() - getPaddingTop()));
			}
			return scrollRange;
		}
	}
}

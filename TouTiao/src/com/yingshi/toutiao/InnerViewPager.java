package com.yingshi.toutiao;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public class InnerViewPager extends ViewPager {

	private boolean mIsBeingDragged =false;
	private int mTouchSlop;
	private float mLastMotionX = 0;
	private OnSingleTouchListener onSingleTouchListener;
	
	public InnerViewPager(Context context) {
		super(context);
	}

	public InnerViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	    mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
	}
	

	public boolean onInterceptTouchEvent(MotionEvent ev) {
        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onTouchEvent will be called and we do the actual
         * scrolling there.
         */

        /*
        * Shortcut the most recurring case: the user is in the dragging
        * state and he is moving his finger.  We want to intercept this
        * motion.
        */
        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE) && mIsBeingDragged) {
            return true;
        }

        /*
         * Don't try to intercept touch if we can't scroll anyway.
         */
//        if (getScrollX() == 0 && !isAbleToScrollHorizontally(1)) {
//            return false;
//        }
        
        switch (action) {
			case MotionEvent.ACTION_DOWN:
                /*
                 * Remember location of down touch.
                 * ACTION_DOWN always refers to pointer index 0.
                 */
                mLastMotionX = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE: 
                final float xDiff = mLastMotionX - (int)ev.getX();
                if (xDiff > mTouchSlop) {
                    mIsBeingDragged = true;
                    mLastMotionX = (int)ev.getX();
                	getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                /* Release the drag */
                mIsBeingDragged = false;
                break;
        }
        // In general, we don't want to intercept touch events. They should be handled by the child view.
        return super.onInterceptTouchEvent(ev);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		boolean canScrollHorizontal = true;
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
                mLastMotionX = ev.getX();
                break;
			case MotionEvent.ACTION_MOVE:
				float deltaX = mLastMotionX - ev.getX();
				int count = getAdapter().getCount();
				int currentItem = getCurrentItem();
				if(deltaX > 0 && currentItem == count - 1){
					canScrollHorizontal = false;
				}
				if(deltaX < 0 && currentItem == 0){
					canScrollHorizontal = false;
				}
		}
		if(canScrollHorizontal)
			return super.onTouchEvent(ev);
		else
			return false;
	}
	
	public void onSingleTouch(View v) {
		if (onSingleTouchListener != null) {
			onSingleTouchListener.onSingleTouch(v);
		}
	}

	public interface OnSingleTouchListener {
		public void onSingleTouch(View v);
	}

	public void setOnSingleTouchListener(
			OnSingleTouchListener onSingleTouchListener) {
		this.onSingleTouchListener = onSingleTouchListener;
	}
}

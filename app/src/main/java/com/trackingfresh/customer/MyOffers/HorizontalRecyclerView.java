package com.trackingfresh.customer.MyOffers;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.LinearLayout;


import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_FLING;
import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;

/**
 * Created by Mohan Raj  on 4/1/2017.
 */
//https://github.com/lauw/Android-SnappingRecyclerView/issues
public class HorizontalRecyclerView extends RecyclerView {
    private boolean _userScrolling = false;
    private boolean _scrolling = false;
    private int _scrollState = SCROLL_STATE_IDLE;
    private long _lastScrollTime = 0;
    private Handler mHandler = new Handler();

    private boolean isScrollStatChangedToFlingIt;
    private boolean _scaleViews = false;
    private Orientation _orientation = Orientation.HORIZONTAL;

    private ChildViewMetrics _childViewMetrics;
    private OnViewSelectedListener _listener;

    public int getCenterItemPosition() {
        return centerItemPosition;
    }

    public void setCenterItemPosition(int centerItemPosition) {
        this.centerItemPosition = centerItemPosition;
    }

    private int centerItemPosition;

    private final static int MINIMUM_SCROLL_EVENT_OFFSET_MS = 20;
    private String TAG = getClass().getSimpleName();
    private Context context;

    public HorizontalRecyclerView(Context context) {
        this(context, null);
        this.context = context;
    }

    public HorizontalRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.context = context;
    }

    public HorizontalRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        this.context = context;
    }

    private void init() {
        setHasFixedSize(true);
        setOrientation(_orientation);
        enableSnapping();
    }

    private boolean scrolling;

    @Override
    public void onChildAttachedToWindow(View child) {
        super.onChildAttachedToWindow(child);

        if (!scrolling && _scrollState == SCROLL_STATE_IDLE) {
            scrolling = true;
            scrollToView(getCenterView());
            updateViews();
        }
    }

    private void enableSnapping() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                updateViews();
//                Log.i(TAG, "onScrolled : _scrolling " + _scrolling);

                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                Log.i(TAG, "onScrollStateChanged : _scrolling " + _scrolling);
                isScrollStatChangedToFlingIt = true;


                /** if scroll is caused by a touch (scroll touch, not any touch) **/
                if (newState == SCROLL_STATE_TOUCH_SCROLL) {
                    /** if scroll was initiated already, it would probably be a tap **/
                    /** if scroll was not initiated before, this is probably a user scrolling **/
                    if (!_scrolling) {
                        _userScrolling = true;
                    }
                } else if (newState == SCROLL_STATE_IDLE) {
                    /** if user is the one scrolling, snap to the view closest to center **/
                    if (_userScrolling) {
                        scrollToView(getCenterView());
                    }

                    _userScrolling = false;
                    _scrolling = false;

                    /** if idle, always check location and correct it if necessary, this is just an extra check **/
                    if (getCenterView() != null && getPercentageFromCenter(getCenterView()) > 0) {
                        scrollToView(getCenterView());
                    }

                    /** if idle, notify listeners of new selected view **/
                    notifyListener();
                } else if (newState == SCROLL_STATE_FLING) {
                    _scrolling = true;
                }

                _scrollState = newState;
            }
        });
    }

    private void notifyListener() {
        View view = getCenterView();
        int position = getChildAdapterPosition(view);

        /** if there is a listener and the index is not the same as the currently selected position, notify listener **/
        if (_listener != null && position != centerItemPosition) {
            _listener.onSelected(view, position);
        }

        centerItemPosition = position;
    }

    /**
     * Set the orientation for this SnappingRecyclerView
     *
     * @param orientation LinearLayoutManager.HORIZONTAL or LinearLayoutManager.VERTICAL
     */
    public void setOrientation(Orientation orientation) {
        this._orientation = orientation;
        _childViewMetrics = new ChildViewMetrics(_orientation);
        setLayoutManager(new LinearLayoutManager(getContext(), _orientation.intValue(), false));
    }

    /**
     * Set the OnViewSelectedListener
     *
     * @param listener the OnViewSelectedListener
     */
    public void setOnViewSelectedListener(OnViewSelectedListener listener) {
        this._listener = listener;
    }

    /**
     * Enable downscaling of views which are not focused, based on how far away they are from the center
     *
     * @param enabled enable or disable the scaling behaviour
     */
    public void enableViewScaling(boolean enabled) {
        this._scaleViews = enabled;
    }

    private void updateViews() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            setMarginsForChild(child);

            if (_scaleViews) {
                float percentage = getPercentageFromCenter(child);
                float scale = 1f - (0.4f * percentage);

                child.setScaleX(scale);
                child.setScaleY(scale);
            }
        }
    }

    /**
     * Adds the margins to a childView so a view will still center even if it's only a single child
     *
     * @param child childView to set margins for
     */
    private void setMarginsForChild(View child) {
        int lastItemIndex = getLayoutManager().getItemCount() - 1;
        int childIndex = getChildAdapterPosition(child);

        int startMargin = 0;
        int endMargin = 0;
        int topMargin = 0;
        int bottomMargin = 0;

        if (_orientation == Orientation.VERTICAL) {
            topMargin = childIndex == 0 ? getCenterLocation() : 0;
            bottomMargin = childIndex == lastItemIndex ? getCenterLocation() : 0;
        } else {
            startMargin = childIndex == 0 ? getCenterLocation() : 0;
            endMargin = childIndex == lastItemIndex ? getCenterLocation() : 0;
        }

        /** if sdk minimum level is 17, set RTL margins **/
        if (_orientation == Orientation.HORIZONTAL && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            ((MarginLayoutParams) child.getLayoutParams()).setMarginStart(startMargin);
            ((MarginLayoutParams) child.getLayoutParams()).setMarginEnd(endMargin);
        }

        /** If layout direction is RTL, swap the margins  **/
        if (ViewCompat.getLayoutDirection(child) == ViewCompat.LAYOUT_DIRECTION_RTL) {
            ((MarginLayoutParams) child.getLayoutParams()).setMargins(endMargin, topMargin, startMargin, bottomMargin);
        } else {
            ((MarginLayoutParams) child.getLayoutParams()).setMargins(startMargin, topMargin, endMargin, bottomMargin);
        }

        /** if sdk minimum level is 18, check if view isn't undergoing a layout pass (this improves the feel of the view by a lot) **/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (!child.isInLayout())
                child.requestLayout();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
//        Log.i(TAG, "dispatchTouchEvent : _scrolling " + _scrolling);
//        isScrollStatChangedToFlingIt = false;

        long currentTime = System.currentTimeMillis();

        /** if touch events are being spammed, this is due to user scrolling right after a tap,
         * so set userScrolling to true **/
        if (_scrolling && _scrollState == SCROLL_STATE_TOUCH_SCROLL) {
            if ((currentTime - _lastScrollTime) < MINIMUM_SCROLL_EVENT_OFFSET_MS) {
                _userScrolling = true;
            }
        }

        _lastScrollTime = currentTime;

        int location = _orientation == Orientation.VERTICAL ? (int) event.getY() : (int) event.getX();

        View targetView = getChildClosestToLocation(location);

        if (!_userScrolling) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (targetView != getCenterView()) {
                    scrollToView(targetView);
                    return true;
                }
            }
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
//        Log.i(TAG, "onInterceptTouchEvent : _scrolling " + _scrolling);
        isScrollStatChangedToFlingIt = false;
        int location = _orientation == Orientation.VERTICAL ? (int) event.getY() : (int) event.getX();

        View targetView = getChildClosestToLocation(location);

        if (targetView != getCenterView()) {
            return true;
        }

        return super.onInterceptTouchEvent(event);
    }

    @Override
    public void scrollToPosition(int position) {
//        Log.i(TAG, "scrollToPosition : _scrolling " + _scrolling);
        try {
            _childViewMetrics.size(getChildAt(0));
            smoothScrollBy(_childViewMetrics.size(getChildAt(0)) * position);
        } catch (Exception error) {
            super.scrollToPosition(position);
        }

    }

    private View getChildClosestToLocation(int location) {
        if (getChildCount() <= 0)
            return null;

        int closestPos = 9999;
        View closestChild = null;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            int childCenterLocation = (int) _childViewMetrics.center(child);
            int distance = childCenterLocation - location;

            /** if child center is closer than previous closest, set it as closest child  **/
            if (Math.abs(distance) < Math.abs(closestPos)) {
                closestPos = distance;
                closestChild = child;
            }
        }

        return closestChild;
    }

    /**
     * Check if the view is correctly centered (allow for 10px offset)
     *
     * @param child the child view
     * @return true if correctly centered
     */
    private boolean isChildCorrectlyCentered(View child) {
        int childPosition = (int) _childViewMetrics.center(child);
        return childPosition > (getCenterLocation() - 10) && childPosition < (getCenterLocation() + 10);
    }

    private View getCenterView() {
        return getChildClosestToLocation(getCenterLocation());
    }

    private void scrollToView(View child) {
//        Log.i(TAG, "scrollToView : _scrolling " + _scrolling);

        if (child == null)
            return;

        stopScroll();

        int scrollDistance = getScrollDistance(child);

        if (scrollDistance != 0)
            smoothScrollBy(scrollDistance);
    }

    private int getScrollDistance(View child) {
        int childCenterLocation = (int) _childViewMetrics.center(child);
        return childCenterLocation - getCenterLocation();
    }

    private float getPercentageFromCenter(View child) {
        float center = getCenterLocation();
        float childCenter = _childViewMetrics.center(child);

        float offSet = Math.max(center, childCenter) - Math.min(center, childCenter);
        float maxOffset = (center + _childViewMetrics.size(child));

        return (offSet / maxOffset);
    }

    private int getCenterLocation() {
        if (_orientation == Orientation.VERTICAL)
            return getMeasuredHeight() / 2;

        return getMeasuredWidth() / 2;
    }

    public void smoothScrollBy(int distance) {
//        Log.i(TAG, "smoothScrollBy : _scrolling " + _scrolling);
        if (_orientation == Orientation.VERTICAL) {
            super.smoothScrollBy(0, distance);
            return;
        }

        super.smoothScrollBy(distance, 0);
    }

    public void scrollBy(int distance) {
//        Log.i(TAG, "scrollBy : _scrolling " + _scrolling);

        if (_orientation == Orientation.VERTICAL) {
            super.scrollBy(0, distance);
            return;
        }

        super.scrollBy(distance, 0);
    }

    private void scrollTo(int position) {
        int currentScroll = getScrollOffset();
        scrollBy(position - currentScroll);
    }

    public int getScrollOffset() {
//        Log.i(TAG, "getScrollOffset : _scrolling " + _scrolling);

        if (_orientation == Orientation.VERTICAL)
            return computeVerticalScrollOffset();

        return computeHorizontalScrollOffset();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);
    }

    private static class ChildViewMetrics {
        private Orientation _orientation;

        public ChildViewMetrics(Orientation orientation) {
            this._orientation = orientation;
        }

        public int size(View view) {
            if (_orientation == Orientation.VERTICAL)
                return view.getHeight();

            return view.getWidth();
        }

        public float location(View view) {
            if (_orientation == Orientation.VERTICAL)
                return view.getY();

            return view.getX();
        }

        public float center(View view) {
            return location(view) + (size(view) / 2);
        }
    }

    public enum Orientation {
        HORIZONTAL(LinearLayout.HORIZONTAL),
        VERTICAL(LinearLayout.VERTICAL);

        int value;

        Orientation(int value) {
            this.value = value;
        }

        public int intValue() {
            return value;
        }
    }

    public interface OnViewSelectedListener {
        void onSelected(View view, int position);
    }


    @Override
    public boolean fling(int velocityX, int velocityY) {
//        LogUtil.info(TAG, "fling : _scrolling " + _scrolling + "isScrollStatChangedToFlingIt : " + isScrollStatChangedToFlingIt);
        if (!isScrollStatChangedToFlingIt)
            return true;
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getLayoutManager();

//these four variables identify the views you see on screen.
        int lastVisibleView = linearLayoutManager.findLastVisibleItemPosition();
        int firstVisibleView = linearLayoutManager.findFirstVisibleItemPosition();
//        LogUtil.info(TAG, "fisrt View : " + linearLayoutManager.findFirstVisibleItemPosition() + " first comp : " + linearLayoutManager.findFirstCompletelyVisibleItemPosition());
//        LogUtil.info(TAG, "last View : " + linearLayoutManager.findLastVisibleItemPosition() + " last comp : " + linearLayoutManager.findLastCompletelyVisibleItemPosition());
//        LogUtil.info(TAG, "Center : " + centerItemPosition + " _scrolling : " + _scrolling);
        View firstView = linearLayoutManager.findViewByPosition(firstVisibleView);
        View lastView = linearLayoutManager.findViewByPosition(lastVisibleView);


        int leftMargin = (getScreenWidth() - lastView.getWidth()) / 2;
        int rightMargin = (getScreenWidth() - firstView.getWidth()) / 2 + firstView.getWidth();
        int leftEdge = lastView.getLeft();
        int rightEdge = firstView.getRight();
        int scrollDistanceLeft = leftEdge - leftMargin;
        int scrollDistanceRight = rightMargin - rightEdge;

        if (velocityX > 0)
            smoothScrollBy(scrollDistanceLeft);
        else
            smoothScrollBy(-scrollDistanceRight);

        return true;
    }


    public int getScreenWidth() {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }
}

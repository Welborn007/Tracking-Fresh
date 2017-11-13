package com.trackingfresh.customer.DetailPage;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by SNK Consulting on 26-09-2017.
 */

public class PaddingItemDecoration extends RecyclerView.ItemDecoration {
    private final int padding, length;

    public PaddingItemDecoration(int padding, int length) {
        this.padding = padding;
        this.length = length;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.left += padding;
        }
        if (parent.getChildAdapterPosition(view) == length) {
            outRect.right += padding;
        }
    }

}

/*extends RecyclerView.ItemDecoration {

    private int mPaddingPx;
    private int mPaddingEdgesPx;

    public PaddingItemDecoration(Activity activity) {
        final Resources resources = activity.getResources();
        mPaddingPx = (int) resources.getDimension(R.dimen.paddingItemDecorationDefault);
        mPaddingEdgesPx = (int) resources.getDimension(R.dimen.paddingItemDecorationEdge);
    }
public PaddingItemDecoration(int mPaddingPx,int mPaddingEdgesPx) {
//        final Resources resources = activity.getResources();
        this.mPaddingPx = mPaddingPx;
        this.mPaddingEdgesPx = mPaddingEdgesPx;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        final int itemPosition = parent.getChildAdapterPosition(view);
        if (itemPosition == RecyclerView.NO_POSITION) {
            return;
        }
        int orientation = getOrientation(parent);
        final int itemCount = state.getItemCount();

        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;

        *//** HORIZONTAL *//*
        if (orientation == LinearLayoutManager.HORIZONTAL) {
            *//** all positions *//*
            left = mPaddingPx;
            right = mPaddingPx;

            *//** first position *//*
            if (itemPosition == 0) {
                left += mPaddingEdgesPx;
            }
            *//** last position *//*
            else if (itemCount > 0 && itemPosition == itemCount - 1) {
                right += mPaddingEdgesPx;
            }
        }
        *//** VERTICAL *//*
        else {
            *//** all positions *//*
            top = mPaddingPx;
            bottom = mPaddingPx;

            *//** first position *//*
            if (itemPosition == 0) {
                top += mPaddingEdgesPx;
            }
            *//** last position *//*
            else if (itemCount > 0 && itemPosition == itemCount - 1) {
                bottom += mPaddingEdgesPx;
            }
        }

        if (!isReverseLayout(parent)) {
            outRect.set(left, top, right, bottom);
        } else {
            outRect.set(right, bottom, left, top);
        }
    }

    private boolean isReverseLayout(RecyclerView parent) {
        if (parent.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
            return layoutManager.getReverseLayout();
        } else {
            throw new IllegalStateException("PaddingItemDecoration can only be used with a LinearLayoutManager.");
        }
    }

    private int getOrientation(RecyclerView parent) {
        if (parent.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
            return layoutManager.getOrientation();
        } else {
            throw new IllegalStateException("PaddingItemDecoration can only be used with a LinearLayoutManager.");
        }
    }
}*/
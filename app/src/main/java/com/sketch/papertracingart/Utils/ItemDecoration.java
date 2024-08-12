package com.sketch.papertracingart.Utils;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.sketch.papertracingart.MyApp;

public class ItemDecoration extends RecyclerView.ItemDecoration {

    private int v, h, ex;

    public ItemDecoration(int v, int h, int ex) {
        this.v = Math.round(dpToPx(v));
        this.h = Math.round(dpToPx(h));
        this.ex = Math.round(dpToPx(ex));
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int spanCount = 1;
        int spanSize = 1;
        int spanIndex = 0;

        int childAdapterPosition = parent.getChildAdapterPosition(view);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            spanCount = staggeredGridLayoutManager.getSpanCount();
            if (layoutParams.isFullSpan()) {
                spanSize = spanCount;
            }
            spanIndex = layoutParams.getSpanIndex();
        } else if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) view.getLayoutParams();
            spanCount = gridLayoutManager.getSpanCount();
            spanSize = gridLayoutManager.getSpanSizeLookup().getSpanSize(childAdapterPosition);
            spanIndex = layoutParams.getSpanIndex();
        } else if (layoutManager instanceof LinearLayoutManager) {
            outRect.left = v;
            outRect.right = v;
            outRect.bottom = h;
        }

        if (spanSize == spanCount) {
            outRect.left = v + ex;
            outRect.right = v + ex;
            outRect.bottom = h;

        } else {
            int itemAllSpacing = (v * (spanCount + 1) + ex * 2) / spanCount;
            int left = v * (spanIndex + 1) - itemAllSpacing * spanIndex + ex;
            int right = itemAllSpacing - left;
            outRect.left = left;
            outRect.right = right;
            outRect.bottom = h;

        }

    }


    public static float dpToPx(float dpValue) {
        float density = MyApp.myApp.getResources().getDisplayMetrics().density;
        return density * dpValue + 0.5f;
    }
}

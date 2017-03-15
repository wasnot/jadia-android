package ai.sensy.jadia;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by akihiroaida on 2017/03/15.
 */

public class LoadScrollListener extends RecyclerView.OnScrollListener {

    private LinearLayoutManager linearLayoutManager;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private OnLoadMoreListener mOnLoadMoreListener;

    LoadScrollListener(LinearLayoutManager linearLayoutManager, OnLoadMoreListener loadMoreListener){
        this.linearLayoutManager = linearLayoutManager;
        mOnLoadMoreListener = loadMoreListener;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        totalItemCount = linearLayoutManager.getItemCount();
        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
        if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
            if (mOnLoadMoreListener != null) {
                mOnLoadMoreListener.onLoadMore();
            }
            isLoading = true;
        }
    }

    public void setLoaded() {
        isLoading = false;
    }
}

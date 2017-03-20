package ai.sensy.jadia;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by akihiroaida on 2017/03/15.
 */

public class LoadScrollListener extends RecyclerView.OnScrollListener {
    private String TAG = LoadScrollListener.class.getSimpleName();

    private LinearLayoutManager linearLayoutManager;
    private boolean isLoading;
    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCount;
    private OnLoadMoreListener mOnLoadMoreListener;
    private int currentPage = 0;

    LoadScrollListener(LinearLayoutManager linearLayoutManager, OnLoadMoreListener loadMoreListener){
        this.linearLayoutManager = linearLayoutManager;
        mOnLoadMoreListener = loadMoreListener;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        totalItemCount = linearLayoutManager.getItemCount();
        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
//        LogUtil.d(TAG, "onScrolled " + totalItemCount + ", "+lastVisibleItem);
        if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
            currentPage++;
            if (mOnLoadMoreListener != null) {
                mOnLoadMoreListener.onLoadMore(currentPage);
            }
            isLoading = true;
        }
    }

    public void setLoaded() {
        isLoading = false;
    }
}

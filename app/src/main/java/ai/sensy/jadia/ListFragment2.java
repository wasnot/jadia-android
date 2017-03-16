package ai.sensy.jadia;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * list
 */

public class ListFragment2 extends Fragment implements MainActivity.OnWindowFocusChangedListener {

    private static final String TAG = ListFragment2.class.getSimpleName();

    // RecyclerViewとAdapter
    private RecyclerView mRecyclerView = null;
    private RecyclerAdapter2 mAdapter = null;
    private boolean mIsFetching = false;
    private LoadScrollListener mScrollListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        // RecyclerViewの参照を取得
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        View emptyMessage = view.findViewById(R.id.emptyMessage);
        emptyMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecyclerView != null && mRecyclerView.getVisibility() == View.GONE) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mAdapter.showFooterProgress();
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        refresh();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mScrollListener = new LoadScrollListener((LinearLayoutManager) mRecyclerView.getLayoutManager(), new OnLoadMoreListener() {
            @Override
            public void onLoadMore(final int currentPage) {
                LogUtil.d(TAG, "onLoadMore " +currentPage );
//スクロールされた時の処理
                mAdapter.showFooterProgress();
                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.e("haint", "Load More 2");
                        mAdapter.dissmissFooterProgress();
//                        if (currentPage < 3) {
//                            //Load data
//                            List<Item> newList = new ArrayList<>();
////                        int count = new Random().nextInt(9) + 1;
//                            for (int i = 0; i < 5; i++) {
//                                newList.add(new Item());
//                            }
//                            mAdapter.addAll(newList);
//                        }
                        mScrollListener.setLoaded();
                    }
                }, 2000);
            }
        });
        mRecyclerView.addOnScrollListener(mScrollListener);
//        mRecyclerView.addOnScrollListener(new EndlessScrollListener((LinearLayoutManager) mRecyclerView.getLayoutManager()) {
//            @Override
//            void onLoadMore(int current_page) {
//                LogUtil.d(TAG, "onLoadMore" + current_page);
////スクロールされた時の処理
//                mAdapter.showFooterProgress();
//                //Load more data for reyclerview
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        LogUtil.e("haint", "Load More 2");
//                        mAdapter.dissmissFooterProgress();
//                        //Load data
//                        List<Item> newList = new ArrayList<>();
////                        int count = new Random().nextInt(9) + 1;
//                        for (int i = 0; i < 20; i++) {
//                            newList.add(new Item());
//                        }
//                        mAdapter.addAll(newList);
//                    }
//                }, 2000);
//            }
//        });
    }

    @Override
    public void onResume() {
        LogUtil.d(TAG, "onResume");
        super.onResume();
        refresh();
    }

    @Override
    public void onPause() {
        LogUtil.d(TAG, "onPause");
        super.onPause();
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        LogUtil.d(TAG, "onWindowFocusChanged " + hasFocus);
        if (hasFocus) {
            refresh();
        }
    }

    private void refresh() {
        LogUtil.d(TAG, "refresh " + mIsFetching);
        if (getActivity() == null || !isAdded()) {
            return;
        }

        List<Item> newList = new ArrayList<>();
//                        int count = new Random().nextInt(9) + 1;
        for (int i = 0; i < 2; i++) {
            newList.add(new Item());
        }
        if (newList.size() == 0) {
            LogUtil.d(TAG, "fetching from refresh :" + newList.size());
        }
        mAdapter = new RecyclerAdapter2(getActivity(), newList);
        mRecyclerView.setAdapter(mAdapter);
        checkEmpty(newList.size());
    }

    private void checkEmpty(int newCount) {
        LogUtil.d(TAG, "checkEmpty " + mIsFetching + ", " + newCount);
        if (getActivity() == null || !isAdded()) {
            return;
        }
        if (mRecyclerView == null || mAdapter == null) {
            return;
        }
        if (newCount == 0 && (mAdapter.getItemCount() - 2) == 0) {
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
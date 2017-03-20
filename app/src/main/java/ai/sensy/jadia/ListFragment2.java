package ai.sensy.jadia;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * list
 */

public class ListFragment2 extends Fragment implements MainActivity.OnWindowFocusChangedListener {

    private static final String TAG = ListFragment2.class.getSimpleName();

    // RecyclerViewとAdapter
    private RecyclerView mRecyclerView = null;
    private RecyclerAdapter2 mAdapter = null;
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
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mScrollListener = new LoadScrollListener((LinearLayoutManager) mRecyclerView.getLayoutManager(), new OnLoadMoreListener() {
            @Override
            public void onLoadMore(final int currentPage) {
                LogUtil.d(TAG, "onLoadMore " + currentPage);
//スクロールされた時の処理
                loadNext(currentPage);
            }
        });
        mRecyclerView.addOnScrollListener(mScrollListener);
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
        LogUtil.d(TAG, "refresh ");
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
        LogUtil.d(TAG, "checkEmpty " + ", " + newCount);
        if (getActivity() == null || !isAdded()) {
            return;
        }
        if (mRecyclerView == null || mAdapter == null) {
            return;
        }
        if (newCount == 0 && mAdapter.getContentCount() == 0) {
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void loadNext(final int currentPage) {
        LogUtil.d(TAG, "loadNext");
//        mAdapter.showFooterProgress();
        //Load more data for reyclerview
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtil.e(TAG, "Load More " + currentPage);
                mAdapter.dismissFooterProgress();
                //Load data
                List<Item> newList = new ArrayList<>();
                if (currentPage < 2 || false) {
                    Random r = new Random();
//                    if (r.nextInt(3) == 0 || false) {
                    int color = Color.rgb(r.nextInt(255), r.nextInt(255), r.nextInt(255));
//                        int count = new Random().nextInt(9) + 1;
                    for (int i = 0; i < 3; i++) {
                        newList.add(new Item(color));
                    }
//                    }
                    mAdapter.addAll(newList);
                }
                if (newList.size() == 0) {
                    checkEmpty(0);
                }
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.showFooterProgress();
                        mScrollListener.setLoaded();
                    }
                }, 2000);
            }
        }, 2000);
    }
}
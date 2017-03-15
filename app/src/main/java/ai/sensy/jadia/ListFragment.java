package ai.sensy.jadia;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * list
 */

public class ListFragment extends Fragment implements MainActivity.OnWindowFocusChangedListener,
        Fetching.OnMessageDbListener, Fetching.OnMessageFetchListener {

    private static final String TAG = ListFragment.class.getSimpleName();

    // RecyclerViewとAdapter
    private RecyclerView mRecyclerView = null;
    private RecyclerAdapter mAdapter = null;
    private boolean mIsFetching = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (getActivity() == null || !isAdded() || msg == null) {
                return;
            }
            int count = msg.arg1;
            if (count == 0 && !mIsFetching) {
                LogUtil.d(TAG, "fetching from handler :" + count);
                mIsFetching = true;
                Fetching.fetchNextMessage(getActivity(), null,
                        ListFragment.this);
                return;
            }
//            if (mRecyclerView.getFooterViewsCount() > 0) {
//                count -= mRecyclerView.getFooterViewsCount();
//            }
            if (mAdapter.getItemCount() <= count) {
                count = mAdapter.getItemCount();
            }
            boolean needFetch;
            String lastMessageId = null;
            if (count > 0) {
                Item last = mAdapter.getItem(count - 1);
                lastMessageId = last != null ? last.messageId : null;

                Fetching db = new Fetching(getActivity());
                List<Item> nextList = db.getNextItemList(lastMessageId);
                db.closeDatabase();
                if (nextList != null && nextList.size() > 0) {
                    LogUtil.d(TAG, "no need fetch:" + nextList.size());
                    mAdapter.addAll(nextList);
                    needFetch = false;
                } else {
                    LogUtil.d(TAG, "need fetch");
                    needFetch = true;
                }
            } else {
                LogUtil.d(TAG, "need fetch:" + count);
                needFetch = true;
            }

            if (mIsFetching) {
                mAdapter.dissmissFooterProgress();
            } else if (needFetch) {
                LogUtil.d(TAG, "startFetching");
                LogUtil.d(TAG, "fetching from needFetch ");
                mIsFetching = true;
                Fetching.fetchNextMessage(getActivity(),
                        lastMessageId, ListFragment.this);
            }
        }
    };

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
        mRecyclerView.addOnScrollListener(new EndlessScrollListener((LinearLayoutManager) mRecyclerView.getLayoutManager()) {
            @Override
            void onLoadMore(int current_page) {
                LogUtil.d(TAG, "onLoadMore" + current_page);
//スクロールされた時の処理
                mAdapter.showFooterProgress();
//                Message msg = new Message();
//                msg.arg1 = mAdapter.getItemCount() - 1;
//                mHandler.sendMessageDelayed(msg, 500);
            }
        });
//        mRecyclerView.addOnScrollListener(new LoadScrollListener(mRecyclerView.getLayoutManager(),
//                new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore() {
//
//                LogUtil.e("haint", "Load More");
//                mUsers.add(null);
//                mUserAdapter.notifyItemInserted(mUsers.size() - 1);
//                //Load more data for reyclerview
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        LogUtil.e("haint", "Load More 2");
//                        //Remove loading item
//                        mUsers.remove(mUsers.size() - 1);
//                        mUserAdapter.notifyItemRemoved(mUsers.size());
//                        //Load data
//                        int index = mUsers.size();
//                        int end = index + 20;
//                        for (int i = index; i < end; i++) {
//                            User user = new User();
//                            user.setName("Name " + i);
//                            user.setEmail("alibaba" + i + "@gmail.com");
//                            mUsers.add(user);
//                        }
//                        mUserAdapter.notifyDataSetChanged();
//                        mUserAdapter.setLoaded();
//                    }
//                }, 5000);
//            }
//        }));
    }

    @Override
    public void onResume() {
        LogUtil.d(TAG, "onResume");
        super.onResume();
        Fetching.setListener(this);
        refresh();
    }

    @Override
    public void onPause() {
        LogUtil.d(TAG, "onPause");
        super.onPause();
        Fetching.setListener(null);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        LogUtil.d(TAG, "onWindowFocusChanged " + hasFocus);
        if (hasFocus) {
            refresh();
        }
    }

    @Override
    public void onUpdate(List<Item> list) {
        LogUtil.d(TAG, "onUpdate " + mIsFetching + ", " + (list != null ? list.size() : -1));
        if (mIsFetching) {
            mIsFetching = false;
        }
        if (getActivity() == null || !isAdded()) {
            LogUtil.e(TAG, "onUpdate detached");
            return;
        }
        if (mAdapter == null) {
            LogUtil.e(TAG, "onUpdate adapter is null");
            return;
        }
        int size = 0;
        if (list != null && list.size() > 0) {
            LogUtil.e(TAG, "onUpdate added '" + list.get(0).message + "', " + ("Knock! Knock!"
                    .equals(list.get(0).message)));
            size = list.size();
            // 追加
            mAdapter.addAll(list);
        } else {
            LogUtil.e(TAG, "onUpdate removed");
            // 削除されたかチェック
            refresh();
        }
        checkEmpty(size);
//        dissmissFooterProgress();
    }

    @Override
    public void onFetched(boolean result, int count) {
        LogUtil.d(TAG, "onFetch " + mIsFetching + ", " + result + "," + count);
        if (getActivity() == null || !isAdded()) {
            LogUtil.e(TAG, "onFetch detached");
            return;
        }
        // no network
//        if (!result && !NetworkUtil.checkNetwork(getActivity())) {
//            Toast.makeText(getActivity(), R.string.message_error_toast_network, Toast.LENGTH_SHORT)
//                    .show();
//        }
        LogUtil.d(TAG, "onFetch " + mAdapter.getItemCount());
        if (mIsFetching) {
            mIsFetching = false;
        }
        checkEmpty(count);
        mAdapter.dissmissFooterProgress();
    }

    private void refresh() {
        LogUtil.d(TAG, "refresh " + mIsFetching);
        if (getActivity() == null || !isAdded()) {
            return;
        }
        Fetching db = new Fetching(getActivity());
        List<Item> newList = db.getNextItemList(null);
        db.closeDatabase();
        if (newList.size() == 0) {
            LogUtil.d(TAG, "fetching from refresh :" + newList.size());
            Fetching.fetchNextMessage(getActivity(), null, this);
        }
        mAdapter = new RecyclerAdapter(getActivity(), newList);
        mRecyclerView.setAdapter(mAdapter);
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
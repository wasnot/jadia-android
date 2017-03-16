package ai.sensy.jadia;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Message Adapter for RecyclerView
 */

class RecyclerAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = RecyclerAdapter2.class.getSimpleName();

    private final static int VIEW_TYPE_HEADER = 1;
    private final static int VIEW_TYPE_FOOTER = 2;

    private LayoutInflater mLayoutInflater;
    private List<Item> mData;

    RecyclerAdapter2(Context context, List<Item> objects) {
        mLayoutInflater = LayoutInflater.from(context);
        mData = new ArrayList<>(objects.size() + 2);
        mData.add(null);
        mData.addAll(objects);
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position) == null) {
            if (position == 0) {
                return VIEW_TYPE_HEADER;
            } else {
                return VIEW_TYPE_FOOTER;
            }
        } else {
            return 0;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        switch (i) {
            case VIEW_TYPE_HEADER:
                return new HeaderViewHolder(mLayoutInflater
                        .inflate(R.layout.layout_header, null, false));
            case VIEW_TYPE_FOOTER:
                View view = mLayoutInflater.inflate(R.layout.layout_loading, null, false);
                return new LoadingViewHolder(view);
            default:
                return new MessageViewHolder(mLayoutInflater
                        .inflate(R.layout.list_item_expandable, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (!(viewHolder instanceof MessageViewHolder)) {
            if (viewHolder instanceof LoadingViewHolder) {
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) viewHolder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }
            return;
        }
        final MessageViewHolder holder = (MessageViewHolder) viewHolder;

        if (mData != null && mData.size() > position && mData.get(position) != null) {
            Item item = mData.get(position);
            holder.messageText.setText(item.message);
        }

    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    void addAll(Collection<? extends Item> collection) {
        for (Item item : collection) {
            if (!mData.contains(item)) {
                mData.add(item);
                notifyItemInserted(mData.size() - 1);
            }
        }
    }

    Item getItem(int position) {
        return mData != null && mData.size() > position ? mData.get(position) : null;
    }

    void showFooterProgress() {
        LogUtil.d(TAG, "dissmissFooterProgress ");
        mData.add(null);
        notifyItemInserted(mData.size() - 1);
    }

    void dissmissFooterProgress() {
        LogUtil.d(TAG, "dissmissFooterProgress ");
        //Remove loading item
        mData.remove(mData.size() - 1);
        notifyItemRemoved(mData.size());
    }

    private class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView messageText;
        TextView sendDateText;
        ImageView button;
        View messageLayout;
        TextView testText;

        MessageViewHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.messageTextView);
            sendDateText = (TextView) itemView.findViewById(R.id.sendDateTextView);
            button = (ImageView) itemView.findViewById(R.id.expandButton);
            messageLayout = itemView.findViewById(R.id.messageLayout);
            testText = (TextView) itemView.findViewById(R.id.testText);
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }
}

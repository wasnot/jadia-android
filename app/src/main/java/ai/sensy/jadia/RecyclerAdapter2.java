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
import java.util.Locale;

/**
 * Message Adapter for RecyclerView
 */

class RecyclerAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = RecyclerAdapter2.class.getSimpleName();

    private final static int VIEW_TYPE_HEADER = 1;
    private final static int VIEW_TYPE_FOOTER = 2;

    private LayoutInflater mLayoutInflater;
    private List<Item> mData;
    private RecyclerView mView;

    RecyclerAdapter2(Context context, List<Item> objects) {
        mLayoutInflater = LayoutInflater.from(context);
        mData = new ArrayList<>(objects.size() + 2);
        mData.add(new HeaderItem());
        mData.addAll(objects);
        mData.add(new FooterItem());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mView = null;
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position) instanceof HeaderItem) {
            return VIEW_TYPE_HEADER;
        } else if (mData.get(position) instanceof FooterItem) {
            return VIEW_TYPE_FOOTER;
        } else {
            return 0;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        switch (i) {
            case VIEW_TYPE_HEADER:
                return new HeaderViewHolder(mLayoutInflater
                        .inflate(R.layout.layout_header, viewGroup, false));
            case VIEW_TYPE_FOOTER:
                View view = mLayoutInflater.inflate(R.layout.layout_loading, viewGroup, false);
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
                FooterItem item = (FooterItem) mData.get(position);
                loadingViewHolder.progressBar.setVisibility(item.space == 0? View.VISIBLE: View.GONE);
                loadingViewHolder.itemView.setMinimumHeight(item.space);
            }
            return;
        }
        final MessageViewHolder holder = (MessageViewHolder) viewHolder;

        if (mData != null && mData.size() > position && mData.get(position) != null) {
            Item item = mData.get(position);
            item.moreTwoLines = isEllipsize(holder.messageText, item.message);
            if (item.moreTwoLines) {
                holder.button.setVisibility(View.VISIBLE);
            } else {
                holder.button.setVisibility(View.INVISIBLE);
            }
            if (item.isExpanded) {
                holder.messageText.setMaxLines(200);
                holder.button.setImageResource(android.R.drawable.arrow_up_float);
            } else {
                holder.messageText.setMaxLines(2);
                holder.button.setImageResource(android.R.drawable.arrow_down_float);
            }
            holder.messageText.setText(item.message);
            holder.sendDateText.setText(getTimeString(item.sendDate));
            holder.messageLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.button.performClick();
                }
            });
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Item item = mData.get(holder.getAdapterPosition());
                    if (holder.messageText.getMaxLines() == 2) {
                        item.isExpanded = true;
                        holder.button.setImageResource(android.R.drawable.arrow_up_float);
                        holder.messageText.setMaxLines(200);
                    } else {
                        item.isExpanded = false;
                        holder.messageText.setMaxLines(2);
                        holder.button.setImageResource(android.R.drawable.arrow_down_float);
                    }
                }
            });

            // The TextView hasn't been laid out, so we need to set an observer
            // The observer fires once layout's done, when we can check the ellipsizing
            ViewTreeObserver vto = holder.messageText.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            LogUtil.d(TAG, "isellipsize " + holder.messageText.getWidth());
                            // Remove the now unnecessary observer
                            // It wouldn't fire again for reused views anyways
                            ViewTreeObserver obs = holder.messageText.getViewTreeObserver();
                            obs.removeOnGlobalLayoutListener(this);
                        }
                    }
            );
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

    int getContentCount() {
        int count = 0;
        for (Item item : mData) {
            if (!(item instanceof FooterItem) && !(item instanceof HeaderItem)) {
                count += 1;
            }
        }
        return count;
    }

    void showFooterProgress() {
        LogUtil.d(TAG, "showFooterProgress " + mView.getChildCount() + ", " + mData.size());
        int count = 1;
        if (getContentCount() > 0) {
            int height = 0;
            for (int i = 0; i < mView.getChildCount(); i++) {
                if (i < mData.size() && !(mData.get(i) instanceof FooterItem)) {
                    height += mView.getChildAt(i).getHeight();
                }
            }
            LogUtil.d(TAG, "showFooterProgress, check height: " + height + ", " + mView.getHeight());
            if (height < mView.getHeight()) {
                FooterItem item = new FooterItem();
                item.space = mView.getHeight() - height;
                mData.add(item);
                count +=1;
            }
        }
        mData.add(new FooterItem());
        notifyItemRangeInserted(mData.size() - 1, count);
    }

    void dismissFooterProgress() {
        LogUtil.d(TAG, "dismissFooterProgress ");
        //Remove loading item
        while (mData.get(mData.size() - 1) instanceof FooterItem) {
            mData.remove(mData.size() - 1);
            notifyItemRemoved(mData.size());
        }
    }

    private String getTimeString(long timeMilli) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy/M/d HH:mm", Locale.getDefault());
        if (timeMilli == 0) {
            return sf.format(new Date());
        }

        Date sendDate = new Date(timeMilli);
        Calendar send = Calendar.getInstance();
        send.setTimeInMillis(timeMilli);
        return sf.format(sendDate);
    }

    private static boolean isEllipsize(TextView textView, String msg) {
        LogUtil.d(TAG,
                "isEllipsize size:" + textView.getTextSize() + "," + textView.getText().length());
        float textSize = textView.getTextSize();
        int nc = msg.length() - msg.replaceAll("\n", "").length();

        String[] splits = msg.split("\n", 0);
        LogUtil.d(TAG,
                "isEllipsize splits" + "," + splits.length + "," + nc);
        if (splits.length > 2 || nc >= 2) {
            return true;
        } else if (splits.length == 2) {
            for (String line : splits) {
                if (isEllipsizeLine(textSize, line, 1)) {
                    return true;
                }
            }
            return false;
        }
        return isEllipsizeLine(textSize, msg, 2);
    }

    private static boolean isEllipsizeLine(float textsize, String line, int lineCount) {
        Paint paint = new Paint();
        paint.setTextSize(textsize);
        final float size = paint.measureText(line);
        LogUtil.d(TAG, "isEllipsize width:414" + "," + size);
        // text is elipsized.
        return (int) size > 414 * lineCount;
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

package ai.sensy.jadia;

/**
 * Created by akihiroaida on 2017/03/15.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * fetch
 */
class Fetching {

    private final static String TAG = Fetching.class.getSimpleName();

    private static OnMessageDbListener sListener;

    private static Handler sHandler;

    private List<String> key = new ArrayList<>();

    private List<Item> list = new ArrayList<>();

    Fetching(Context context) {
    }

    void closeDatabase() {
    }

    long putItem(Item item) {
        if (item == null) {
            return -1;
        }
        key.add(item.messageId);
        list.add(item);

        List<Item> list = new ArrayList<>();
        list.add(item);
        update(list);
        return 0;
    }

    private void update(final List<Item> list) {
        if (sHandler != null) {
            sHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (sListener != null) {
                        sListener.onUpdate(list);
                    }
                }
            });
        }
    }

    /**
     * 表示用のactivity
     */
    List<Item> getNextItemList(String messageId) {
        int index = 0;
        int limit = Math.min(list.size(), 10);
        if (messageId != null && messageId.length() > 0 && key.contains(messageId)) {
            index = key.indexOf(messageId);
        }
        LogUtil.d(TAG, "getNextItemList ");
        return list.subList(index, limit);
    }

    static void setListener(OnMessageDbListener l) {
        sListener = l;
        if (sListener == null) {
            sHandler = null;
        } else if (sHandler == null) {
            sHandler = new Handler();
        }
    }

    /**
     * messageIdの次のページを取得する。nullなら最初。
     */
    static void fetchNextMessage(final Context con, String messageId,
            final OnMessageFetchListener listener) {
        LogUtil.d(TAG, "fetchNextMessage " + messageId + ", ");
        int page = 0;
        if (messageId != null && messageId.length() > 0) {
            // count 0 の時
            page = 1;
        }

        new AsyncTask<Object, Object, Void>() {
            private int count;

            @Override
            protected Void doInBackground(Object... params) {
                Random r = new Random();
                count = r.nextInt(9) + 1;
                Fetching f = new Fetching(con);
                RandomString rs = new RandomString();
                for (int i = 0; i < count; i++) {
                    Item item = new Item();
                    item.messageId = f.nextSessionId();
                    item.message = rs.nextString();
                    item.sendDate = new Date().getTime();
                    f.putItem(item);
                }
                f.closeDatabase();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                listener.onFetched(true, count);
            }
        }.execute();
    }

    private SecureRandom random = new SecureRandom();

    String nextSessionId() {
        return new BigInteger(130, random).toString(32);
    }

    interface OnMessageFetchListener {

        void onFetched(boolean result, int count);
    }

    interface OnMessageDbListener {

        void onUpdate(List<Item> list);
    }
}

class RandomString {

    private static final char[] symbols;

    static {
        StringBuilder tmp = new StringBuilder();
        for (char ch = '0'; ch <= '9'; ++ch) {
            tmp.append(ch);
        }
        for (char ch = 'a'; ch <= 'z'; ++ch) {
            tmp.append(ch);
        }
        symbols = tmp.toString().toCharArray();
    }

    private final Random random = new Random();

    private final char[] buf;

    RandomString() {
        Random r = new Random();
        int length = r.nextInt(99) + 1;
        if (length < 1) {
            throw new IllegalArgumentException("length < 1: " + length);
        }
        buf = new char[length];
    }

    String nextString() {
        for (int idx = 0; idx < buf.length; ++idx) {
            buf[idx] = symbols[random.nextInt(symbols.length)];
        }
        return new String(buf);
    }
}

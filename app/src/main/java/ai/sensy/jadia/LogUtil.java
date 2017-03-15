package ai.sensy.jadia;

import android.util.Log;

/** ログ出し用 */
public class LogUtil {

    private static final boolean isLog = true;

    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG && isLog) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (BuildConfig.DEBUG && isLog) {
            Log.i(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (BuildConfig.DEBUG && isLog) {
            Log.v(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (BuildConfig.DEBUG && isLog) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG && isLog) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (BuildConfig.DEBUG && isLog) {
            Log.e(tag, msg, tr);
        }
    }
}

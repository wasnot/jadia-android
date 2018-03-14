package ai.sensy.jadia;

import android.content.Context;
import android.os.Environment;
import android.os.Parcelable;
import android.os.storage.StorageManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by akihiroaida on 15/09/30.
 */
public class StorageVolumeHelper {
    public static final String EXTRA_STORAGE_VOLUME = "storage_volume";

    /**
     * ICS以降で追加された隠しメソッドを使う。
     */
    public static List<Parcelable> getStorageVolume(Context context) {
        List<Parcelable> paths = new ArrayList<Parcelable>();
        try {
            StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Method getVolumeList = sm.getClass().getDeclaredMethod("getVolumeList");
            Parcelable[] volumeList = (Parcelable[]) getVolumeList.invoke(sm);
            for (Parcelable volume : volumeList) {
                Object emulated = getValue(volume, "isEmulated");
                Object removable = getValue(volume, "isRemovable");
                Log.d("aa", "em: "+emulated+", rm:"+removable);
                boolean isEmulated = emulated != null ? (Boolean) emulated : false;
                boolean isRemovable = removable != null ? (Boolean) removable : false;
                Object state = getValue(volume, "getState");
                Log.d("aa", "state:"+state);
                if (!isEmulated
                        && isRemovable
                        && (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY
                        .equals(state)))
                    paths.add(volume);
                // String path = (String) getValue(volume, "getPath");
                // boolean removable = (Boolean) getValue(volume,
                // "isRemovable");
                // これはLollipop以上でのみ使用可能
                // String state = (String) getValue(volume, "getState");
                // if (removable) {
                // paths.add(path + ":" + userLabel + ":" + state);
                // }
                // paths.add(path + ":" + removable + ":" + state + ":" +
                // SdcardUtil.isMounted(path));
                // paths.add(path);
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return paths;
    }

    private static String getVolumeState(StorageManager sm, String path) {
        try {
            Method getVolumeList = sm.getClass().getDeclaredMethod("getVolumeState", String.class);
            return (String) getVolumeList.invoke(sm, path);
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ICS以降で追加された隠しメソッドを使う。
     */
    private static List<String> getRemovableStoragePaths(Context context) {
        List<String> paths = new ArrayList<String>();
        try {
            StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Method getVolumeList = sm.getClass().getDeclaredMethod("getVolumeList");
            Object[] volumeList = (Object[]) getVolumeList.invoke(sm);
            for (Object volume : volumeList) {
                String path = (String) getValue(volume, "getPath");
                boolean removable = (Boolean) getValue(volume, "isRemovable");
                // これはLollipop以上でのみ使用可能
                String state = (String) getValue(volume, "getState");
                // if (removable) {
                // paths.add(path + ":" + userLabel + ":" + state);
                // }
                // paths.add(path + ":" + removable + ":" + state + ":" +
                // SdcardUtil.isMounted(path));
                paths.add(path);
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return paths;
    }

    protected static Object getValue(Object target, String name) {
        try {
            Method getValue = target.getClass().getDeclaredMethod(name);
            return getValue.invoke(target);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}

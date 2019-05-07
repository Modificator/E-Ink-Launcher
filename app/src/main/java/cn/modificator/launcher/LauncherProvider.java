package cn.modificator.launcher;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.modificator.launcher.util.NoLocaleSQLiteHelper;
import cn.modificator.launcher.util.Utilities;


public class LauncherProvider extends ContentProvider {

    public static final int SCHEMA_VERSION = 1;

    public static final String AUTHORITY = "cn.modificator.launcher.settings".intern();

    private final ChangeListenerWrapper mListenerWrapper = new ChangeListenerWrapper();
    private Handler mListenerHandler;

    @Override
    public boolean onCreate() {
        mListenerHandler = new Handler(mListenerWrapper);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    public static class DatabaseHelper extends NoLocaleSQLiteHelper {
        private final Handler mWidgetHostResetHandler;
        private final Context mContext;
        private long mMaxItemId = -1;
        private long mMaxScreenId = -1;

        DatabaseHelper(Context context, Handler widgetHostResetHandler) {
            this(context, widgetHostResetHandler, Utilities.LAUNCHER_DB);


        }

        public DatabaseHelper(Context context, Handler widgetHostResetHandler, String tableName) {
            super(context, tableName, SCHEMA_VERSION);
            mWidgetHostResetHandler = widgetHostResetHandler;
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    private static class ChangeListenerWrapper implements Handler.Callback {

        private static final int MSG_LAUNCHER_PROVIDER_CHANGED = 1;
        private static final int MSG_APP_WIDGET_HOST_RESET = 2;

        private LauncherProviderChangeListener mListener;

        @Override
        public boolean handleMessage(Message msg) {
            if (mListener != null) {
                switch (msg.what) {
                    case MSG_LAUNCHER_PROVIDER_CHANGED:
                        mListener.onLauncherProviderChanged();
                        break;
                    case MSG_APP_WIDGET_HOST_RESET:
                        mListener.onAppWidgetHostReset();
                        break;
                }
            }
            return true;
        }
    }
}

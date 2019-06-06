package cn.modificator.launcher;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;

import cn.modificator.launcher.util.NoLocaleSQLiteHelper;
import cn.modificator.launcher.util.Utilities;

public class IconCache {
    private static final String TAG = "Launcher.IconCache";


    private static final class IconDB extends NoLocaleSQLiteHelper {

        private final static String TABLE_NAME = "icons";
        private final static String COLUMN_ROWID = "rowid";
        private final static String COLUMN_COMPONENT = "componentName";
        private final static String COLUMN_USER = "profileId";
        private final static String COLUMN_LAST_UPDATED = "lastUpdated";
        private final static String COLUMN_VERSION = "version";
        private final static String COLUMN_ICON = "icon";
        private final static String COLUMN_ICON_LOW_RES = "icon_low_res";
        private final static String COLUMN_ICON_COLOR = "icon_color";
        private final static String COLUMN_LABEL = "label";

        public IconDB(Context context) {
            super(context, LauncherConfig.APP_ICONS_DB, LauncherConfig.LAUNCHER_ICON_DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    COLUMN_COMPONENT + " TEXT NOT NULL, " +
                    COLUMN_USER + " INTEGER NOT NULL, " +
                    COLUMN_LAST_UPDATED + " INTEGER NOT NULL DEFAULT 0, " +
                    COLUMN_VERSION + " INTEGER NOT NULL DEFAULT 0, " +
                    COLUMN_ICON + " BLOB, " +
                    COLUMN_ICON_LOW_RES + " BLOB, " +
                    COLUMN_ICON_COLOR + " INTEGER NOT NULL DEFAULT 0, " +
                    COLUMN_LABEL + " TEXT, " +
                    "PRIMARY KEY (" + COLUMN_COMPONENT + ", " + COLUMN_USER + ") " +
                    ");");
        }

        public void delete(String whereClause, String[] whereArgs) {
            try {
                getWritableDatabase().delete(TABLE_NAME, whereClause, whereArgs);
            } catch (SQLException e) {
                Log.d(TAG, "Ignoring sqlite exception", e);
            }
        }

        public void insertOrReplace(ContentValues values){
            try {
                getWritableDatabase().insertWithOnConflict(TABLE_NAME, null,values,SQLiteDatabase.CONFLICT_REPLACE);
            } catch (SQLException e) {
                Log.d(TAG, "Ignoring sqlite exception", e);
            }
        }

        private ContentValues newContentValues(Bitmap icon, Bitmap lowResIcon, int iconColor,
                                               String label) {
            ContentValues values = new ContentValues();
            values.put(IconDB.COLUMN_ICON, Utilities.flattenBitmap(icon));
            values.put(IconDB.COLUMN_ICON_LOW_RES, Utilities.flattenBitmap(lowResIcon));
            values.put(IconDB.COLUMN_ICON_COLOR, iconColor);

            values.put(IconDB.COLUMN_LABEL, label);

            return values;
        }
    }
}

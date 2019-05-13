package cn.modificator.launcher.util;

import android.database.sqlite.SQLiteDatabase;

public class LauncherDbUtils {
    /**
     * Utility class to simplify managing sqlite transactions
     */
    public static class SQLiteTransaction implements AutoCloseable {
        private final SQLiteDatabase mDb;

        public SQLiteTransaction(SQLiteDatabase db) {
            mDb = db;
            db.beginTransaction();
        }

        public void commit() {
            mDb.setTransactionSuccessful();
        }

        @Override
        public void close() {
            mDb.endTransaction();
        }
    }
}

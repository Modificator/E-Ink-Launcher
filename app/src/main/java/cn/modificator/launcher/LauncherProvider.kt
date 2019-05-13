package cn.modificator.launcher

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Handler
import android.os.Message
import androidx.core.database.sqlite.transaction
import cn.modificator.launcher.util.NoLocaleSQLiteHelper
import cn.modificator.launcher.util.Utilities


class LauncherProvider : ContentProvider() {

    private val mListenerWrapper = ChangeListenerWrapper()
    private var mListenerHandler: Handler? = null
    protected var mOpenHelper: DatabaseHelper? = null

    override fun onCreate(): Boolean {
        mListenerHandler = Handler(mListenerWrapper)
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    @Synchronized
    protected fun createDbIfNotExists() {
        if (mOpenHelper == null) {
            mOpenHelper = DatabaseHelper(context, mListenerHandler)
            mOpenHelper!!.createEmptyDB(mOpenHelper)
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    class DatabaseHelper(private val mContext: Context, private val mWidgetHostResetHandler: Handler?, tableName: String) : NoLocaleSQLiteHelper(mContext, tableName, SCHEMA_VERSION) {
        private var mMaxItemId: Long = -1
        private var mMaxScreenId: Long = -1

        internal constructor(context: Context, widgetHostResetHandler: Handler) : this(context, widgetHostResetHandler, Utilities.LAUNCHER_DB) {


        }

        override fun onCreate(db: SQLiteDatabase) {

            mMaxItemId = 1
            mMaxScreenId = 0
            addWorkspacesTable(db, false)

            onEmptyDbCreated()
        }

        protected fun onEmptyDbCreated() {
            if (mWidgetHostResetHandler != null) {

            }
        }

        private fun addWorkspacesTable(db: SQLiteDatabase, optional: Boolean) {
            val ifNotExists = if (optional) " IF NOT EXISTS " else ""
            db.execSQL("CREATE TABLE " + ifNotExists + LauncherSettings.WorkspaceScreens.TABLE_NAME + " (" +
                    LauncherSettings.WorkspaceScreens._ID + " INTEGER PRIMARY KEY," +
                    LauncherSettings.WorkspaceScreens.SCREEN_RANK + " INTEGER," +
                    LauncherSettings.ChangeLogColumns.MODIFIED + " INTEGER NOT NULL DEFAULT 0" +
                    ");")
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        }

        override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            super.onDowngrade(db, oldVersion, newVersion)
            createEmptyDB(db)
        }

        fun createEmptyDB(db: SQLiteDatabase) {
            db.transaction {
                execSQL("DROP TABLE IF EXISTS " + LauncherSettings.Favorites.TABLE_NAME)
                execSQL("DROP TABLE IF EXISTS " + LauncherSettings.WorkspaceScreens.TABLE_NAME)
                onCreate(this)
            }

        }
    }

    private class ChangeListenerWrapper : Handler.Callback {

        private val mListener: LauncherProviderChangeListener? = null

        override fun handleMessage(msg: Message): Boolean {
            if (mListener != null) {
                when (msg.what) {
                    MSG_LAUNCHER_PROVIDER_CHANGED -> mListener.onLauncherProviderChanged()
                    MSG_APP_WIDGET_HOST_RESET -> mListener.onAppWidgetHostReset()
                }
            }
            return true
        }

        companion object {

            private val MSG_LAUNCHER_PROVIDER_CHANGED = 1
            private val MSG_APP_WIDGET_HOST_RESET = 2
        }
    }

    companion object {

        val SCHEMA_VERSION = 1

        val AUTHORITY = "cn.modificator.launcher.settings".intern()
    }
}

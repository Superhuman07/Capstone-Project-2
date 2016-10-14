package admin.com.UnSpammer.DataBaseHelpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Admin on 10/10/2016.
 */
public class ListDBHelper extends SQLiteOpenHelper {
    private static int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "blacklist.db";

    public ListDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createBlacklistTable(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if (i1 != 1) {
            String dropTable = "DROP TABLE IF EXISTS " + ListsContract.BlackListEntry.TABLE_NAME;
            sqLiteDatabase.execSQL(dropTable);
            onCreate(sqLiteDatabase);
        }
    }
    // Creating the blacklist table
    private void createBlacklistTable (SQLiteDatabase db) {
        String blacklistTableQuery =
                "CREATE TABLE " + ListsContract.BlackListEntry.TABLE_NAME + " (" +
                        ListsContract.BlackListEntry.COLUMN_NUMBER + " TEXT NOT NULL PRIMARY KEY, " +
                        ListsContract.BlackListEntry.COLUMN_NAME + " TEXT );";
        db.execSQL(blacklistTableQuery);
    }
}

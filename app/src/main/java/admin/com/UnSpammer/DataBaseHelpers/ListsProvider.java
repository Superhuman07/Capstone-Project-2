package admin.com.UnSpammer.DataBaseHelpers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class ListsProvider extends ContentProvider {

    public static final int BLACKLIST = 1;
    public static final int BLACKLIST_ID = 2;
    private final String TAG = this.getClass().getSimpleName();

    public ListsProvider() {
    }

    public static final UriMatcher sUriMatcher = buildUriMatcher();
    private ListDBHelper mDBHelper;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int rows;
        switch (sUriMatcher.match(uri)) {
            case BLACKLIST:
                rows = db.delete(ListsContract.BlackListEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;

            case BLACKLIST_ID:
                rows = db.delete(ListsContract.BlackListEntry.COLUMN_NUMBER,
                        selection, selectionArgs);
                break;

            default: throw new UnsupportedOperationException("Unknown Uri: " + uri.toString());
        }
        if (selection== null || rows !=0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case BLACKLIST:
                return ListsContract.BlackListEntry.CONTENT_TYPE;
            case BLACKLIST_ID:
                return ListsContract.BlackListEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri! ");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        long id;
        Uri retUri;
        switch (sUriMatcher.match(uri)) {
            case BLACKLIST:
                id = db.insert(ListsContract.BlackListEntry.TABLE_NAME,
                        null,values);
                if (id>0) {
                    retUri = ListsContract.BlackListEntry.buildBlockedNumberUri(id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows!");
                }
                break;

            default: throw new UnsupportedOperationException("Unknown URI!");
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return retUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case BLACKLIST:
                db.beginTransaction();
                int retCount = 0;
                try {
                    for (ContentValues value: values) {
                        try {
                            long id = db.insert(ListsContract.BlackListEntry.TABLE_NAME,
                                    null,value);
                            if (id != -1) {
                                retCount++;
                            }
                        } catch (SQLiteConstraintException e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return retCount;

            default: throw new UnsupportedOperationException("Unknown URI");
        }
    }

    @Override
    public boolean onCreate() {
        mDBHelper = new ListDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case BLACKLIST:
                cursor = db.query(
                        ListsContract.BlackListEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                Log.e(TAG, "In BlackLIST query");
                break;

            case BLACKLIST_ID:
                cursor = db.query(ListsContract.BlackListEntry.TABLE_NAME,
                        projection,
                        ListsContract.BlackListEntry.COLUMN_NUMBER + "=?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                Log.e(TAG, "In BlackLIST_ID query");
                break;

            default: throw new UnsupportedOperationException("Unknown URI!" + uri.toString());
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return 0;
    }

    public static UriMatcher buildUriMatcher() {
        String content = ListsContract.CONTENT_AUTHORITY;
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(content, ListsContract.PATH_ALLOWEDLIST, BLACKLIST);
        matcher.addURI(content, ListsContract.PATH_ALLOWEDLIST + "/#", BLACKLIST_ID);
        return matcher;
    }
}

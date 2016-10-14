package admin.com.UnSpammer.DataBaseHelpers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Admin on 10/10/2016.
 */
public class ListsContract {
    // Setting out the content authority
    public static final String CONTENT_AUTHORITY = "admin.com.UnSpammer";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ALLOWEDLIST = "allow";

    public static final class BlackListEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ALLOWEDLIST).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ALLOWEDLIST;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ALLOWEDLIST;

        // Setting up the columns
        public static final String TABLE_NAME = "allowedlist";
        public static final String COLUMN_NAME = "contactName";
        public static final String COLUMN_NUMBER = "contactNumber";

        // Function to build Uri to find contact by its number
        public static Uri buildBlockedNumberUri (long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}

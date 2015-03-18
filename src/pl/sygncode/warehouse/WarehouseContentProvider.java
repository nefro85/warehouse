package pl.sygncode.warehouse;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class WarehouseContentProvider extends ContentProvider implements Res {

    public static final UriMatcher MATCHER;

    public static Uri item(long id) {
        return Uri.withAppendedPath(ITEM, String.valueOf(id));
    }

    public static Uri storage(long id) {
        return Uri.withAppendedPath(STORAGE, String.valueOf(id));
    }

    interface Match {
        int ITEM_LIST = 1;
        int ITEM_BY_ID = 2;
        int STORAGE_LIST = 3;
        int STORAGE_BY_ID = 4;
    }

    static {
        MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        MATCHER.addURI(AUTHORITY, "item", Match.ITEM_LIST);
        MATCHER.addURI(AUTHORITY, "item/#", Match.ITEM_BY_ID);
        MATCHER.addURI(AUTHORITY, "storage", Match.STORAGE_LIST);
        MATCHER.addURI(AUTHORITY, "storage/#", Match.STORAGE_BY_ID);
    }

    private DatabaseHandler dbHnd;

    @Override
    public boolean onCreate() {

        dbHnd = new DatabaseHandler(getContext());
        return true;
    }

    String tab(Uri uri) {
        int match = MATCHER.match(uri);

        switch (match) {
            case Match.ITEM_BY_ID:
            case Match.ITEM_LIST:
                return Item.TABLE_NAME;
            case Match.STORAGE_BY_ID:
            case Match.STORAGE_LIST:
                return Storage.TABLE_NAME;
        }
        throw new IllegalStateException();
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor c;

        final SQLiteDatabase db = dbHnd.getReadableDatabase();

        c = db.query(tab(uri), projection, selection, selectionArgs, null, null, sortOrder);


        return c;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {


        long idx = dbHnd.getWritableDatabase().insert(tab(uri), null, values);
        switch (MATCHER.match(uri)) {
            case Match.ITEM_BY_ID:
            case Match.ITEM_LIST:
                return item(idx);
            case Match.STORAGE_BY_ID:
            case Match.STORAGE_LIST:
                return storage(idx);
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}

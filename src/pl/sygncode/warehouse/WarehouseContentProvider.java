package pl.sygncode.warehouse;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by sygnowsk on 2015-02-06.
 */
public class WarehouseContentProvider extends ContentProvider {

    public static final String PKG = "pl.sygncode.warehouse";
    public static final String NAME = "warehouseDataProvider";
    public static final String CONTENT = "content://" + PKG + "." + NAME + "/";
    public static final Uri ITEM = Uri.parse(CONTENT + Case.ITEM.name() + "/");
    public static final UriMatcher MATCHER;

    enum Case {
        ITEM
    }

    static {
        MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        MATCHER.addURI(PKG, "item/#", Case.ITEM.ordinal());
    }

    public static class Strict {

    }

    private DatabaseHandler dbHnd;

    @Override
    public boolean onCreate() {

        dbHnd = new DatabaseHandler(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor c = null;

        final Case onCase = Case.values()[MATCHER.match(uri)];
        switch (onCase) {
            case ITEM:
                c = dbHnd.getReadableDatabase().query(onCase.name(), projection, selection, selectionArgs, null, null, sortOrder);
                break;
        }

        return c;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
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

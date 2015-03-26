package pl.sygncode.warehouse;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class WarehouseContentProvider extends ContentProvider implements Res {

    public static final UriMatcher MATCHER;
    public static final Uri ITEM = Uri.parse(CONTENT + "item");
    public static final Uri STORAGE = Uri.parse(CONTENT + "storage");

    public static Uri item(long id) {
        return Uri.withAppendedPath(ITEM, String.valueOf(id));
    }

    public static Uri storage(long id) {
        return Uri.withAppendedPath(STORAGE, String.valueOf(id));
    }

    public static Uri storageChildren(long superStorageId) {
        return Uri.withAppendedPath(STORAGE, "children/" + String.valueOf(superStorageId));
    }

    public static Uri itemByStorage(long storageId) {
        return Uri.withAppendedPath(ITEM, "storage/" + storageId);
    }

    public static Uri search(String search) {
        return Uri.parse(CONTENT + "search/" + search);
    }

    interface Match {
        int ITEM_LIST = 1;
        int ITEM_BY_ID = 2;
        int STORAGE_LIST = 3;
        int STORAGE_BY_ID = 4;
        int STORAGE_CHILDREN = 5;
        int ITEM_BY_STORAGE = 6;
        int SEARCH = 7;
    }

    static {
        MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        MATCHER.addURI(AUTHORITY, "item", Match.ITEM_LIST);
        MATCHER.addURI(AUTHORITY, "item/#", Match.ITEM_BY_ID);
        MATCHER.addURI(AUTHORITY, "storage", Match.STORAGE_LIST);
        MATCHER.addURI(AUTHORITY, "storage/#", Match.STORAGE_BY_ID);
        MATCHER.addURI(AUTHORITY, "storage/children/#", Match.STORAGE_CHILDREN);
        MATCHER.addURI(AUTHORITY, "item/storage/#", Match.ITEM_BY_STORAGE);
        MATCHER.addURI(AUTHORITY, "search/*", Match.SEARCH);
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
            case Match.ITEM_BY_STORAGE:
                return Item.TABLE_NAME;
            case Match.STORAGE_BY_ID:
            case Match.STORAGE_LIST:
            case Match.STORAGE_CHILDREN:
                return Storage.TABLE_NAME;
        }
        throw new IllegalStateException();
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor c;


        String last = uri.getLastPathSegment();
        int match = MATCHER.match(uri);
        switch (match) {
            case Match.STORAGE_CHILDREN:

                if (!"0".equals(last)) {
                    selection = Storage.SUPER_ID + "=" + last;
                }

                break;
            case Match.STORAGE_BY_ID:
                if (!"0".equals(last)) {
                    selection = Storage.ID + "=" + last;
                } else {
                    selection = Storage.SUPER_ID + " IS NULL";
                }
                break;
            case Match.ITEM_BY_STORAGE:
                if (!"0".equals(last)) {
                    selection = Item.STORAGE_ID + "=" + last;
                }
                break;
        }

        final SQLiteDatabase db = dbHnd.getReadableDatabase();
        if (match == Match.SEARCH) {

            String like = Item.NAME + " LIKE " + "'" + last + "%'";
            c = db.query(Item.TABLE_NAME, new String[]{Item.STORAGE_ID, Item.NAME}, like, null, null, null, Item.NAME);

            Set<Long> ids = new HashSet<Long>();
            if (c.moveToFirst()) {
                do {
                    ids.add(c.getLong(c.getColumnIndexOrThrow(Item.STORAGE_ID)));
                } while (c.moveToNext());
            }
            c.close();

            String in = "";
            for (Iterator<Long> it = ids.iterator(); it.hasNext(); ) {
                in += it.next();
                if (it.hasNext()) {
                    in += ",";
                }
            }

            String sel = Storage.ID + " IN (" + in + ")";
            c = db.query(Storage.TABLE_NAME, Storage.PROJ, sel, null, null, null, Storage.FLAG);

        } else {
            c = db.query(tab(uri), projection, selection, selectionArgs, null, null, sortOrder);
        }
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
        switch (MATCHER.match(uri)) {
            case Match.STORAGE_BY_ID:

                SQLiteDatabase db = dbHnd.getReadableDatabase();
                String id = uri.getLastPathSegment();
                int deleteCount = db.delete(Storage.TABLE_NAME, Storage.ID + "=" + id, null);

                if (!"0".equals(id)) {
                    String where = Storage.SUPER_ID + "=" + id;
                    deleteCount += db.delete(Storage.TABLE_NAME, where, null);

                    deleteCount += db.delete(Item.TABLE_NAME, Item.STORAGE_ID + "=" + id, null);
                }

                return deleteCount;

        }

        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}

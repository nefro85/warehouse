package pl.sygncode.warehouse;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHandler extends SQLiteOpenHelper {


    public DatabaseHandler(Context context) {
        super(context, "data.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "";
        sql += "CREATE TABLE IF NOT EXISTS " + Storage.TABLE_NAME + "(";
        sql += Storage.ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" + ",";
        sql += Storage.SUPER_ID + " INTEGER" + ",";
        sql += Storage.NAME + " TEXT" + ",";
        sql += Storage.FLAG + " INTEGER" + ",";
        sql += Storage.SEQUENCE + " INTEGER" + ",";
        sql += Storage.COUNT + " INTEGER";
        sql += ");";

        db.execSQL(sql);
        sql = "";

        sql += "CREATE TABLE IF NOT EXISTS " + Item.TABLE_NAME + "(";
        sql += Item.ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" + ",";
        sql += Item.STORAGE_ID + " INTEGER" + ",";
        sql += Item.NAME + " TEXT" + ",";
        sql += Item.TYPE_NAME + " TEXT";
        sql += ");";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

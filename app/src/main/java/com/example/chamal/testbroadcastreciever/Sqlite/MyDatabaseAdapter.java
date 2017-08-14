package com.example.chamal.testbroadcastreciever.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Chamal on 11/1/2016.
 */


public class MyDatabaseAdapter{
    DBHelper helper;

    public MyDatabaseAdapter(Context context) {
        helper = new DBHelper(context);
    }

    public long insertData(String contact, String videoId){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.CONTACT, contact);
        System.out.println("insertData 1 : "+contact);

        contentValues.put(DBHelper.VIDEOID, videoId);
        System.out.println("insertData 2 : "+videoId);

        long id = db.insert(DBHelper.TABLE_NAME, null, contentValues);
        db.close();
        return id;
    }

    public String getVideoId(String contact){
        System.out.println("getVideoId: "+contact);
        String vId = null;
        String[] columns = {DBHelper.VIDEOID};
        SQLiteDatabase db = helper.getReadableDatabase();

//        Cursor cursor = db.rawQuery("SELECT "+DBHelper.VIDEOID+" FROM "+DBHelper.TABLE_NAME+" WHERE "+DBHelper.CONTACT+" = ?", new String[]{contact});

        Cursor cursor = db.query(true, DBHelper.TABLE_NAME, columns, DBHelper.CONTACT+" LIKE ?",
                new String[] {"%" + contact}, null, null, null,
                null);

            if (cursor.getCount() == 0) {
                vId = "trailer";
            } else {
                cursor.moveToLast();
                vId = cursor.getString(0);
            }

        System.out.println("Get Video ID "+vId);

        cursor.close();
        db.close();
        return vId;
    }

    class DBHelper extends SQLiteOpenHelper {

     private static final String DATABASE_NAME = "videoCall.db";
     private static final String TABLE_NAME = "ContactInfo";
     private static final int DATABASE_VERSION = 1;
     private static final String CONTACT = "contact";
     private static final String VIDEOID = "videoId";
     private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(id INTEGER PRIMARY KEY AUTOINCREMENT, "+CONTACT+" VARCHAR(255), "+VIDEOID+" VARCHAR(255));";
     private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
     private Context context;

     public DBHelper(Context context) {
         super(context, DATABASE_NAME, null, DATABASE_VERSION);
         this.context = context;
     }

     @Override
     public void onCreate(SQLiteDatabase db) {
         try {
             db.execSQL(CREATE_TABLE);
         } catch (SQLException e) {
             e.printStackTrace();
         }
     }

     @Override
     public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         try {
             db.execSQL(DROP_TABLE);
             onCreate(db);
         } catch (SQLException e) {
             e.printStackTrace();
         }
     }
 }
}

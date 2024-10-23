package org.techtown.jenstar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MarkerDBHelper extends SQLiteOpenHelper {


    // 데이터베이스 정보
    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 5;

    // 테이블 정보
    private static final String TABLE_NAME = "markers";
    private static final String COLUMN_ID = "id";
    private static  final String COLUMN_TITLE = "title";
    private static final String COLUMN_SNIPPET = "snippet";
    private static final String COLUMN_LAT = "lat";
    private static final String COLUMN_LNG = "lng";


    // 생성자
    public MarkerDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // 테이블 생성
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " TEXT,"
                + COLUMN_TITLE + " TEXT PRIMARY KEY,"
                + COLUMN_SNIPPET + " TEXT,"
                + COLUMN_LAT + " REAL,"
                + COLUMN_LNG + " REAL"
                + ")";
        db.execSQL(CREATE_USERS_TABLE);
    }

    // 데이터베이스 업그레이드 시 테이블 재생성
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // 외래키 활성화
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // 새로운 사용자 추가
    public boolean addMarker(String id, String title, String snippet, String lat, String lng){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_ID},
                COLUMN_ID + "=? AND " + COLUMN_TITLE + "=? AND " + COLUMN_SNIPPET + "=? AND " + COLUMN_LAT + "=? AND " + COLUMN_LNG + "=?",
                new String[]{id, title, snippet, lat, lng},
                null, null, null);
        int count = cursor.getCount();
        cursor.close();

        long result;

        if(count == 0) {
            values.put(COLUMN_ID, id);
            values.put(COLUMN_TITLE, title);
            values.put(COLUMN_SNIPPET, snippet);
            values.put(COLUMN_LAT, lat);
            values.put(COLUMN_LNG, lng);

            result = db.insert(TABLE_NAME, null, values);
            db.close();
            Log.e("dasdas", "addMarker: " + result );
        }
        else {
            result = -1;
        }

        db.close();
        return result != -1;
    }

}

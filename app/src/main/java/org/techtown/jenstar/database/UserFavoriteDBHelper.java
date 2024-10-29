package org.techtown.jenstar.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class UserFavoriteDBHelper extends SQLiteOpenHelper {

    // 데이터베이스 정보
    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 8;

    // 테이블 정보
    private static final String TABLE_NAME = "user_favorites";
    private static final String COLUMN_USER_ID = "user_id"; // 사용자 ID
    private static final String COLUMN_MARKER_TITLE = "marker_title"; // 마커 ID

    // 생성자
    public UserFavoriteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // 테이블 생성
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FAVORITES_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_USER_ID + " TEXT,"
                + COLUMN_MARKER_TITLE + " TEXT,"
                + "PRIMARY KEY (" + COLUMN_USER_ID + ", " + COLUMN_MARKER_TITLE + ")"
                + ")";
        db.execSQL(CREATE_FAVORITES_TABLE);
    }

    // 데이터베이스 업그레이드 시 테이블 재생성
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // 테이블 존재 여부 확인
    public void createTableIfNotExists() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{TABLE_NAME});

        if (cursor.getCount() == 0) {
            // 테이블이 존재하지 않으면 생성
            onCreate(db);
        }

        cursor.close();
        db.close();
    }

    // 즐겨찾기 추가
    public boolean addFavorite(String userId, String markerTitle) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_MARKER_TITLE, markerTitle);

        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result != -1; // -1이면 삽입 실패
    }

    // 특정 사용자의 즐겨찾기 마커 가져오기
    public List<String> getUserFavorites(String userId) {
        List<String> favoriteMarkers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_MARKER_TITLE},
                COLUMN_USER_ID + "=?",
                new String[]{userId},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String markerTitle = cursor.getString(cursor.getColumnIndex(COLUMN_MARKER_TITLE));
                favoriteMarkers.add(markerTitle);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return favoriteMarkers;
    }

    // 특정 사용자의 특정 마커 즐겨찾기 삭제
    public boolean removeFavorite(String userId, String markerTitle) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_NAME,
                COLUMN_USER_ID + "=? AND " + COLUMN_MARKER_TITLE + "=?",
                new String[]{userId, markerTitle});
        db.close();
        return rowsAffected > 0; // 삭제 성공 여부 반환
    }


}

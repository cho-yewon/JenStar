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

public class MarkerDBHelper extends SQLiteOpenHelper {


    // 데이터베이스 정보
    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 12;

    // 테이블 정보
    private static final String TABLE_NAME = "markers";
    private static final String COLUMN_ID = "id";
    private static  final String COLUMN_TITLE = "title";
    private static final String COLUMN_SNIPPET = "snippet";
    private static final String COLUMN_LAT = "lat";
    private static final String COLUMN_LNG = "lng";

    public static class Marker {
        public String id;
        public String title;
        public String snippet;
        public Double lat;
        public Double lng;
        public String roadAddress;

        public Marker(String id, String title, String snippet, Double lat, Double lng) {
            this.id = id;
            this.title = title;
            this.snippet = snippet;
            this.lat = lat;
            this.lng = lng;
        }

        public String getRoadAddress() {
            return roadAddress;
        }

        public void setRoadAddress(String roadAddress) {
            this.roadAddress = roadAddress;
        }
    }


    // 생성자
    public MarkerDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
        if(!isTableExists(db, TABLE_NAME))
            onCreate(db);
    }

    private boolean isTableExists(SQLiteDatabase db, String tableName) {
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{tableName});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
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

    private boolean isColumnExists(SQLiteDatabase db, String tableName, String columnName) {
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
        boolean exists = false;
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String currentColumn = cursor.getString(cursor.getColumnIndex("name"));
            if (currentColumn.equals(columnName)) {
                exists = true;
                break;
            }
        }
        cursor.close();
        return exists;
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

    //사용자 마커 정보 변경
    public boolean updateMarker(String id, String title, String snippet, String lat, String lng) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        long result;

        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_SNIPPET, snippet);
        values.put(COLUMN_LAT, lat);
        values.put(COLUMN_LNG, lng);

        result = db.update(TABLE_NAME, values, COLUMN_ID + "=? AND " + COLUMN_TITLE + "=?", new String[]{id, title});

        db.close();
        return result != -1;
    }

    //마커 삭제
    public boolean deleteMarker(String id, String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(TABLE_NAME, COLUMN_ID + "=? AND " + COLUMN_TITLE + "=?", new String[]{id, title});
        db.close();
        return deletedRows > 0; // 삭제된 행이 1개 이상이면 성공으로 간주
    }

    //마커 정보 가져오기
    public List<Marker> getMarkers() {
        List<Marker> markerList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                null,null,null,
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                // 각 컬럼 값을 가져옴
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                @SuppressLint("Range") String snippet = cursor.getString(cursor.getColumnIndex(COLUMN_SNIPPET));
                @SuppressLint("Range") Double lat = cursor.getDouble(cursor.getColumnIndex(COLUMN_LAT));
                @SuppressLint("Range") Double lng = cursor.getDouble(cursor.getColumnIndex(COLUMN_LNG));

                // Marker 객체 생성 후 리스트에 추가
                Marker marker = new Marker(id, title, snippet, lat, lng);
                markerList.add(marker);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return markerList;
    }

    //승인해야할 마커 가져오기
    public List<Marker> getApproveMarkers() {
        List<Marker> markerApproveList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                null,null,null,
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                // 각 컬럼 값을 가져옴
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                @SuppressLint("Range") String snippet = cursor.getString(cursor.getColumnIndex(COLUMN_SNIPPET));
                @SuppressLint("Range") Double lat = cursor.getDouble(cursor.getColumnIndex(COLUMN_LAT));
                @SuppressLint("Range") Double lng = cursor.getDouble(cursor.getColumnIndex(COLUMN_LNG));

                // Marker 객체 생성 후 리스트에 추가
                Marker marker = new Marker(id, title, snippet, lat, lng);
                markerApproveList.add(marker);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return markerApproveList;
    }

    public Marker getMarkerById(String markerTitle) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                null,
                COLUMN_TITLE + "=?",
                new String[]{markerTitle},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
            @SuppressLint("Range") String snippet = cursor.getString(cursor.getColumnIndex(COLUMN_SNIPPET));
            @SuppressLint("Range") Double lat = cursor.getDouble(cursor.getColumnIndex(COLUMN_LAT));
            @SuppressLint("Range") Double lng = cursor.getDouble(cursor.getColumnIndex(COLUMN_LNG));
            cursor.close();
            return new Marker(id, title, snippet, lat, lng);
        }
        return null;
    }

    public List<String> getFavoriteMarkers(String userId, UserFavoriteDBHelper favoriteDBHelper) {
        List<String> favoriteMarkers = favoriteDBHelper.getUserFavorites(userId);
        return favoriteMarkers;
    }


}

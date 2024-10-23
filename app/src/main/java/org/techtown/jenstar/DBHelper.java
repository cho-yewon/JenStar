package org.techtown.jenstar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    // 데이터베이스 정보
    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 5;

    // 테이블 정보
    private static final String TABLE_NAME = "users";
    private static final String COLUMN_ID = "id";
    private static  final String COLUMN_AUTHORITY = "authority";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_BIRTH = "birth";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_EMAIL = "email";


    // 생성자
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // 테이블 생성
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " TEXT PRIMARY KEY,"
                + COLUMN_AUTHORITY + " NUMBER,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_BIRTH + " TEXT,"
                + COLUMN_PHONE + " TEXT,"
                + COLUMN_EMAIL + " TEXT"
                + ")";
        db.execSQL(CREATE_USERS_TABLE);
    }

    // 데이터베이스 업그레이드 시 테이블 재생성
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // 새로운 사용자 추가
    public boolean addUser(String id, String password, String username, String birth, String phone, String email ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, id);
        values.put(COLUMN_AUTHORITY, 1);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_BIRTH, birth);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_EMAIL, email);

        long result = db.insert(TABLE_NAME, null, values);
        if(result == -1) {
            Log.e("DBHelper", "Insertion failed for user: " + id);
        } else {
            Log.i("DBHelper", "Insertion succeeded for user: " + id);
        }
        db.close();
        return result != -1; // -1은 실패
    }

    // 사용자 로그인 검증
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_ID},
                COLUMN_ID + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password},
                null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    //사용자 권한 탐색
    public int checkAuthority(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_AUTHORITY},
                COLUMN_ID + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password},
                null, null, null);
        int authorityNumber = -1;
        if (cursor != null && cursor.moveToFirst()) {
            // 첫 번째 행으로 이동하여 값 가져오기
            authorityNumber = cursor.getInt(0);
            Log.d("DBHelper", "가져온 authority 값: " + authorityNumber);
        }
        cursor.close();
        db.close();
        return authorityNumber;
    }

    // 회원가입 아이디 중복 체크
    public boolean duplicateID(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_ID},
                COLUMN_ID + "=?",
                new String[]{id},
                null, null, null);

        boolean idCheck = (cursor.getCount() > 0);

        cursor.close();
        db.close();

        return idCheck;
    }
}

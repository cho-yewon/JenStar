package org.techtown.jenstar.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 8;

    private static final String TABLE_NAME = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_AUTHORITY = "authority";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_BIRTH = "birth";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_EMAIL = "email";

    private final Context context;
    private static final String DB_PATH = "/data/data/org.techtown.jenstar/databases/";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

        if (!checkDatabaseExists()) {
            try {
                copyDatabase();
            } catch (Exception e) {
                Log.e("DBHelper", "Error copying database: " + e.getMessage());
            }
        }
    }

    private boolean checkDatabaseExists() {
        File dbFile = new File(DB_PATH + DATABASE_NAME);
        return dbFile.exists();
    }

    private void copyDatabase() throws Exception {
        InputStream input = context.getAssets().open(DATABASE_NAME);
        String outFileName = DB_PATH + DATABASE_NAME;
        OutputStream output = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = input.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }

        output.flush();
        output.close();
        input.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
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

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 기존 데이터베이스를 유지하고 업그레이드할 수 있도록 로직을 추가할 수 있습니다.
        // 필요시 새로운 열을 추가하거나, 데이터베이스 변경 사항을 반영합니다.
        if (oldVersion < newVersion) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN new_column TEXT DEFAULT ''"); // 예시 코드
            Log.i("DBHelper", "Database upgraded to version " + newVersion);
        }
    }

    public boolean addUser(String id, String password, String username, String birth, String phone, String email) {
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
        if (result == -1) {
            Log.e("DBHelper", "Insertion failed for user: " + id);
        } else {
            Log.i("DBHelper", "Insertion succeeded for user: " + id);
        }
        db.close();
        return result != -1;
    }

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

    public int checkAuthority(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_AUTHORITY},
                COLUMN_ID + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password},
                null, null, null);
        int authorityNumber = -1;
        if (cursor != null && cursor.moveToFirst()) {
            authorityNumber = cursor.getInt(0);
            Log.d("DBHelper", "가져온 authority 값: " + authorityNumber);
        }
        cursor.close();
        db.close();
        return authorityNumber;
    }

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

package com.example.habittracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * @author Aaron Sinn
 * DbHelper provides methods for setting up and interacting with the database
 */
public class DbHelper extends SQLiteOpenHelper {

    private final String createUsersTable = "CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT)";
    private final String createHabitsTable = "CREATE TABLE " +
            "habits(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT, type TEXT, frequency TEXT," +
            "count TEXT, user_id INTEGER, FOREIGN KEY(user_id) REFERENCES course(id) ON DELETE CASCADE)";
    public DbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createUsersTable);
        db.execSQL(createHabitsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * @param username
     * @return all information about a user
     */
    public Cursor getUserByUsername(String username){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM users WHERE username=?", new String[]{username});
        return cursor;
    }

    /**
     *
     * @param username
     * @param password
     * @return all information about a user
     */
    public Cursor getUserByUsernameAndPassword(String username, String password) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM users " +
                "WHERE username=? AND password = ?", new String[]{username, password});
        return cursor;
    }

    /**
     * @param username
     * @param password
     * @return ID for newly created row or -1 on error
     */
    public long addUser(String username, String password) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        return sqLiteDatabase.insert("users", null, contentValues);
    }
}

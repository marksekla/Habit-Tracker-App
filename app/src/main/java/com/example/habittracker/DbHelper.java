package com.example.habittracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * @author Aaron Sinn & Mark Sekla
 * DbHelper provides methods for setting up and interacting with the database
 */
public class DbHelper extends SQLiteOpenHelper {

    private final String createUsersTable = "CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT)";
    private final String createHabitsTable = "CREATE TABLE " +
            "habits(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT, type TEXT, frequency TEXT," +
            "count TEXT, user_id INTEGER, FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE)";

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
        // Handle schema migrations here
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

    /**
     * @param habit
     * @return True on successful insertion, false on error
     */
    public boolean addHabit(Habit habit) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", habit.getTitle());
        values.put("description", habit.getDescription());
        values.put("type", habit.getType());
        values.put("frequency", habit.getFrequency());
        values.put("count", habit.getCount());
        values.put("user_id", habit.getUserId());

        long result = sqLiteDatabase.insert("habits", null, values);
        if (result == -1) {
            Log.e("DB_ERROR", "Insertion failed");
            return false;
        } else {
            Log.d("DB_SUCCESS", "Inserted at row: " + result);
            return true;
        }
    }

    /**
     * @param userId
     * @return ArrayList of all the habits for the user_id
     */
    public ArrayList<Habit> getHabitsByUserId(int userId) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        ArrayList<Habit> habits = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM habits WHERE user_id = ?",
                new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                String frequency = cursor.getString(cursor.getColumnIndexOrThrow("frequency"));
                int count = cursor.getInt(cursor.getColumnIndexOrThrow("count"));

                Habit habit = new Habit(id, userId, title, description, type, frequency, count);
                habits.add(habit);
            } while (cursor.moveToNext());
        }
        return habits;
    }

    /**
     * Get habits by user ID and day of the week
     * @param userId the user ID
     * @param day the day of the week to filter by
     * @return ArrayList of habits for the specified user and day
     */
    public ArrayList<Habit> getHabitsByUserIdAndDay(int userId, String day) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        ArrayList<Habit> habits = new ArrayList<>();

        // Convert abbreviated day name from tab to full day name if needed
        String fullDayName = day;
        if (day.equals("Mon")) fullDayName = "Monday";
        else if (day.equals("Tue")) fullDayName = "Tuesday";
        else if (day.equals("Wed")) fullDayName = "Wednesday";
        else if (day.equals("Thu")) fullDayName = "Thursday";
        else if (day.equals("Fri")) fullDayName = "Friday";
        else if (day.equals("Sat")) fullDayName = "Saturday";
        else if (day.equals("Sun")) fullDayName = "Sunday";

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM habits WHERE user_id = ? AND frequency = ?",
                new String[]{String.valueOf(userId), fullDayName});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                String frequency = cursor.getString(cursor.getColumnIndexOrThrow("frequency"));
                int count = cursor.getInt(cursor.getColumnIndexOrThrow("count"));

                Habit habit = new Habit(id, userId, title, description, type, frequency, count);
                habits.add(habit);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return habits;
    }

    /**
     * Deletes a habit from the database by its ID
     * @param habitId the ID of the habit to delete
     * @return true if successfully deleted, false otherwise
     */
    public boolean deleteHabit(int habitId) {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete("habits", "id = ?", new String[]{String.valueOf(habitId)});
        return result > 0;
    }

    /**
     * Updates an existing habit in the database
     * @param habit the habit with updated values
     * @return true if successfully updated, false otherwise
     */
    public boolean updateHabit(Habit habit) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", habit.getTitle());
        values.put("description", habit.getDescription());
        values.put("type", habit.getType());
        values.put("frequency", habit.getFrequency());
        values.put("count", habit.getCount());

        int result = db.update("habits", values, "id = ?", new String[]{String.valueOf(habit.getId())});
        return result > 0;
    }

    /**
     * Nukes database.
     */
    public void ResetDatabase(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS users");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS habits");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS courses");
    }
}

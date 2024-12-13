package com.example.mygameproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class HighScoreDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SequenceGame.db";
    private static final int DATABASE_VERSION = 1;

    // Table and column names
    private static final String TABLE_HIGH_SCORES = "high_scores";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_SCORE = "score";

    // Create table SQL
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_HIGH_SCORES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_NAME + " TEXT," +
                    COLUMN_SCORE + " INTEGER)";

    // Drop table SQL
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_HIGH_SCORES;

    public HighScoreDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    // Method to insert a new high score
    public long insertHighScore(String name, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_SCORE, score);
        return db.insert(TABLE_HIGH_SCORES, null, values);
    }

    // Method to get top 5 high scores
    public List<HighScore> getTopFiveScores() {
        List<HighScore> highScores = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_NAME,
                COLUMN_SCORE
        };

        String sortOrder = COLUMN_SCORE + " DESC";
        String limit = "5";

        Cursor cursor = db.query(
                TABLE_HIGH_SCORES,
                projection,
                null,
                null,
                null,
                null,
                sortOrder,
                limit
        );

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            int score = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE));
            highScores.add(new HighScore(name, score));
        }
        cursor.close();

        return highScores;
    }

    // Method to check if score is in top 5
    public boolean isTopFiveScore(int score) {
        List<HighScore> topScores = getTopFiveScores();
        if (topScores.size() < 5) return true;
        return score > topScores.get(topScores.size() - 1).getScore();
    }

    // High Score model class
    public static class HighScore {
        private String name;
        private int score;

        public HighScore(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() { return name; }
        public int getScore() { return score; }
    }
}
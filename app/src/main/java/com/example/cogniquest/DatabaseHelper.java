package com.example.cogniquest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CogniQuest.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_QUIZZES = "quizzes";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESC = "description";
    public static final String COLUMN_QUESTIONS_COUNT = "questions_count";
    public static final String COLUMN_DIFFICULTY = "difficulty";

    // Quotes Table
    public static final String TABLE_QUOTES = "quotes";
    public static final String COLUMN_QUOTE_ID = "id";
    public static final String COLUMN_QUOTE_TEXT = "text";
    public static final String COLUMN_QUOTE_AUTHOR = "author";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_QUIZZES_TABLE = "CREATE TABLE " + TABLE_QUIZZES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_DESC + " TEXT,"
                + COLUMN_QUESTIONS_COUNT + " INTEGER,"
                + COLUMN_DIFFICULTY + " TEXT" + ")";
        db.execSQL(CREATE_QUIZZES_TABLE);
        
        String CREATE_QUOTES_TABLE = "CREATE TABLE " + TABLE_QUOTES + "("
                + COLUMN_QUOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_QUOTE_TEXT + " TEXT,"
                + COLUMN_QUOTE_AUTHOR + " TEXT" + ")";
        db.execSQL(CREATE_QUOTES_TABLE);
        
        // Insert dummy data initially
        insertDummyData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZZES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUOTES);
        onCreate(db);
    }
    
    private void insertDummyData(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, "Ancient History Mysteries");
        values.put(COLUMN_DESC, "Explore the unknown events of ancient times.");
        values.put(COLUMN_QUESTIONS_COUNT, 15);
        values.put(COLUMN_DIFFICULTY, "Hard");
        db.insert(TABLE_QUIZZES, null, values);

        values = new ContentValues();
        values.put(COLUMN_TITLE, "Pop Culture 2023");
        values.put(COLUMN_DESC, "Test your knowledge on movies, music, and trends.");
        values.put(COLUMN_QUESTIONS_COUNT, 20);
        values.put(COLUMN_DIFFICULTY, "Medium");
        db.insert(TABLE_QUIZZES, null, values);
        
        values = new ContentValues();
        values.put(COLUMN_TITLE, "Space Exploration Basics");
        values.put(COLUMN_DESC, "A beginner's guide to the stars.");
        values.put(COLUMN_QUESTIONS_COUNT, 10);
        values.put(COLUMN_DIFFICULTY, "Easy");
        db.insert(TABLE_QUIZZES, null, values);
    }

    // CREATE
    public long addQuiz(Quiz quiz) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, quiz.getTitle());
        values.put(COLUMN_DESC, quiz.getDescription());
        values.put(COLUMN_QUESTIONS_COUNT, quiz.getQuestionsCount());
        values.put(COLUMN_DIFFICULTY, quiz.getDifficulty() != null ? quiz.getDifficulty().getDisplayName() : Difficulty.EASY.getDisplayName());

        long id = db.insert(TABLE_QUIZZES, null, values);
        db.close();
        return id;
    }

    // READ ALL
    public List<Quiz> getAllQuizzes() {
        List<Quiz> quizList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_QUIZZES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Quiz quiz = new Quiz();
                quiz.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                quiz.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
                quiz.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESC)));
                quiz.setQuestionsCount(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUESTIONS_COUNT)));
                quiz.setDifficulty(Difficulty.fromString(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIFFICULTY))));
                quizList.add(quiz);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return quizList;
    }

    // UPDATE
    public int updateQuiz(Quiz quiz) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, quiz.getTitle());
        values.put(COLUMN_DESC, quiz.getDescription());
        values.put(COLUMN_QUESTIONS_COUNT, quiz.getQuestionsCount());
        values.put(COLUMN_DIFFICULTY, quiz.getDifficulty() != null ? quiz.getDifficulty().getDisplayName() : Difficulty.EASY.getDisplayName());

        return db.update(TABLE_QUIZZES, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(quiz.getId())});
    }

    // DELETE
    public void deleteQuiz(int quizId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_QUIZZES, COLUMN_ID + " = ?",
                new String[]{String.valueOf(quizId)});
        db.close();
    }

    // --- QUOTES TABLE OPERATIONS ---
    
    public void insertQuotes(List<Quote> quotes) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_QUOTES, null, null); // Clear old quotes
            for (Quote quote : quotes) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_QUOTE_TEXT, quote.getText());
                values.put(COLUMN_QUOTE_AUTHOR, quote.getAuthor());
                db.insert(TABLE_QUOTES, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<Quote> getAllQuotes() {
        List<Quote> quoteList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_QUOTES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String text = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUOTE_TEXT));
                String author = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUOTE_AUTHOR));
                quoteList.add(new Quote(text, author));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return quoteList;
    }
}

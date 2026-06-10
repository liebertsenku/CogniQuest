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
    private static final int DATABASE_VERSION = 4;

    public static final String TABLE_QUIZZES = "quizzes";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESC = "description";
    public static final String COLUMN_QUESTIONS_COUNT = "questions_count";
    public static final String COLUMN_DIFFICULTY = "difficulty";
    public static final String COLUMN_CATEGORY = "category";

    // Quotes Table
    public static final String TABLE_QUOTES = "quotes";
    public static final String COLUMN_QUOTE_ID = "id";
    public static final String COLUMN_QUOTE_TEXT = "text";
    public static final String COLUMN_QUOTE_AUTHOR = "author";

    // Questions Table
    public static final String TABLE_QUESTIONS = "questions";
    public static final String COLUMN_Q_ID = "id";
    public static final String COLUMN_Q_QUIZ_ID = "quiz_id";
    public static final String COLUMN_Q_TEXT = "question_text";
    public static final String COLUMN_Q_OPTION_A = "option_a";
    public static final String COLUMN_Q_OPTION_B = "option_b";
    public static final String COLUMN_Q_OPTION_C = "option_c";
    public static final String COLUMN_Q_OPTION_D = "option_d";
    public static final String COLUMN_Q_CORRECT = "correct_option";

    // Quiz History Table
    public static final String TABLE_QUIZ_HISTORY = "quiz_history";
    public static final String COLUMN_H_ID = "id";
    public static final String COLUMN_H_USERNAME = "username";
    public static final String COLUMN_H_QUIZ_TITLE = "quiz_title";
    public static final String COLUMN_H_SCORE = "score";
    public static final String COLUMN_H_TOTAL = "total_questions";
    public static final String COLUMN_H_CATEGORY = "category";
    public static final String COLUMN_H_TIMESTAMP = "timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_QUIZZES_TABLE = "CREATE TABLE " + TABLE_QUIZZES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_DESC + " TEXT,"
                + COLUMN_CATEGORY + " TEXT,"
                + COLUMN_QUESTIONS_COUNT + " INTEGER,"
                + COLUMN_DIFFICULTY + " TEXT" + ")";
        db.execSQL(CREATE_QUIZZES_TABLE);
        
        String CREATE_QUOTES_TABLE = "CREATE TABLE " + TABLE_QUOTES + "("
                + COLUMN_QUOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_QUOTE_TEXT + " TEXT,"
                + COLUMN_QUOTE_AUTHOR + " TEXT" + ")";
        db.execSQL(CREATE_QUOTES_TABLE);

        String CREATE_QUESTIONS_TABLE = "CREATE TABLE " + TABLE_QUESTIONS + "("
                + COLUMN_Q_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_Q_QUIZ_ID + " INTEGER,"
                + COLUMN_Q_TEXT + " TEXT,"
                + COLUMN_Q_OPTION_A + " TEXT,"
                + COLUMN_Q_OPTION_B + " TEXT,"
                + COLUMN_Q_OPTION_C + " TEXT,"
                + COLUMN_Q_OPTION_D + " TEXT,"
                + COLUMN_Q_CORRECT + " TEXT" + ")";
        db.execSQL(CREATE_QUESTIONS_TABLE);

        String CREATE_QUIZ_HISTORY_TABLE = "CREATE TABLE " + TABLE_QUIZ_HISTORY + "("
                + COLUMN_H_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_H_USERNAME + " TEXT,"
                + COLUMN_H_QUIZ_TITLE + " TEXT,"
                + COLUMN_H_CATEGORY + " TEXT,"
                + COLUMN_H_SCORE + " INTEGER,"
                + COLUMN_H_TOTAL + " INTEGER,"
                + COLUMN_H_TIMESTAMP + " TEXT" + ")";
        db.execSQL(CREATE_QUIZ_HISTORY_TABLE);
        
        // Insert dummy data initially
        insertDummyData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZZES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ_HISTORY);
        onCreate(db);
    }
    
    private void insertDummyData(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, "Ancient History Mysteries");
        values.put(COLUMN_DESC, "Explore the unknown events of ancient times.");
        values.put(COLUMN_CATEGORY, "History");
        values.put(COLUMN_QUESTIONS_COUNT, 3);
        values.put(COLUMN_DIFFICULTY, "Hard");
        long q1Id = db.insert(TABLE_QUIZZES, null, values);

        values = new ContentValues();
        values.put(COLUMN_TITLE, "Pop Culture 2023");
        values.put(COLUMN_DESC, "Test your knowledge on movies, music, and trends.");
        values.put(COLUMN_CATEGORY, "Arts");
        values.put(COLUMN_QUESTIONS_COUNT, 3);
        values.put(COLUMN_DIFFICULTY, "Medium");
        long q2Id = db.insert(TABLE_QUIZZES, null, values);
        
        values = new ContentValues();
        values.put(COLUMN_TITLE, "Space Exploration Basics");
        values.put(COLUMN_DESC, "A beginner's guide to the stars.");
        values.put(COLUMN_CATEGORY, "Science");
        values.put(COLUMN_QUESTIONS_COUNT, 5);
        values.put(COLUMN_DIFFICULTY, "Easy");
        long q3Id = db.insert(TABLE_QUIZZES, null, values);

        // Insert Questions for Space Exploration Basics (Easy)
        insertQuestionHelper(db, q3Id, "Which planet is closest to the Sun?", "Venus", "Mercury", "Earth", "Mars", "B");
        insertQuestionHelper(db, q3Id, "What is the largest planet in our solar system?", "Saturn", "Jupiter", "Neptune", "Uranus", "B");
        insertQuestionHelper(db, q3Id, "Which planet is known as the Red Planet?", "Mars", "Venus", "Jupiter", "Mercury", "A");
        insertQuestionHelper(db, q3Id, "What galaxy is Earth located in?", "Andromeda", "Milky Way", "Triangulum", "Sombrero", "B");
        insertQuestionHelper(db, q3Id, "Who was the first person to step on the Moon?", "Yuri Gagarin", "Buzz Aldrin", "Neil Armstrong", "John Glenn", "C");

        // Insert Questions for Pop Culture 2023 (Medium)
        insertQuestionHelper(db, q2Id, "Which movie won the Oscar for Best Picture in 2023?", "Avatar: The Way of Water", "Top Gun: Maverick", "Everything Everywhere All at Once", "The Banshees of Inisherin", "C");
        insertQuestionHelper(db, q2Id, "Which artist released the highly acclaimed album 'Midnights'?", "Taylor Swift", "Beyonce", "Adele", "Harry Styles", "A");
        insertQuestionHelper(db, q2Id, "What was the highest-grossing film of 2023?", "Oppenheimer", "The Super Mario Bros. Movie", "Barbie", "Guardians of the Galaxy Vol. 3", "C");

        // Insert Questions for Ancient History Mysteries (Hard)
        insertQuestionHelper(db, q1Id, "The Great Pyramid of Giza was built for which Egyptian Pharaoh?", "Tutankhamun", "Khufu", "Ramses II", "Akhenaten", "B");
        insertQuestionHelper(db, q1Id, "Which ancient civilization built the mountain city of Machu Picchu?", "Aztecs", "Mayans", "Incas", "Olmecs", "C");
        insertQuestionHelper(db, q1Id, "Who was the first Emperor of the Roman Empire?", "Julius Caesar", "Nero", "Marcus Aurelius", "Augustus", "D");
    }

    private void insertQuestionHelper(SQLiteDatabase db, long quizId, String text, String oA, String oB, String oC, String oD, String correct) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_Q_QUIZ_ID, quizId);
        values.put(COLUMN_Q_TEXT, text);
        values.put(COLUMN_Q_OPTION_A, oA);
        values.put(COLUMN_Q_OPTION_B, oB);
        values.put(COLUMN_Q_OPTION_C, oC);
        values.put(COLUMN_Q_OPTION_D, oD);
        values.put(COLUMN_Q_CORRECT, correct);
        db.insert(TABLE_QUESTIONS, null, values);
    }

    // CREATE
    public long addQuiz(Quiz quiz) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, quiz.getTitle());
        values.put(COLUMN_DESC, quiz.getDescription());
        values.put(COLUMN_CATEGORY, quiz.getCategory());
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
                int catIndex = cursor.getColumnIndex(COLUMN_CATEGORY);
                if(catIndex != -1) {
                    quiz.setCategory(cursor.getString(catIndex));
                }
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
        values.put(COLUMN_CATEGORY, quiz.getCategory());
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

    // --- QUESTIONS TABLE OPERATIONS ---

    public long addQuestion(Question question) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_Q_QUIZ_ID, question.getQuizId());
        values.put(COLUMN_Q_TEXT, question.getQuestionText());
        values.put(COLUMN_Q_OPTION_A, question.getOptionA());
        values.put(COLUMN_Q_OPTION_B, question.getOptionB());
        values.put(COLUMN_Q_OPTION_C, question.getOptionC());
        values.put(COLUMN_Q_OPTION_D, question.getOptionD());
        values.put(COLUMN_Q_CORRECT, question.getCorrectOption());

        long id = db.insert(TABLE_QUESTIONS, null, values);
        db.close();
        return id;
    }

    public List<Question> getQuestionsForQuiz(int quizId) {
        List<Question> questionList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_QUESTIONS, null, COLUMN_Q_QUIZ_ID + " = ?",
                new String[]{String.valueOf(quizId)}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Question question = new Question();
                question.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_Q_ID)));
                question.setQuizId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_Q_QUIZ_ID)));
                question.setQuestionText(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_Q_TEXT)));
                question.setOptionA(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_Q_OPTION_A)));
                question.setOptionB(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_Q_OPTION_B)));
                question.setOptionC(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_Q_OPTION_C)));
                question.setOptionD(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_Q_OPTION_D)));
                question.setCorrectOption(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_Q_CORRECT)));
                questionList.add(question);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return questionList;
    }

    public int updateQuestion(Question question) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_Q_TEXT, question.getQuestionText());
        values.put(COLUMN_Q_OPTION_A, question.getOptionA());
        values.put(COLUMN_Q_OPTION_B, question.getOptionB());
        values.put(COLUMN_Q_OPTION_C, question.getOptionC());
        values.put(COLUMN_Q_OPTION_D, question.getOptionD());
        values.put(COLUMN_Q_CORRECT, question.getCorrectOption());

        int count = db.update(TABLE_QUESTIONS, values, COLUMN_Q_ID + " = ?",
                new String[]{String.valueOf(question.getId())});
        db.close();
        return count;
    }

    public void deleteQuestion(int questionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_QUESTIONS, COLUMN_Q_ID + " = ?",
                new String[]{String.valueOf(questionId)});
        db.close();
    }

    // --- QUIZ HISTORY TABLE OPERATIONS ---

    public long addQuizHistory(String username, String quizTitle, String category, int score, int totalQuestions) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_H_USERNAME, username);
        values.put(COLUMN_H_QUIZ_TITLE, quizTitle);
        values.put(COLUMN_H_CATEGORY, category);
        values.put(COLUMN_H_SCORE, score);
        values.put(COLUMN_H_TOTAL, totalQuestions);
        
        // Simple human readable date format
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        String currentDateTime = android.text.format.DateFormat.format("MMM dd, yyyy, h:mm a", new java.util.Date()).toString();
        values.put(COLUMN_H_TIMESTAMP, currentDateTime);

        long id = db.insert(TABLE_QUIZ_HISTORY, null, values);
        db.close();
        return id;
    }

    public List<QuizHistory> getQuizHistory(String username) {
        List<QuizHistory> historyList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_QUIZ_HISTORY, null, COLUMN_H_USERNAME + " = ?",
                new String[]{username}, null, null, COLUMN_H_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                QuizHistory history = new QuizHistory();
                history.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_H_ID)));
                history.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_H_USERNAME)));
                history.setQuizTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_H_QUIZ_TITLE)));
                int catIndex = cursor.getColumnIndex(COLUMN_H_CATEGORY);
                if(catIndex != -1) {
                    history.setCategory(cursor.getString(catIndex));
                }
                history.setScore(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_H_SCORE)));
                history.setTotalQuestions(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_H_TOTAL)));
                history.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_H_TIMESTAMP)));
                historyList.add(history);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return historyList;
    }
}

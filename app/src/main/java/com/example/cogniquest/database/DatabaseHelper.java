package com.example.cogniquest.database;
import com.example.cogniquest.model.Question;
import com.example.cogniquest.model.Quiz;
import com.example.cogniquest.model.QuizHistory;
import com.example.cogniquest.model.Quote;
import com.example.cogniquest.model.Difficulty;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CogniQuest.db";
    private static final int DATABASE_VERSION = 8;

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
        values.put(COLUMN_TITLE, "IPA Terpadu");
        values.put(COLUMN_DESC, "Uji pemahamanmu tentang sel makhluk hidup dan gaya gerak dalam sains.");
        values.put(COLUMN_CATEGORY, "IPA");
        values.put(COLUMN_QUESTIONS_COUNT, 4);
        values.put(COLUMN_DIFFICULTY, "Medium");
        long q1Id = db.insert(TABLE_QUIZZES, null, values);

        values = new ContentValues();
        values.put(COLUMN_TITLE, "Matematika");
        values.put(COLUMN_DESC, "Asah kemampuan aljabar, statistika, dan geometri dasar.");
        values.put(COLUMN_CATEGORY, "Matematika");
        values.put(COLUMN_QUESTIONS_COUNT, 4);
        values.put(COLUMN_DIFFICULTY, "Medium");
        long q2Id = db.insert(TABLE_QUIZZES, null, values);
        
        values = new ContentValues();
        values.put(COLUMN_TITLE, "IPS Terpadu");
        values.put(COLUMN_DESC, "Uji wawasanmu tentang sejarah kemerdekaan Indonesia dan kondisi geografis ASEAN.");
        values.put(COLUMN_CATEGORY, "IPS");
        values.put(COLUMN_QUESTIONS_COUNT, 4);
        values.put(COLUMN_DIFFICULTY, "Easy");
        long q3Id = db.insert(TABLE_QUIZZES, null, values);

        values = new ContentValues();
        values.put(COLUMN_TITLE, "Bahasa Indonesia");
        values.put(COLUMN_DESC, "Uji kemampuan tata bahasa, unsur intrinsik karya sastra, dan kalimat efektif.");
        values.put(COLUMN_CATEGORY, "Bahasa Indonesia");
        values.put(COLUMN_QUESTIONS_COUNT, 4);
        values.put(COLUMN_DIFFICULTY, "Easy");
        long q4Id = db.insert(TABLE_QUIZZES, null, values);

        values = new ContentValues();
        values.put(COLUMN_TITLE, "English Quiz");
        values.put(COLUMN_DESC, "Test your grammar, vocabulary, and reading comprehension basics.");
        values.put(COLUMN_CATEGORY, "Bahasa Inggris");
        values.put(COLUMN_QUESTIONS_COUNT, 4);
        values.put(COLUMN_DIFFICULTY, "Easy");
        long q5Id = db.insert(TABLE_QUIZZES, null, values);

        values = new ContentValues();
        values.put(COLUMN_TITLE, "PPKn");
        values.put(COLUMN_DESC, "Uji pemahaman nilai-nilai Pancasila, konstitusi, dan hak asasi manusia.");
        values.put(COLUMN_CATEGORY, "PPKn");
        values.put(COLUMN_QUESTIONS_COUNT, 4);
        values.put(COLUMN_DIFFICULTY, "Easy");
        long q6Id = db.insert(TABLE_QUIZZES, null, values);

        // Insert Questions for IPA Terpadu SMP (Medium)
        insertQuestionHelper(db, q1Id, "Organel sel yang berfungsi sebagai tempat respirasi sel dan menghasilkan energi adalah?", "Kloroplas", "Mitokondria", "Ribosom", "Lisosom", "B");
        insertQuestionHelper(db, q1Id, "Zat yang dihasilkan dari proses fotosintesis tumbuhan hijau adalah?", "Oksigen dan Glukosa", "Karbondioksida dan Air", "Nitrogen dan Protein", "Oksigen dan Karbondioksida", "A");
        insertQuestionHelper(db, q1Id, "Satuan internasional untuk gaya adalah?", "Watt", "Joule", "Newton", "Pascal", "C");
        insertQuestionHelper(db, q1Id, "Perubahan wujud benda dari gas langsung menjadi padat disebut?", "Menyublim", "Mengkristal (Deposisi)", "Mengembun", "Mencair", "B");

        // Insert Questions for Matematika SMP (Medium)
        insertQuestionHelper(db, q2Id, "Hasil dari (-12) x 3 + 20 : (-4) adalah?", "-41", "-31", "-11", "41", "A");
        insertQuestionHelper(db, q2Id, "Nilai x yang memenuhi persamaan linear 3x - 5 = 7 adalah?", "2", "3", "4", "5", "C");
        insertQuestionHelper(db, q2Id, "Sebuah segitiga siku-siku memiliki panjang alas 6 cm dan tinggi 8 cm. Panjang hipotenusanya (sisi miring) adalah?", "10 cm", "12 cm", "14 cm", "16 cm", "A");
        insertQuestionHelper(db, q2Id, "Median dari data nilai: 6, 7, 5, 8, 9, 7, 6 adalah?", "6", "7", "8", "9", "B");

        // Insert Questions for IPS Terpadu SMP (Easy)
        insertQuestionHelper(db, q3Id, "Siapakah tokoh yang membacakan teks Proklamasi Kemerdekaan Indonesia pada tanggal 17 Agustus 1945?", "Mohammad Hatta", "Soekarno", "Sutan Sjahrir", "Sayuti Melik", "B");
        insertQuestionHelper(db, q3Id, "Negara anggota ASEAN yang tidak memiliki garis pantai laut adalah?", "Kamboja", "Laos", "Vietnam", "Myanmar", "B");
        insertQuestionHelper(db, q3Id, "Kerajaan Hindu tertua di Indonesia yang terletak di Kalimantan Timur adalah?", "Kutai", "Tarumanegara", "Majapahit", "Sriwijaya", "A");
        insertQuestionHelper(db, q3Id, "Garis khayal yang membagi wilayah persebaran fauna Indonesia Barat (Asiatis) dan Indonesia Tengah (Peralihan) dinamakan?", "Garis Weber", "Garis Wallace", "Garis Khatulistiwa", "Garis Bujur", "B");

        // Insert Questions for Bahasa Indonesia SMP (Easy)
        insertQuestionHelper(db, q4Id, "Manakah dari kalimat berikut yang merupakan kalimat efektif?", "Para siswa-siswa sedang belajar di kelas.", "Siswa-siswa sedang belajar di dalam kelas.", "Banyak siswa-siswa sedang belajar di kelas.", "Para siswa sedang belajar di kelas.", "D");
        insertQuestionHelper(db, q4Id, "Tokoh utama yang memiliki watak baik dalam suatu cerita atau karya sastra disebut?", "Protagonis", "Antagonis", "Tritagonis", "Figuran", "A");
        insertQuestionHelper(db, q4Id, "Penulisan kata serapan yang baku di bawah ini adalah?", "Apotik", "Analisa", "Praktik", "Aktifitas", "C");
        insertQuestionHelper(db, q4Id, "Majas yang membandingkan benda mati seolah-olah hidup dan bertingkah laku seperti manusia disebut majas?", "Hiperbola", "Personifikasi", "Metafora", "Asosiasi", "B");

        // Insert Questions for English Quiz (Easy)
        insertQuestionHelper(db, q5Id, "She ________ to school by bus every day.", "go", "goes", "going", "went", "B");
        insertQuestionHelper(db, q5Id, "\"The weather is very hot today.\" What is the antonym of \"hot\"?", "Warm", "Cold", "Cool", "Dry", "B");
        insertQuestionHelper(db, q5Id, "\"I have a new cat. ________ fur is white and soft.\" The correct possessive pronoun is?", "Its", "It's", "His", "Her", "A");
        insertQuestionHelper(db, q5Id, "Which of the following is a polite expression to ask for help?", "Give me a hand!", "Can you help me, please?", "I need you now!", "Hey, do this!", "B");

        // Insert Questions for PPKn SMP (Easy)
        insertQuestionHelper(db, q6Id, "Rumusan dasar negara Pancasila secara sah tercantum dalam Pembukaan UUD 1945 alinea ke-?", "Pertama", "Kedua", "Ketiga", "Keempat", "D");
        insertQuestionHelper(db, q6Id, "Lambang sila ketiga Pancasila yang berbunyi 'Persatuan Indonesia' adalah?", "Rantai Emas", "Pohon Beringin", "Kepala Banteng", "Padi dan Kapas", "B");
        insertQuestionHelper(db, q6Id, "Badan yang merumuskan dan menetapkan rancangan UUD 1945 dalam sidang-sidangnya adalah?", "BPUPKI", "PPKI", "KNIP", "DPR", "B");
        insertQuestionHelper(db, q6Id, "Sikap menghormati perbedaan suku, agama, ras, dan antargolongan di Indonesia sesuai dengan semboyan negara yaitu?", "Tut Wuri Handayani", "Bhinneka Tunggal Ika", "Jalesveva Jayamahe", "Kartika Eka Paksi", "B");
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

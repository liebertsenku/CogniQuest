package com.example.cogniquest.model;

public class QuizHistory {
    private int id;
    private String username;
    private String quizTitle;
    private String category;
    private int score;
    private int totalQuestions;
    private String timestamp;

    public QuizHistory() {}

    public QuizHistory(String username, String quizTitle, String category, int score, int totalQuestions, String timestamp) {
        this.username = username;
        this.quizTitle = quizTitle;
        this.category = category;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.timestamp = timestamp;
    }

    public QuizHistory(int id, String username, String quizTitle, String category, int score, int totalQuestions, String timestamp) {
        this.id = id;
        this.username = username;
        this.quizTitle = quizTitle;
        this.category = category;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getQuizTitle() { return quizTitle; }
    public void setQuizTitle(String quizTitle) { this.quizTitle = quizTitle; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}

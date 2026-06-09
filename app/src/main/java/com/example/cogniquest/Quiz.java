package com.example.cogniquest;

public class Quiz {
    private int id;
    private String title;
    private String description;
    private int questionsCount;
    private Difficulty difficulty;

    public Quiz() {
    }

    public Quiz(int id, String title, String description, int questionsCount, Difficulty difficulty) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.questionsCount = questionsCount;
        this.difficulty = difficulty;
    }
    
    public Quiz(String title, String description, int questionsCount, Difficulty difficulty) {
        this.title = title;
        this.description = description;
        this.questionsCount = questionsCount;
        this.difficulty = difficulty;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuestionsCount() {
        return questionsCount;
    }

    public void setQuestionsCount(int questionsCount) {
        this.questionsCount = questionsCount;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
}

package com.example.to_dolist.models;

public class ToDoTaskModel {

    private final String userId;
    private final String taskId;
    private final String title;
    private final String description;

    public ToDoTaskModel(String userId, String taskId, String title, String description) {
        this.userId = userId;
        this.taskId = taskId;
        this.title = title;
        this.description = description;
    }
    
    public String getUserId() {
        return userId;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}

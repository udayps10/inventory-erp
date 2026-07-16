package com.project.inventoryerp;

public class GreetingResponse {
    private String message;
    private String timestamp;

    public GreetingResponse(String message, String timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
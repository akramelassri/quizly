package com.example.quizly.websocket.payload;

public class IncomingMessage {
    public String action; // e.g., "JOIN", "SUBMIT_ANSWER"
    public String role; // e.g., "HOST", "STUDENT"
    public String name; // e.g., "Anas"
    public String firebaseToken; // Optional, for the professor
    public String payload; // Generic data, like the answer picked
}

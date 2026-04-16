package com.example.quizly.websocket.payload;

public class ChoiceDTO {
    public Long id;
    public String text;
    // We intentionally leave out the boolean 'is_correct' so players can't cheat!

    public ChoiceDTO(Long id, String text) {
        this.id = id;
        this.text = text;
    }
}
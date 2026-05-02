package com.example.quizly.websocket.payload;

import java.util.List;

public class QuestionDTO {
    public Long id;
    public String text;
    public List<ChoiceDTO> choices;

    public QuestionDTO(Long id, String text, List<ChoiceDTO> choices) {
        this.id = id;
        this.text = text;
        this.choices = choices;
    }
}
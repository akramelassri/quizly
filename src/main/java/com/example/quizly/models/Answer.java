package com.example.quizly.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "answers", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "participant_id", "question_id" })
})
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private SessionParticipant participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "choice_id", nullable = false)
    private Choice choice;

    // --- NEW WAGER COLUMN ---
    @Column(nullable = false)
    private Integer wager;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    public Answer() {
    }

    @PrePersist
    protected void onCreate() {
        if (submittedAt == null) {
            submittedAt = LocalDateTime.now();
        }
    }

    public Integer getWager() {
        return wager;
    }

    public void setWager(Integer wager) {
        this.wager = wager;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SessionParticipant getParticipant() {
        return participant;
    }

    public void setParticipant(SessionParticipant participant) {
        this.participant = participant;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Choice getChoice() {
        return choice;
    }

    public void setChoice(Choice choice) {
        this.choice = choice;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}
package com.example.quizapp.quiz;

public class Answer {

    private int answerID;
    private String answerText;
    private boolean checked = false;
    private boolean correct;

    public Answer(String text, boolean correct) {
        this.answerID = -1;
        this.answerText = text;
        this.correct = correct;
    }

    public Answer(int answerID, String text, boolean correct) {
        this.answerID = answerID;
        this.answerText = text;
        this.correct = correct;
    }

    public int getAnswerID() {
        return this.answerID;
    }

    public String getAnswerText() {
        return this.answerText;
    }

    public boolean isCorrect() {
        return this.correct;
    }

    public boolean isChecked() {
        return this.checked;
    }

    /**
     * Flip the state of the checked flag for this answer.
     */
    public void check() {
        this.checked = !this.checked;
    }

}

package com.example.quizapp.quiz;

import java.util.List;

public class Question {

    private int questionID;
    private String questionText;
    private List<Answer> answers;
    private boolean answeredCorrectly = false;

    /**
     * Constructor: Create a Question object from information from the code.
     *
     * @param questionText text of the question
     * @param answers      list of the possible answers
     */
    public Question(String questionText, List<Answer> answers) {
        this.questionID = -1;
        this.questionText = questionText;
        this.answers = answers;
    }

    /**
     * Constructor: Create a Question object which consists information from the database.
     *
     * @param questionID        id of the question
     * @param questionText      text of the question
     * @param answers           list of the possible answers
     * @param answeredCorrectly information whether the Question was answered correctly before
     */
    public Question(int questionID, String questionText, List<Answer> answers, boolean answeredCorrectly) {
        this.questionID = questionID;
        this.questionText = questionText;
        this.answers = answers;
        this.answeredCorrectly = answeredCorrectly;
    }

    /** Getter */
    public int getQuestionID() {
        return this.questionID;
    }

    /** Getter */
    public String getQuestionText() {
        return this.questionText;
    }

    /** Getter */
    public List<Answer> getAnswers() {
        return this.answers;
    }

    /** Getter */
    public boolean getAnsweredCorrectly() {
        return this.answeredCorrectly;
    }
}

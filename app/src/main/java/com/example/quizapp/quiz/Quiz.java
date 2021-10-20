package com.example.quizapp.quiz;

public interface Quiz {

    /**
     * Get the next Question in the quiz.
     *
     * @return the next question
     */
    public Question getNextQuestion() throws Exception;


}

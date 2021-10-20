package com.example.quizapp.quiz;


import android.content.Context;

import java.util.Optional;

public class PracticeQuiz {

    private Question activeQuestion;
    private DatabaseHelper databaseHelper;

    public PracticeQuiz(Context context) {
        this.databaseHelper = new DatabaseHelper(context);
    }

    /**
     * Get the next question from the database. If no more questions can be found, notify the user.
     *
     * @return information whether a next question has been found
     */
    public boolean selectNextQuestion() {
        Optional<Question> question = databaseHelper.getPracticeQuestion();

        if (question.isPresent()) {
            activeQuestion = question.get();
            return true;
        } else {
            activeQuestion = null;
            return false;
        }
    }

    /** Getter */
    public Question getActiveQuestion() {
        return activeQuestion;
    }

    /**
     * Submit the question and how that question is answered. The question is updated in the
     * database to indicate whether it was answered correctly or not.
     *
     * @param question the submitted question
     */
    public void submitAnswers(Question question) {
        // check if all answers are answered correctly
        boolean correctlyAnswered = true;
        for (Answer a : question.getAnswers()) {
            if (a.isCorrect() != a.isChecked()) {
                correctlyAnswered = false;
                break;
            }
        }

        // update the question in the database
        if (correctlyAnswered != question.getAnsweredCorrectly()) {
            databaseHelper.setQuestionsAnsweredCorrectly(question.getQuestionID(), correctlyAnswered);
        }
    }

    /**
     * Reset the practiced state of all questions in the database.
     */
    public void resetQuestions() {
        databaseHelper.resetQuestions();
    }
}

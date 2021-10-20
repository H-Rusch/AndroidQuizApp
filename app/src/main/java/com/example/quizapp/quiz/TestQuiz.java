package com.example.quizapp.quiz;

import android.content.Context;

import java.util.List;

public class TestQuiz {
    private final int NUMBER_OF_QUESTIONS = 10;
    private final double FAIL_PERCENTAGE = 0.1;
    private boolean passed;
    private boolean running = true;
    private int activeQuestionIndex = 0;
    private List<Question> questionList;
    private DatabaseHelper databaseHelper;

    public TestQuiz(Context context) {
        this.databaseHelper = new DatabaseHelper(context);

        questionList = databaseHelper.getTestQuestionList(NUMBER_OF_QUESTIONS);
    }

    public boolean isNextQuestionPossible() {
        return activeQuestionIndex < NUMBER_OF_QUESTIONS - 1;
    }

    public boolean isPreviousQuestionPossible() {
        return activeQuestionIndex > 0;
    }

    public boolean isSubmitPossible() {
        return running;
    }

    public void selectNextQuestion() {
        activeQuestionIndex++;
    }

    public void selectPreviousQuestion() {
        activeQuestionIndex--;
    }

    public Question getActiveQuestion() {
        return questionList.get(activeQuestionIndex);
    }

    public int getQuizSize() {
        return this.questionList.size();
    }

    public int getActiveQuestionCount() {
        return this.activeQuestionIndex + 1;
    }

    /**
     * When the test ist submitted, the quiz is changed to stop accepting more answers.
     * All the given answers are checked if they were answered correctly. If a question was not
     * answered correctly, this information will be saved in the database, so the question can be
     * practiced later.
     * The test is passed if 90% of the questions were answered correctly.
     *
     * @return information whether the quiz has been completed successfully
     */
    public boolean submit() {
        activeQuestionIndex = 0;
        if (running) {
            running = false;

            int falselyAnsweredQuestionCount = 0;
            for (int i = 0; i < NUMBER_OF_QUESTIONS; i++) {
                Question question = questionList.get(i);
                boolean correctlyAnswered = true;
                for (Answer a : question.getAnswers()) {
                    if (a.isCorrect() != a.isChecked()) {
                        correctlyAnswered = false;
                        break;
                    }
                }

                if (!correctlyAnswered) {
                    falselyAnsweredQuestionCount++;
                }

                if (correctlyAnswered != question.getAnsweredCorrectly()) {
                    databaseHelper.setQuestionsAnsweredCorrectly(question.getQuestionID(), correctlyAnswered);
                } else {
                    databaseHelper.setQuestionsAnsweredCorrectly(question.getQuestionID(), question.getAnsweredCorrectly());
                }
            }
            passed = falselyAnsweredQuestionCount <= Math.ceil((double) NUMBER_OF_QUESTIONS * FAIL_PERCENTAGE);
        }

        return passed;
    }
}

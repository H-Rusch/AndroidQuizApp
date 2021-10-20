package com.example.quizapp.user_interface;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.R;
import com.example.quizapp.quiz.PracticeQuiz;

public class PracticeActivity extends AppCompatActivity {

    private PracticeQuiz quiz;
    private QuestionFragment questionFragment;
    private Button button;

    private View.OnClickListener loadQuestionListener = view -> loadQuestion();
    private View.OnClickListener submitAnswerListener = view -> submitAnswer();
    private View.OnClickListener resetQuestions = view ->  resetQuestions();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        // cache the UI-elements
        questionFragment = (QuestionFragment) getSupportFragmentManager()
                .findFragmentById(R.id.question_fragment);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(submitAnswerListener);

        quiz = new PracticeQuiz(this);

        loadQuestion();
    }

    /**
     * Load the next question from the database. If a question is available, it will be loaded in
     * the ui. Else the option is given to reset all progress.
     */
    private void loadQuestion() {
        if (quiz.selectNextQuestion()) {
            questionFragment.setQuestion(quiz.getActiveQuestion());
            questionFragment.showNewQuestion();

            // change functionality of the button to the submit button
            button.setText(R.string.submit_button_text);
            button.setOnClickListener(submitAnswerListener);
        } else {
            questionFragment.noQuestionsLeft();

            // change functionality of the button to reset the progress
            button.setText(R.string.reset_button_text);
            button.setOnClickListener(resetQuestions);
        }
    }

    /**
     * Submit the answer given. If the answer is answered correctly, it is updated in the database.
     * Change the UI to the showResult mode.
     */
    private void submitAnswer() {
        quiz.submitAnswers(quiz.getActiveQuestion());
        questionFragment.showResults();

        // change functionality of the button to the load next button
        button.setText(R.string.next_button_text);
        button.setOnClickListener(loadQuestionListener);
    }

    /**
     * Reset the answered state of all questions and then load some more.
     */
    private void resetQuestions() {
        quiz.resetQuestions();

        loadQuestion();
    }
}

package com.example.quizapp.user_interface;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.R;
import com.example.quizapp.quiz.TestQuiz;

public class TestActivity extends AppCompatActivity {

    private TestQuiz quiz;
    private QuestionFragment questionFragment;
    private Button prevButton;
    private Button nextButton;
    private TextView progressBar;

    private final View.OnClickListener previousQuestionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            previous();
        }
    };

    private final View.OnClickListener nextQuestionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            next();
        }
    };

    private final View.OnClickListener submitTestListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            submit();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        questionFragment = (QuestionFragment) getSupportFragmentManager()
                .findFragmentById(R.id.question_fragment);
        nextButton = (Button) findViewById(R.id.nextButton);
        prevButton = (Button) findViewById(R.id.prevButton);
        progressBar = (TextView) findViewById(R.id.progressBar);

        quiz = new TestQuiz(this);

        loadQuestion();
    }

    private void loadQuestion() {
        progressBar.setText(String.valueOf(quiz.getActiveQuestionCount() + " / " + quiz.getQuizSize()));
        questionFragment.setQuestion(quiz.getActiveQuestion());

        if (quiz.isNextQuestionPossible()) {
            nextButton.setOnClickListener(nextQuestionListener);
            nextButton.setText(R.string.next_button_text);
            nextButton.setEnabled(true);
        } else {
            if (quiz.isSubmitPossible()) {
                nextButton.setOnClickListener(submitTestListener);
                nextButton.setText(R.string.submit_button_text);
            } else {
                nextButton.setEnabled(false);
            }
        }
        if (quiz.isPreviousQuestionPossible()) {
            prevButton.setOnClickListener(previousQuestionListener);
            prevButton.setEnabled(true);
        } else {
            prevButton.setEnabled(false);
        }
    }

    private void next() {
        quiz.selectNextQuestion();
        loadQuestion();
    }

    private void previous() {
        quiz.selectPreviousQuestion();
        loadQuestion();
    }

    private void submit() {
        if (quiz.submit()) {
            Toast.makeText(this, R.string.test_passed, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, R.string.test_failed, Toast.LENGTH_LONG).show();
        }
        questionFragment.showResults();
        loadQuestion();
    }
}

package com.example.quizapp.user_interface;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizapp.R;
import com.example.quizapp.quiz.Answer;
import com.example.quizapp.quiz.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionFragment extends Fragment {

    private RecyclerView rvAnswers;
    private TextView questionTextView;
    private AnswerAdapter answerAdapter;

    private List<Answer> answerList = new ArrayList<>();

    public QuestionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        questionTextView = view.findViewById(R.id.question_text);

        answerAdapter = new AnswerAdapter(this.answerList);
        rvAnswers = view.findViewById(R.id.recycler_answers);
        rvAnswers.setHasFixedSize(true);
        rvAnswers.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rvAnswers.setAdapter(answerAdapter);
    }

    /**
     * Update the Fragment with the information from the Question. Notify the adapter, the list
     * of answers has been changed, so the recycler view will be updated.
     *
     * @param question the question which should be represented in the fragment
     */
    public void setQuestion(Question question) {
        questionTextView.setText(question.getQuestionText());
        answerList.clear();
        answerList.addAll(question.getAnswers());
        Collections.shuffle(answerList);
        answerAdapter.notifyDataSetChanged();
    }

    public void showNewQuestion() {
        answerAdapter.setShowResult(false);
    }

    public void showResults() {
        answerAdapter.setShowResult(true);
        answerAdapter.notifyDataSetChanged();
    }

    public void noQuestionsLeft() {
        questionTextView.setText(R.string.no_practice_question_text);
        answerList.clear();
        answerAdapter.notifyDataSetChanged();
    }
}
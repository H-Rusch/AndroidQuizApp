package com.example.quizapp.user_interface;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizapp.R;
import com.example.quizapp.quiz.Answer;

import java.util.List;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.ViewHolder> {

    private List<Answer> answerList;
    private boolean showResult = false;

    public AnswerAdapter(List<Answer> answerList) {
        this.answerList = answerList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View answerView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_answer, parent, false);

        return new ViewHolder(answerView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Answer answer = this.answerList.get(position);

        CheckBox checkBox = holder.checkBox;
        checkBox.setChecked(answer.isChecked());

        TextView textView = holder.textView;
        textView.setText(answer.getAnswerText());

        if (showResult) {
            // remove click listeners when the elements are not active
            checkBox.setEnabled(false);
            checkBox.setOnClickListener(null);
            textView.setOnClickListener(null);

            // colors of the answers
            if (answer.isChecked() == answer.isCorrect()) {
                textView.setTextColor(Color.GREEN);
            } else {
                textView.setTextColor(Color.RED);
            }
        } else {
            // set click listeners when the elements are active
            checkBox.setEnabled(true);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    answer.check();
                }
            });
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    answer.check();
                    checkBox.setChecked(!checkBox.isChecked());
                }
            });

            // normal colors of the answers
            textView.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return this.answerList.size();
    }

    /**
     * Set the adapters show result mode. When in show result mode, the elements are not able to be
     * interacted with and they are highlighted to show whether the answers are correct.
     * When not in show result mode, all elements are able to be interacted with.
     *
     * @param showResult whether the adapter should go into show result mode
     */
    public void setShowResult(boolean showResult) {
        this.showResult = showResult;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBox;
        public TextView textView;

        public ViewHolder(View view) {
            super(view);

            checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            textView = (TextView) view.findViewById(R.id.textView);
        }
    }
}


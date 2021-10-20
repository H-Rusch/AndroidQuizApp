package com.example.quizapp.user_interface;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.R;

public class MainActivity extends AppCompatActivity {

    private ImageView practiceButton;
    private ImageView testButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        practiceButton = (ImageView) findViewById(R.id.practiceImageButton);
        testButton = (ImageView) findViewById(R.id.testImageButton);

        practiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PracticeActivity.class));
            }
        });

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TestActivity.class));
            }
        });
    }
}
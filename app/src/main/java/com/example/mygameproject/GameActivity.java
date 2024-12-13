package com.example.mygameproject;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstaneState) {
        super.onCreate(savedInstaneState);
        setContentView(new GameView(this));
    }
}

package com.example.mygameproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class HighScoreActivity extends AppCompatActivity {
    private HighScoreDatabase dbHelper;
    private ListView highScoreListView;
    private Button backToMainButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        // Initialize database helper
        dbHelper = new HighScoreDatabase(this);

        // Find views
        highScoreListView = findViewById(R.id.highScoreListView);
        backToMainButton = findViewById(R.id.backToMainButton);

        // Populate high scores
        populateHighScores();

        // Setup back button
        backToMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return to main activity
                Intent intent = new Intent(HighScoreActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    private void populateHighScores() {
        // Retrieve top 5 scores
        final List<HighScoreDatabase.HighScore> highScores = dbHelper.getTopFiveScores();

        // Create custom adapter for high scores
        ArrayAdapter<HighScoreDatabase.HighScore> adapter =
                new ArrayAdapter<HighScoreDatabase.HighScore>(this,
                        R.layout.high_score_item, highScores) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        // If the view is not recycled, inflate a new view
                        if (convertView == null) {
                            convertView = getLayoutInflater().inflate(R.layout.high_score_item, parent, false);
                        }

                        // Get current high score
                        HighScoreDatabase.HighScore highScore = getItem(position);

                        // Find views in the layout
                        TextView rankTextView = convertView.findViewById(R.id.rankTextView);
                        TextView nameTextView = convertView.findViewById(R.id.nameTextView);
                        TextView scoreTextView = convertView.findViewById(R.id.scoreTextView);

                        // Set data
                        rankTextView.setText(String.valueOf(position + 1));
                        nameTextView.setText(highScore.getName());
                        scoreTextView.setText(String.valueOf(highScore.getScore()));

                        return convertView;
                    }
                };

        // Set the adapter to the ListView
        highScoreListView.setAdapter(adapter);
    }
}
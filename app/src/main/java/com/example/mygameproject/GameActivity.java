package com.example.mygameproject;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private GameLogic gameLogic;
    private HighScoreDatabase dbHelper;

    // UI Components
    private LinearLayout sequenceContainer;
    private LinearLayout playContainer;
    private TextView sequenceDisplay;
    private TextView instructionText;

    // Accelerometer
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private List<Integer> userSequence;

    // Game state
    private boolean isLearningSequence = true;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize game components
        gameLogic = new GameLogic();
        dbHelper = new HighScoreDatabase(this);
        userSequence = new ArrayList<>();

        // Setup sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Initialize UI components
        initializeUI();
    }

    private void initializeUI() {
        sequenceContainer = findViewById(R.id.sequenceContainer);
        playContainer = findViewById(R.id.playContainer);
        sequenceDisplay = findViewById(R.id.sequenceDisplay);
        instructionText = findViewById(R.id.instructionText);

        // Initial setup to show sequence
        displaySequence();
    }

    private void displaySequence() {
        // Clear previous sequence display
        sequenceContainer.removeAllViews();

        // Display sequence as colored buttons
        for (Integer color : gameLogic.getSequence()) {
            Button colorButton = new Button(this);
            colorButton.setBackgroundColor(getColorForIndex(color));
            colorButton.setEnabled(false);
            sequenceContainer.addView(colorButton);
        }

        // Automatically transition to play screen after sequence display
        new Handler().postDelayed(this::preparePlayScreen, 3000);
    }

    private void preparePlayScreen() {
        isLearningSequence = false;
        sequenceContainer.setVisibility(View.GONE);
        playContainer.setVisibility(View.VISIBLE);
        instructionText.setText("Tilt phone to match sequence!");
        userSequence.clear();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isLearningSequence) return;

        float x = event.values[0];
        float y = event.values[1];

        int detectedDirection = -1;
        if (Math.abs(x) > Math.abs(y)) {
            detectedDirection = x > 0 ? 3 : 2; // West or East
        } else {
            detectedDirection = y > 0 ? 0 : 1; // North or South
        }

        userSequence.add(detectedDirection);

        // Check sequence if user input matches game sequence length
        if (userSequence.size() == gameLogic.getSequenceLength()) {
            checkSequence();
        }
    }

    private void checkSequence() {
        if (gameLogic.validateSequence(userSequence)) {
            // Correct sequence
            score += 4;
            gameLogic.nextRound();
            isLearningSequence = true;
            playContainer.setVisibility(View.GONE);
            sequenceContainer.setVisibility(View.VISIBLE);
            displaySequence();
        } else {
            // Game Over
            gameOver();
        }
    }

    private void gameOver() {
        // Check if score is in top 5
        if (dbHelper.isTopFiveScore(score)) {
            showNameInputDialog();
        } else {
            showHighScores();
        }
    }

    private void showNameInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("High Score!");

        final EditText input = new EditText(this);
        input.setHint("Enter your name");
        builder.setView(input);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = input.getText().toString();
                dbHelper.insertHighScore(name, score);
                showHighScores();
            }
        });

        builder.show();
    }

    private void showHighScores() {
        // TODO: Implement high score screen display
    }

    private int getColorForIndex(int index) {
        switch (index) {
            case 0: return getResources().getColor(R.color.red);
            case 1: return getResources().getColor(R.color.blue);
            case 2: return getResources().getColor(R.color.green);
            case 3: return getResources().getColor(R.color.yellow);
            default: return getResources().getColor(R.color.white);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for this implementation
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}

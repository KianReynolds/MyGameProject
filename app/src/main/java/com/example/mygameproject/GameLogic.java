package com.example.mygameproject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameLogic {
    private List<Integer> sequence;
    private int currentRound;
    private int sequenceLength;
    private Random random;

    public GameLogic() {
        random = new Random();
        sequence = new ArrayList<>();
        currentRound = 1;
        sequenceLength = 4;
        generateSequence();
    }

    // Generate a random sequence of colors
    public void generateSequence() {
        sequence.clear();
        for (int i = 0; i < sequenceLength; i++) {
            // Assuming 4 colors: 0-Red, 1-Blue, 2-Green, 3-Yellow
            sequence.add(random.nextInt(4));
        }
    }

    // Validate user's sequence
    public boolean validateSequence(List<Integer> userSequence) {
        if (userSequence.size() != sequence.size()) {
            return false;
        }

        for (int i = 0; i < sequence.size(); i++) {
            if (!sequence.get(i).equals(userSequence.get(i))) {
                return false;
            }
        }
        return true;
    }

    // Progress to next round
    public void nextRound() {
        currentRound++;
        sequenceLength += 2;
        generateSequence();
    }

    // Getters
    public List<Integer> getSequence() {
        return sequence;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public int getSequenceLength() {
        return sequenceLength;
    }

    // Translate color index to direction for accelerometer
    public static String getDirectionForColor(int colorIndex) {
        switch (colorIndex) {
            case 0: return "North"; // Red
            case 1: return "South"; // Blue
            case 2: return "East";  // Green
            case 3: return "West";  // Yellow
            default: return "Invalid";
        }
    }
}
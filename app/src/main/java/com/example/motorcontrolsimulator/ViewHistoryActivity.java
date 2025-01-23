package com.example.motorcontrolsimulator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class ViewHistoryActivity extends AppCompatActivity {

    private TextView historyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);

        historyTextView = findViewById(R.id.historyTextView);

        displayHistoricalData();
    }

    private void displayHistoricalData() {
        SharedPreferences sharedPreferences = getSharedPreferences("HistoricalData", MODE_PRIVATE);
        String historyData = sharedPreferences.getString("history", "No historical data found.");

        // Format the data for display (if needed)
        String formattedData = formatHistoryData(historyData);

        historyTextView.setText(formattedData);
    }

    // Helper function to format the data (example)
    private String formatHistoryData(String data) {
        // If you stored data as "timestamp,waterLevel,motorStatus|...",
        // you can format it here for better readability.
        StringBuilder formatted = new StringBuilder();
        String[] dataPoints = data.split("\\|");
        for (String dataPoint : dataPoints) {
            String[] parts = dataPoint.split(",");
            if (parts.length == 3) {
                formatted.append("Time: ").append(parts[0])
                        .append(", Water Level: ").append(parts[1])
                        .append(", Motor: ").append(parts[2])
                        .append("\n");
            }
        }
        return formatted.toString();
    }
}
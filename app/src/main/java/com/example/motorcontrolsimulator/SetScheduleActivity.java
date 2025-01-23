package com.example.motorcontrolsimulator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class SetScheduleActivity extends AppCompatActivity {

    private TimePicker timePickerSchedule;
    private EditText durationInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_schedule);

        timePickerSchedule = findViewById(R.id.timePickerSchedule);
        durationInput = findViewById(R.id.durationInput);
        Button saveScheduleButton = findViewById(R.id.saveScheduleButton);

        saveScheduleButton.setOnClickListener(v -> saveSchedule());
    }

    private void saveSchedule() {
        int startHour = timePickerSchedule.getHour();
        int startMinute = timePickerSchedule.getMinute();
        int duration = Integer.parseInt(durationInput.getText().toString());

        // Save to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("ScheduleStartHour", startHour);
        editor.putInt("ScheduleStartMinute", startMinute);
        editor.putInt("ScheduleDuration", duration);
        editor.putBoolean("ScheduleSet", true);
        editor.apply();

        Toast.makeText(this, "Schedule saved", Toast.LENGTH_SHORT).show();
        finish(); // Close this activity
    }
}
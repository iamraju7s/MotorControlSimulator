package com.example.motorcontrolsimulator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

public class SetOnOffTimeActivity extends AppCompatActivity {

    private TimePicker timePickerOn;
    private TimePicker timePickerOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_on_off_time);

        timePickerOn = findViewById(R.id.timePickerOn);
        timePickerOff = findViewById(R.id.timePickerOff);
        Button saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> saveOnOffTimes());
    }

    private void saveOnOffTimes() {
        int onHour = timePickerOn.getHour();
        int onMinute = timePickerOn.getMinute();
        int offHour = timePickerOff.getHour();
        int offMinute = timePickerOff.getMinute();

        // Save to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("OnHour", onHour);
        editor.putInt("OnMinute", onMinute);
        editor.putInt("OffHour", offHour);
        editor.putInt("OffMinute", offMinute);
        editor.apply();

        Toast.makeText(this, "On/Off times saved", Toast.LENGTH_SHORT).show();
        finish(); // Close this activity
    }
}
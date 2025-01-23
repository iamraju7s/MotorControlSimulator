package com.example.motorcontrolsimulator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Context;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    // UI Elements
    private TextView waterLevelValue;
    private TextView motorStatusValue;
    private Button setOnOffTimeButton;
    private Button setScheduleButton;
    private Button viewHistoryButton;

    // Simulation Variables
    private Handler handler = new Handler();
    private int currentWaterLevel = 50; // Initial mock water level
    private boolean isMotorOn = false; // Initial motor status
    private static final String CHANNEL_ID = "motor_control_channel";
    private int notificationId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        waterLevelValue = findViewById(R.id.waterLevelValue);
        motorStatusValue = findViewById(R.id.motorStatusValue);
        setOnOffTimeButton = findViewById(R.id.setOnOffTimeButton);
        setScheduleButton = findViewById(R.id.setScheduleButton);
        viewHistoryButton = findViewById(R.id.viewHistoryButton);

        // Create notification channel (required for Android Oreo and above)
        createNotificationChannel();

        // Start the simulation
        startSimulation();

        // Set up button listeners
        setUpButtonListeners();
    }

    // Button Listener Setup
    private void setUpButtonListeners() {
        setOnOffTimeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SetOnOffTimeActivity.class);
            startActivity(intent);
        });

        setScheduleButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SetScheduleActivity.class);
            startActivity(intent);
        });

        viewHistoryButton.setOnClickListener(v -> {
            // Intent to open ViewHistoryActivity (To be implemented)
            // Intent intent = new Intent(MainActivity.this, ViewHistoryActivity.class);
            // startActivity(intent);
        });
    }

    // Simulation Start
    private void startSimulation() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateMockData();
                updateUI();
                checkAndNotify(); // Check for conditions and send notifications
                handler.postDelayed(this, 1000); // Update every second
            }
        }, 1000);
    }

    // Data Update Logic
    private void updateMockData() {
        // Simulate water level change
        currentWaterLevel += (Math.random() > 0.5) ? 1 : -1;
        currentWaterLevel = Math.max(0, Math.min(100, currentWaterLevel)); // Keep within 0-100

        // Get current time
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);

        // Motor control based on water level
        if (currentWaterLevel < 30) {
            isMotorOn = true;
        } else if (currentWaterLevel > 70) {
            isMotorOn = false;
        }

        // User-defined on/off timing logic
        SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        int onHour = sharedPreferences.getInt("OnHour", -1);
        int onMinute = sharedPreferences.getInt("OnMinute", -1);
        int offHour = sharedPreferences.getInt("OffHour", -1);
        int offMinute = sharedPreferences.getInt("OffMinute", -1);

        if (onHour == hour && onMinute == minute) {
            isMotorOn = true;
        } else if (offHour == hour && offMinute == minute) {
            isMotorOn = false;
        }

        // Check for scheduled operation
        boolean scheduleSet = sharedPreferences.getBoolean("ScheduleSet", false);
        if (scheduleSet) {
            int scheduleStartHour = sharedPreferences.getInt("ScheduleStartHour", -1);
            int scheduleStartMinute = sharedPreferences.getInt("ScheduleStartMinute", -1);
            int scheduleDuration = sharedPreferences.getInt("ScheduleDuration", 0);
            long scheduleStartTime = calculateMinutes(scheduleStartHour, scheduleStartMinute);
            long currentTime = calculateMinutes(hour, minute);

            if (currentTime >= scheduleStartTime && currentTime < scheduleStartTime + scheduleDuration) {
                isMotorOn = true; // Override other logic with schedule
            } else if (currentTime >= scheduleStartTime + scheduleDuration) {
                // If schedule is over, reset the flag
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("ScheduleSet", false);
                editor.apply();
            }
        }
    }

    // UI Update
    private void updateUI() {
        waterLevelValue.setText(currentWaterLevel + "%");
        motorStatusValue.setText(isMotorOn ? "ON" : "OFF");
    }

    // Helper function to calculate total minutes from hour and minute
    private long calculateMinutes(int hour, int minute) {
        return hour * 60 + minute;
    }

    // Create Notification Channel
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Motor Control Channel";
            String description = "Channel for Motor Control Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Check conditions and send notification
    private void checkAndNotify() {
        if (currentWaterLevel < 30) {
            sendNotification("Water Level Low", "Water level is below 30%. Motor turned ON.");
        } else if (currentWaterLevel > 70) {
            sendNotification("Water Level High", "Water level is above 70%. Motor turned OFF.");
        }

        if (isMotorOn) {
            sendNotification("Motor Status Changed", "Motor has been turned ON.");
        } else {
            sendNotification("Motor Status Changed", "Motor has been turned OFF.");
        }
    }

    // Send Notification
    private void sendNotification(String title, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Corrected resource name
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId++, builder.build());
    }
}
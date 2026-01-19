package com.example.projectnotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        TextView txtWelcome = findViewById(R.id.txtWelcome);
        TextView txtLocation = findViewById(R.id.txtLocation);
        ImageView imgFlag = findViewById(R.id.imgFlag);
        Button btnStart = findViewById(R.id.btnStart);

        SharedPreferences pref = getSharedPreferences("UserSetting", MODE_PRIVATE);
        String country = pref.getString("country", "id");
        String location = pref.getString("location", "Jakarta, Indonesia");

        // Jika pakai Fake GPS, ubah sesuai lokasi
        if (location.toLowerCase().contains("tokyo")) {
            country = "ja";
            location = "Tokyo, Japan";
        } else if (location.toLowerCase().contains("london")) {
            country = "en";
            location = "London, UK";
        } else {
            country = "id";
            location = "Jakarta, Indonesia";
        }

        // Simpan kembali
        pref.edit().putString("country", country).putString("location", location).apply();

        // Set UI
        txtLocation.setText(location);
        switch (country) {
            case "en": imgFlag.setImageResource(R.drawable.flag_uk); break;
            case "ja": imgFlag.setImageResource(R.drawable.flag_jp); break;
            default: imgFlag.setImageResource(R.drawable.flag_id);
        }

        // Sapaan sesuai waktu
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String time;
        if (hour >= 4 && hour < 11) time = "morning";
        else if (hour < 15) time = "afternoon";
        else if (hour < 18) time = "evening";
        else time = "night";

        txtWelcome.setText(getGreeting(country, time));
        Toast.makeText(this, getGreeting(country, time) + " 👋", Toast.LENGTH_SHORT).show();

        btnStart.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
    }

    private String getGreeting(String country, String time) {
        switch (country) {
            case "ja": return time.equals("morning") ? "おはようございます" :
                    time.equals("afternoon") ? "こんにちは" : "こんばんは";
            case "en": return time.equals("morning") ? "Good Morning" :
                    time.equals("afternoon") ? "Good Afternoon" :
                            time.equals("evening") ? "Good Evening" : "Good Night";
            default: return time.equals("morning") ? "Selamat Pagi" :
                    time.equals("afternoon") ? "Selamat Siang" :
                            time.equals("evening") ? "Selamat Sore" : "Selamat Malam";
        }
    }
}

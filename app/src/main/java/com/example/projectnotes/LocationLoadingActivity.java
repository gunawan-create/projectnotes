package com.example.projectnotes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;

public class LocationLoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_loading);

        // Langsung deteksi lokasi begitu activity terbuka
        detectActualLocation();

        // Delay 3 detik agar user melihat proses loading & lokasi tersimpan
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        }, 3000);
    }

    @SuppressLint("MissingPermission")
    private void detectActualLocation() {
        try {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // Coba ambil lokasi dari GPS atau Network
            Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc == null) {
                loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            SharedPreferences pref = getSharedPreferences("UserSetting", MODE_PRIVATE);
            String finalLocation = "Jakarta, Indonesia"; // Default kalau gagal

            if (loc != null) {
                double lat = loc.getLatitude();
                double lon = loc.getLongitude();

                Log.d("LOCATION_TEST", "Koordinat: " + lat + ", " + lon);

                // --- LOGIKA "BRUTE FORCE" (Deteksi berdasarkan angka Koordinat) ---
                if (lat > 30 && lat < 40 && lon > 130 && lon < 150) {
                    // Range wilayah Jepang/Tokyo
                    finalLocation = "Tokyo, Japan";
                } else if (lat > 50 && lat < 60 && lon > -5 && lon < 5) {
                    // Range wilayah Inggris/London
                    finalLocation = "London, UK";
                } else if (lat > -8 && lat < -5 && lon > 105 && lon < 110) {
                    // Range wilayah Jakarta
                    finalLocation = "Jakarta, Indonesia";
                } else {
                    // Jika di luar itu, coba pakai Geocoder sebagai cadangan terakhir
                    try {
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                        if (addresses != null && !addresses.isEmpty()) {
                            finalLocation = addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName();
                        }
                    } catch (Exception e) {
                        Log.e("LOCATION_TEST", "Geocoder Gagal");
                    }
                }

                // Simpan hasil ke SharedPreferences
                pref.edit().putString("location", finalLocation).apply();

                // Munculkan Toast agar kita tahu angka yang dibaca aplikasi
                Toast.makeText(this, "Detected: " + finalLocation + "\nLat: " + lat, Toast.LENGTH_LONG).show();

            } else {
                // Jika lokasi masih NULL dari sensor
                Toast.makeText(this, "Sensor GPS belum siap, pakai lokasi terakhir", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("LOCATION_TEST", "Error: " + e.getMessage());
        }
    }
}
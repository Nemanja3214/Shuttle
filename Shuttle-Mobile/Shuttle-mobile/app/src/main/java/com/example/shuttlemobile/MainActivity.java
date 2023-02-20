package com.example.shuttlemobile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.shuttlemobile.unregistered.LoginActivity;
import com.example.shuttlemobile.util.NotificationUtil;
import com.example.shuttlemobile.util.SettingsUtil;
import com.google.android.material.snackbar.Snackbar;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    LocationManager locationManager;
    LocationListener locationListener;
    private boolean openingLogin = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkForNetworkConnection();
        createNotificationChannels();
        initLocationListener();
        initLocationManager();

        // We fetch the reference to the sharedPreferences once, here, and then
        // SettingsUtil has it forever, which makes changing key-value prefs easier.
        SettingsUtil.init(
            getSharedPreferences(SettingsUtil.PREF_FILE, Context.MODE_PRIVATE)
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableLocationListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableLocationListening();
    }

    private void checkForNetworkConnection() {
        ConnectivityManager mng = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities networkCapabilities = mng.getNetworkCapabilities(mng.getActiveNetwork());

        if (networkCapabilities == null) {
            Toast.makeText(this, "Your device is offline.", Toast.LENGTH_SHORT).show();
        } else {
            if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_SUSPENDED)
            ) {
                Toast.makeText(this, "You are connected.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Your device is offline.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final CharSequence n_passenger = getString(R.string.channel_passenger);
            final int imp_passenger = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel ch_passenger = new NotificationChannel(NotificationUtil.PASSENGER_NOTIFICATION_CHANNEL_ID, n_passenger, imp_passenger);

            final CharSequence n_driver = getString(R.string.channel_driver);
            final int imp_driver = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel ch_driver = new NotificationChannel(NotificationUtil.DRIVER_NOTIFICATION_CHANNEL_ID, n_driver, imp_driver);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            notificationManager.createNotificationChannel(ch_passenger);
            notificationManager.createNotificationChannel(ch_driver);
        }
    }

    private void initLocationListener() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (location != null) {
                    Log.e("Location", location.toString());
                } else {
                    Log.e("Location", "null");
                }
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
            }
        };
    }

    private void initLocationManager() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("", "No location permission, asking");
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            }, 13579);
        }
        enableLocationListening();
        createLocationRequest();
    }

    @SuppressLint("MissingPermission")
    private void createLocationRequest() {
        Log.e("", "createLocationRequest()");
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000L, (float) 0.1, locationListener);
        enableLocationListening();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("", "onRequestPermissionsResult()");
        if (requestCode == 13579) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                for (String perm : permissions) {
                    if (perm.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Log.e("PERMISSION_GRANTED", "Manifest.permission.ACCESS_FINE_LOCATION");
                        createLocationRequest();
                    } else if (perm.equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        Log.e("PERMISSION_GRANTED", "Manifest.permission.ACCESS_COARSE_LOCATION");
                        createLocationRequest();
                    }
                }
            }
        }
    }

    private void onHasLocations() {
        if (openingLogin) {
            return;
        }
        openingLogin = true;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        }, 3000);
    }

    private void enableLocationListening() {
        boolean gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!gps_enabled && !network_enabled) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Enable Location");
            alertDialog.setMessage("This app requires for your location to be turned on.");
            alertDialog.setPositiveButton("Location Settings", (dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                enableLocationListening();
            });
            alertDialog.setNegativeButton("Cancel", (dialog, which) -> MainActivity.this.finishAffinity());
            alertDialog.create().show();
        } else {
            onHasLocations();
        }
    }

    private void disableLocationListening() {
        locationManager.removeUpdates(locationListener);
    }
}
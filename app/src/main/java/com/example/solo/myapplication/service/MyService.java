package com.example.solo.myapplication.service;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.solo.myapplication.R;
import com.example.solo.myapplication.utils.Constants;
import com.example.solo.myapplication.utils.StoreData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import static com.example.solo.myapplication.utils.Constants.DEST_LAT;
import static com.example.solo.myapplication.utils.Constants.DEST_LNG;


public class MyService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final int LOCATION_INTERVAL = 10000;
    public static final int FASTEST_LOCATION_INTERVAL = 5000;
    GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest = new LocationRequest();
    public final static String MY_ACTION = "MY_ACTION";


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest.setInterval(LOCATION_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_LOCATION_INTERVAL);

        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY; //by default

        mLocationRequest.setPriority(priority);
        mLocationClient.connect();

        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(Bundle dataBundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if (!mLocationClient.isConnected()) {
            mLocationClient.connect();
        }
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);
        } catch (IllegalStateException e) {
            StoreData.putBoolean(this, Constants.IS_SERVICE_STARTED, false);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            Location tempLocation = new Location("temp");

            tempLocation.setLatitude(Double.parseDouble(StoreData.getString(this, DEST_LAT, "0.0")));
            tempLocation.setLongitude(Double.parseDouble(StoreData.getString(this, DEST_LNG, "0.0")));

            Log.v("dest Lat", " " + tempLocation.getLatitude());
            Log.v("dest Lng", " " + tempLocation.getLongitude());

            if (tempLocation.getLongitude() == 0 || tempLocation.getLongitude() == 0)
                return;

            if (location.distanceTo(tempLocation) < 1000) {
                Intent intent = new Intent();
                intent.setAction(MY_ACTION);
                sendBroadcast(intent);
                showNotification();
                stopSelf();
            } else {
                cancelNotification(this);
            }

        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

    }

    private void showNotification() {
        String CHANNEL_ID = "ch1";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Tracker";
            String description = "Shown when user reach 1 km range of destination";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("GPS Tracker")
                .setContentText("You are now within 1 km range of destination")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(1, mBuilder.build());


    }


    public static void cancelNotification(Context context) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) context.getSystemService(ns);
        nMgr.cancel(1);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onDestroy() {
        mLocationClient.disconnect();
        super.onDestroy();
    }

}
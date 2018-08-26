package com.example.solo.myapplication.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.solo.myapplication.R;
import com.example.solo.myapplication.service.MyService;
import com.example.solo.myapplication.utils.Constants;
import com.example.solo.myapplication.utils.StoreData;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.solo.myapplication.utils.Constants.CURRENT_ADDRESS;
import static com.example.solo.myapplication.utils.Constants.DESTINATION_ADDRESS;
import static com.example.solo.myapplication.utils.Constants.DEST_LAT;
import static com.example.solo.myapplication.utils.Constants.DEST_LNG;
import static com.example.solo.myapplication.utils.Constants.IS_SERVICE_STARTED;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CURRENT_LOCATION = 1001;
    private static final int REQUEST_CODE_DEST_LOCATION = 1002;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1003;

    @BindView(R.id.btnCurLocation)
    Button btnCurLocation;

    @BindView(R.id.btnDestLocation)
    Button btnDestLocation;

    @BindView(R.id.tvCurLocation)
    TextView tvCurLocation;

    @BindView(R.id.tvDestLocation)
    TextView tvDestLocation;

    @BindView(R.id.tbtn)
    ToggleButton tbtn;

    String curtAddress, destAddress, destLat, destLng;
    boolean isServiceEnabled;

    MyReceiver myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        init();

        tbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    if (hasLcaionPermission()) {
                        startServiceFunction();
                    } else {
                        tbtn.setChecked(false);
                        requestLocationPermission();
                    }
                } else {
                    stopService(new Intent(MainActivity.this, MyService.class));
                    StoreData.putBoolean(MainActivity.this, Constants.IS_SERVICE_STARTED, false);
                }
            }
        });
    }

    @Override
    protected void onStart() {

        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyService.MY_ACTION);
        registerReceiver(myReceiver, intentFilter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(myReceiver);
        super.onStop();
    }

    private void init() {
        curtAddress = StoreData.getString(this, CURRENT_ADDRESS, "CURRENT LOCATION IS UNKNOWN");
        destAddress = StoreData.getString(this, DESTINATION_ADDRESS, "DESTINATION LOCATION IS UNKNOWN");
        destLat = StoreData.getString(this, DEST_LAT, "0");
        destLng = StoreData.getString(this, DEST_LNG, "0");
        isServiceEnabled = StoreData.getBoolean(this, IS_SERVICE_STARTED, false);
        tbtn.setChecked(isServiceEnabled);
        tvCurLocation.setText(curtAddress);
        tvDestLocation.setText(destAddress);
    }

    private void pickLocation(int resquestCode) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        LatLngBounds latLngBounds = new LatLngBounds(new LatLng(0, 0), new LatLng(0, 0));
        builder.setLatLngBounds(latLngBounds);

        try {
            startActivityForResult(builder.build(this), resquestCode);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e("", "onClick: GooglePlayServicesRepairableException: " + e.getMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("", "onClick: GooglePlayServicesNotAvailableException: " + e.getMessage());
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CURRENT_LOCATION) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                tvCurLocation.setText(place.getAddress());
                StoreData.putString(this, CURRENT_ADDRESS, String.valueOf(place.getAddress()));
            }
        } else if (requestCode == REQUEST_CODE_DEST_LOCATION) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                tvDestLocation.setText(place.getAddress());
                StoreData.putString(this, DESTINATION_ADDRESS, String.valueOf(place.getAddress()));
                Toast.makeText(this, "Selected lat lang" + place.getLatLng().latitude + ", " + place.getLatLng().longitude, Toast.LENGTH_SHORT).show();
                StoreData.putString(this, DEST_LAT, String.valueOf(place.getLatLng().latitude));
                StoreData.putString(this, DEST_LNG, String.valueOf(place.getLatLng().longitude));
            }
        }

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCurLocation:
                pickLocation(REQUEST_CODE_CURRENT_LOCATION);
                break;
            case R.id.btnDestLocation:
                pickLocation(REQUEST_CODE_DEST_LOCATION);
                break;
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
    }

    private boolean hasLcaionPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case REQUEST_CODE_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tbtn.setChecked(true);
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showRationalePermissionDialog();
                } else {
                    requestLocationPermission();
                }
                break;
        }
    }

    private void showRationalePermissionDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setMessage("Click on 'APP INFO' and allow permission to access device location");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "APP INFO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });

        alertDialog.show();
    }

    private void startServiceFunction() {
        startService(new Intent(this, MyService.class));
        StoreData.putBoolean(this, IS_SERVICE_STARTED, true);
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            tbtn.setChecked(false);
        }

    }
}

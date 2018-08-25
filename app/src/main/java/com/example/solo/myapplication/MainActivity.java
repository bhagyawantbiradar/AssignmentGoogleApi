package com.example.solo.myapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        curtAddress = StoreData.getString(this, CURRENT_ADDRESS, "CURRENT LOCATION UNKNOWN");
        destAddress = StoreData.getString(this, DESTINATION_ADDRESS, "DESTINATION LOCATION UNKNOWN");
        destLat = StoreData.getString(this, DEST_LAT, "0");
        destLng = StoreData.getString(this, DEST_LNG, "0");

    }

    private void pickLocation(int resquestCode) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(new LatLng(132154.5, 1215454.5), new LatLng(123454.54, 564545.5));
        builder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);

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
            case R.id.tbtn:
                if (hasLcaionPermission()) {

                } else {
                    requestLocationPermission();
                }
                break;
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
    }

    private boolean hasLcaionPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
3
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case REQUEST_CODE_LOCATION_PERMISSION:
                if (grantResults.length<0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startServiceFunction();
                }else if(!ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                    showRationalePermissionDialog();
                } else {
                    requestLocationPermission();
                }
                break;
        }
    }

    private void showRationalePermissionDialog() {
        AlertDialog alertDialog = new AlertDialog(this);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "APP INFO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }

    private void startServiceFunction() {

    }
}

package com.example.gauchomap;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.animation.Animator;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback {

    private LottieAnimationView animationView;
    private GoogleMap googleMap;
    private GnssStatus.Callback mGnssStatusCallback;
    private Toolbar mToolbar;

    private FloatingActionButton menu;
        private FloatingActionButton menuCancel;
        private FloatingActionButton settings;
        private FloatingActionButton stats;
    private FloatingActionButton navigate;
        private FloatingActionButton navigateCancel;
        private FloatingActionButton bike;
        private FloatingActionButton search;

    private TextView satelliteCountTextView;
    private TextView satelliteFixCountTextView;
    private TextView statsDebugTextView;
    private TextView notificationDebugTextView;
    private TextView themeDebugTextView;
    private TextView mapTypeDebugTextView;
    private TextView mapZoomDebugTextView;
    private TextView pollingSpeedDebugTextView;

    private ArrayList<Satellite> satelliteArray;
    private ArrayAdapter adapter;

    private Animation fromBottom;
    private Animation toBottom;
    private Animation fromBottom2;
    private Animation toBottom2;

    private LocationManager mLocationManager;

    private Marker currentLocation;

    private boolean autoCameraButtonPressed;
    private boolean menuClicked;
    private boolean navigateClicked;
    private boolean statsDebug;
    private boolean notificationDebug;

    private String[] settingsValues;

    private String theme;
    private int mapType;
    private int mapZoom;
    private int pollingSpeed;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize Shared Preferences
        SharedPreferences prefs = getSharedPreferences("GauchoMapStorage", MODE_PRIVATE);
        autoCameraButtonPressed = prefs.getBoolean("autoCameraButton", false);

        // Initialize Preferences and load settings variables
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        statsDebug = sharedPreferences.getBoolean("stats", false);
        notificationDebug = sharedPreferences.getBoolean("notifications", false);
        settingsValues = new String[] {sharedPreferences.getString("themes", "0"),
                sharedPreferences.getString("mapType", "0"),
                sharedPreferences.getString("mapZoom", "1"),
                sharedPreferences.getString("pollingSpeed", "1")};
        // Decode settingsValues array
        decodeSettings(settingsValues);

        // Set up animations
        animationView = findViewById(R.id.animationView);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);
        fromBottom2 = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottom2 = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);

        // Set up toolbar
        mToolbar = findViewById(R.id.appToolbar);
        setSupportActionBar(mToolbar);

        // Set up Google Maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MainActivity.this::onMapReady);

        // Set up fab buttons
        menu = findViewById(R.id.menuFab);
            menuCancel = findViewById(R.id.cancelMenuFab);
            settings = findViewById(R.id.settingsFab);
            stats = findViewById(R.id.statsFab);
        navigate = findViewById(R.id.navigationFab);
            navigateCancel = findViewById(R.id.cancelNavigateFab);
            bike = findViewById(R.id.bikeFab);
            search = findViewById(R.id.searchFab);
        menuClicked = false;
        navigateClicked = false;
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!navigateClicked){
                    menuClicked = true;
                    onMenuButtonClicked();
                }
            }
        });
        menuCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuClicked = false;
                onMenuButtonClicked();
            }
        });
        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!menuClicked){
                    navigateClicked = true;
                    onNavigateButtonClicked();
                }
            }
        });
        navigateCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateClicked = false;
                onNavigateButtonClicked();
            }
        });

        // Set up Location Manager
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Set up debug data
        satelliteCountTextView = findViewById(R.id.satelliteCount);
        satelliteFixCountTextView = findViewById(R.id.satelliteFixCount);
        statsDebugTextView = findViewById(R.id.statsDebug);
            statsDebugTextView.setText(String.valueOf(statsDebug));
        notificationDebugTextView = findViewById(R.id.notificationDebug);
            notificationDebugTextView.setText(String.valueOf(notificationDebug));
        themeDebugTextView = findViewById(R.id.themeDebug);
            themeDebugTextView.setText(theme);
        mapTypeDebugTextView = findViewById(R.id.mapTypeDebug);
            mapTypeDebugTextView.setText(String.valueOf(mapType));
        mapZoomDebugTextView = findViewById(R.id.mapZoomDebug);
            mapZoomDebugTextView.setText(String.valueOf(mapZoom));
        pollingSpeedDebugTextView = findViewById(R.id.pollingSpeedDebug);
            pollingSpeedDebugTextView.setText(String.valueOf(pollingSpeed));


        // Set up satelliteArray
        if(satelliteArray == null){
            satelliteArray = new ArrayList<>();
        }
//        satelliteList = findViewById(R.id.satelliteList);
//        satelliteList.setVisibility(View.INVISIBLE);
//        satelliteList.setBackgroundColor(Color.WHITE);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, satelliteArray);
//        satelliteList.setAdapter(adapter);

        // Set up GNSS
        mGnssStatusCallback = new GnssStatus.Callback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSatelliteStatusChanged(GnssStatus status) {
                super.onSatelliteStatusChanged(status);
                satelliteArray.clear();

                int satelliteCount = status.getSatelliteCount();
                int satelliteFixCount = 0;

                for(int sat = 0; sat<satelliteCount; sat++ ){
                    double azimuth = status.getAzimuthDegrees(sat);
                    double elevation = status.getElevationDegrees(sat);
                    double carrierFrequency = status.getCarrierFrequencyHz(sat);
                    double noiseDensity = status.getCn0DbHz(sat);
                    int constellationName = status.getConstellationType(sat);
                    int SVID = status.getSvid(sat);
                    if(status.usedInFix(sat)){
                        satelliteFixCount++;
                    }
                    Satellite satellite = new Satellite(sat + 1,azimuth,elevation,carrierFrequency,noiseDensity,constellationName,SVID);
                    satelliteArray.add(satellite);
                }
                satelliteCountTextView.setText("" + satelliteCount);
                satelliteFixCountTextView.setText("" + satelliteFixCount);
                adapter.notifyDataSetChanged();
            }
        };

        //Close Animation and make view visible
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // animation : invisible
                animationView.setVisibility(View.INVISIBLE);
                // toolbar : visible
                mToolbar.setVisibility(View.VISIBLE);
                // fabs : visible
                menu.setVisibility(View.VISIBLE);
                navigate.setVisibility(View.VISIBLE);
                // debug : visible if condition met
                // debug : invisible if condition not met
                if(statsDebug){
                    satelliteCountTextView.setVisibility(View.VISIBLE);
                    satelliteFixCountTextView.setVisibility(View.VISIBLE);
                    statsDebugTextView.setVisibility(View.VISIBLE);
                    notificationDebugTextView.setVisibility(View.VISIBLE);
                    themeDebugTextView.setVisibility(View.VISIBLE);
                    mapTypeDebugTextView.setVisibility(View.VISIBLE);
                    mapZoomDebugTextView.setVisibility(View.VISIBLE);
                    pollingSpeedDebugTextView.setVisibility(View.VISIBLE);
                }
            }
        }, 4500);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMapType(mapType);
    }

    @Override
    public void onLocationChanged(Location location) {
        if(currentLocation != null){
            currentLocation.remove();
        }

        final LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());

        currentLocation = googleMap.addMarker(new MarkerOptions()
                .position(loc)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("Current Location"));

        //auto-centering
        if(autoCameraButtonPressed){
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, mapZoom));
        }
    }

    // Toolbar button setup
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.appToolbar);
        mToolbar.inflateMenu(R.menu.main_activity_menu);
        mToolbar.setOnMenuItemClickListener(item -> onOptionsItemSelected(item));
        return true;
    }

    // Toolbar button logic
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.autoCameraButton:
                Log.e("Button: ", "autoCameraButton pressed");
                autoCameraButtonPressed = !autoCameraButtonPressed;
                break;
            default:
                break;
        }
        return true;
    }

    public void onMenuButtonClicked() {
        if(menuClicked){
            settings.setVisibility(View.VISIBLE);
            settings.setAnimation(fromBottom);
            settings.getAnimation().start();
            stats.setVisibility(View.VISIBLE);
            stats.setAnimation(fromBottom);
            stats.getAnimation().start();
            menu.setVisibility(View.INVISIBLE);
            menuCancel.setVisibility(View.VISIBLE);

            settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    menuClicked = false;
                    onMenuButtonClicked();
                    Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(myIntent);
                }
            });
            stats.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    menuClicked = false;
                    onMenuButtonClicked();
                    Intent myIntent = new Intent(MainActivity.this, StatisticsActivity.class);
                    startActivity(myIntent);
                }
            });
        } else {
            settings.setVisibility(View.INVISIBLE);
            settings.setClickable(false);
            settings.setAnimation(toBottom);
            settings.getAnimation().start();
            stats.setVisibility(View.INVISIBLE);
            stats.setClickable(false);
            stats.setAnimation(toBottom);
            stats.getAnimation().start();
            menu.setVisibility(View.VISIBLE);
            menuCancel.setVisibility(View.INVISIBLE);
        }

    }

    public void onNavigateButtonClicked() {
        if(navigateClicked){
            bike.setVisibility(View.VISIBLE);
            bike.setAnimation(fromBottom2);
            bike.getAnimation().start();
            search.setVisibility(View.VISIBLE);
            search.setAnimation(fromBottom2);
            search.getAnimation().start();
            navigate.setVisibility(View.INVISIBLE);
            navigateCancel.setVisibility(View.VISIBLE);
        } else {
            bike.setVisibility(View.INVISIBLE);
            bike.setClickable(false);
            bike.setAnimation(toBottom2);
            bike.getAnimation().start();
            search.setVisibility(View.INVISIBLE);
            search.setClickable(false);
            search.setAnimation(toBottom2);
            search.getAnimation().start();
            navigate.setVisibility(View.VISIBLE);
            navigateCancel.setVisibility(View.INVISIBLE);
        }
    }

    public void decodeSettings(String[] settingsValuesArray){
        if(settingsValuesArray[0].equals("0")){
            theme = "Dark";
        } else {
            theme = "Light";
        }

        if(settingsValuesArray[1].equals("0")){
            mapType = 1;
        } else if(settingsValuesArray[1].equals("1")){
            mapType = 4;
        } else {
            mapType = 3;
        }

        if(settingsValuesArray[2].equals("0")){
            mapZoom = 17;
        } else if(settingsValuesArray[2].equals("1")){
            mapZoom = 16;
        } else {
            mapZoom = 15;
        }

        if(settingsValuesArray[3].equals("0")){
            pollingSpeed = 2000;
        } else if(settingsValuesArray[3].equals("1")){
            pollingSpeed = 1000;
        } else {
            pollingSpeed = 500;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStart() throws SecurityException {
        super. onStart();
        String[] permissions = {Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET};

        if (checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            Log.e("Polling Speed: ", "" + pollingSpeed);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, pollingSpeed, 0, this);
            mLocationManager.registerGnssStatusCallback(mGnssStatusCallback);
        } else {
            Log.e("Permissions", "Permissions Requested");
            requestPermissions(permissions, 123);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Initialize Editor for saving data
        SharedPreferences.Editor  mEditor = getSharedPreferences("GauchoMapStorage", MODE_PRIVATE).edit();
        //save autoCameraButtonPressed state
        mEditor.putBoolean("autoCameraButton", autoCameraButtonPressed).apply();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStop() {
        super.onStop();
        mLocationManager.removeUpdates(this);
        mLocationManager.unregisterGnssStatusCallback(mGnssStatusCallback);
    }
}
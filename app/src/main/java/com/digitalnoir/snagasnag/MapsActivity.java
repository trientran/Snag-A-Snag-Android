package com.digitalnoir.snagasnag;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.digitalnoir.snagasnag.fragment.AddSizzleFragment;
import com.digitalnoir.snagasnag.model.Sizzle;
import com.digitalnoir.snagasnag.utility.LogUtil;
import com.digitalnoir.snagasnag.utility.PermissionUtils;
import com.digitalnoir.snagasnag.utility.SizzleLoader;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.digitalnoir.snagasnag.fragment.AddSizzleFragment.EXTRA_SELECTED_ADDRESS;
import static com.digitalnoir.snagasnag.fragment.AddSizzleFragment.EXTRA_SELECTED_LAT_LNG;
import static com.digitalnoir.snagasnag.utility.DataUtil.DISPLAY_SIZZLE_URL_TAG;
import static com.digitalnoir.snagasnag.utility.DataUtil.SIZZLE_BASE_URL;

public class MapsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<Sizzle>>,
        SharedPreferences.OnSharedPreferenceChangeListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapLongClickListener,
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        AddSizzleFragment.OnCloseBtnClickListener {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = MapsActivity.class.getName();

    /**
     * List of sizzles
     */
    private List<Sizzle> mSizzles;

    /**
     * Constant value for the sizzle loader ID. We can choose any integer.
     * We may use multiple loaders.
     */
    private static final int SIZZLE_LOADER_ID = 1;

    /**
     * Code used in requesting runtime permissions.
     */
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PICK_IMAGE_REQUEST = 99;
    Bitmap bitmap;


    /**
     * My location button
     */
    private ImageButton mMyLocationBtn;

    /**
     * Refresh button
     */
    private ImageButton mRefreshBtn;

    /**
     * Add button
     */
    private ImageButton mAddBtn;

    private FrameLayout fragmentContainer;

    private Button mAddSizzleAlertBtn;

    /* *************************************************************************************
     * Below is all declarations for Location service
     */

    /**
     * Constant used in the location settings dialog.
     */
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LOCATION = "location";
    private final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-transfusionTime-string";

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Provides access to the Location Settings API.
     */
    private SettingsClient mSettingsClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private LocationSettingsRequest mLocationSettingsRequest;

    /**
     * Callback for Location events.
     */
    private LocationCallback mLocationCallback;

    /**
     * Represents a geographical location.
     */
    private Location mLastLocation;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    private Boolean mRequestingLocationUpdates;

    /**
     * Time when the location was updated represented as a String.
     */
    private String mLastUpdateTime;

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    /* *************************************************************************************/


    /* *************************************************************************************
     * Below is all declarations for Map service
     */

    /**
     * call a GoogleMap object
     */
    private GoogleMap mMap = null;

    /**
     * List of markerOptions to be added to Google Map
     */
    List<MarkerOptions> markerOptions = new ArrayList<>();

    /**
     * default location (Australia)
     */
    private static final LatLng BRISBANE = new LatLng(-27.47093, 153.0235);
    private static final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);
    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);
    private static final LatLng ADELAIDE = new LatLng(-34.92873, 138.59995);
    private static final LatLng PERTH = new LatLng(-31.952854, 115.857342);

    /**
     * current marker (on map long click)
     */
    private List<Marker> newTempMarkers = new ArrayList<>();

    /**
     * current marker (on map long click)
     */
    private boolean tempMarker = true;

    /**
     * GeoDataClient object
     */
    protected GeoDataClient mGeoDataClient;

    /**
     * Keeps track of the selected marker.
     */
    private Marker mSelectedMarker;

    /**
     * Keeps track of the selected address.
     */
    String mSelectedAddress = null;

    private Bundle mSavedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Initialize Loader
        initializeLoader();

        // Obtain a reference to the SharedPreferences file for this app
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // And register to be notified of preference changes
        // So we know when the user has adjusted the query settings
        prefs.registerOnSharedPreferenceChangeListener(this);
        String username = prefs.getString("username", "missing");
        int userId = prefs.getInt("userId", 0);

        mSizzles = new ArrayList<>();

        LogUtil.debug("TrienGetPref", username + " " + prefs.getInt("userId", 0));

        // set up my location button
        mMyLocationBtn = (ImageButton) findViewById(R.id.myLocBtn);
        mMyLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mLastLocation != null) {
                    // move camera to current location
                    LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12);
                    // mMap.animateCamera(cameraUpdate);
                    mMap.moveCamera(cameraUpdate);
                }

                onMyLocationButtonClick();

            }
        });



        mAddBtn = (ImageButton) findViewById(R.id.addBtn);

        fragmentContainer = (FrameLayout) findViewById(R.id.fragmentContainer);
        mAddSizzleAlertBtn = (Button) findViewById(R.id.addSizzleAlertBtn);


        mRefreshBtn = (ImageButton) findViewById(R.id.refreshBtn);
        mRefreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onRefreshBtnClick();
            }
        });


        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LogUtil.debug("triensiz", String.valueOf(mSizzles));

                if (mAddSizzleAlertBtn.getVisibility() == View.GONE) {
                    mAddBtn.setImageResource(R.drawable.ic_cancel_add_snag);
                    mAddSizzleAlertBtn.setVisibility(View.VISIBLE);
                } else {
                    mAddBtn.setImageResource(R.drawable.ic_add_snag);
                    mAddSizzleAlertBtn.setVisibility(View.GONE);
                }

            }
        });



        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        new OnMapAndViewReadyListener(mapFragment, this);

        // initialize mRequestingLocationUpdates and mLastUpdateTime
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);

        // initialize FusedLocationProviderClient and SettingsClient object to invoke location settings
        // on app first time startup
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        // Kick off the process of building the LocationCallback, LocationRequest, and
        // LocationSettingsRequest objects.
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();

        mSavedInstanceState = savedInstanceState;

    }

    /**
     * Initialize Loader to begin loading data on background thread
     */
    private void initializeLoader() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(SIZZLE_LOADER_ID, null, this);
        } else {
            Log.e(LOG_TAG, "Error loading");
            Toast.makeText(this, getString(R.string.toast_check_internet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<List<Sizzle>> onCreateLoader(int i, Bundle bundle) {

        // create new SizzleLoader instance to kick off loading data on background thread
        String allSizzleURL = SIZZLE_BASE_URL + DISPLAY_SIZZLE_URL_TAG;

        return new SizzleLoader(this, allSizzleURL);
    }

    /**
     * As loader always load data on background thread. We gotta check if an object used inside onLoadFinished
     * is not null
     */
    @Override
    public void onLoadFinished(Loader<List<Sizzle>> loader, List<Sizzle> sizzles) {

        LogUtil.debug("triensizzle", String.valueOf(sizzles));

        // If there is a valid list of {@link Sizzle}, then extract Lat Long values from all sizzles
        // and add them all as markers on map.
        if (sizzles != null && !sizzles.isEmpty()) {

            // Add all markers on map
            addAllMarkersOnMap(sizzles);

        }

    }


    private void moveCameraToAustralia() {
        // move camera to Australia by default
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(PERTH)
                .include(SYDNEY)
                .include(ADELAIDE)
                .include(BRISBANE)
                .include(MELBOURNE)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the User will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the User has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Important note: onMapReady is triggered after onStart(). Therefore,
        // be careful when invoking any method associated with mMap as it only instantiated here.
        // We should check if mMap is not null before invoking a relevant method
        mMap = googleMap;

        // Set listener for marker click event.
        mMap.setOnMarkerClickListener(this);
        // Set listener for my location icon
        mMap.setOnMyLocationClickListener(this);
        mMap.setOnMapLongClickListener(this);
        enableMyLocation();


        LogUtil.debug("trienoption", markerOptions.toString());

        // startLocationUpdates();
        moveCameraToAustralia();

        //if map doesn't have markers, add markers to map
        if (this.markerOptions != null && !this.markerOptions.isEmpty()) {
            for (MarkerOptions markerOption : this.markerOptions) {
                this.mMap.addMarker(markerOption);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // We receive location updates if permission has been granted
        if (checkPermissions()) {
            startLocationUpdates();
        } else if (!checkPermissions()) {
            requestPermissions();
        }

    }


    // helper method to add all markers on map
    private void addAllMarkersOnMap(List<Sizzle> sizzles) {

        // make the marker icons bigger
        Bitmap resizedBitmap = resizeBitmap(R.drawable.ic_snag_pin);

        // store MarkerOptions to markerOptions array


        // check if mMap is not null before invoking addMarker method to avoid "null object reference" error
        if (mMap != null) {

            for (Sizzle sizzle : sizzles) {
                // build markers with sizzleIds tagged
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.valueOf(sizzle.getLatitude()), Double.valueOf(sizzle.getLongitude())))
                        .title(sizzle.getName())
                        .snippet(sizzle.getAddress())
                        .icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap))).setTag(sizzle);

                // add all sizzles into mSizzles

                mSizzles.add(sizzle);
            }

        } else {

            for (Sizzle sizzle : sizzles) {
                // build markers with sizzleIds tagged
                markerOptions.add(new MarkerOptions()
                        .position(new LatLng(Double.valueOf(sizzle.getLatitude()), Double.valueOf(sizzle.getLongitude())))
                        .title(sizzle.getName())
                        .snippet(sizzle.getAddress())
                        .icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)));

                // add all sizzles into mSizzles
                mSizzles.add(sizzle);

            }

        }

    }

    // bitmap resize tool
    private Bitmap resizeBitmap(int drawable) {
        int height = 200;
        int width = 200;
        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(drawable);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    @Override
    public void onLoaderReset(Loader<List<Sizzle>> loader) {
        // clear sizzle list
         mSizzles = new ArrayList<>();
    }

    public void onRefreshBtnClick() {
        // clear all markers so the app reload updated data and recreate all markers
        mMap.clear();

        // Restart loader to reload updated data and recreate all markers
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }


    @Override
    public void onMapLongClick(LatLng latLng) {

        if (mAddSizzleAlertBtn.getVisibility() == View.VISIBLE) {

            // display views properly
            setUpViewsOnMapLongClick();

            // create a new marker with yellow icon on selected position, add it to newTempMarkers list
            newTempMarkers.add(addMarker(latLng, tempMarker));

            // move camera to the selected location but adjust the marker to show on top of
            // Create Sizzle popup window
            // First move camera to selected location and zoom to 17 degree
            CameraUpdate cameraUpdate1 = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            mMap.moveCamera(cameraUpdate1);
            // Then get the screen height of user's device, then move camera accordingly
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int screenHeight = metrics.heightPixels;
            LogUtil.debug("trienHeight", String.valueOf(screenHeight));
            CameraUpdate cameraUpdate2 = CameraUpdateFactory.scrollBy(0, (screenHeight / 2) - 350);
            // mMap.animateCamera(cameraUpdate); // this can be used to animate the camera smoothly
            mMap.moveCamera(cameraUpdate2);

            // Creating a new head fragment
            AddSizzleFragment addSizzleFragment = new AddSizzleFragment();
            if(mSavedInstanceState == null) {

                // Add the fragment to its container using a transaction
                getFragmentManager().beginTransaction()
                        .add(R.id.fragmentContainer, addSizzleFragment)
                        .commit();
            }

            Double lati = (latLng.latitude);
            Double loni = (latLng.longitude);

            Geocoder myGeo = new Geocoder(getApplicationContext(), Locale.getDefault());


            try {
                List<Address> myAddresses = myGeo.getFromLocation(lati, loni, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                if (myAddresses != null && myAddresses.size() > 0) {

                    mSelectedAddress = myAddresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                } else {

                    mSelectedAddress = "";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            LogUtil.debug("addressSnag", mSelectedAddress);
            LogUtil.debug("latlongSnag", latLng.toString());

            // send selected address to AddSizzleFragment
            Bundle bundle = new Bundle();
            bundle.putString(EXTRA_SELECTED_ADDRESS, mSelectedAddress);
            bundle.putParcelable(EXTRA_SELECTED_LAT_LNG, latLng);
            addSizzleFragment.setArguments(bundle);

        }
    }

    private void setUpViewsOnMapLongClick() {

        //fragmentContainer.setVisibility(View.VISIBLE);
        mAddBtn.setImageResource(R.drawable.ic_add_snag);
        mAddSizzleAlertBtn.setVisibility(View.GONE);
        mRefreshBtn.setVisibility(View.GONE);
        mMyLocationBtn.setVisibility(View.GONE);
    }

    /**
     * AddMarker method. If adding temporary one, create a marker with yello snag pin icon
     * If a permanent one, pick the blue one
     */
    private Marker addMarker( Sizzle sizzle, boolean tempMarker) {

        Marker newMarker;

        if (tempMarker) {

            newMarker  = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.valueOf(sizzle.getLatitude()), Double.valueOf(sizzle.getLongitude())))
                    .title("selected location")
                    .icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap(R.drawable.ic_snag_pin_yellow))));

        }

        else {

            newMarker  =  mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.valueOf(sizzle.getLatitude()), Double.valueOf(sizzle.getLongitude())))
                    .title(sizzle.getName())
                    .snippet(sizzle.getAddress())
                    .icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap(R.drawable.ic_snag_pin))));

            newMarker.setTag(sizzle);
        }

        return newMarker;
    }


    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
            }

            // Update the value of mLastLocation from the Bundle and move the camera to the last
            // known location.
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mLastLocation
                // is not null.
                setLastLocation((Location) savedInstanceState.getParcelable(KEY_LOCATION));
                // Move camera to the last known location
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())));
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }

        }
    }

/*    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            //showMissingPermissionError();
            //this.recreate();
            mPermissionDenied = false;
        }

    }*/

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we remove location updates. Here, we resume receiving
        // location updates if the user has requested them.
        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates();
        } else if (!checkPermissions()) {
            requestPermissions();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Remove location updates to save battery.
        stopLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Remove location updates to save battery.
        stopLocationUpdates();
    }

    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(KEY_LOCATION, mLastLocation);
        savedInstanceState.putString(KEY_LAST_UPDATED_TIME_STRING, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }


     /* *************************************************************************************
        *************************************************************************************
        *************************************************************************************
        *************************************************************************************
     * Location and permission supporting methods
     */

    /**
     * Preserved method to get Last known location which can be used in User Preferences later.
     */
    private void setLastLocation(Location lastLocation) {

        mLastLocation = lastLocation;

    }

    /**
     * Creates a callback for receiving location events.
     */
    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                setLastLocation(locationResult.getLastLocation());
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

            }
        };
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This app uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real time location
     * updates.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * actions after users choose to or not to change location settings
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(LOG_TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(LOG_TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        break;
                }
                break;
        }
    }

    /**
     * Requests location updates from the FusedLocationApi. Note: we don't call this unless location
     * runtime permission has been granted.
     */
    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(LOG_TAG, "All location settings are satisfied.");

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        try {
                            mFusedLocationClient.getLastLocation()
                                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Location> task) {
                                            if (task.isSuccessful() && task.getResult() != null) {

                                                setLastLocation(task.getResult());

                                            } else {
                                                Log.w(LOG_TAG, "Failed to get location.");
                                            }
                                        }
                                    });
                        } catch (SecurityException unlikely) {
                            Log.e(LOG_TAG, "Lost location permission." + unlikely);
                        }


                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(LOG_TAG,
                                        "Location settings are not satisfied. Attempting to upgrade " +
                                                "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(MapsActivity.this,
                                            REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(LOG_TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(LOG_TAG, errorMessage);
                                Toast.makeText(MapsActivity.this,
                                        errorMessage,
                                        Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                        }


                    }
                });
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    private void stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            LogUtil.debug(LOG_TAG, "stopLocationUpdates: updates never requested, no-op.");
            return;
        }

        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRequestingLocationUpdates = false;
                    }
                });
    }

    @Override
    public boolean onMyLocationButtonClick() {
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;

            // start location updates
            startLocationUpdates();
        }


        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location", Toast.LENGTH_LONG).show();
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(LOG_TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(LOG_TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mRequestingLocationUpdates) {
                    Log.i(LOG_TAG, "Permission granted, updates requested, starting location updates");
                    startLocationUpdates();
                }
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Enable the my location layer if the permission has been granted (disabled as we
                // use another icon in place of default my location btn)
                //enableMyLocation();
            } else {
                // Display the missing permission error dialog when the fragments resume.
                mPermissionDenied = true;
            }
        } else {
            return;
        }
    }


    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(LOG_TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MapsActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(LOG_TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the OnCloseBtnClickListener item.
     * @param listener         The listener associated with the Snackbar OnCloseBtnClickListener.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

        // return the current userID and username

        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        String username = mSettings.getString("username", "missing");
        int userId = mSettings.getInt("userId", 0);

        LogUtil.debug("TrienGetPref2", username + " " + userId);
    }


    @Override
    public void onCloseBtnClick() {

        // remove addSizzleFragment
        AddSizzleFragment addSizzleFragment = (AddSizzleFragment) getFragmentManager()
                .findFragmentById(R.id.fragmentContainer);
        getFragmentManager().beginTransaction()
                .remove(addSizzleFragment).commit();

        // enable mRefreshBtn and mMyLocationBtn
        mRefreshBtn.setVisibility(View.VISIBLE);
        mMyLocationBtn.setVisibility(View.VISIBLE);

        // remove all new temporary markers
        for (Marker marker : newTempMarkers) {
            marker.remove();
        }
    }

    @Override
    public void onSnagItBtnClick(Sizzle sizzle) {

        // remove fragment and adjust views accordingly
        onCloseBtnClick();

        // create a new permanent marker on the selected location
        addMarker(sizzle, !tempMarker);

        onRefreshBtnClick();
    }

}

package com.mobileutilities.myplacescatalog;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.List;
import java.util.Locale;

import adapters.LocationAdapter;
import bean.LocationDTO;
import database.LocationOperation;

/**
 * Created by admin on 5/3/2016.
 */
public class DetailActivty extends Activity implements LocationListener {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private TextView mRefreshLocation;
    private LocationManager locationManager;
    private String mAddress;
    private String mCity;
    public Location mLocation;
    public double longitude;
    public double latitude;
    private ListView mMyLocationListView;
    //The minimum distance to change updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters
    //The minimum time beetwen updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0;//1000 * 60 * 1; // 1 minute
    private final static boolean forceNetwork = false;
    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;
    private boolean locationServiceAvailable;
    private LocationOperation locationOperation;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        // Create the InterstitialAd and set the adUnitId.
        mInterstitialAd = new InterstitialAd(this);
        // Defined in res/values/strings.xml
        mInterstitialAd.setAdUnitId(getString(R.string.interestial_ad_unit_id));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });
        requestNewInterstitial();

        mRefreshLocation = (TextView) findViewById(R.id.changeLocation);
        mRefreshLocation.setOnClickListener(onRefreshClickListener);
        locationOperation = new LocationOperation(DetailActivty.this);
        initLocationService(this);
        if(locationServiceAvailable){
            new GetLocationTask().execute("");
        }else{
            new AlertDialog.Builder(DetailActivty.this)
                    .setTitle("Alert")
                    .setMessage("Cannot get the location. Please turn on internet and GPS and refresh")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationOperation.open();
        LocationAdapter adapter = new LocationAdapter(this,locationOperation.getAllLocations());
        locationOperation.close();
        mMyLocationListView=(ListView)findViewById(R.id.my_location_list);
        mMyLocationListView.setAdapter(adapter);
        mMyLocationListView.setEmptyView(findViewById(R.id.emptyElement));

    }



    View.OnClickListener onRefreshClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.changeLocation){
                initLocationService(DetailActivty.this);
                new GetLocationTask().execute("");
            }
        }
    };
    @Override
    public void onLocationChanged(Location location)     {
        // do stuff here with location object
        mLocation = location;
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /**
     * Sets up location service after permissions is granted
     */
    @TargetApi(23)
    private void initLocationService(Context context) {


        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return  ;
        }

        try   {
            this.latitude = 0.0;
            this.longitude = 0.0;
            this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            // Get GPS and network status
            this.isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            this.isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (forceNetwork) isGPSEnabled = false;

            if (!isNetworkEnabled && !isGPSEnabled)    {
                // cannot get location
                this.locationServiceAvailable = false;
            }
            //else
            {
                this.locationServiceAvailable = true;

                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, DetailActivty.this);
                    if (locationManager != null)   {
                        mLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }//end if

                if (isGPSEnabled)  {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null)  {
                    }
                } else {
                    //turnGPSOn();
                }
            }
        } catch (Exception ex)  {
            Log.i( "sdfcdd","Error creating location service: " + ex.getMessage() );

        }
    }

    public void saveLovation(View view) {

        LayoutInflater li = LayoutInflater.from(DetailActivty.this);
        View promptsView = li.inflate(R.layout.save_location_dailog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                DetailActivty.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.edt_location_name);
        final LinearLayout save = (LinearLayout) promptsView
                .findViewById(R.id.ll_save_location);
        ((TextView)promptsView.findViewById(R.id.address)).setText(mAddress);
        ((TextView)promptsView.findViewById(R.id.city)).setText(mCity);
        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationOperation.open();
                LocationDTO locationDTO = new LocationDTO();
                locationDTO.setmLat(mLocation.getLatitude());
                locationDTO.setmLong(mLocation.getLongitude());
                locationDTO.setmLocationName(userInput.getText().toString());
                locationDTO.setmLocationImage(2);
                locationDTO.setAddress(mAddress+" "+mCity);
                locationOperation.addLocation(locationDTO);
                LocationAdapter adapter = new LocationAdapter(DetailActivty.this,locationOperation.getAllLocations());
                locationOperation.close();
                mMyLocationListView=(ListView)findViewById(R.id.my_location_list);
                mMyLocationListView.setAdapter(adapter);
                mMyLocationListView.setEmptyView(findViewById(R.id.emptyElement));
                alertDialog.dismiss();
                Toast.makeText(DetailActivty.this, "Location saved",Toast.LENGTH_SHORT).show();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }
        });
        // show it
        alertDialog.show();

    }
    /*View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.about:
                    startActivity(new Intent(DetailActivty.this,AboutUsActivity.class));
                    break;
                case R.id.rate:
                    //startActivity(new Intent(DetailActivty.this,MoreAppsActivity.class));
                    rateMyApp();
                    break;
                case R.id.more:
                    moreApp();
                    break;
                default:break;
            }
        }
    };
*/
    public void showMenu(View view) {
        Log.i("dsds","dsd");
        LayoutInflater li = LayoutInflater.from(DetailActivty.this);
        View promptsView = li.inflate(R.layout.menu_dailog, null);


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                DetailActivty.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);


        final LinearLayout about = (LinearLayout) promptsView
                .findViewById(R.id.about);
        final LinearLayout rate = (LinearLayout) promptsView
                .findViewById(R.id.rate);
        final LinearLayout more = (LinearLayout) promptsView
                .findViewById(R.id.more);


        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                startActivity(new Intent(DetailActivty.this,AboutUsActivity.class));
            }
        });
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                rateMyApp();
            }
        });
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                moreApp();
            }
        });
        // show it
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initLocationService(this);
                    new GetLocationTask().execute("");
                } else {
                    // Permission Denied
                    Toast.makeText(DetailActivty.this, "Get location permission denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    private class GetLocationTask extends AsyncTask<String, Void, String> {
        ProgressDialog pd = new ProgressDialog(DetailActivty.this);
        String address;
        String city;
        String state;
        @Override
        protected String doInBackground(String... params) {
            try {


                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(DetailActivty.this, Locale.getDefault());

                addresses = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                address = addresses.get(0).getAddressLine(0) + addresses.get(0).getAddressLine(1); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                 city = addresses.get(0).getLocality();
                 state = addresses.get(0).getAdminArea();

                mAddress = address;
                mCity = city+" - "+state;

                mAddress = "Pashan";
                mCity = "Pune, Maharashtra";
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();

            }catch (Exception e){
                e.printStackTrace();
            }
           return "";
        }

        @Override
        protected void onPostExecute(String result) {
            pd.dismiss();
            if(address!=null && city!=null) {
                ((TextView) findViewById(R.id.address)).setText(mAddress);
                ((TextView) findViewById(R.id.city)).setText(city + " - " + state);
            } else {
                ((TextView) findViewById(R.id.address)).setText("Cannot get location. Please try again");
            }
            if (mInterstitialAd.isLoaded()) {
                //mInterstitialAd.show();
            }
        }

        @Override
        protected void onPreExecute() {

            pd.setMessage("Getting you current location...");
            pd.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
    private void rateMyApp() {
        Uri uri = Uri.parse("market://details?id=" + DetailActivty.this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + DetailActivty.this.getPackageName())));
        }
    }
    private void moreApp() {
        //https://play.google.com/store/apps/developer?id=PC+Developers
        Uri uri = Uri.parse("https://play.google.com/store/apps/developer?id=PC+Developers");
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + DetailActivty.this.getPackageName())));
        }
    }
}

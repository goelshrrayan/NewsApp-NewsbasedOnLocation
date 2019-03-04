package com.example.android.socialcops;

import android.*;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;
import android.app.Application;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private ArrayList<RecyclerItem> mRecyclerList;
    private String latitude,longitude;
//Location
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;


    private RequestQueue mRequestQueue;
   public int l;

//Shrrayan Sheel Goel 04-03-2019

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OneSignal.startInit(this)
                .autoPromptLocation(true)
                .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .setNotificationReceivedHandler(new ExampleNotificationReceivedHandler())
                .init();

        OneSignal.sendTag("test1", "test1");//One Signal
        setContentView(R.layout.activity_main);




//**********************8Location

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1000); // 1 second, in milliseconds

        //**************************88Location



        mRecyclerView = (RecyclerView) findViewById(R.id.Recycler_View);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerList = new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(this);
        Log.i("lat + long",latitude+longitude);




        //SEARCHBAR
        EditText editText=(EditText) findViewById(R.id.edit_text);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
filter(s.toString());
            }
        });
    }


    private void filter(String text) {
        ArrayList<RecyclerItem> filteredList=new ArrayList<>();

        for(RecyclerItem item: mRecyclerList){
            if(item.getmDescription().toLowerCase().contains(text.toLowerCase())||item.getmTitle().toLowerCase().contains(text.toLowerCase()))
            {filteredList.add(item);
            }
        }
        mRecyclerViewAdapter.filteredlist(filteredList);


    }


    private void parseJSON(String city) {
        String Url = "https://newsapi.org/v2/everything?q="+city+"&from=2019-03-03&sortBy=publishedAt&apiKey=652973187c354813b1b08c45b022a935";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("articles");
                                            l=jsonArray.length();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject articles = jsonArray.getJSONObject(i);
                                String Image = articles.getString("urlToImage");
                                String Title = articles.getString("title");
                                String description = articles.getString("description");


                               mRecyclerList.add(new RecyclerItem(Image, Title, description));

                            }
                            mRecyclerViewAdapter = new RecyclerViewAdapter(MainActivity.this, mRecyclerList);
                            mRecyclerView.setAdapter(mRecyclerViewAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                    if (cacheEntry == null) {
                        cacheEntry = new Cache.Entry();
                    }
                    final long cacheHitButRefreshed = 30 * 1000; // in 30 seconds cache will be hit, but also refreshed on background
                    final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
                    long now = System.currentTimeMillis();
                    final long softExpire = now + cacheHitButRefreshed;
                    final long ttl = now + cacheExpired;
                    cacheEntry.data = response.data;
                    cacheEntry.softTtl = softExpire;
                    cacheEntry.ttl = ttl;
                    String headerValue;
                    headerValue = response.headers.get("Date");
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    headerValue = response.headers.get("Last-Modified");
                    if (headerValue != null) {
                        cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    cacheEntry.responseHeaders = response.headers;
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(new JSONObject(jsonString), cacheEntry);
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            protected void deliverResponse(JSONObject response) {
                super.deliverResponse(response);
            }

            @Override
            public void deliverError(VolleyError error) {
                super.deliverError(error);
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                return super.parseNetworkError(volleyError);
            }
        };

        mRequestQueue.add(request);


    }


    @Override
    protected void onResume() {
        super.onResume();
        //Now lets connect to the API
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(this.getClass().getSimpleName(), "onPause()");

        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
            mGoogleApiClient.disconnect();
        }


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,this);

        } else {
            //If everything went fine lets get latitude and longitude
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();

            latitude ="Latitude:- " +currentLatitude + "";


            longitude ="Longitude:- "+ currentLongitude + "";


Log.i("LAt And Long", latitude+" "+longitude);
            if(latitude!=null&longitude!=null) {
                String city="delhi";
                Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(currentLatitude, currentLongitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("city try",city);
                }
                try {
                    if (addresses.size() > 0) {
                        city = addresses.get(0).getPostalCode();
                        Log.i("city try",city);
                    }
                }
                catch (NullPointerException e)
                { city="delhi"; }

                Log.i("City catch",city);
                if(city.charAt(0)=='1') {
                    if (city.charAt(1) == '1')
                        city = "Delhi";
                    if (city.charAt(1) == '2')
                        city = "Haryana";
                    if (city.charAt(1) == '3')
                        city = "Haryana";
                    if (city.charAt(1) == '4' || city.charAt(1) == '5' || city.charAt(1) == '6')
                        city = "Punjab";
                    if (city.charAt(1) == '7')
                        city = "Himachal Pardesh";
                    if (city.charAt(1) == '8' || city.charAt(1) == '9')
                        city = "Jammu & Kashmir";
                }
                else if(city.charAt(0)=='2')
                    city = "Uttar Pardesh";
                else if(city.charAt(0)=='3')
                {  if (city.charAt(1) == '0' || city.charAt(1) == '1' || city.charAt(1) == '2'|| city.charAt(1) == '3'|| city.charAt(1) == '4')
                city="Rajasthan";
                else city="Gujrat";}

                else if(city.charAt(0)=='4')
                {if (city.charAt(1) == '0' || city.charAt(1) == '1' || city.charAt(1) == '2'|| city.charAt(1) == '3'|| city.charAt(1) == '4')
                city="Maharashtra";
                else
                city="Chattisgarh";}
                else if(city.charAt(0)=='5')
                {if (city.charAt(1) == '0' || city.charAt(1) == '1' || city.charAt(1) == '2'|| city.charAt(1) == '3')
                city="Andhra Pardesh";
                else city="Karnataka";}
                else if(city.charAt(0)=='6')
                { if (city.charAt(1) == '0' || city.charAt(1) == '1' || city.charAt(1) == '2'|| city.charAt(1) == '3'|| city.charAt(1) == '4')
                city="Tamil Nadu";
                else city="Kerela";}


                Log.i("City ",city);
                parseJSON(city);
                }


            }

        }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
      /*
             * Google Play services can resolve some errors it detects.
             * If the error has a resolution, try sending an Intent to
             * start a Google Play services activity that can resolve
             * error.
             */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                    /*
                     * Thrown if Google Play services canceled the original
                     * PendingIntent
                     */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
                /*
                 * If no resolution is available, display a dialog to the
                 * user with the error.
                 */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        if(latitude!=null&longitude!=null) {
            String city="delhi";
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(currentLatitude, currentLongitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("city try",city);

            }
            try {
                if (addresses.size() > 0) {
                    city = addresses.get(0).getCountryName();
                    Log.i("city try",city);
                }
            }
            catch (NullPointerException e)
            { city="delhi"; }

            if(city.charAt(0)=='1') {
                if (city.charAt(1) == '1')
                    city = "Delhi";
                if (city.charAt(1) == '2')
                    city = "Haryana";
                if (city.charAt(1) == '3')
                    city = "Haryana";
                if (city.charAt(1) == '4' || city.charAt(1) == '5' || city.charAt(1) == '6')
                    city = "Punjab";
                if (city.charAt(1) == '7')
                    city = "Himachal Pardesh";
                if (city.charAt(1) == '8' || city.charAt(1) == '9')
                    city = "Jammu & Kashmir";
            }
            else if(city.charAt(0)=='2')
                city = "Uttar Pardesh";
            else if(city.charAt(0)=='3')
            {  if (city.charAt(1) == '0' || city.charAt(1) == '1' || city.charAt(1) == '2'|| city.charAt(1) == '3'|| city.charAt(1) == '4')
                city="Rajasthan";
            else city="Gujrat";}

            else if(city.charAt(0)=='4')
            {if (city.charAt(1) == '0' || city.charAt(1) == '1' || city.charAt(1) == '2'|| city.charAt(1) == '3'|| city.charAt(1) == '4')
                city="Maharashtra";
            else
                city="Chattisgarh";}
            else if(city.charAt(0)=='5')
            {if (city.charAt(1) == '0' || city.charAt(1) == '1' || city.charAt(1) == '2'|| city.charAt(1) == '3')
                city="Andhra Pardesh";
            else city="Karnataka";}
            else if(city.charAt(0)=='6')
            { if (city.charAt(1) == '0' || city.charAt(1) == '1' || city.charAt(1) == '2'|| city.charAt(1) == '3'|| city.charAt(1) == '4')
                city="Tamil Nadu";
            else city="Kerela";}



            Log.i("City catch",city);
            parseJSON(city);
        }

    }


    private class ExampleNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
        /**
         * Callback to implement in your app to handle when a notification is received while your app running
         *  in the foreground or background.
         *
         *  Use a NotificationExtenderService instead to receive an event even when your app is closed (not 'forced stopped')
         *     or to override notification properties.
         *
         * @param notification Contains information about the notification received.
         */
        @Override
        public void notificationReceived(OSNotification notification) {
            Log.w("OneSignalExample", "notificationReceived!!!!!!");
            DebuggingHelper d=new DebuggingHelper();
            d.printObject(notification);
            d.printObject(notification.payload);
        }
    }

    private class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        /**
         * Callback to implement in your app to handle when a notification is opened from the Android status bar or in app alert
         *
         * @param openedResult Contains information about the notification opened and the action taken on it.
         */
        @Override
        public void notificationOpened(OSNotificationOpenResult openedResult) {
            Log.w("OneSignalExample", "notificationOpened!!!!!!");
           DebuggingHelper d=new DebuggingHelper();
           d.printObject(openedResult.action);
            d.printObject(openedResult.notification);

        }
    }

    public class DebuggingHelper {

         void printObject(Object obj) {
            for (Field field : obj.getClass().getDeclaredFields()) {
                String name = field.getName();
                try {
                    Object value = field.get(obj);
                    System.out.printf("Field name: %s, Field value: %s%n", name, value);
                } catch (Throwable t){}
            }
        }
    }


}

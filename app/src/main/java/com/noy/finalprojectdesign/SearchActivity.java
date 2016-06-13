package com.noy.finalprojectdesign;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.noy.finalprojectdesign.Model.Checkin;
import com.noy.finalprojectdesign.Model.Model;
import com.noy.finalprojectdesign.Receivers.AlarmReceiver;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class SearchActivity extends Activity {

    static final int PLACE_PICKER_RESULT = 1;
    private final static String RTL_CHAR = "\u200E";
    private static final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 0;

    EditText location;
    static TimeEditText tet;
    DateEditText det;
    LatLng locLat;
    Button searchBtn;
    ProgressBar progressBar;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(this);
        Model.getInstance().init(this);
        AlarmReceiver.getInstance().init(getApplicationContext());

        setContentView(R.layout.activity_search);

        location = (EditText) findViewById(R.id.searchLocation);
        tet = (TimeEditText) findViewById(R.id.searchTime);
        det = (DateEditText) findViewById(R.id.searchDate);
        searchBtn = (Button) findViewById(R.id.searchBtn);
        progressBar = (ProgressBar) findViewById(R.id.activity_indicator);
        progressBar.setVisibility(View.GONE);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        String locationProvider = LocationManager.NETWORK_PROVIDER;
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
//                        int o = 1;
//                        return;
            ActivityCompat.requestPermissions(SearchActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSION_ACCESS_COURSE_LOCATION);
        } else {
            Location lastlocation = locationManager.getLastKnownLocation(locationProvider);

            if (lastlocation != null) {
                locLat = new LatLng(lastlocation.getLatitude(), lastlocation.getLongitude());
            } else {
                locLat = new LatLng(34.819934, 32.088674);
            }
        }

//        location.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(hasFocus){
//                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//
//                    try {
//                        startActivityForResult(builder.build(SearchActivity.this), PLACE_PICKER_RESULT);
//                    } catch (GooglePlayServicesRepairableException e) {
//                        e.printStackTrace();
//                    } catch (GooglePlayServicesNotAvailableException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });

       /* Model.getInstance().getAllLocalCheckinsAsync(new Model.GetCheckinsListener() {
            @Override
            public void onResult(List<Checkin> checkins) {
                int i = 0;
            }
        });
*/
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(SearchActivity.this), PLACE_PICKER_RESULT);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*int userId = data.findPath("USER_ID").asInt();
                Long time = data.findPath("TIME").asLong();
                String lng = data.findPath("LNG").asText();
                String lat = data.findPath("LAT").asText();
                JsonNode types = data.findPath("TYPES");*/

                progressBar.setVisibility(View.VISIBLE);

                String time[] = SearchActivity.tet.getText().toString().split(":");
                String date[] = det.getText().toString().split("/");
                Calendar calendar = Calendar.getInstance();
                calendar.set(new Integer(date[0]), new Integer(date[1]), new Integer(date[2]), new Integer(time[0]), new Integer(time[1]));

                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    try {
                        String data = new GetDataFromServer(calendar, locLat, progressBar).execute().get();
                        Intent intent = new Intent(SearchActivity.this, suggestionsList.class);
                        intent.putExtra("suggestions", data);
                        startActivity(intent);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "There is no internet connection", Toast.LENGTH_LONG);
                }

                progressBar.setVisibility(View.GONE);
/*                Intent intent = new Intent(SearchActivity.this, suggestionsList.class);
                startActivity(intent);*/
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (PLACE_PICKER_RESULT):
                if (resultCode == Activity.RESULT_OK) {
                    com.google.android.gms.location.places.Place place = PlacePicker.getPlace(data, this);
                    location.setText(RTL_CHAR + place.getName());
                    locLat = place.getLatLng();
                }
                break;
        }
    }

    private class GetDataFromServer extends AsyncTask<String, Void, String> {

        protected  Calendar cal;
        protected  LatLng  latLng;
        protected  ProgressBar progressBar;

        public GetDataFromServer(Calendar cal, LatLng latLng, ProgressBar progressBar) {
            this.cal = cal;
            this.latLng = latLng;
            this.progressBar = progressBar;
        }

        @Override
        protected String doInBackground(String... urls) {
            Log.d("SEARCH_START", "start");
            //android.os.Debug.waitForDebugger();

            URL url;
            HttpURLConnection urlConnection = null;
            StringBuilder jsonResults = new StringBuilder();
            String result = "no results:(";
            try {

                JSONObject data = new JSONObject();
                data.put("USER_ID", 1);
                data.put("TIME", cal.getTimeInMillis());
                data.put("LNG", latLng.longitude);
                data.put("LAT", latLng.latitude);
                data.put("TYPES", prepareDataToServer(cal));

                url = new URL("http://checkmatep-sikole.rhcloud.com/Recommandations");
                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Charset", "UTF-8");
                urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
                String ds = data.toString();
                wr.write(ds);

                wr.flush();
                wr.close();

                int i = urlConnection.getResponseCode();
                InputStream in;

                if (i >= 400) {
                    in = urlConnection.getErrorStream();
                } else {
                    in = urlConnection.getInputStream();
                }

                BufferedReader input = new BufferedReader(
                        new InputStreamReader(in, "UTF-8"));
                String str;
                while (null != (str = input.readLine())) {
                    jsonResults.append(str).append("\r\n");
                }
                input.close();

                /*InputStreamReader isw = new InputStreamReader(in);

                int d = isw.read();
                char[] buff = new char[1024];
                //TODO ArrayIndexOutOfBoundsException
                while (d != -1) {
                    jsonResults.append(buff, 0, d);
                    d = isw.read();
                }*/

                Log.d("SEARCH_SERVER", jsonResults.toString());

                result = jsonResults.toString();
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return result;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("SEARCH_RESULTS", result);
        }

        public JSONArray prepareDataToServer(Calendar cal) {

            Utils.TimePart currentTimePart = Utils.TimePart.getTimePart(cal);
            Utils.TimePart nextTimePart = currentTimePart.getNext();
            Utils.TimePart prevTimePart = currentTimePart.getPrevious();

            List<Integer> times = new ArrayList<Integer>();
            times.add(currentTimePart.ordinal());
            times.add(nextTimePart.ordinal());
            times.add(prevTimePart.ordinal());
            List<Checkin> checkins = Model.getInstance().getLocalCheckins(times);

            HashMap<String, Integer> checkinsMap = new HashMap<String, Integer>();
            for (Checkin checkin : checkins) {
                if (checkinsMap.containsKey(checkin.getType())) {
                    checkinsMap.put(checkin.getType(), checkin.getCount() + checkinsMap.get(checkin.getType()));
                } else {
                    checkinsMap.put(checkin.getType(), checkin.getCount());
                }
            }

            JSONArray jarray = new JSONArray();
            try {
                Iterator it = checkinsMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    JSONObject jobj = new JSONObject();

                    jobj.put("type", pair.getKey());
                    jobj.put("count", pair.getValue());

                    jarray.put(jobj);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressBar.setVisibility(View.GONE);

            return jarray;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_COURSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    String locationProvider = LocationManager.NETWORK_PROVIDER;

                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.
                        return;
                    }
                    Location lastlocation = locationManager.getLastKnownLocation(locationProvider);
                    locLat = new LatLng(lastlocation.getLatitude(), lastlocation.getLongitude());


                } else {
                    //TODO talk about it
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}

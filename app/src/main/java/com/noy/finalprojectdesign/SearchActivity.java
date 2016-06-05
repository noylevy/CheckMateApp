package com.noy.finalprojectdesign;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.noy.finalprojectdesign.Model.Checkin;
import com.noy.finalprojectdesign.Model.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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


public class SearchActivity extends Activity {

    static final int PLACE_PICKER_RESULT = 1;
    private final static String RTL_CHAR = "\u200E";

    EditText location;
    static TimeEditText tet;
    DateEditText det;
    LatLng locLat;
    Button searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        location = (EditText) findViewById(R.id.searchLocation);
        tet = (TimeEditText) findViewById(R.id.searchTime);
        det = (DateEditText) findViewById(R.id.searchDate);
        searchBtn = (Button) findViewById(R.id.searchBtn);


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

                String time[] = SearchActivity.tet.getText().toString().split(":");
                String date[] = det.getText().toString().split("/");
                Calendar calendar = Calendar.getInstance();
                calendar.set(new Integer(date[0]), new Integer(date[1]), new Integer(date[2]), new Integer(time[0]), new Integer(time[1]));

                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    new GetDataFromServer(calendar, locLat).execute();
                } else {
                    Log.d("SEARCH_SERVER", "no connection");
                }

                Intent intent = new Intent(SearchActivity.this, suggestionsList.class);
                startActivity(intent);
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
        protected  LatLng latLng;

        public GetDataFromServer(Calendar cal, LatLng latLng){
            this.cal = cal;
            this.latLng = latLng;
        }

        @Override
        protected String doInBackground(String... urls) {
            Log.d("SEARCH_START", "start");
            android.os.Debug.waitForDebugger();

            URL url;
            HttpURLConnection urlConnection = null;
            StringBuilder jsonResults = new StringBuilder();

            try {

                JSONObject data = new JSONObject();
                data.put("USER_ID", 1);
                data.put("TIME", cal.getTimeInMillis());
                data.put("LNG", latLng.longitude);
                data.put("LAT", latLng.latitude);
                data.put("TYPES", prepareDataToServer(cal));

                //url = new URL("http://localhost:8181/CheckMateServer/recommendation");
                //url = new URL("http://localhost:9000/Recommandations/1/123456/34.819934/32.088674/''");
                url = new URL("http://localhost:9000/Recommandations/1/123456/34.819934/32.088674");
                //TODO can open connection - toast and return to search.
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());


                /*JSONArray checkIns = new JSONArray();
                JSONObject checkIn = new JSONObject();
                checkIn.put("type", "cafe");
                checkIn.put("count", 7);
                checkIns.put(checkIn);*/

//                JSONObject data = new JSONObject();
//                data.put("USER_ID", 1);
//                data.put("TIME", cal.getTimeInMillis());
//                data.put("LNG", latLng.longitude);
//                data.put("LAT", latLng.latitude);
//                data.put("TYPES", prepareDataToServer(cal));

                wr.write(data.toString());
                //urlConnection.setRequestProperty("Connection", "keep-alive");

                int status = urlConnection.getResponseCode();

                InputStream in = urlConnection.getInputStream();
                InputStreamReader isw = new InputStreamReader(in);

                int d = isw.read();
                char[] buff = new char[1024];
                //TODO ArrayIndexOutOfBoundsException
                while (d != -1) {
                    jsonResults.append(buff, 0, d);
                    d = isw.read();
                }

             /*   wr.flush();
                wr.close();
*/
                Log.d("SEARCH_SERVER", jsonResults.toString());

                return jsonResults.toString();
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return "no results:(";
        }



        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("SEARCH_RESULTS", result);
        }

        public JSONArray prepareDataToServer(Calendar cal){

            Utils.TimePart currentTimePart =  Utils.TimePart.getTimePart(cal);
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

            return jarray;
        }
    }
}

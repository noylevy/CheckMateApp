package com.noy.finalprojectdesign;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.*;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.Calendar;


public class SearchActivity extends Activity {

    static final int PLACE_PICKER_RESULT = 1;
    private final static String RTL_CHAR = "\u200E";

    EditText location;
    TimeEditText tet;
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
                String time[] =tet.getText().toString().split(":");
                String date[] =det.getText().toString().split("/");
                Calendar calendar = Calendar.getInstance();
                calendar.set(new Integer(date[0]),new Integer(date[1]),new Integer(date[2]),new Integer(time[0]),new Integer( time[1]));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet get = new HttpGet(
                            "http://X.X.X.X:8080/HearIt/services/AuthMySQL");
                    get.setHeader("content-type", "application/json");

                    JSONObject data = new JSONObject();
                    data.put("calander", calendar.getTimeInMillis());
                    data.put("location", locLat);

                    StringEntity entity = new StringEntity(data.toString());
                    //get.setEntity(entity);
                    HttpResponse resp = httpClient.execute(get);
                    //return EntityUtils.toString(resp.getEntity());

                } catch (Exception E) {
                    E.printStackTrace();
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
}

package com.noy.finalprojectdesign;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class suggestionsList extends Activity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public static List<Integer> unlikeList;
    public static List<Integer> likeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions_list);
        mRecyclerView = (RecyclerView) findViewById(R.id.suggstionsList);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        try {
        // specify an adapter (see also next example)
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String suggestions = extras.getString("suggestions");
            JSONArray suggestionsArray = new JSONArray(suggestions);
            mAdapter = new SuggestionAdapter(suggestionsArray);
        }
        } catch (JSONException e) {
            //TODO: add "no data found....."
            e.printStackTrace();
        }

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback = new SuggestionTouchHelper((SuggestionAdapter) mAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecyclerView);

        unlikeList = new LinkedList<Integer>();
        likeList = new LinkedList<Integer>();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new SendActionsToServer(unlikeList, likeList).execute();
        } else {
            Log.d("SEARCH_SERVER", "no connection");
        }

    }

    private class SendActionsToServer extends AsyncTask<String, Void, String> {

        protected List<Integer> unlikeList;
        protected List<Integer> likeList;

        public SendActionsToServer(List<Integer> unlikeList, List<Integer> likeList){
            this.unlikeList = unlikeList;
            this.likeList = likeList;
        }

        @Override
        protected String doInBackground(String... urls) {
            android.os.Debug.waitForDebugger();

            URL url;
            HttpURLConnection urlConnection = null;
            StringBuilder jsonResults = new StringBuilder();

            try {
                //TODO change url?!
                url = new URL("http://localhost:9000/Recommandations/1/123456/34.819934/32.088674");
                //TODO can open connection - toast and return to search.
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());

                JSONArray userActions = new JSONArray();
                for (int id: unlikeList) {
                    JSONObject curr = new JSONObject();
                    curr.put("action", -1);
                    curr.put("id", id);
                    userActions.put(curr);
                }

                for (int id: likeList) {
                    JSONObject curr = new JSONObject();
                    curr.put("action", 1);
                    curr.put("id", id);
                    userActions.put(curr);
                }

                unlikeList.clear();
                likeList.clear();

                wr.write(userActions.toString());
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
                Log.d("USER_ACTIONS_SERVER", jsonResults.toString());

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
            Log.d("USER_ACTIONS_RESULT", result);
        }
    }
}

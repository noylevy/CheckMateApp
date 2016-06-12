package com.noy.finalprojectdesign;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.noy.finalprojectdesign.Model.EmotionsPlace;
import com.noy.finalprojectdesign.Model.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class suggestionsList extends Activity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public static List<EmotionsPlace> dislikeList;
    public static List<EmotionsPlace> likeList;

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

        dislikeList = new LinkedList<EmotionsPlace>();
        likeList = new LinkedList<EmotionsPlace>();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new SendActionsToServer(dislikeList, likeList).execute();
        } else {
            Log.d("SEARCH_SERVER", "no connection");
        }
    }

    private class SendActionsToServer extends AsyncTask<String, Void, String> {

        protected List<EmotionsPlace> dislikeList;
        protected List<EmotionsPlace> likeList;

        public SendActionsToServer(List<EmotionsPlace> unlikeList, List<EmotionsPlace> likeList){
            this.dislikeList = unlikeList;
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
                url = new URL("http://checkmatep-sikole.rhcloud.com/Emotions");
                //url = new URL("http://localhost:9000/Emotions");
                //TODO can open connection - toast and return to search.
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());

                JSONArray userActions = new JSONArray();
                for (EmotionsPlace place: dislikeList) {
                    JSONObject curr = new JSONObject();
                    curr.put("ACTION", "0");
                    curr.put("PLACE_ID", place.getPlaceId());
                    curr.put("GOOG_TYPE", place.getGoogleType());
                    userActions.put(curr);
                }

                for (EmotionsPlace place: likeList) {
                    JSONObject curr = new JSONObject();
                    curr.put("ACTION", "1");
                    curr.put("PLACE_ID", place.getPlaceId());
                    curr.put("GOOG_TYPE", place.getGoogleType());
                    userActions.put(curr);
                }

                dislikeList.clear();
                likeList.clear();

                JSONObject obj = new JSONObject();
                //TODO: get real user id
                obj.put("USER_ID", 1);
                obj.put("EMOTIONS", userActions);
                wr.write(obj.toString());
                wr.flush();
                wr.close();

                int status = urlConnection.getResponseCode();

                //TODO: get data resposne...
                if (status == 200) {
                    Log.d("USER_ACTIONS_SERVER", jsonResults.toString());

                    return "true";
                }
                else{
                    return "false";
                }
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

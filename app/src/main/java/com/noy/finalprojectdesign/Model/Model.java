package com.noy.finalprojectdesign.Model;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInstaller;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.noy.finalprojectdesign.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


/**
 * Created by Anna on 05-Mar-16.
 */
public class Model {
    protected static final String TAG = "Model";
    private final static Model instance = new Model();
    private Context context = null;
    private SharedPreferences sharedPrefs;
    private final static String PREF_FILE = "PREF_FILE";
    private final static String LAST_SYNC_TIME = "LAST_SYNC_TIME";
    private final static String USER_ID = "USER_ID";
    ModelSql local = new ModelSql();

    private Model() {
    }

    public static Model getInstance() {
        return instance;
    }

    public void init(Context context) {
        if (this.context == null) {
            this.context = context;
            sharedPrefs = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
            local.init(context);
        }
    }

    public interface GetCheckinsListener {
        public void onResult(List<Checkin> checkins);
    }

    public interface GetCheckinListener {
        public void onResult(Checkin checkin);
    }

    public interface GetCheckinListnListener {
        public void onResult(List<Checkin> checkin);
    }

    public interface SimpleSuccessListener {
        public void onResult(boolean result);
    }

    // Local database

    public Checkin getLocalCheckin(String type, Utils.TimePart time) {
        return local.getCheckinByTypeAndTime(type, time.ordinal());
    }

    public long addLocalCheckin(Checkin checkin) {
        return local.addOrUpdateCheckin(checkin);
    }

    public int deleteLocalCheckin(String type, int time) {
        return local.deleteCheckin(type, time);
    }

    public List<Checkin> getLocalCheckins(List<Integer> time){
        return local.getCheckinByTime(time);
    }

/*
    public void getLocalCheckinsAsync(final GetCheckinListnListener listener, final List<Integer> time) {
        class GetCheckinAsyncTask extends AsyncTask<String, String, List<Checkin>> {
            @Override
            protected List<Checkin> doInBackground(String... params) {
                return local.getCheckinByTime(time);
            }

            @Override
            protected void onPostExecute(List<Checkin> checkin) {
                super.onPostExecute(checkin);
                listener.onResult(checkin);
            }
        }

        GetCheckinAsyncTask task = new GetCheckinAsyncTask();
        task.execute();
    }
*/


    public void getLocalCheckinsAsync(final GetCheckinListnListener listener, final String type, final List<Integer> time) {
        class GetCheckinAsyncTask extends AsyncTask<String, String, List<Checkin>> {
            @Override
            protected List<Checkin> doInBackground(String... params) {
                return local.getCheckinByTypeAndTime(type,time);
            }

            @Override
            protected void onPostExecute(List<Checkin> checkin) {
                super.onPostExecute(checkin);
                listener.onResult(checkin);
            }
        }

        GetCheckinAsyncTask task = new GetCheckinAsyncTask();
        task.execute();
    }


    public void getLocalCheckinsAsync(final GetCheckinListener listener, final String type, final int time) {
        class GetCheckinAsyncTask extends AsyncTask<String, String, Checkin> {
            @Override
            protected Checkin doInBackground(String... params) {
                return local.getCheckinByTypeAndTime(type,time);
            }

            @Override
            protected void onPostExecute(Checkin checkin) {
                super.onPostExecute(checkin);
                listener.onResult(checkin);
            }
        }

        GetCheckinAsyncTask task = new GetCheckinAsyncTask();
        task.execute();
    }

    public void addOrUpdateLocalCheckinAsync(final Checkin checkin) {

        addOrUpdateLocalCheckinAsync(new SimpleSuccessListener() {
            @Override
            public void onResult(boolean result) {

            }
        }, checkin);    }

    public void addOrUpdateLocalCheckinAsync(final SimpleSuccessListener listener, final Checkin checkin) {
        class AddCheckinAsyncTask extends AsyncTask<String, String, Long> {
            @Override
            protected Long doInBackground(String... params) {
                return local.addOrUpdateCheckin(checkin);
            }

            @Override
            protected void onPostExecute(Long id) {
                super.onPostExecute(id);
                listener.onResult(id > -1);
            }
        }

        AddCheckinAsyncTask task = new AddCheckinAsyncTask();
        task.execute();
    }

    public void deleteLocalCheckinAsync(final SimpleSuccessListener listener,final String type,final int time) {
        class DeleteCheckinAsyncTask extends AsyncTask<String, String, Integer> {
            @Override
            protected Integer doInBackground(String... params) {
                return local.deleteCheckin(type, time);
            }

            @Override
            protected void onPostExecute(Integer id) {
                super.onPostExecute(id);
                listener.onResult(id > -1);
            }
        }

        DeleteCheckinAsyncTask task = new DeleteCheckinAsyncTask();
        task.execute();
    }

    public void getAllLocalCheckinsAsync(final GetCheckinsListener listener) {
        class GetCheckinsAsyncTask extends AsyncTask<String, String, List<Checkin>> {
            @Override
            protected List<Checkin> doInBackground(String... params) {
                return local.getAllCheckins();
            }

            @Override
            protected void onPostExecute(List<Checkin> checkins) {
                super.onPostExecute(checkins);
                listener.onResult(checkins);
            }
        }

        GetCheckinsAsyncTask task = new GetCheckinsAsyncTask();
        task.execute();
    }

    public void saveCheckinsOnServer(final SimpleSuccessListener listener, final JSONArray checkins) {
        class SaveCheckinsAsyncTask extends AsyncTask<String, String, Long> {
            @Override
            protected Long doInBackground(String... params) {
                HttpURLConnection urlConnection = null;
                URL url;
                Long result = new Long(-1);
                try {
                    JSONObject data = new JSONObject();
                    data.put("USER_ID", Model.getInstance().getUserId());
                    data.put("CHECKINS", checkins);

                    url = new URL("http://checkmatep-sikole.rhcloud.com/Checkins");
                    //url = new URL("http://192.168.1.17:9000/Checkins");
                    //url = new URL("http://localhost:9000/Checkins");
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

                    result = new Long(urlConnection.getResponseCode());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return result;
            }

            @Override
            protected void onPostExecute(Long id) {
                super.onPostExecute(id);
                listener.onResult(id > -1);
            }
        }

        SaveCheckinsAsyncTask task = new SaveCheckinsAsyncTask();
        task.execute();
    }

    public void saveUserOnServer(final SimpleSuccessListener listener, final String userId, final String token,final String name, final String gender, final String birthday) {
        class SaveUserAsyncTask extends AsyncTask<String, String, Long> {
            @Override
            protected Long doInBackground(String... params) {
                HttpURLConnection urlConnection = null;
                URL url;
                Long result = new Long(-1);
                try {
                    JSONObject data = new JSONObject();
                    data.put("USER_ID", userId);
                    data.put("NAME", name);
                    data.put("GENDER", gender);
                    data.put("BIRTH_DATE", birthday);
                    data.put("TOKEN", token);

                    url = new URL("http://checkmatep-sikole.rhcloud.com/Login");
                    //url = new URL("http://192.168.1.17:9000/Login");
                    //url = new URL("http://localhost:9000/Login");
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

                    result = new Long(urlConnection.getResponseCode());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return result;
            }

            @Override
            protected void onPostExecute(Long id) {
                super.onPostExecute(id);
                listener.onResult(id > -1);
            }
        }

        SaveUserAsyncTask task = new SaveUserAsyncTask();
        task.execute();
    }

    public void getCheckinsFromFacebook() {
        if(AccessToken.getCurrentAccessToken() != null) {
            //"me/tagged_places?fields=created_time,place{name,category_list,location}"

            final String lastSync = getLastSyncTime();
            setLastSyncTime(Utils.getCurrentTimestamp());

            Bundle parameters = new Bundle();
            parameters.putString("fields", "created_time.order(reverse_chronological)," +
                    "place{name,category_list,location}");

            GraphRequest gr = new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "me/tagged_places",
                    parameters,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            try {
                                JSONObject data = response.getJSONObject();
                                JSONArray checkInsJson = data.getJSONArray("data");
                                Calendar lastSyncCal = Utils.parseTimestampToCal(lastSync);

                                saveCheckinsOnServer(new SimpleSuccessListener() {
                                    @Override
                                    public void onResult(boolean result) {
                                    }
                                },checkInsJson);

                                boolean newCheckins = true;
                                //TODO: check if new with the paging......
                                for (int i = 0; i < checkInsJson.length() && newCheckins; i++) {
                                    JSONObject obj = checkInsJson.getJSONObject(i);
//                                    Log.d(TAG, "getCheckinsFromFacebook " + obj.get("created_time"));
                                    newCheckins = proccessCheckIn(obj, lastSyncCal);
                                }

                                if(newCheckins) {
                                    GraphRequest nextRequest = response.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);
                                    if (nextRequest != null) {
                                        nextRequest.setCallback(this);
                                        nextRequest.executeAsync();
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
            gr.executeAsync();
        }
    }

    // Shared Preferences
    public String getLastSyncTime() {
        return sharedPrefs.getString(LAST_SYNC_TIME, null);
    }
    public void setLastSyncTime(String timestamp) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(LAST_SYNC_TIME, timestamp);
        editor.commit();
    }

    public String getUserId() {
        return sharedPrefs.getString(USER_ID, null);
    }
    public void setUserId(String userId) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(USER_ID, userId);
        editor.commit();
    }

    private boolean proccessCheckIn(JSONObject joCheckIn, Calendar lastSyncCal) throws JSONException {
        JSONObject place =  joCheckIn.getJSONObject("place");
        JSONArray categoryList =  place.getJSONArray("category_list");

        JSONObject category;
        Checkin checkin;
        String categoryType;
        String createdTime;
        Utils.TimePart tp;
        Calendar createdTimeCal;
        createdTime = joCheckIn.getString("created_time");
        try {
            createdTimeCal = Utils.parseCreatedTimeToCal(createdTime);
            if(lastSyncCal!= null && createdTimeCal.before(lastSyncCal)){
                return false;
            }
            tp = Utils.TimePart.getTimePart(createdTimeCal);
            for (int i = 0; i < categoryList.length(); i++) {
                category = categoryList.getJSONObject(i);
                categoryType = category.getString("name");
                checkin = getLocalCheckin(categoryType, tp);
                if (checkin != null) {
                    checkin.increaseCount();
                } else {
                    checkin = new Checkin(categoryType, tp);
                }
                addOrUpdateLocalCheckinAsync(checkin);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
}

package com.noy.finalprojectdesign;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.noy.finalprojectdesign.Model.EmotionsPlace;
import com.noy.finalprojectdesign.Model.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by noy on 22/05/2016.
 */
public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.ViewHolder> {

    List<Place> lPlaces = new ArrayList<Place>();

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView id,name,address,phone,type;
        public int chosenGoogleType;
        public ListView hours;
        public ToggleButton like;
        public ImageView image;
        LinearLayout detailsLayout;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);

            id = (TextView) v.findViewById(R.id.placeId);
            name = (TextView) v.findViewById(R.id.placeName);
            address = (TextView) v.findViewById(R.id.placeAddress);
            phone = (TextView) v.findViewById(R.id.placePhone);
            type = (TextView) v.findViewById(R.id.placeTypeName);
            hours = (ListView) v.findViewById(R.id.placeHours);
            image = (ImageView) v.findViewById(R.id.placeImage);
            like = (ToggleButton) v.findViewById(R.id.placeLike);
            detailsLayout = (LinearLayout) v.findViewById(R.id.placeDetailsLinear);

            like.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                    // Place is liked
                    EmotionsPlace place = new EmotionsPlace(chosenGoogleType, id.getText().toString());
                    if (isChecked) {
                        suggestionsList.likeList.add(place);
                    } else {
                        suggestionsList.likeList.remove(place);
                    }
                }
            });
        }
        private int mOriginalHeight = 0;
        private boolean mIsViewExpanded = false;
        @Override
        public void onClick(final View v) {
            if (mOriginalHeight == 0) {
                mOriginalHeight = v.getHeight();
            }
            ValueAnimator valueAnimator;
            int sizeToExpand = (int) (mOriginalHeight * 1);
            if (hours.getAdapter().getCount() > 0) {
                sizeToExpand = (int)( mOriginalHeight * (hours.getAdapter().getCount() * 0.23));
            }

            if (!mIsViewExpanded) {
                mIsViewExpanded = true;

                valueAnimator = ValueAnimator.ofInt(mOriginalHeight, mOriginalHeight +sizeToExpand );
                detailsLayout.setVisibility(View.VISIBLE);
            } else {
                mIsViewExpanded = false;
                valueAnimator = ValueAnimator.ofInt(mOriginalHeight + sizeToExpand, mOriginalHeight);
                detailsLayout.setVisibility(View.GONE);
            }
            valueAnimator.setDuration(300);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    Integer value = (Integer) animation.getAnimatedValue();
                    v.getLayoutParams().height = value.intValue();
                    v.requestLayout();
                }
            });

            valueAnimator.start();
        }
    }

    public SuggestionAdapter(JSONArray places){
/*

        lPlaces.add(new Place("1", "קפה גידי","דרך בן גוריון 108, רמת גן", "0525888888",
                new String[]{"M: 8:00 – 6:00","T: 8:00 – 6:00",
                             "W: 8:00 – 6:00","Th: 8:00 – 6:00",
                             "F: 8:00 – 6:00","St: Closed","S: Closed"},
                1,"Cafe","CnRtAAAATLZNl354RwP_9UKbQ_5Psy40texXePv4oAlgP4qNEkdIrkyse7rPXYGd9D_Uj1rVsQdWT4oRz4QrYAJNpFX7rzqqMlZw2h2E2y5IKMUZ7ouD_SlcHxYq1yL4KbKUv3qtWgTK0A6QbGh87GB3sscrHRIQiG2RrmU_jF4tENr9wGS_YxoUSSDrYjWmrNfeEHSGSc3FyhNLlBU"));
        lPlaces.add(new Place("2", "קפה זליק","איפשהו בגבעתיים", "03 - 58962148", null,1,"",""));
        lPlaces.add(new Place("3", "מקס ברנר","הרבה סניפים", "111", null,1,"",""));

*/

        JSONObject jPlace;
        JSONArray openHoursTextJson;
        String[] openHoursText;
        Place place;
        for (int i = 0; i < places.length(); i++){
            try {
                jPlace = (JSONObject) places.get(i);
                String placeId = jPlace.getString("placeId");
                String name = jPlace.getString("name");
                String address = jPlace.getString("address");
                String phoneNumber = jPlace.getString("phoneNumber");
                openHoursTextJson = jPlace.getJSONArray("openHoursText");
                int length = openHoursTextJson.length();
                openHoursText = new String[length];
                if (length > 0) {
                    String fullHourText ;
                    for (int j = 0; j < length; j++) {
                        fullHourText = openHoursTextJson.getString(j);
                        if (fullHourText.contains("יום ראשון")) {
                            fullHourText = fullHourText.replace("יום ראשון","א");
                        } else if (fullHourText.contains("יום שני")) {
                            fullHourText = fullHourText.replace("יום שני", "ב");
                        } else if (fullHourText.contains("יום שלישי")) {
                            fullHourText = fullHourText.replace("יום שלישי", "ג");
                        } else if (fullHourText.contains("יום רביעי")) {
                            fullHourText = fullHourText.replace("יום רביעי", "ד");
                        } else if (fullHourText.contains("יום חמישי")) {
                            fullHourText = fullHourText.replace("יום חמישי", "ה");
                        } else if (fullHourText.contains("יום שישי")) {
                            fullHourText = fullHourText.replace("יום שישי", "ו");
                        } else if (fullHourText.contains("יום שבת")) {
                            fullHourText = fullHourText.replace("יום שבת", "ש");
                        }
                        openHoursText[j] = fullHourText;
                    }
                }

                JSONObject jo = jPlace.getJSONObject("chosenType");
                String chosenType = jo.getString("name");
                int chosenTypeId = jo.getInt("id");
                String photo = jPlace.getString("photo");
                place = new Place(placeId, name, address,
                            phoneNumber,  openHoursText,
                            chosenTypeId,  chosenType,  photo);

                this.lPlaces.add(place);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.suggestion_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Place place = lPlaces.get(position);
        holder.id.setText(place.getPlaceId());
        holder.name.setText(place.getName());
        holder.address.setText(place.getAddress());
        holder.phone.setText(place.getPhoneNumber());
        holder.type.setText(place.getChosenType());
        holder.chosenGoogleType = place.getChosenTypeId();

        String[] hours = new String[]{};
        if (place.getOpenHours() != null) {
            hours = place.getOpenHours();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(holder.itemView.getContext(),
                R.layout.hour_item,hours );
        holder.hours.setAdapter(adapter);

        if (place.getPhoto() != null && place.getPhoto() != "") {
            new DownloadImageTask(holder.image).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,place.getPhoto());
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected void onPreExecute() {
            bmImage.setImageResource(R.drawable.wait);
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=200&key=AIzaSyB9y8JYZK-7D2Cag6oFEK5km8DOg5SAn6w&photoreference=" + urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", "image download error");
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (result == null) {
                bmImage.setImageResource(R.drawable.place);
            } else {
                int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, bmImage.getContext().getResources().getDisplayMetrics());
                int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, bmImage.getContext().getResources().getDisplayMetrics());
                bmImage.getLayoutParams().height = height;
                bmImage.getLayoutParams().width = width;
                bmImage.setImageBitmap(result);
            }
        }
    }

    @Override
    public int getItemCount() {
        return lPlaces.size();
    }

    public void remove(int position) {
        lPlaces.remove(position);
        notifyItemRemoved(position);
    }
}

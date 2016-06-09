package com.noy.finalprojectdesign;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.noy.finalprojectdesign.Model.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by noy on 22/05/2016.
 */
public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.ViewHolder> {

    List<Place> lPlaces = new ArrayList<Place>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name, details;

        public ViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.placeName);
            details = (TextView) v.findViewById(R.id.placeDetails);

            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = 0;
                    int index = suggestionsList.likeList.indexOf(id);
                    if (index >= 0) {
                        suggestionsList.likeList.remove(index);
                    } else {
                        suggestionsList.likeList.add(id);
                    }
                }
            });
        }
    }

    public SuggestionAdapter(JSONArray places){
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
                    for (int j = 0; j < length; j++) {
                        openHoursText[j] = openHoursTextJson.getString(j);
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
        holder.name.setText(place.getName());
        holder.details.setText(place.getChosenType());
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

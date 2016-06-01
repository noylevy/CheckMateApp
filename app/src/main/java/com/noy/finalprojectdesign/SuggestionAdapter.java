package com.noy.finalprojectdesign;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.noy.finalprojectdesign.Model.Place;

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
        }
    }

    public SuggestionAdapter(){
        lPlaces.add(new Place(1, "קפה גידי","קפה שכונתי", "דרך בן גוריון 108, רמת גן", ""));
        lPlaces.add(new Place(2, "קפה זליק","ממש טוב", "איפשהו בגבעתיים", ""));
        lPlaces.add(new Place(3, "מקס ברנר","שוקולדד", "הרבה סניפים", ""));
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
        holder.details.setText(place.getDetails());
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

package com.noy.finalprojectdesign;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by noy on 23/05/2016.
 */

public class SuggestionTouchHelper extends ItemTouchHelper.SimpleCallback {
    private SuggestionAdapter mSuggestionAdapter;

    public SuggestionTouchHelper(SuggestionAdapter suggestionAdapter) {

        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.mSuggestionAdapter = suggestionAdapter;
    }


    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        //TODO: Not implemented here
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //Remove item
        TextView chosen = (TextView) viewHolder.itemView.findViewById(R.id.placeId);
        suggestionsList.unlikeList.add(chosen.getText().toString());
        mSuggestionAdapter.remove(viewHolder.getAdapterPosition());
    }
}
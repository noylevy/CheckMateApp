package com.noy.finalprojectdesign;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

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
        int id = 0;
        suggestionsList.unlikeList.add(id);
        mSuggestionAdapter.remove(viewHolder.getAdapterPosition());
    }
}
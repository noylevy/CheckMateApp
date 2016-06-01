package com.noy.finalprojectdesign;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

/**
 * Created by noy on 31/05/2016.
 */
public class DatePickerListener extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    int year;
    int month;
    int day;

    public void setDate(int y, int m, int d){
        year = y;
        month = m;
        day = d;
    }

    interface onDateSetListener{
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth);
    }
    private onDateSetListener listener;

    public void setOnDateSetListener(onDateSetListener ls){
        listener = ls;
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new DatePickerDialog(getActivity(),this,year,month,day);

        return dialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        listener.onDateSet(view,year,monthOfYear,dayOfMonth);
    }
}
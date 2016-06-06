package com.noy.finalprojectdesign;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by adi on 08-Mar-16.
 */
public class Utils {

    public enum TimePart {
        H00_02,
        H02_04,
        H04_06,
        H06_08,
        H08_10,
        H10_12,
        H12_14,
        H14_16,
        H16_18,
        H18_20,
        H20_22,
        H22_00;

        public static TimePart fromInt(int x) {
            switch (x) {
                case 0:
                    return H00_02;
                case 1:
                    return H02_04;
                case 2:
                    return H04_06;
                case 3:
                    return H06_08;
                case 4:
                    return H08_10;
                case 5:
                    return H10_12;
                case 6:
                    return H12_14;
                case 7:
                    return H14_16;
                case 8:
                    return H16_18;
                case 9:
                    return H18_20;
                case 10:
                    return H20_22;
                case 11:
                    return H22_00;
            }
            return null;
        }

        public static TimePart getTimePart(Calendar cal){

            int hour = cal.get(Calendar.HOUR_OF_DAY);

            if(hour == 0 || hour == 1){
                return TimePart.H00_02;
            }
            else if(hour == 2 || hour == 3){
                return TimePart.H02_04;
            }
            else if(hour == 4 || hour == 5){
                return TimePart.H04_06;
            }
            else if(hour == 6 || hour == 7){
                return TimePart.H06_08;
            }
            else if(hour == 8 || hour == 9){
                return TimePart.H08_10;
            }
            else if(hour == 10 || hour == 11){
                return TimePart.H10_12;
            }
            else if(hour == 12 || hour == 13){
                return TimePart.H12_14;
            }
            else if(hour == 14 || hour == 15){
                return TimePart.H14_16;
            }
            else if(hour == 16 || hour == 17){
                return TimePart.H16_18;
            }
            else if(hour == 18 || hour == 19){
                return TimePart.H18_20;
            }
            else if(hour == 20 || hour == 21){
                return TimePart.H20_22;
            }
            else if(hour == 22 || hour == 23){
                return TimePart.H22_00;
            }
            return null;
        }

        /**
         *
         * @param date a string like '2014-11-30T13:14:28+0000' from facebook - created_time of the checkin
         * @return
         */
        public static TimePart getTimePart(String date) throws ParseException {

            Calendar cal = parseCreatedTimeToCal(date);

            return getTimePart(cal);
        }

        public TimePart getNext(){

            int next = this.ordinal() + 1;
            if (next > 11) next = 0;

            return Utils.TimePart.fromInt(next);
        }

        public TimePart getPrevious(){
            int previous = this.ordinal() - 1;
            if (previous < 0) previous = 11;

            return Utils.TimePart.fromInt(previous);

        }

    }
    public final static String FACEBOOK_CREATED_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss+SSSS";
    public final static String APP_DEFAULT_TIMESTAMP_FORMAT =  "dd/MM/yyyy HH:mm:ss";


    public static String getCurrentTimestamp() {
        Calendar cal = Calendar.getInstance();
        DateFormat formatter = new SimpleDateFormat(APP_DEFAULT_TIMESTAMP_FORMAT);
        return formatter.format((cal.getTime()));
    }

    public static Calendar parseTimestampToCal(String timestamp) throws ParseException{
        if (timestamp != null) {
            return getCalendar(timestamp, APP_DEFAULT_TIMESTAMP_FORMAT);
        }
        return null;
    }

    @NonNull
    public static Calendar parseCreatedTimeToCal(String date) throws ParseException {
        if (date != null) {
            return getCalendar(date, FACEBOOK_CREATED_TIME_FORMAT);
        }
        return null;
    }

    @NonNull
    private static Calendar getCalendar(String date, String dateFormatString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatString);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateFormat.parse(date));
        return cal;
    }
}

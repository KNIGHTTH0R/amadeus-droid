package amadeuslms.amadeus.utils;

import android.content.Context;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import amadeuslms.amadeus.R;

/**
 * Created by zambom on 22/07/17.
 */

public class DateUtils {

    public static String dateMsg(Context context, String date){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date_time = sdf.parse(date);

            Calendar this_date = Calendar.getInstance();
            this_date.setTime(date_time);

            this_date.set(Calendar.HOUR_OF_DAY, 0);
            this_date.set(Calendar.MINUTE, 0);
            this_date.set(Calendar.SECOND, 0);
            this_date.set(Calendar.MILLISECOND, 0);

            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DAY_OF_MONTH, -1);

            yesterday.set(Calendar.HOUR_OF_DAY, 0);
            yesterday.set(Calendar.MINUTE, 0);
            yesterday.set(Calendar.SECOND, 0);
            yesterday.set(Calendar.MILLISECOND, 0);

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            if(this_date.equals(today)){
                SimpleDateFormat out = new SimpleDateFormat("HH:mm");
                return out.format(date_time);
            } else if(this_date.equals(yesterday)){
                return "yesterday";
            } else{
                SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd");
                return out.format(date_time);
            }

        }catch (ParseException e){
            System.out.println(e.getMessage());
        }

        return null;
    }

    public static String getHour(String date){
        try{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date_time = simpleDateFormat.parse(date);

            SimpleDateFormat out = new SimpleDateFormat("HH:mm");

            return out.format(date_time);
        }catch (ParseException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static String displayDate(Context context, String last_date, String new_date){
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date _last_date = sdf.parse(last_date);
            Date _new_date = sdf.parse(new_date);

            Calendar prev_date = Calendar.getInstance();
            prev_date.setTime(_last_date);

            prev_date.set(Calendar.HOUR_OF_DAY, 0);
            prev_date.set(Calendar.MINUTE, 0);
            prev_date.set(Calendar.SECOND, 0);
            prev_date.set(Calendar.MILLISECOND, 0);

            Calendar next_date = Calendar.getInstance();
            next_date.setTime(_new_date);

            next_date.set(Calendar.HOUR_OF_DAY, 0);
            next_date.set(Calendar.MINUTE, 0);
            next_date.set(Calendar.SECOND, 0);
            next_date.set(Calendar.MILLISECOND, 0);

            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DAY_OF_MONTH, -1);

            yesterday.set(Calendar.HOUR_OF_DAY, 0);
            yesterday.set(Calendar.MINUTE, 0);
            yesterday.set(Calendar.SECOND, 0);
            yesterday.set(Calendar.MILLISECOND, 0);

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            if (!prev_date.equals(next_date)) {
                if (next_date.equals(today)) {
                    return context.getResources().getString(R.string.today);
                } else if (next_date.equals(yesterday)) {
                    return context.getResources().getString(R.string.yesterday);
                } else {
                    return DateFormat.getDateInstance(DateFormat.LONG).format(_new_date);
                }
            } else {
                return "";
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }

        return "";
    }
}

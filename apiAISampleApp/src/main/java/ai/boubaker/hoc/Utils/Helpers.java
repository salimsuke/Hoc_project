package ai.boubaker.hoc.Utils;

import android.text.format.DateFormat;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Salim on 5/16/2018.
 */

public class Helpers {

    public static String formatDateTime(long time){

        Timestamp timestamp = new Timestamp(time);
        Date date = new Date(timestamp.getTime());
        return date.toString();
    }

    public static String getDate(long time, long delay) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("hh:mm:ss", cal).toString();
        return date;
    }

}

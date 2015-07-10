package uk.me.peteharris.shouldigotoyorkpubs.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import lombok.Getter;

@Getter
public class BadTime {
    private final static int TYPE_RACEDAY = 1;

    String label;
    Date start;
    Date end;
    String type;

    public String getDateString(DateFormat df) {
        if(end.equals(start))
            return df.format(start);
        else
            return String.format("%s to %s", df.format(start), df.format(end));
    }

    public boolean isItNow(Date d){
        return d.after(start)
                && d.getTime() < end.getTime() + 1000 * 60 * 60 * 24;
    }
}

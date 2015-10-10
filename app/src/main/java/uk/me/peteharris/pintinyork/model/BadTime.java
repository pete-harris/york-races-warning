package uk.me.peteharris.pintinyork.model;

import java.text.DateFormat;
import java.util.Date;

public class BadTime {
    private final static int TYPE_RACEDAY = 1;

    public String label;
    public Date start;
    public Date end;
    public String type;

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

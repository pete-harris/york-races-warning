package uk.me.peteharris.base.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.util.Date;

public class BadTime implements Parcelable {
    private final static int TYPE_RACEDAY = 1;

    public String label;
    public Date start;
    public Date end;
    public String type;

    public String getDateString(DateFormat df) {
        if(null == end || end.equals(start))
            return df.format(start);
        else
            return String.format("%s to %s", df.format(start), df.format(end));
    }

    public boolean isItNow(Date d){
        return d.after(start)
                && d.getTime() < (null == end ? start.getTime()  : end.getTime()) + 1000 * 60 * 60 * 24;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.label);
        dest.writeLong(this.start != null ? this.start.getTime() : -1);
        dest.writeLong(this.end != null ? this.end.getTime() : -1);
        dest.writeString(this.type);
    }

    public BadTime() {
    }

    protected BadTime(Parcel in) {
        this.label = in.readString();
        long tmpStart = in.readLong();
        this.start = tmpStart == -1 ? null : new Date(tmpStart);
        long tmpEnd = in.readLong();
        this.end = tmpEnd == -1 ? null : new Date(tmpEnd);
        this.type = in.readString();
    }

    public static final Parcelable.Creator<BadTime> CREATOR = new Parcelable.Creator<BadTime>() {
        @Override
        public BadTime createFromParcel(Parcel source) {
            return new BadTime(source);
        }

        @Override
        public BadTime[] newArray(int size) {
            return new BadTime[size];
        }
    };
}

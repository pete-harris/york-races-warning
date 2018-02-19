package uk.me.peteharris.pintinyork.base;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import uk.me.peteharris.pintinyork.base.model.BadTime;
import uk.me.peteharris.pintinyork.base.model.Pub;

public class DataHelper {

    public static ArrayList<BadTime> loadData(Context context) {
        try {
            Reader isr = new BufferedReader(new InputStreamReader(context.getAssets().open("badTimes.json")));
            Gson gson = new GsonBuilder()
//                    .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                    .setDateFormat("yyyy-M-d")
                    .create();
            Type type = new TypeToken<ArrayList<BadTime>>() {
            }.getType();
            return gson.fromJson(isr, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
    
    static ArrayList<Pub> loadPubList(Context context) {
        try {
            Reader isr = new BufferedReader(new InputStreamReader(context.getAssets().open("publist.json")));
            Gson gson = new GsonBuilder()
                    .create();
            Type type = new TypeToken<ArrayList<Pub>>() {
            }.getType();
            return gson.fromJson(isr, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


    public static BadTime isItBad(ArrayList<BadTime> badTimes) {
        Date now = new Date();
        for(BadTime bt: badTimes){
            if(bt.isItNow(now)){
                return bt;
            }
        }
        return null;
    }

    public static boolean isWeekend() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY
                || now.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
    }
}

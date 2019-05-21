package uk.me.peteharris.pintinyork

import android.content.Context

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.ArrayList
import java.util.Calendar
import java.util.Date

import uk.me.peteharris.pintinyork.model.BadTime
import uk.me.peteharris.pintinyork.model.Pub

class DataHelper {

    val isWeekend: Boolean
        get() {
            val now = Calendar.getInstance()
            return now.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY || now.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
        }

    fun loadData(context: Context): ArrayList<BadTime>? {
        try {
            val inputStream = BufferedReader(InputStreamReader(context.assets.open("badTimes.json")))
            val gson = GsonBuilder()
                //                    .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setDateFormat("yyyy-M-d")
                .create()
            val type = object : TypeToken<ArrayList<BadTime>>() {
            }.type
            return gson.fromJson<ArrayList<BadTime>>(inputStream, type)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun loadPubList(context: Context): ArrayList<Pub>? {
        try {
            val isr = BufferedReader(InputStreamReader(context.assets.open("publist.json")))
            val gson = GsonBuilder()
                .create()
            val type = object : TypeToken<ArrayList<Pub>>() {

            }.type
            return gson.fromJson<ArrayList<Pub>>(isr, type)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null

    }
}

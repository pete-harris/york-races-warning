package uk.me.peteharris.shouldigotoyorkpubs;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.me.peteharris.shouldigotoyorkpubs.model.BadTime;


public class MainActivity extends AppCompatActivity {

    @Bind(R.id.badList)
    RecyclerView mRecyclerView;
    @Bind(R.id.shouldIGoIn)
    ImageView shouldIGoIn;

    private ArrayList<BadTime> badTimes;
    private LinearLayoutManager mLayoutManager;
    private BadTimeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        loadData();
    }

    private void loadData() {
        try {
            Reader isr = new BufferedReader(new InputStreamReader(getAssets().open("badTimes.json")));
            Gson gson = new GsonBuilder()
//                    .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                    .setDateFormat("yyyy-mm-dd")
                    .create();
            Type type = new TypeToken<ArrayList<BadTime>>() {}.getType();
            badTimes = gson.fromJson(isr, type);

            mAdapter = new BadTimeAdapter(badTimes);
            mRecyclerView.setAdapter(mAdapter);

            BadTime current = isItBad(badTimes);
            if(null != current) {
                shouldIGoIn.setImageResource(R.drawable.raceday);
            } else {
                Calendar now = Calendar.getInstance();
                if(now.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY
                    || now.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
                    shouldIGoIn.setImageResource(R.drawable.weekend);
                } else {
                    shouldIGoIn.setImageResource(R.drawable.haveapint);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BadTime isItBad(ArrayList<BadTime> badTimes) {
        Date now = new Date();
        for(BadTime bt: badTimes){
            if(bt.isItNow(now)){
                return bt;
            }
        }
        return null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        @Bind(android.R.id.text1) TextView text1;
        @Bind(android.R.id.text2) TextView text2;
        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public class BadTimeAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<BadTime> mBadTimes;
        java.text.DateFormat df = DateFormat.getDateFormat(MainActivity.this);

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder


        // Provide a suitable constructor (depends on the kind of dataset)
        public BadTimeAdapter(ArrayList<BadTime> badTimes) {
            mBadTimes = badTimes;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            final BadTime bt = mBadTimes.get(position);
            // - replace the contents of the view with that element
            holder.text1.setText(bt.getLabel());
            holder.text2.setText(bt.getDateString(df));
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mBadTimes.size();
        }
    }
}

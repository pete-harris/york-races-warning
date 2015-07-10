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
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.me.peteharris.shouldigotoyorkpubs.model.BadTime;
import uk.me.peteharris.shouldigotoyorkpubs.model.Pub;


public class MainActivity extends AppCompatActivity {

    @Bind(R.id.shouldIGoIn)
    ImageView shouldIGoIn;
    @Bind(R.id.text)
    TextView textView;

    private ArrayList<BadTime> badTimes;
    private ArrayList<Pub> pubs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        badTimes = DataHelper.loadData(this);
        pubs = DataHelper.loadPubList(this);
        int randomIndex = new Random().nextInt(pubs.size());

        String text;

        BadTime current = DataHelper.isItBad(badTimes);
        if(null != current) {
            shouldIGoIn.setImageResource(R.drawable.raceday);
            text = getString(R.string.raceday, current.getLabel());
        } else if (DataHelper.isWeekend()) {
            shouldIGoIn.setImageResource(R.drawable.weekend);
            text = getString(R.string.weekend);
        } else {
            shouldIGoIn.setImageResource(R.drawable.haveapint);
            text = getString(R.string.haveapint, pubs.get(randomIndex).getName());
        }
        textView.setText(text);
    }
}

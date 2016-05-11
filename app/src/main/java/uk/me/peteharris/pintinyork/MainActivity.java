package uk.me.peteharris.pintinyork;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.me.peteharris.pintinyork.model.BadTime;
import uk.me.peteharris.pintinyork.model.Pub;


public class MainActivity extends AppCompatActivity {

    @Bind(R.id.shouldIGoIn)
    ImageView shouldIGoIn;
    @Bind(R.id.text)
    TextView textView;

    private ArrayList<BadTime> badTimes;
    private ArrayList<Pub> pubs;

    Pub randomPub = null;
    private MenuItem testitem;

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
            text = getString(R.string.raceday, current.label);
            randomPub = null;
        } else if (DataHelper.isWeekend()) {
            shouldIGoIn.setImageResource(R.drawable.weekend);
            text = getString(R.string.weekend);
            randomPub = null;
        } else {
            shouldIGoIn.setImageResource(R.drawable.haveapint);
            randomPub = pubs.get(randomIndex);
            text = getString(R.string.haveapint, randomPub.name);
        }
        textView.setText(text);
        invalidateOptionsMenu();

        new RaceDayNotificationReceiver.Helper(this).scheduleAlarm();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);

        menu.findItem(R.id.action_directions).setVisible(null != randomPub);

        if(BuildConfig.DEBUG){
            testitem = menu.add("Test Notification");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_raceday: {
                Intent intent = new Intent(this, RaceDays.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_directions: {
                Intent intent = new Intent(Intent.ACTION_VIEW, randomPub.getAddressUri());
                startActivity(intent);
                return true;
            }
        }
        if(item == testitem){
            RaceDayNotificationReceiver.showNotification(this, badTimes.get(0));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

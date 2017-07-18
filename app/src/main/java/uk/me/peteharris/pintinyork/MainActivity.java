package uk.me.peteharris.pintinyork;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Random;

import uk.me.peteharris.pintinyork.databinding.ActivityMainBinding;
import uk.me.peteharris.pintinyork.model.BadTime;
import uk.me.peteharris.pintinyork.model.Pub;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    private ArrayList<BadTime> badTimes;
    private ArrayList<Pub> pubs;

    Pub randomPub = null;
    private MenuItem testitem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        badTimes = DataHelper.loadData(this);
        pubs = DataHelper.loadPubList(this);
        int randomIndex = new Random().nextInt(pubs.size());

        BadTime current = DataHelper.isItBad(badTimes);
        if(null != current) {
            randomPub = null;
            binding.shouldIGoIn.setImageResource(R.drawable.raceday);
            binding.setGoInText(getString(R.string.raceday, current.label));
        } else if (DataHelper.isWeekend()) {
            randomPub = null;
            binding.shouldIGoIn.setImageResource(R.drawable.weekend);
            binding.setGoInText(getString(R.string.weekend));
        } else {
            randomPub = pubs.get(randomIndex);
            binding.shouldIGoIn.setImageResource(R.drawable.pint);
            binding.setGoInText(getString(R.string.haveapint, randomPub.name));
        }
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
                Intent intent = new Intent(this, RaceDaysActivity.class);
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

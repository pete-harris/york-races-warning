package uk.me.peteharris.pintinyork.base;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import uk.me.peteharris.pintinyork.R;
import uk.me.peteharris.pintinyork.base.model.BadTime;
import uk.me.peteharris.pintinyork.base.model.Pub;
import uk.me.peteharris.pintinyork.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private Pub randomPub = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        ArrayList<BadTime> badTimes = DataHelper.loadData(this);
        ArrayList<Pub> pubs = DataHelper.loadPubList(this);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);

        menu.findItem(R.id.action_directions).setVisible(null != randomPub);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_raceday) {
            Intent intent = new Intent(this, RaceDaysActivity.class);
            startActivity(intent);
            return true;
        } else if (i == R.id.action_publist) {
            Intent intent = new Intent(this, PubListActivity.class);
            startActivity(intent);
            return true;
        } else if (i == R.id.action_directions) {
            Intent intent = new Intent(Intent.ACTION_VIEW, randomPub.getAddressUri());
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

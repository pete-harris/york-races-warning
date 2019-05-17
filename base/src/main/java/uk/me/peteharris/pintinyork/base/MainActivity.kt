package uk.me.peteharris.pintinyork.base

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import uk.me.peteharris.pintinyork.R
import uk.me.peteharris.pintinyork.base.model.Pub
import java.util.*


class MainActivity : AppCompatActivity() {

    private var randomPub: Pub? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val badTimes = DataHelper.loadData(this)
        val pubs = DataHelper.loadPubList(this)
        val randomIndex = Random().nextInt(pubs!!.size)

        val current = DataHelper.isItBad(badTimes!!)
        if (null != current) {
            randomPub = null
            shouldIGoIn.setImageResource(R.drawable.raceday)
            shouldIGoInText.text = getString(R.string.raceday, current.label)
        } else if (DataHelper.isWeekend()) {
            randomPub = null
            shouldIGoIn.setImageResource(R.drawable.weekend)
            shouldIGoInText.text = getString(R.string.weekend)
        } else {
            randomPub = pubs[randomIndex]
            shouldIGoIn.setImageResource(R.drawable.pint)
            shouldIGoInText.text = getString(R.string.haveapint, randomPub!!.name)
        }
        invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_main, menu)

        menu.findItem(R.id.action_directions).isVisible = null != randomPub

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val i = item.itemId
        when (i) {
            R.id.action_raceday -> {
                val intent = Intent(this, RaceDaysActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.action_publist -> {
                val intent = Intent(this, PubListActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.action_directions -> {
                val intent = Intent(Intent.ACTION_VIEW, randomPub!!.addressUri)
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}

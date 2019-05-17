package uk.me.peteharris.pintinyork.base

import android.content.Intent
import android.net.Uri
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

        val dataHelper = DataHelper()
        val badTimes = dataHelper.loadData(this)
        val pubs = dataHelper.loadPubList(this)
        val randomIndex = Random().nextInt(pubs!!.size)

        val current = dataHelper.isItBad(badTimes!!)
        when {
            null != current -> {
                randomPub = null
                shouldIGoIn.setImageResource(R.drawable.raceday)
                shouldIGoInText.text = getString(R.string.raceday, current.label)
            }
            dataHelper.isWeekend -> {
                randomPub = null
                shouldIGoIn.setImageResource(R.drawable.weekend)
                shouldIGoInText.text = getString(R.string.weekend)
            }
            else -> {
                randomPub = pubs[randomIndex]
                shouldIGoIn.setImageResource(R.drawable.pint)
                shouldIGoInText.text = getString(R.string.haveapint, randomPub!!.name)
            }
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
        return when (item.itemId) {
            R.id.action_raceday -> {
                val intent = Intent(this, RaceDaysActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_publist -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.map_url)))
                startActivity(intent)
                true
            }
            R.id.action_directions -> {
                randomPub?.let {
                    val intent = Intent(Intent.ACTION_VIEW, it.addressUri)
                    startActivity(intent)
                    true
                } ?: true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

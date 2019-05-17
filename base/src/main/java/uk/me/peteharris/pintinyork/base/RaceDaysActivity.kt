package uk.me.peteharris.pintinyork.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_race_days.*
import uk.me.peteharris.pintinyork.R
import uk.me.peteharris.pintinyork.base.model.BadTime
import java.util.*


class RaceDaysActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_race_days)

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recycler.setHasFixedSize(true)

        // use a linear layout manager
        recycler.layoutManager = LinearLayoutManager(this)

        val badTimes = DataHelper().loadData(this)

        recycler.adapter = BadTimeAdapter(badTimes)
    }

    internal class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val text1: TextView = v.findViewById(android.R.id.text1)
        val text2: TextView = v.findViewById(android.R.id.text2)
    }

    internal inner class BadTimeAdapter
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder


    // Provide a suitable constructor (depends on the kind of dataset)
        (private val mBadTimes: ArrayList<BadTime>) : RecyclerView.Adapter<ViewHolder>() {
        private val df: java.text.DateFormat = DateFormat.getDateFormat(this@RaceDaysActivity)

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            // create a new view
            val v = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_2, parent, false)
            // set the view's size, margins, paddings and layout parameters
            return ViewHolder(v)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            // - get element from your dataset at this position
            val bt = mBadTimes[position]
            // - replace the contents of the view with that element
            holder.text1.text = bt.label
            holder.text2.text = bt.getDateString(df)
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount(): Int {
            return mBadTimes.size
        }
    }
}

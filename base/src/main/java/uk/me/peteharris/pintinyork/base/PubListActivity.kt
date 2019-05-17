package uk.me.peteharris.pintinyork.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_race_days.*
import uk.me.peteharris.pintinyork.R
import uk.me.peteharris.pintinyork.base.model.Pub
import java.util.*


class PubListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_race_days)

        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(this)

        val pubs = DataHelper.loadPubList(this)

        recycler.adapter = PubAdapter(pubs)
    }


    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        internal var text1: TextView = v.findViewById(android.R.id.text1)
    }

    inner class PubAdapter(private val pubs: ArrayList<Pub>) : RecyclerView.Adapter<ViewHolder>() {

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            // create a new view
            val v = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            // set the view's size, margins, paddings and layout parameters
            return ViewHolder(v)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val pub = pubs[position]
            holder.text1.text = pub.name
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount(): Int {
            return pubs.size
        }
    }
}

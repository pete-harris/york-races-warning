package uk.me.peteharris.pintinyork;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.me.peteharris.pintinyork.model.BadTime;


public class RaceDays extends AppCompatActivity {

    @Bind(R.id.badList)
    RecyclerView mRecyclerView;

    private ArrayList<BadTime> badTimes;
    private LinearLayoutManager mLayoutManager;
    private BadTimeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_days);

        ButterKnife.bind(this);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        badTimes = DataHelper.loadData(this);

        mAdapter = new BadTimeAdapter(badTimes);
        mRecyclerView.setAdapter(mAdapter);
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        @Bind(android.R.id.text1)
        TextView text1;
        @Bind(android.R.id.text2) TextView text2;
        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public class BadTimeAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<BadTime> mBadTimes;
        java.text.DateFormat df = DateFormat.getDateFormat(RaceDays.this);

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

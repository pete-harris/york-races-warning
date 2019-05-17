package uk.me.peteharris.pintinyork.base;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import uk.me.peteharris.pintinyork.R;
import uk.me.peteharris.pintinyork.base.model.BadTime;
import uk.me.peteharris.pintinyork.databinding.ActivityRaceDaysBinding;


public class RaceDaysActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityRaceDaysBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_race_days);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        binding.badList.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.badList.setLayoutManager(mLayoutManager);

        ArrayList<BadTime> badTimes = DataHelper.loadData(this);

        BadTimeAdapter mAdapter = new BadTimeAdapter(badTimes);
        binding.badList.setAdapter(mAdapter);
    }



    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView text1;
        final TextView text2;
        ViewHolder(View v) {
            super(v);
            text1 = v.findViewById(android.R.id.text1);
            text2 = v.findViewById(android.R.id.text2);
        }
    }

    class BadTimeAdapter extends RecyclerView.Adapter<ViewHolder> {
        private final ArrayList<BadTime> mBadTimes;
        final java.text.DateFormat df = DateFormat.getDateFormat(RaceDaysActivity.this);

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder


        // Provide a suitable constructor (depends on the kind of dataset)
        BadTimeAdapter(ArrayList<BadTime> badTimes) {
            mBadTimes = badTimes;
        }

        // Create new views (invoked by the layout manager)
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                             int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            // set the view's size, margins, paddings and layout parameters
            return new ViewHolder(v);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            // - get element from your dataset at this position
            final BadTime bt = mBadTimes.get(position);
            // - replace the contents of the view with that element
            holder.text1.setText(bt.label);
            holder.text2.setText(bt.getDateString(df));
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mBadTimes.size();
        }
    }
}

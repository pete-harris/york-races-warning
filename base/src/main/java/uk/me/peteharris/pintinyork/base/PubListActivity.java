package uk.me.peteharris.pintinyork.base;

import android.databinding.DataBindingUtil;
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

import uk.me.peteharris.pintinyork.R;
import uk.me.peteharris.pintinyork.base.model.BadTime;
import uk.me.peteharris.pintinyork.base.model.Pub;
import uk.me.peteharris.pintinyork.databinding.ActivityRaceDaysBinding;


public class PubListActivity extends AppCompatActivity {

    ActivityRaceDaysBinding binding;

    private ArrayList<Pub> pubs;
    private LinearLayoutManager mLayoutManager;
    private PubAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = DataBindingUtil.setContentView(this, R.layout.activity_race_days);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        binding.recycler.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        binding.recycler.setLayoutManager(mLayoutManager);

        pubs = DataHelper.loadPubList(this);

        mAdapter = new PubAdapter(pubs);
        binding.recycler.setAdapter(mAdapter);
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text1;
        public ViewHolder(View v) {
            super(v);
            text1 = v.findViewById(android.R.id.text1);
        }
    }

    public class PubAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<Pub> pubs;

        public PubAdapter(ArrayList<Pub> pubs) {
            this.pubs = pubs;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Pub pub = pubs.get(position);
            holder.text1.setText(pub.name);
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return pubs.size();
        }
    }
}

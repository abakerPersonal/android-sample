package com.sonos.abaker.android_sample;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sonos.abaker.android_sample.model.Group;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alan.baker on 10/25/17.
 */

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupViewHolder> {

    public List<Group> groups = new ArrayList<>();

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class GroupViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private final ViewDataBinding binding;

        public GroupViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TEST", "CLICKY CLICK");
                }
            });
        }

        public void bind(Object obj) {
            binding.setVariable(BR.group,obj);
            binding.executePendingBindings();
        }
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.group_row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new GroupViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(GroupViewHolder holder, int position) {
        final Group group = groups.get(position);
        holder.bind(group);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public void updateGroups(List<Group> groups) {
        this.groups = groups;
        notifyDataSetChanged();
    }
}

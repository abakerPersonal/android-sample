package com.sonos.abaker.android_sample;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sonos.abaker.android_sample.databinding.GroupsActivityBinding;
import com.sonos.abaker.android_sample.model.Group;
import com.sonos.abaker.android_sample.discover.GroupDiscoveryService;
import com.sonos.abaker.android_sample.discover.GroupDiscoveryServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class GroupsActivity extends AppCompatActivity implements GroupsActivityHandler {

    final GroupDiscoveryService groupDiscoveryService = new GroupDiscoveryServiceImpl();
    private RecyclerView recyclerView;
    private GroupsAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.groups_activity);

        GroupsActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.groups_activity);
        binding.setHandler(this);

        recyclerView = (RecyclerView) findViewById(R.id.group_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new GroupsAdapter();
        recyclerView.setAdapter(mAdapter);

        groupDiscoveryService.getDiscoveredGroupsObservable()
                .subscribe(new Observer<SortedMap<String, Group>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {}

                    @Override
                    public void onNext(@NonNull SortedMap<String, Group> groupSortedMap) {
                        if (!groupSortedMap.isEmpty()) {
                            List<Group> groupsList = new ArrayList<Group>(groupSortedMap.values());
                            updateData(groupsList);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {}

                    @Override
                    public void onComplete() {}
                });

        groupDiscoveryService.start();
    }

    public void updateData(final List<Group> groups) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.updateGroups(groups);
            }
        });
    }

    @Override
    public void onClickRefresh(View view) {
        groupDiscoveryService.stop();
        groupDiscoveryService.start();
    }
}

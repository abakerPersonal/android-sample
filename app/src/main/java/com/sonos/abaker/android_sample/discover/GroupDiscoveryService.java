package com.sonos.abaker.android_sample.discover;

import com.sonos.abaker.android_sample.model.Group;

import java.util.SortedMap;

import io.reactivex.Observable;

/**
 * Created by alan.baker on 10/23/17.
 */

public interface GroupDiscoveryService {

    void start();

    void stop();

    Observable<SortedMap<String, Group>> getDiscoveredGroupsObservable();
}

package com.sonos.abaker.android_sample.model;

import android.databinding.ObservableField;

import com.sonos.abaker.android_sample.BR;

/**
 * Created by alan.baker on 10/27/17.
 */

public class ControlActivityPageModel {

    public final ObservableField<String> trackName = new ObservableField<>("No Music");
    public final ObservableField<Integer> volume = new ObservableField<>(0);

}

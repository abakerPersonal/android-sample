package com.sonos.abaker.android_sample.model;

import android.databinding.ObservableField;


import com.sonos.abaker.android_sample.control.response.PlaybackMetadataResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.PublicKey;
import java.util.Observer;

/**
 * Created by alan.baker on 10/27/17.
 */

public class ControlActivityPageModel {
    private static final String LOG_TAG = ControlActivityPageModel.class.getSimpleName();

    public final ObservableField<String> trackName = new ObservableField<>("No Music");
    public final ObservableField<String> artistAndAlbum = new ObservableField<>("");
    public final ObservableField<Integer> volume = new ObservableField<>(0);
    public final ObservableField<Boolean> muted = new ObservableField<>(false);

    public final ObservableField<Integer> currentPlaybackPosition = new ObservableField<>(0);
    public final ObservableField<Integer> maxPlaybackPostion = new ObservableField<>(0);

    public void updateMetadata(PlaybackMetadataResponse metadataResponse) {
        trackName.set(metadataResponse.getTrackName());

        if (metadataResponse.getAlbumName() != null && !metadataResponse.getAlbumName().isEmpty()) {
            artistAndAlbum.set(metadataResponse.getArtistName() + " - " + metadataResponse.getAlbumName());
        } else {
            artistAndAlbum.set(metadataResponse.getArtistName());
        }

        maxPlaybackPostion.set(metadataResponse.getDurationMillis());
    }

}

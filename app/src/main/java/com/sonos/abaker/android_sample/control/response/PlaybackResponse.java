package com.sonos.abaker.android_sample.control.response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alan.baker on 10/27/17.
 */

public class PlaybackResponse extends BaseResponse {

    public enum PlaybackState {
        PLAYBACK_STATE_BUFFERING,
        PLAYBACK_STATE_IDLE,
        PLAYBACK_STATE_PAUSED,
        PLAYBACK_STATE_PLAYING;
    }

    private PlaybackState playbackState;
    private int positionMillis;

    public PlaybackResponse(JSONArray fromJSON) throws JSONException {
        super(fromJSON);

        JSONObject playback = fromJSON.getJSONObject(1);
        playbackState = PlaybackState.valueOf(playback.getString("playbackState"));
        positionMillis = playback.getInt("positionMillis");
    }

    public PlaybackState getPlaybackState() {
        return playbackState;
    }

    public int getPositionMillis() {
        return positionMillis;
    }

    @Override
    public String toString() {
        return "PlaybackResponse{" +
                "playbackState=" + playbackState +
                ", positionMillis=" + positionMillis +
                ", groupId='" + groupId + '\'' +
                ", householdId='" + householdId + '\'' +
                ", command='" + command + '\'' +
                ", namespace='" + namespace + '\'' +
                ", type=" + type +
                ", response='" + response + '\'' +
                ", success=" + success +
                '}';
    }
}

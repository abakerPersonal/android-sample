package com.sonos.abaker.android_sample.control.response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alan.baker on 10/27/17.
 */

public class GroupVolumeResponse extends BaseResponse {

    private static final String MUTED_KEY = "muted";
    private static final String FIXED_KEY = "fixed";
    private static final String VOLUME_KEY = "volume";

    private boolean muted;
    private boolean fixed;
    private int volume;

    public GroupVolumeResponse(JSONArray fromJson) throws JSONException{
        super(fromJson);

        JSONObject volumeData = fromJson.getJSONObject(1);
        muted = volumeData.getBoolean(MUTED_KEY);
        fixed = volumeData.getBoolean(FIXED_KEY);
        volume = volumeData.getInt(VOLUME_KEY);
    }

    public boolean isMuted() {
        return muted;
    }

    public boolean isFixed() {
        return fixed;
    }

    public int getVolume() {
        return volume;
    }

    @Override
    public String toString() {
        return "GroupVolumeResponse{" +
                "muted=" + muted +
                ", fixed=" + fixed +
                ", volume=" + volume +
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

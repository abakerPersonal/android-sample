package com.sonos.abaker.android_sample.control.response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alan.baker on 10/27/17.
 */

public class PlaybackMetadataResponse extends BaseResponse {

    private String trackName;
    private String artistName;
    private String albumName;
    private String imageUrl;
    private String sourceName;
    private int durationMillis;


    public PlaybackMetadataResponse(JSONArray fromJSON) throws JSONException {
        super(fromJSON);

        JSONObject container = fromJSON.getJSONObject(1);
        JSONObject currentItem = container.getJSONObject("currentItem");
        JSONObject track = currentItem.getJSONObject("track");
        trackName = track.getString("name");
        imageUrl = track.getString("imageUrl");
        durationMillis = track.getInt("durationMillis");
        JSONObject artist = track.getJSONObject("artist");
        artistName = artist.getString("name");

        if (track.has("album")) {
            JSONObject album = track.getJSONObject("album");
            albumName = album.getString("name");
        }

        JSONObject service = track.getJSONObject("service");
        sourceName = service.getString("name");
    }

    public String getTrackName() {
        return trackName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getSourceName() {
        return sourceName;
    }

    public int getDurationMillis() {
        return durationMillis;
    }

    public String getAlbumName() {
        return albumName;
    }

    @Override
    public String toString() {
        return "PlaybackMetadataResponse{" +
                "trackName='" + trackName + '\'' +
                ", artistName='" + artistName + '\'' +
                ", albumName='" + albumName + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", sourceName='" + sourceName + '\'' +
                ", durationMillis=" + durationMillis +
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

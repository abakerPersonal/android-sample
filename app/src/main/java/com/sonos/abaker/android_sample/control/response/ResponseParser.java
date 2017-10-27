package com.sonos.abaker.android_sample.control.response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.sonos.abaker.android_sample.control.response.BaseResponse.Types;

/**
 * Created by alan.baker on 10/27/17.
 */

public class ResponseParser {

    private static String TYPE_KEY = "type";

    public static BaseResponse fromJsonString(String jsonString) throws JSONException {
        BaseResponse response;

        JSONArray jsonResponse = new JSONArray(jsonString);
        JSONObject main = jsonResponse.getJSONObject(0);
        Types type = Types.fromString(main.getString(TYPE_KEY));

        switch (type) {
            case GROUP_VOLUME:
                response = new GroupVolumeResponse(jsonResponse);
                break;
            case METADATA_STATUS:
                response = new PlaybackMetadataResponse(jsonResponse);
                break;
            case PLAYBACK_STATUS:
                response = new PlaybackResponse(jsonResponse);
                break;
            default:
                response = new BaseResponse(jsonResponse);

        }



        return response;
    }


}

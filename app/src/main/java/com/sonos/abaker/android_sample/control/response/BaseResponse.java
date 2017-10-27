package com.sonos.abaker.android_sample.control.response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by alan.baker on 10/26/17.
 */

public class BaseResponse {

    public enum Types {
        NONE("none"),
        GROUP_VOLUME("groupVolume"),
        METADATA_STATUS("metadataStatus");

        private final String text;

        /**
         * @param text
         */
        Types(final String text) {
            this.text = text;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return text;
        }

        public static Types fromString(String text) {
            for (Types b : Types.values()) {
                if (b.text.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    private static final String NAMESPACE_KEY = "namespace";
    private static String HOUSEHOLD_ID_KEY = "householdId";
    private static String GROUP_ID_KEY = "groupId";
    private static String TYPE_KEY = "type";
    private static String RESPONSE_KEY = "response";
    private static String SUCCESS_KEY = "success";

    protected String groupId;
    protected String householdId;
    protected String command;
    protected String namespace;
    protected Types type;
    protected String response;
    protected Boolean success;


    public BaseResponse(JSONArray fromJSON) throws JSONException {
        JSONObject main = fromJSON.getJSONObject(0);
        // Common elements to all responses
        namespace = main.getString(NAMESPACE_KEY);
        groupId = main.getString(GROUP_ID_KEY);
        householdId = main.getString(HOUSEHOLD_ID_KEY);
        type = Types.fromString(main.getString(TYPE_KEY));

        if (main.has(RESPONSE_KEY)) {
            response = main.getString(RESPONSE_KEY);
        }
        if (main.has(SUCCESS_KEY)) {
            success = main.getBoolean(SUCCESS_KEY);
        }
    }

    public String getGroupId() {
        return groupId;
    }

    public String getHouseholdId() {
        return householdId;
    }

    public String getCommand() {
        return command;
    }

    public String getNamespace() {
        return namespace;
    }

    public Types getType() {
        return type;
    }

    public String getResponse() {
        return response;
    }

    public Boolean getSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "groupId='" + groupId + '\'' +
                ", householdId='" + householdId + '\'' +
                ", command='" + command + '\'' +
                ", namespace='" + namespace + '\'' +
                ", type='" + type + '\'' +
                ", response='" + response + '\'' +
                ", success=" + success +
                '}';
    }

}

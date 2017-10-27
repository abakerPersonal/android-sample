package com.sonos.abaker.android_sample.control.request;

import com.sonos.abaker.android_sample.model.Group;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.jar.JarException;

/**
 * Created by alan.baker on 10/26/17.
 */

public class BaseRequest {

    // SONOS control command namespaces
    public enum Namespace {
        GROUP_VOLUME("groupVolume:1"),
        PLAYBACK_METADATA("playbackMetadata:1");

        private final String text;

        /**
         * @param text
         */
        Namespace(final String text) {
            this.text = text;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return text;
        }
    }

    // Sonos Control Commands
    public enum Command {
        SUBSCRIBE("subscribe"),
        GROUP_SET_VOLUME("setVolume");

        private final String text;

        /**
         * @param text
         */
        Command(final String text) {
            this.text = text;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return text;
        }
    }

    private final String NAMESPACE_KEY = "namespace";
    private final String COMMAND_KEY = "command";
    private final String HOUSEHOLD_ID_KEY = "householdId";
    private final String GROUP_ID_KEY = "groupId";

    protected Group group;
    protected Command command;
    protected Namespace namespace;
    protected final HashMap<String, String> data = new HashMap<>();

    public BaseRequest(Group group) {
        this.group = group;
    }

    public String toJsonString() throws JSONException {
        JSONObject commandJSON = new JSONObject();
        commandJSON.put(NAMESPACE_KEY, namespace.toString());
        commandJSON.put(COMMAND_KEY, command.toString());
        commandJSON.put(HOUSEHOLD_ID_KEY, group.getHouseholdId());
        commandJSON.put(GROUP_ID_KEY, group.getId());

        JSONObject dataJSON = new JSONObject();
        for (String key : data.keySet()) {
            dataJSON.put(key, data.get(key));
        }

        JSONArray array = new JSONArray();
        array.put(commandJSON);
        array.put(dataJSON);

        return array.toString();
    }



}

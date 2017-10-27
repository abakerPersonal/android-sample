package com.sonos.abaker.android_sample.control.request;

import com.sonos.abaker.android_sample.model.Group;

/**
 * Created by alan.baker on 10/27/17.
 */

public class SetMuteRequest extends BaseRequest {

    private final String MUTE_KEY = "muted";

    public SetMuteRequest(Group group, boolean muted) {
        super(group);
        this.namespace = Namespace.GROUP_VOLUME;
        this.command = Command.GROUP_SET_MUTE;
        this.data.put(MUTE_KEY, muted);
    }
}

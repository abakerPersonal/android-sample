package com.sonos.abaker.android_sample.control.request;

import com.sonos.abaker.android_sample.model.Group;

/**
 * Created by alan.baker on 10/26/17.
 */

public class SetGroupVolumeCommandRequest extends BaseRequest {

    private final String VOLUME_KEY = "volume";

    public SetGroupVolumeCommandRequest(Group group, int volume) {
        super(group);
        this.namespace = Namespace.GROUP_VOLUME;
        this.command = Command.GROUP_SET_VOLUME;
        this.data.put(VOLUME_KEY, String.valueOf(volume));
    }
}

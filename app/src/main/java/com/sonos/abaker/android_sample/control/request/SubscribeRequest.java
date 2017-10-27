package com.sonos.abaker.android_sample.control.request;

import com.sonos.abaker.android_sample.model.Group;

/**
 * Created by alan.baker on 10/27/17.
 */

public class SubscribeRequest extends BaseRequest {

    public SubscribeRequest(Namespace namespace, Group group) {
        super(group);
        this.namespace = namespace;
        this.command = Command.SUBSCRIBE;
    }
}

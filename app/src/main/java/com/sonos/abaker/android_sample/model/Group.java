package com.sonos.abaker.android_sample.model;

import java.util.Date;

/**
 * Created by alan.baker on 10/23/17.
 */

public class Group {
    private String id;
    private String name;
    private String configId;
    private String uuid;
    private Boolean groupCoordinator;
    private String websocketURL;
    private String bootId;
    private String householdId;
    private Date maxAge;
    private String NTS;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Boolean getGroupCoordinator() {
        return groupCoordinator;
    }

    public void setGroupCoordinator(Boolean groupCoordinator) {
        this.groupCoordinator = groupCoordinator;
    }

    public String getWebsocketURL() {
        return websocketURL;
    }

    public void setWebsocketURL(String websocketURL) {
        this.websocketURL = websocketURL;
    }

    public String getBootId() {
        return bootId;
    }

    public void setBootId(String bootId) {
        this.bootId = bootId;
    }

    public String getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(String householdId) {
        this.householdId = householdId;
    }

    public Date getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Date maxAge) {
        this.maxAge = maxAge;
    }

    public String getNTS() {
        return NTS;
    }

    public void setNTS(String NTS) {
        this.NTS = NTS;
    }
}

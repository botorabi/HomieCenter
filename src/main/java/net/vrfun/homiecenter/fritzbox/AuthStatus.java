/*
 * Copyright (c) 2018 - 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.fritzbox;

import org.h2.util.StringUtils;

import java.util.Map;

/**
 * Information about the status of authentication.
 *
 * @author          boto
 * Creation Date    6th June 2018
 */
public class AuthStatus {

    private String SID;
    private String challenge;
    private int blockTime;
    private Map<String, String> rights;

    public String getSID() {
        return SID;
    }

    public void setSID(String SID) {
        this.SID = SID;
    }

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    public int getBlockTime() {
        return blockTime;
    }

    public void setBlockTime(int blockTime) {
        this.blockTime = blockTime;
    }

    public Map<String, String> getRights() {
        return rights;
    }

    public void setRights(Map<String, String> rights) {
        this.rights = rights;
    }

    public boolean hasValidSID() {
        try {
            long sid = Long.parseLong(SID);
            return sid != 0L;
        }
        catch (Throwable throwable) {
            // a valid sid contains numbers and letters!
            return !StringUtils.isNullOrEmpty(SID);
        }
    }

    public boolean isAuthenticated() {
        return hasValidSID();
    }

    @Override
    public String toString() {
        String rightsString = "";
        if (rights != null) {
            for (Map.Entry<String, String> entry: rights.entrySet()) {
                if (!rightsString.isEmpty()) {
                    rightsString += ", ";
                }
                rightsString += entry.getKey() + ": " + entry.getValue();
            }
        }

        return "SID: " + SID + ", " +
                "Challenge: " + challenge + ", " +
                "BlockTime: " + blockTime + ", " +
                "Rights: [" + rightsString + "]";
    }
}

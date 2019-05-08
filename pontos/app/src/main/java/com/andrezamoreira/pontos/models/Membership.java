package com.andrezamoreira.pontos.models;

public class Membership {
    private String targetId;
    private String membershipType;
    private String membershipStatus;


    public String getTargetID() {
        return targetId;
    }

    public void setTargetID(String value) {
        this.targetId = value;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String value) {
        this.membershipType = value;
    }

    public String getMembershipStatus() {
        return membershipStatus;
    }

    public void setMembershipStatus(String value) {
        this.membershipStatus = value;
    }
}
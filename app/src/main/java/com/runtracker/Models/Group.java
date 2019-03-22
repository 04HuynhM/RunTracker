package com.runtracker.Models;

public class Group {
    private int group_id;
    private String admin;
    private String groupName;
    private int[] members;

    public Group(int group_id, String admin, String groupName, int[] members) {
        this.group_id = group_id;
        this.admin = admin;
        this.groupName = groupName;
        this.members = members;
    }

    public int getGroup_id() {
        return group_id;
    }

    public String getAdmin() {
        return admin;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int[] getMembers() {
        return members;
    }

    public void setMembers(int[] members) {
        this.members = members;
    }
}

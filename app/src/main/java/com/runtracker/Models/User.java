package com.runtracker.Models;

public class User {
    private String username;
    private String name;
    private String email;
    private String password;
    private String profilePicture;
    private boolean isAdmin;
    private int currentWeight;
    private int weightGoal;
    private int dailyStepGoal;
    private int[] groupInvitations;
    private int[] joinedGroups;

    public User(String username,
                String name,
                String email,
                String password,
                String profilePicture,
                boolean isAdmin,
                int currentWeight,
                int weightGoal,
                int stepGoal,
                int[] groupInvitations,
                int[] joinedGroups) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
        this.currentWeight = currentWeight;
        this.weightGoal = weightGoal;
        this.dailyStepGoal = stepGoal;
        this.groupInvitations = groupInvitations;
        this.joinedGroups = joinedGroups;
        this.profilePicture = profilePicture;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public int getDailyStepGoal() {
        return dailyStepGoal;
    }

    public void setDailyStepGoal(int dailyStepGoal) {
        this.dailyStepGoal = dailyStepGoal;
    }


    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(int currentWeight) {
        this.currentWeight = currentWeight;
    }

    public int getWeightGoal() {
        return weightGoal;
    }

    public void setWeightGoal(int weightGoal) {
        this.weightGoal = weightGoal;
    }

    public int[] getGroupInvitations() {
        return groupInvitations;
    }

    public void setGroupInvitations(int[] groupInvitations) {
        this.groupInvitations = groupInvitations;
    }

    public int[] getJoinedGroups() {
        return joinedGroups;
    }

    public void setJoinedGroups(int[] joinedGroups) {
        this.joinedGroups = joinedGroups;
    }
}

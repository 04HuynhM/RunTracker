package com.runtracker.Models;

import java.util.ArrayList;

public class User {

    private String user_id;
    private String name;
    private String email;
    private String password;
    private int currentWeight;
    private int weightGoal;
    private int dailyStepGoal;
    private ArrayList<Run> runs;

    public User(String user_id, String name, String email, String password, int currentWeight, int weightGoal, int stepGoal) {
        this.user_id = user_id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.currentWeight = currentWeight;
        this.weightGoal = weightGoal;
        this.dailyStepGoal = stepGoal;
    }

    public User(String user_id, String name, String email, String password, int currentWeight, int weightGoal, ArrayList<Run> runs) {
        this.user_id = user_id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.currentWeight = currentWeight;
        this.weightGoal = weightGoal;
        this.runs = runs;
    }

    public String getUser_id() {
        return user_id;
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

    public ArrayList<Run> getRuns() {
        return runs;
    }

    public void setRuns(ArrayList<Run> runs) {
        this.runs = runs;
    }
}

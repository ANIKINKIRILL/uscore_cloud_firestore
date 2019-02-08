package com.example.admin.uscore001.models;

public class Option {

    private int score;
    private String option;

    public Option(int score, String option) {
        this.score = score;
        this.option = option;
    }

    Option(){

    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}

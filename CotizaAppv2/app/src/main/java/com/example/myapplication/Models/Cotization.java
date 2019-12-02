package com.example.myapplication.Models;

import android.net.Uri;

import java.util.ArrayList;

public class Cotization {
    String id;
    String description;
    double averageCost;
    String figureVolume;
    String url;

    public Cotization(String id, String description, double averageCost, String figureVolume, String url) {
        this.id = id;
        this.description = description;
        this.averageCost = averageCost;
        this.figureVolume = figureVolume;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAverageCost() {
        return averageCost;
    }

    public void setAverageCost(double averageCost) {
        this.averageCost = averageCost;
    }

    public String getFigureVolume() {
        return figureVolume;
    }

    public void setFigureVolume(String figureVolume) {
        this.figureVolume = figureVolume;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}

package com.example.myapplication.Models;

import java.util.ArrayList;

public class Cotization {
    String id;
    String description;
    double averageCost;
    ArrayList<MeasurePoint> width;
    ArrayList<MeasurePoint> height;

    public Cotization(String id, String description, double averageCost, ArrayList<MeasurePoint> width, ArrayList<MeasurePoint> height) {
        this.id = id;
        this.description = description;
        this.averageCost = averageCost;
        this.width = width;
        this.height = height;
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

    public ArrayList<MeasurePoint> getWidth() {
        return width;
    }

    public void setWidth(ArrayList<MeasurePoint> width) {
        this.width = width;
    }

    public ArrayList<MeasurePoint> getHeight() {
        return height;
    }

    public void setHeight(ArrayList<MeasurePoint> height) {
        this.height = height;
    }
}

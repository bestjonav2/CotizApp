package com.example.myapplication.Models;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;

public class MeasurePoint {
    private AnchorNode anchorNode;
    private Anchor anchor;
    private double distanceToLastPoint;

    public MeasurePoint(AnchorNode anchorNode, Anchor anchor, double distance) {
        this.anchor = anchor;
        this.anchorNode = anchorNode;
        this.distanceToLastPoint = distance;
    }

    public AnchorNode getAnchorNode() {
        return anchorNode;
    }

    public Anchor getAnchor() {
        return anchor;
    }

    public double getDistanceToLastPoint() {
        return distanceToLastPoint;
    }

    public void setDistanceToLastPoint(double distance) {
        this.distanceToLastPoint = distance;
    }
}

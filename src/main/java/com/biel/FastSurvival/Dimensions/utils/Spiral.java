package com.biel.FastSurvival.Dimensions.utils;

import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Spiral {
    Function<Double, Double> radius;
    int maxHeight;
    double verticalStep, angularStep;
    boolean direction;
    Function<Double, Double> verticalAcc;
    double height = 0;
    double angle = 0;

    public Spiral(double angle, Function<Double, Double> radius, int maxHeight, double verticalStep, boolean direction, Function<Double, Double> verticalAcc) {
        this.radius = radius;
        this.maxHeight = maxHeight;
        this.verticalStep = verticalStep;
        this.angularStep = angularStep;
        this.verticalAcc = verticalAcc;
        this.angle = angle;
        this.direction = direction;
    }

    public Vector next() {
        Double r = radius.apply(height);
        Vector vector = new Vector(
                Math.sin(angle) * r,
                height,
                Math.cos(angle) * r
        );
        height += verticalStep;
        angle += Math.asin(1.2/r) * (direction ? 1 : -1);
        verticalStep += verticalAcc.apply(height);

        return vector;
    }

    public List<Vector> nextTo(int toHeight) {
        List<Vector> vectors = new ArrayList<>();
        while (height < toHeight) {
            vectors.add(next());
        }
        return vectors;
    }

    public List<Vector> nextToEnd() {
        return nextTo(maxHeight);
    }

    public boolean hasNext() {
        return height < maxHeight;
    }
}
package com.biel.FastSurvival.Dimensions.Sky.hexgen;

import org.bukkit.util.Vector;
import org.joml.Vector3f;

public final class HexMetrics {
    public static final float outerRadius = 18f;
    public static final float innerRadius = outerRadius * 0.866025404f;
    public static final float vertDist = (float) Math.sqrt(Math.pow(2 * innerRadius, 2) - Math.pow(innerRadius, 2));
    public static final Vector[] corners = {
            new Vector(0f, 0f, outerRadius),
            new Vector(innerRadius, 0f, 0.5f * outerRadius),
            new Vector(innerRadius, 0f, -0.5f * outerRadius),
            new Vector(0f, 0f, -outerRadius),
            new Vector(-innerRadius, 0f, -0.5f * outerRadius),
            new Vector(-innerRadius, 0f, 0.5f * outerRadius)
    };
}

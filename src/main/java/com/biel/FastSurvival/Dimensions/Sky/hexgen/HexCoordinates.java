package com.biel.FastSurvival.Dimensions.Sky.hexgen;

import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HexCoordinates {
    public int x;
    public int z;

    public HexCoordinates(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int getY() {
        return -x - z;
    }

    public Vector getCenter() {
        return getCenter(0);
    }
    public Vector getCenter(int y) {
        int cx = Math.round((((x + 0.0f) * 2) + z) * HexMetrics.innerRadius);
        int cz = Math.round((z + 0.0f) * HexMetrics.vertDist);
        return new Vector(cx, y, cz);
    }

    public List<Vector> getCorners() {
        Vector center = getCenter();
        return Arrays.asList(HexMetrics.corners).stream().map(v -> {
            return v.clone().add(center);
        }).collect(Collectors.toList());
    }

    public static HexCoordinates fromOffsetCoordinates(int x, int z) {
        return new HexCoordinates(x - z / 2, z);
    }

    public static HexCoordinates fromPosition(Vector position) {
        return fromPosition(new Vector3i(position.getBlockX(), position.getBlockY(), position.getBlockZ()));
    }

    public static HexCoordinates fromPosition(Vector3i position) {
        float x = position.x / (HexMetrics.innerRadius * 2f);
        float y = -x;
        float offset = position.z / (HexMetrics.outerRadius * 3f);
        x -= offset;
        y -= offset;
        int iX = Math.round(x);
        int iY = Math.round(y);
        int iZ = Math.round(-x - y);

        if (iX + iY + iZ != 0) {
//            System.out.println("Rounding error");
            float dX = Math.abs(x - iX);
            float dY = Math.abs(y - iY);
            float dZ = Math.abs(-x - y - iZ);

            if (dX > dY && dX > dZ) {
                iX = -iY - iZ;
            } else if (dZ > dY) {
                iZ = -iX - iY;
            }
        }

        return new HexCoordinates(iX, iZ);
    }


    @Override
    public String toString() {
        return Integer.toString(x) + ", " + Integer.toString(z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HexCoordinates that = (HexCoordinates) o;
        return x == that.x &&
                z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }
}

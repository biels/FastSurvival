package com.biel.FastSurvival.Dimensions.Moon;

import org.bukkit.*;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class ClaySpiralPopulator extends BlockPopulator {
    World world;
    Random random;
    Chunk chunk;
    Location center;

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        if (!(random.nextInt(110) <= 1)) return;
        this.world = world;
        this.random = random;
        this.chunk = chunk;
        double toRadians = Math.PI / 180;
        int centerX = (chunk.getX() << 4) + random.nextInt(16);
        int centerZ = (chunk.getZ() << 4) + random.nextInt(16);
        int centerY = world.getHighestBlockYAt(centerX, centerZ);
        center = new Location(world, centerX, centerY, centerZ);

        populateSpiral( 0 * toRadians, h -> 5.0 + h * 0, 50,  0.6, false, h -> 0.001, 1000, Material.STAINED_GLASS, DyeColor.WHITE.getWoolData());
        populateSpiral(180 * toRadians,  h -> 8.0 + h * 0, 50,  0.6, true, h -> 0.01, 1000, Material.STAINED_GLASS, DyeColor.LIGHT_BLUE.getWoolData());
        populateSpiral( 90 * toRadians, h -> 12.0 + h * 0, 50,  0.6, false, h -> 0.01, 1000, Material.STAINED_GLASS, DyeColor.ORANGE.getWoolData());
        populateSpiral(270 * toRadians,  h -> 17.0 + h * 0, 50,  0.6, true, h -> 0.02, 1000, Material.BONE_BLOCK, (byte)0);
       // populateSpiral( h -> 12.0 + h * 0.05, 50,  0.2,  3 * toRadians, h -> 0.0, h -> -0.001 * toRadians, 1000, Material.CONCRETE, DyeColor.CYAN.getWoolData());
        //populateSpiral( h -> 20.0, 50, 0.8, -4 * toRadians, h -> 0.0, h ->  0.001 * toRadians, 1000, Material.CONCRETE, DyeColor.CYAN.getWoolData());

    }

    public void populateSpiral(double angle, Function<Double, Double> radius, int maxHeight, double verticalStep, boolean direction,
                               Function<Double, Double> verticalAcc,
                               int fillChance, Material material, byte data) {
        Spiral spiral = new Spiral(angle, radius, maxHeight, verticalStep, direction, verticalAcc);
        spiral.nextToEnd().stream()
                .filter(vector -> random.nextInt(1000) < fillChance)
                .map(vector -> center.clone().add(vector))
                .map(Location::getBlock)
                .forEach(block -> {
                    block.setType(material);
                    //block.setMetadata("spiral", );
                    block.setData(data);
                });
    }


    static class Spiral {
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
}

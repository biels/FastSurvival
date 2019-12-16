package com.biel.FastSurvival.Dimensions.Moon;

import com.biel.FastSurvival.Dimensions.utils.Spiral;
import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.*;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;
import java.util.function.Function;

public class ClaySpiralPopulator extends BlockPopulator {
    World world;
    Random random;
    Chunk chunk;
    Location center;

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        if (!(random.nextInt(1000) <= 1)) return;
        this.world = world;
        this.random = random;
        this.chunk = chunk;
        double toRadians = Math.PI / 180;
        int centerX = (chunk.getX() << 4) + random.nextInt(16);
        int centerZ = (chunk.getZ() << 4) + random.nextInt(16);
        int centerY = world.getHighestBlockYAt(centerX, centerZ);
        center = new Location(world, centerX, centerY, centerZ);
        double radius = Utils.NombreEntre(5,10);
        double height = Utils.Possibilitat(2) ? Utils.NombreEntre(20, 50) : Utils.NombreEntre(10, 18) + radius;
        int number = Utils.NombreEntre(2,4);
        if(radius < 6) number--;
        double constant = Math.sqrt(height) / radius;
        for (int i = 0; i < number; i++) {
            populateSpiral( i * (360 / number) * toRadians, h -> radius, (int) height,  0.6, false, h -> 0.001, 1000, Material.LEGACY_STAINED_CLAY, DyeColor.WHITE.getWoolData());
        }
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
                    //block.setData(data);
                });
    }



}

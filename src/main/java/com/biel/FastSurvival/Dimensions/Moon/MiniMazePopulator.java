package com.biel.FastSurvival.Dimensions.Moon;

import com.biel.FastSurvival.Utils.Cuboid;
import com.biel.FastSurvival.Utils.SimpleMazeGenerator;
import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.stream.Collector.Characteristics.UNORDERED;

public class MiniMazePopulator extends BlockPopulator {

    @Override
    public void populate(World world, Random random, Chunk source) {
        if (!(random.nextInt(120) <= 1)) {
            return;
        }
        int centerX = (source.getX() << 4) + random.nextInt(16);
        int centerZ = (source.getZ() << 4) + random.nextInt(16);
        int centerY = world.getHighestBlockYAt(centerX, centerZ) - 1;
        Location start = new Location(world, centerX, centerY, centerZ);
//        Bukkit.broadcastMessage(start.toString());

        int sideLength = Utils.NombreEntre(6, 10) * 2;
        Vector relCenter = new Vector(sideLength / 2.0, 0, sideLength / 2.0);
        Cuboid floorCuboid = Utils.getCuboidAround(start.clone().add(relCenter), sideLength / 2, 0, sideLength / 2);
        double variance = Utils.getCornerHeightVariance(floorCuboid);
        double y = Math.floor(Utils.getCornerHeightAverage(floorCuboid) - 0.4);
        start.setY(y);
        Location center = start.clone().add(relCenter);
        if(Math.sqrt(variance) > 0.5) return;
//        Bukkit.broadcastMessage(y + ", var: " + variance + ", stdev: " + Math.sqrt(variance));
        Cuboid airCuboid = Utils.getCuboidAround(center.clone().add(0, 2, 0), sideLength / 2, 1, sideLength / 2);

        airCuboid
                .forEach(block -> block.setType(Material.AIR));
        SimpleMazeGenerator generator = new SimpleMazeGenerator();
        generator.generateMaze(sideLength);
        generator.build(start, 1, 1,
//                Material.LIGHT_GRAY_CONCRETE, Material.WHITE_CONCRETE,
                Material.WHITE_TERRACOTTA, Material.LIGHT_GRAY_CONCRETE,
                Material.YELLOW_CONCRETE, Material.GREEN_CONCRETE);
    }
}

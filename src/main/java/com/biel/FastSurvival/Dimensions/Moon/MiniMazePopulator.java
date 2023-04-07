package com.biel.FastSurvival.Dimensions.Moon;

import com.biel.FastSurvival.Utils.Cuboid;
import com.biel.FastSurvival.Utils.SimpleMazeGenerator;
import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.stream.Collector.Characteristics.UNORDERED;

public class MiniMazePopulator extends BlockPopulator {

    @Override
    public void populate(World world, Random random, Chunk source) {
        if (!(random.nextInt(121) <= 1)) {
            return;
        }
        int centerX = (source.getX() << 4) + random.nextInt(16);
        int centerZ = (source.getZ() << 4) + random.nextInt(16);
        int centerY = world.getHighestBlockYAt(centerX, centerZ) - 1;
        Location start = new Location(world, centerX, centerY, centerZ);
        if(start.getBlock().getType() == Material.LIME_STAINED_GLASS)  return;
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
                Material.WHITE_CONCRETE_POWDER, Material.LIGHT_GRAY_CONCRETE,
                Material.YELLOW_CONCRETE, Material.GREEN_CONCRETE);

        // Place a chest below the start point containing abandoned maze items
        Location chestLocation = start.clone().add(0, -1, 0);
        chestLocation.getBlock().setType(Material.CHEST);
        Chest chest = (Chest) chestLocation.getBlock().getState();
        chest.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
        chest.getInventory().addItem(new ItemStack(Material.COOKED_CHICKEN, 10));
        chest.getInventory().addItem(new ItemStack(Material.COOKED_MUTTON, 10));

        if(Utils.Possibilitat(20)){
            chest.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 3));
            chest.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 9));
            chest.getInventory().addItem(new ItemStack(Material.DIAMOND, 1));
            chest.getInventory().addItem(new ItemStack(Material.EMERALD, 1));
        }
        if(Utils.Possibilitat(10)){
            chest.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 1));
            chest.getInventory().addItem(new ItemStack(Material.GOLDEN_CARROT, 1));
            chest.getInventory().addItem(new ItemStack(Material.GOLDEN_SWORD, 1));
        }
        if(Utils.Possibilitat(5)){
            chest.getInventory().addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1));
        }

    }
}

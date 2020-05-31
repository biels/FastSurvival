package com.biel.FastSurvival.Dimensions.Moon;

import com.biel.FastSurvival.Utils.Utils;
import jdk.nashorn.internal.ir.Block;
import org.bukkit.*;
import org.bukkit.util.Vector;

import java.util.Locale;
import java.util.Random;

public class RocketPopulator {
    public void populate(World world, Random random, Chunk source) {
        if (!(random.nextInt(90) <= 1)) {
            return;
        }
    }
    Vector up = new Vector(0, 0, 1).normalize();
    Vector side = Utils.getVectorInPlaneY(up, new Vector(0,0,1));
    int height = 50;
    int roofHeight = 12;
    int cutBodyPart = 4;

    public double getWidthAt(int y) {
        return Math.sin(y / (height / Math.PI)) * 7.0;
    }

    public void generateMid(Location center) {
            Location cylCenter = center.clone();
        for (int i = 0; i < height - roofHeight; i++) {
            cylCenter.add(up);
            if (i >= cutBodyPart) {
//                Bukkit.broadcastMessage("side:" + side);
                Vector r = side.clone().normalize().multiply(getWidthAt(i));
                Utils.getCylBlocks(cylCenter, (int) r.length(), 1, false, up).forEach(block -> {
                    block.setType((((block.getX()) + (block.getY()) + (block.getZ()))) % 2 == 0 ? Material.WHITE_CONCRETE : Material.RED_CONCRETE);
//                    Bukkit.broadcastMessage(String.valueOf(block.getY()));
//                    Bukkit.broadcastMessage("Radius: " + side);
                });
            }
        }
    }
    public void generateRoof(Location center) {
            Location cylCenter = center.clone();
        for (int i = height - roofHeight; i < height; i++) {
            cylCenter.add(up);
            Vector r = side.clone().multiply(getWidthAt(i));
            Utils.getCylBlocks(cylCenter, (int) r.length(), 1, false, up).forEach(block ->
                    block.setType(Material.RED_CONCRETE));
        }
    }

    public void generate(Location center) {
        center = center.toVector().toBlockVector().add(new Vector(0.5, 0.5, 0.5)).toLocation(center.getWorld());
        generateMid(center);
        generateRoof(center.clone().add(up.clone().multiply(height - roofHeight)));
        center.getBlock().setType(Material.DIAMOND_BLOCK);
//        Utils.getCylBlocks(center, 3,1,false, up).forEach(block -> block.setType(Material.GOLD_BLOCK));
    }
}

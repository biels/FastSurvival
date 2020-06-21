package com.biel.FastSurvival.Dimensions.Moon;

import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.*;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.Random;

public class RocketPopulator extends BlockPopulator {

    @Override
    public void populate(World world, Random random, Chunk source) {
        if (!(random.nextInt(1200) <= 1)) {
            return;
        }
        int centerX = (source.getX() << 4) + random.nextInt(16);
        int centerZ = (source.getZ() << 4) + random.nextInt(16);
        int centerY = world.getHighestBlockYAt(centerX, centerZ) - 1;
        Location start = new Location(world, centerX, centerY, centerZ);
        generate(start);
    }
    double toRadians = 180 / Math.PI;
    Vector up = new Vector(0, 1, 0).normalize();
    Vector side = Utils.getVectorInPlaneY(up, new Vector(0, 0, 1));
    int height = 50;
    int roofHeight = 12;
    int BodyBottom = 10;
    int cutBodyPart = 4;
    int legNum = 3;

    public double getWidthAt(int y) {
        return Math.sin(y / (height / Math.PI)) * 6.9;
    }

    public Material getBodyColorAt(Location location, Location cylCenter) {
        Vector vector = Utils.CrearVector(cylCenter, location);
        int angle = (int) (side.angle(vector) * toRadians);
        int y = (int) location.getY();
//        Bukkit.broadcastMessage("center: " + cylCenter + "location: " + location);
        return (((y / 2) + (angle / 24)) % 2 == 1) ? Material.WHITE_CONCRETE : Material.RED_CONCRETE;
    }

    public void generateMid(Location center) {
        Location cylCenter = center.clone();
        for (int i = 0; i < height - roofHeight; i++) {
            cylCenter.add(up);
            Vector r = side.clone().normalize().multiply(getWidthAt(i));
            if (i >= cutBodyPart + BodyBottom) {
                Utils.getCylBlocks(cylCenter, (int) r.length(), 1, false, up).forEach(block -> {
                    block.setType(getBodyColorAt(block.getLocation(), cylCenter));
                });
            } else if (i > cutBodyPart) {
                Utils.getCylBlocks(cylCenter, (int) r.length(), 1, false, up).forEach(block -> {
                    block.setType(Material.RED_CONCRETE);
                });
            }
            if (i % 5 == 0 && i > 8){
                Utils.getCylBlocks(cylCenter, (int) r.length() - 1, 1, true, up).forEach(block -> {
                    block.setType(Material.BIRCH_PLANKS);
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

    public void generateLegs(Location center) {
        Location loc = center.clone().add(up.clone().multiply(cutBodyPart));
        int p1Y = 4;
        int p2Y = 8;
        int p3Y = -10;
        int p3X = 12;
//        Utils.drawTriangle(loc.clone(), loc.clone().add(0, 10, 5), loc.clone().add(0, -10, 5)).forEach(l -> {
//            l.getBlock().setType(Material.REDSTONE_BLOCK);});
        Vector sideVector = side.clone();
        for (int i = 0; i < legNum; i++) {
            Utils.drawTriangle(loc.clone().add(up.clone().multiply(p1Y)).add(sideVector.clone().multiply(getWidthAt(p1Y)))
                    , loc.clone().add(up.clone().multiply(p2Y)).add(sideVector.clone().multiply(getWidthAt(p2Y)))
                    , loc.clone().add(up.clone().multiply(p3Y)).add(sideVector.clone().multiply(p3X))).forEach(l -> {
                l.getBlock().setType(Material.RED_CONCRETE);
            });
            sideVector.rotateAroundAxis(up, (360/ legNum) / toRadians);
//            Utils.getLineBetween(loc.clone().add(up.clone().multiply(p1Y)).add(sideVector.clone().multiply(getWidthAt(p1Y)))
//                    , loc.clone().add(up.clone().multiply(p3Y)).add(sideVector.clone().multiply(10))).forEach(v -> {
//                        Utils.getSphereLocations(v.toLocation(Objects.requireNonNull(loc.getWorld())), 2.0, false)
//                        .forEach(l -> l.getBlock().setType(Material.RED_CONCRETE));
//            });

        }
    }

    public void generate(Location location) {
        location = location.toVector().toBlockVector().toLocation(location.getWorld());
//        location.getBlock().setType(Material.DIAMOND_BLOCK);
        Location center = location.clone().add(up.clone().multiply(10));
//        center.getBlock().setType(Material.GOLD_BLOCK);
        generateMid(center);
        generateRoof(center.clone().add(up.clone().multiply(height - roofHeight)));
        generateLegs(center);
//        Utils.getCylBlocks(location, 3,1,false, up).forEach(block -> block.setType(Material.GOLD_BLOCK));
    }
}

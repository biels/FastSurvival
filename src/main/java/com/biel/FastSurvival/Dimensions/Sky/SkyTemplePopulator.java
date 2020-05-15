package com.biel.FastSurvival.Dimensions.Sky;

import com.biel.FastSurvival.SpecialItems.Items.UndeadProofLifeStealerItem;
import com.biel.FastSurvival.Utils.Cuboid;
import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Lantern;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SkyTemplePopulator extends BlockPopulator {
    int xwidth = 34;
    int zwidth = 20;

    @Override
    public void populate(@NotNull World world, @NotNull Random random, @NotNull Chunk source) {

    }

    public static List<Vector> getColumnVectors(Vector corner, Vector up, Vector front, int xw, int zw) {
        ArrayList<Vector> l = new ArrayList<>();
        up = up.normalize();
        Vector down = up.clone().multiply(-1);
        front = front.normalize();
        //Vector corner = center.clone().add(new Vector(-1 * xw / 2, 0, -1 * zw / 2));
        Vector direction = front.clone().crossProduct(down).normalize().multiply(-7);
        Vector current = corner.clone();
        for (int i = 0; i < 4; i++) {
            int w = i % 2 == 0 ? xw : zw;
            for (int j = 0; j < w; j++) {
                current.add(direction);
                l.add(current.clone());
            }
            direction = direction.crossProduct(down);
        }
        return l.stream().distinct().collect(Collectors.toList());
    }

    public void generateColumn(Location loc, int width, Boolean bottomStairs) {
        Cuboid columnBlocks = new Cuboid(loc).expand(Cuboid.CuboidDirection.Up, 10).expand(Cuboid.CuboidDirection.East, width - 1).expand(Cuboid.CuboidDirection.North, width - 1);
        columnBlocks.getBlocks().forEach(block -> block.setType(Material.QUARTZ_PILLAR));
        AtomicInteger stairWallIndex = new AtomicInteger();
        List<BlockFace> directions = Stream.of(BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH).collect(Collectors.toList());
        List<BlockFace> diagonalDirections = Stream.of(BlockFace.NORTH_EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST, BlockFace.NORTH_WEST).collect(Collectors.toList());
        Cuboid floorStairsBlocks = new Cuboid(loc.add(1, 1, 1)).expand(Cuboid.CuboidDirection.East, width + 1).expand(Cuboid.CuboidDirection.North, width + 1);
        if (bottomStairs) {
            floorStairsBlocks.getWalls().forEach(wall -> {
                wall.getBlocks().forEach(block -> {
                    block.setType(Material.QUARTZ_STAIRS);
                    Directional directional = (Directional) block.getBlockData();
                    directional.setFacing(directions.get(stairWallIndex.get()));
                    block.setBlockData(directional, true);
                });
                stairWallIndex.getAndIncrement();
            });
            stairWallIndex.set(0);
            floorStairsBlocks.upCorners().forEach(corner -> {
                Stairs stairs = (Stairs) corner.getBlockData();
                stairs.setShape(stairWallIndex.get() == 2 ? Stairs.Shape.OUTER_RIGHT : Stairs.Shape.OUTER_LEFT);
                stairWallIndex.getAndIncrement();
            });
        }

        Cuboid ceilStairsBlocks = floorStairsBlocks.clone().shift(Cuboid.CuboidDirection.Up, 9);
        stairWallIndex.set(0);
        ceilStairsBlocks.getWalls().forEach(wall -> {
//            ceilStairsBlocks.corners()[stairWallIndex.get()].setType(Material.GOLD_BLOCK);
            wall.getBlocks().forEach(block -> {
                block.setType(Material.QUARTZ_STAIRS);
                Stairs stairs = (Stairs) block.getBlockData();
                stairs.setFacing(directions.get(stairWallIndex.get()));
                stairs.setHalf(Bisected.Half.TOP);
                block.setBlockData(stairs);
            });
            stairWallIndex.getAndIncrement();
        });
        stairWallIndex.set(0);
    }

    public void generateHangingLantern(Location center, boolean hanging) {
        center.getBlock().setType(Material.IRON_BARS);
        if (hanging) {
            Material lanternMaterial = Utils.Possibilitat(70) ? Material.LANTERN : Material.SEA_LANTERN;
            Block block = center.add(0, -1, 0).getBlock();
            block.setType(lanternMaterial);
            if (lanternMaterial == Material.LANTERN) {
                Lantern lantern = (Lantern) block.getBlockData();
                lantern.setHanging(true);
                block.setBlockData(lantern);
            }
            if (Utils.Possibilitat(50)) block.setType(Material.IRON_BARS);
        } else {
            center.add(0, 1, 0).getBlock().setType(Material.LANTERN);
        }
    }

    public void generateRoof(Location center, int height) {
        for (int i = 0; i < height; i++) {
            Cuboid currentLayer = Utils.getCuboidAround(center.clone().add(0, i, 0), xwidth, 0, zwidth)
                    .expand(Cuboid.CuboidDirection.East, -i).expand(Cuboid.CuboidDirection.West, -i);
            currentLayer.getBlocks().forEach(block -> block.setType(Material.QUARTZ_PILLAR));
            Cuboid currentStairLayer = currentLayer.clone()
                    .expand(Cuboid.CuboidDirection.East, 1).expand(Cuboid.CuboidDirection.West, 1);
            currentStairLayer.getFaces(Cuboid.CuboidDirection.East, Cuboid.CuboidDirection.West).forEach(wall -> wall.getBlocks().forEach(block -> block.setType(Material.CHISELED_QUARTZ_BLOCK)));
            if (i == height - 1)
                currentLayer.expand(Cuboid.CuboidDirection.North, -1).expand(Cuboid.CuboidDirection.East, -1)
                        .expand(Cuboid.CuboidDirection.South, -1).expand(Cuboid.CuboidDirection.West, -1)
                        .getBlocks().forEach(block -> block.setType(Material.QUARTZ_BLOCK));
        }
    }

    public void generateDiagonalBlock(Location center, BlockFace direction) {
        Bukkit.broadcastMessage("direction:" + direction.toString());
        List<Vector> columnVectors = new ArrayList<>();
        Vector column1 = (new Vector(5, 0, 4));
        Vector column2 = (new Vector(4, 0, 5));
        Vector column3 = (new Vector(7, 0, 4));
        Vector column4 = (new Vector(6, 0, 6));
        Vector column5 = (new Vector(4, 0, 7));
        columnVectors.add(column1);
        columnVectors.add(column2);
        columnVectors.add(column3);
        columnVectors.add(column4);
        columnVectors.add(column5);
        int rotationIndex = 0;
        switch (direction) {
            case NORTH_WEST:
                rotationIndex = -2;
                break;
            case NORTH_EAST:
                rotationIndex = -1;
                break;
            case SOUTH_WEST:
                rotationIndex = 0;
                break;
            case SOUTH_EAST:
                rotationIndex = 1;
                break;
        }
        int finalRotationIndex = rotationIndex;
        columnVectors.forEach(vector -> {
            vector.rotateAroundY((Math.PI / 2) * finalRotationIndex);
            Location columnCenter = vector.clone().add(center.toVector()).toLocation(center.getWorld());
            generateColumn(columnCenter, 1, false);
        });
    }

    public void generateAltar(Location center) {
        for (int i = 0; i < 9; i++) {
            if (i + 1 == 1 || i + 1 == 3 || i + 1 == 7 || i + 1 == 9) {
                if (i + 1 == 1) generateDiagonalBlock(center, BlockFace.NORTH_WEST);
                if (i + 1 == 3) generateDiagonalBlock(center, BlockFace.NORTH_EAST);
                if (i + 1 == 7) generateDiagonalBlock(center, BlockFace.SOUTH_WEST);
                if (i + 1 == 9) generateDiagonalBlock(center, BlockFace.SOUTH_EAST);
            }
        }
        center.getBlock().setType(Material.GOLD_BLOCK);
    }

    public void generate(Location center) {
        Cuboid floor = Utils.getCuboidAround(center, xwidth, 0, zwidth);
        floor.clone().shift(Cuboid.CuboidDirection.Up, 1).getBlocks().forEach(block -> {
            if (Utils.Possibilitat(1, 200)) {
                generateHangingLantern(block.getLocation(), false);
            }
        });
        floor.clone().shift(Cuboid.CuboidDirection.Up, 10).getBlocks().forEach(block -> {
            if (Utils.Possibilitat(1, 300)) {
                generateHangingLantern(block.getLocation(), true);
            }
        });
        generateRoof(center.clone().add(0, 11, 0), 6);
        Cuboid outerStairs = floor.clone().expand(Cuboid.CuboidDirection.North, 1).expand(Cuboid.CuboidDirection.East, 1).expand(Cuboid.CuboidDirection.South, 1).expand(Cuboid.CuboidDirection.West, 1);
        outerStairs.getBlocks().forEach(block -> block.setType(Material.CHISELED_QUARTZ_BLOCK));
        floor.getBlocks().forEach(block -> block.setType(Material.QUARTZ_BLOCK));
        getColumnVectors(floor.corners()[0].getLocation().toVector().clone().add(new Vector(3, 0, 3)), new Vector(0, 1, 0), new Vector(1, 0, 0), floor.getSizeZ() / 7, floor.getSizeX() / 7).forEach(vector -> {
            generateColumn(vector.toLocation(center.getWorld()), 5, true);
        });
        generateAltar(center);
    }
}

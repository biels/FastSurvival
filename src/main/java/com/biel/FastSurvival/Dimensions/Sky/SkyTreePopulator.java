package com.biel.FastSurvival.Dimensions.Sky;

import com.biel.FastSurvival.Utils.Cuboid;
import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.generator.BlockPopulator;

import java.util.List;
import java.util.Random;

public class SkyTreePopulator extends BlockPopulator {

    public void populate(World world, Random random, Chunk source) {
        if (!(random.nextInt(90) <= 1)) {
            return;
        } //495
        int centerX = (source.getX() << 4) + random.nextInt(16);
        int centerZ = (source.getZ() << 4) + random.nextInt(16);
        int centerY = world.getHighestBlockYAt(centerX, centerZ);
        Location center = new Location(world, centerX, centerY, centerZ);
        Block bCenter = center.getBlock().getRelative(BlockFace.DOWN);
        Material bCenterType = bCenter.getType();
        bCenter.setType(Material.DIRT);
        boolean gold = Utils.Possibilitat(10);
        boolean generated = world.generateTree(center, TreeType.ACACIA);
        if (generated) {
            Cuboid cuboidAround = Utils.getCuboidAround(center, 7);
            cuboidAround = cuboidAround.expand(Cuboid.CuboidDirection.Up, 22);
            cuboidAround.getBlocks().forEach(b -> {
                if (b.getType() == Material.ACACIA_LEAVES) {
                    b.setType(gold ? Material.GOLD_BLOCK : Material.WHITE_STAINED_GLASS);
                }
                if (b.getType() == Material.ACACIA_LOG) {
                    b.setType(Material.QUARTZ_PILLAR);
                }
            });
        }
        // TODO Add other types of trees
        bCenter.setType(bCenterType);
    }
}
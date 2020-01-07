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
        if (!(random.nextInt(100) <= 1)) {
            return;
        } //495
        int centerX = (source.getX() << 4) + random.nextInt(16);
        int centerZ = (source.getZ() << 4) + random.nextInt(16);
        int centerY = world.getHighestBlockYAt(centerX, centerZ);
        Location center = new Location(world, centerX, centerY, centerZ);
        Block bCenter = center.getBlock().getRelative(BlockFace.DOWN);
        Material bCenterType = bCenter.getType();
        bCenter.setType(Material.DIRT);
        boolean generated = world.generateTree(center, TreeType.ACACIA);
        if (generated) {
            Cuboid cuboidAround = Utils.getCuboidAround(center, 7);
            cuboidAround.expand(Cuboid.CuboidDirection.Up, 12);
            cuboidAround.getBlocks().forEach(b -> {
                if (b.getType() == Material.ACACIA_LEAVES) {
                    b.setType(Material.WHITE_STAINED_GLASS);
                }
                if (b.getType() == Material.ACACIA_LOG) {
                    b.setType(Material.QUARTZ_PILLAR);
                }
            });
        }
        bCenter.setType(bCenterType);
    }
}
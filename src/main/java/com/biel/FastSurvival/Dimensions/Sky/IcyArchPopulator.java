package com.biel.FastSurvival.Dimensions.Sky;

import com.biel.FastSurvival.Utils.Cuboid;
import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Snow;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;

public class IcyArchPopulator extends BlockPopulator {

    public void populate(World world, Random random, Chunk source) {
        if (!(random.nextInt(100) <= 1)) {
            return;
        } //495
        int centerX = (source.getX() << 4) + random.nextInt(16);
        int centerZ = (source.getZ() << 4) + random.nextInt(16);
        int centerY = world.getHighestBlockYAt(centerX, centerZ);
        Location center = new Location(world, centerX, centerY, centerZ);
        Block bCenter = center.getBlock().getRelative(BlockFace.DOWN);
        Vector projectile = new Vector(centerX, centerY, centerZ);
        double scale = 0.7;
        Vector velocity = new Vector(1.3 * scale, 2 * scale, 0);
        Vector acceleration = new Vector(0, -0.098 * scale, 0);
        int archLenght = 30;
        ArrayList<Vector> candleOrigins = new ArrayList<>();
        double decay = 0.24 + Utils.NombreEntre(0, 7) / 100.0;
        for (int i = 0; i < archLenght * 5; i++) {
            velocity.add(acceleration);
            projectile.add(velocity);
            double radius = (5.0 - (projectile.getY() - bCenter.getY()) * decay);
            ArrayList<Location> sphereLocations = Utils.getSphereLocations(projectile.toLocation(world), radius, false);
            sphereLocations.forEach(l -> {
                if (l.getY() < bCenter.getY()) return;
                if (Utils.Possibilitat(1, 400)) candleOrigins.add(l.toVector());
                if (Utils.Possibilitat(10)) l.getBlock().setType(Material.PACKED_ICE);
                else l.getBlock().setType(Material.SNOW_BLOCK);
            });

            if (projectile.getBlockY() == bCenter.getY()) break;
        }
        candleOrigins.forEach(v -> {
            for (int j = 0; j < 7; j++) {
                Boolean isAir = v.toLocation(world).getBlock().getType() == Material.AIR;
                if (isAir && v.getBlockY() > bCenter.getY()) {
                    Utils.getLine(v, new Vector(0, -1, 0), Utils.NombreEntre(1, 3)).forEach(vl -> {
                        vl.toLocation(world).getBlock().setType(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
                    });
                    break;
                } else v.add(new Vector(0, -1, 0));
            }
        });
        bCenter.setType(Material.GOLD_BLOCK);
    }
}

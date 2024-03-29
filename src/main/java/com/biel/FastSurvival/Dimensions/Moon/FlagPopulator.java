package com.biel.FastSurvival.Dimensions.Moon;

import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.*;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.generator.BlockPopulator;

import java.util.List;
import java.util.Random;

public class FlagPopulator extends BlockPopulator {
    private static final int FLAG_CHANCE = 8; // Out of 200
    private static final int FLAG_HEIGHT = 3;

    public void populate(World world, Random random, Chunk source) {
        if (random.nextInt(215) > FLAG_CHANCE) return;
        int centerX = (source.getX() << 4) + random.nextInt(16);
        int centerZ = (source.getZ() << 4) + random.nextInt(16);
        int centerY = world.getHighestBlockYAt(centerX, centerZ);
        Location start = new Location(world, centerX, centerY, centerZ);
        if(start.getBlock().getType() == Material.LIME_STAINED_GLASS)  return;

        BlockFace direction = Utils.getRandomFaceNSEW(random);
        Block top = null;
        for (int y = centerY; y < centerY + FLAG_HEIGHT; y++) {
            top = world.getBlockAt(centerX, y, centerZ);
            top.setType(Material.BIRCH_FENCE);
        }

        Block signBlock = top.getRelative(direction);

        if (Utils.Possibilitat(30)){
            if (Utils.Possibilitat(20)){
//                signBlock.setType(Material.BIRCH_WALL_SIGN);
//                BlockState state = signBlock.getState();
//                if (state instanceof Sign) {
//                    Sign sign = (Sign) state;
//                    org.bukkit.block.data.type.Sign data = (org.bukkit.block.data.type.Sign) state.getBlockData();
//
//                    data.setRotation(direction);
//                    sign.setBlockData(data);
//                    String name = "You";
//                    List<Player> players = world.getPlayers();
//                    if (players.size() != 0){
//                        name = players.get(Utils.NombreEntre(0, players.size()-1)).getName();
//                    }
//                    sign.setLine(0, name);
//                    sign.setLine(1, "got to");
//                    sign.setLine(2, "the");
//                    sign.setLine(3, "moon :D");
//                    sign.update(true);
//                }
            }else{
                signBlock.setType(Material.REDSTONE_LAMP);
            }

        }else{
            signBlock.setType(Material.SPAWNER);
            if(signBlock.getState() instanceof CreatureSpawner){
                CreatureSpawner spawner = (CreatureSpawner) signBlock.getState();
                //spawner.setDelay(20 * 3);

                spawner.setSpawnedType(EntityType.ZOMBIE);
                spawner.update();
            }else{
                signBlock.setType(Material.REDSTONE_LAMP);
            }

        }
    }
}
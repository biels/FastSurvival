package com.biel.FastSurvival.TerrainBoss;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class SlimeBossPopulator extends BlockPopulator {

	@SuppressWarnings("deprecation")
	@Override
    public void populate(World world, Random random, Chunk chunk) {
        if (random.nextInt(400) >= 1) { //18
            return;
        }
        
        Bukkit.broadcastMessage("Populated");
        int sizeX = 16;//Utils.NombreEntre(20, 40);
        int sizeY = 6;
        int sizeZ = 16;//Utils.NombreEntre(20, 40);
        int spawnerChance = 400; // Spawners are generated at a chance of 12/400
        Material matWalls = Material.MOSSY_COBBLESTONE;
        Material matFloor = Material.DIRT;
        Material matDecor = Material.DETECTOR_RAIL;

        //Editing is over
        int lengthH = sizeX / 2;
        int heightH = sizeY / 2;
        int widthH = sizeZ / 2;
        int centerX = chunk.getX() * 16 + 8;
        int centerY = 100;//world.getHighestBlockYAt(chunk.getX() * 16 + 8, chunk.getZ() * 16 + 8)+4;
        if(centerY<5)
        	return;
        int centerZ = chunk.getZ() * 16 + 8;
        int minX = centerX - lengthH;
        int maxX = centerX + lengthH;
        int minY = centerY - heightH;
        int maxY = centerY + heightH;
        int minZ = centerZ - widthH;
        int maxZ = centerZ + widthH;

        // Step 1: Cuboid generation around the entire area
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
            	//if(world.getBlockAt(x, y, minZ).getType().getId()!=0)
                world.getBlockAt(x, y, minZ).setType(matWalls);
            	//if(world.getBlockAt(x, y, maxZ).getType().getId()!=0)
                world.getBlockAt(x, y, maxZ).setType(matWalls);
            }
        }

        for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
            	//if(world.getBlockAt(minX, y, z).getType().getId()!=0)
                world.getBlockAt(minX, y, z).setType(matWalls);
            	//if(world.getBlockAt(maxX, y, z).getType().getId()!=0)
                world.getBlockAt(maxX, y, z).setType(matWalls);
            }
        }
//
//        for (int z = minZ; z <= maxZ; z++) {
//            for (int x = minX; x <= maxX; x++) {
//            	//if(world.getBlockAt(x, minY, z).getType().getId()!=0)
//                world.getBlockAt(x, minY, z).setType(matWalls);
//            	//if(world.getBlockAt(x, maxY, z).getType().getId()!=0)
//                world.getBlockAt(x, maxY, z).setType(matWalls);
//            }
//        }
        
        // Step 2: Add netherrack and web noise to walls (XY, YZ)
        minX++; maxX--;
        minY++; maxY--;
        minZ++; maxZ--;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
            	if(world.getBlockAt(x, y, minZ).getType().getId()!=0)
                world.getBlockAt(x, y, minZ).setType(pickDecor(random, matDecor, matWalls));
            	if(world.getBlockAt(x, y, maxZ).getType().getId()!=0)
                world.getBlockAt(x, y, maxZ).setType(pickDecor(random, matDecor, matWalls));
            }
        }

        for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
            	if(world.getBlockAt(minX,y,z).getType().getId()!=0)
                world.getBlockAt(minX, y, z).setType(pickDecor(random, matDecor, matWalls));
            	if(world.getBlockAt(maxX,y,z).getType().getId()!=0)
            	world.getBlockAt(maxX, y, z).setType(pickDecor(random, matDecor, matWalls));
            }
        }

        // Step 3: Generate a floor of soul sand/mobs and a ceiling of netherrack
        for (int z = minZ; z <= maxZ; z++) {
            for (int x = minX; x <= maxX; x++) {
                int floor = random.nextInt(spawnerChance); // spawner rate
                Block block = world.getBlockAt(x, minY, z);
                if(block.getType().getId()!=0)
                	if (floor < 12) {
                        block.setType(Material.LEGACY_MOB_SPAWNER);
                        CreatureSpawner spawner = (CreatureSpawner) block.getState();
                        if (floor <= 2) {
                            spawner.setSpawnedType(EntityType.ZOMBIE);
                        } else if (floor >= 3 && floor <= 7) {
                        	spawner.setSpawnedType(EntityType.SPIDER);
                        } else if (floor >= 8 && floor <= 10) {
                        	spawner.setSpawnedType(EntityType.SKELETON);
                        } else {
                        	spawner.setSpawnedType(EntityType.GHAST);
                        }
                    } else {
                    	if(block.getType().getId()!=0)
                        block.setType(matFloor);
                    }
                if(world.getBlockAt(x, maxY, z).getType().getId()!=0)
                world.getBlockAt(x, maxY, z).setType(matWalls);
            }
        }

        // Step 4: Stalagmites
        minX++; maxX--;
        minY++; maxY--;
        minZ++; maxZ--;
        
        for (int z = minZ; z <= maxZ; z++) {
            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getRelative(BlockFace.DOWN).getType() != Material.AIR && block.getRelative(BlockFace.DOWN).getType() != matDecor) {
                        int rand = random.nextInt(10);
                        if (rand <= 6) {
                        	if(world.getBlockAt(x, y, z).getType().getId()!=0)
                            world.getBlockAt(x, y, z).setType(Material.AIR);
                        } else {
                        	if(world.getBlockAt(x, y, z).getType().getId()!=0)
                            world.getBlockAt(x, y, z).setType(pickDecor(random, matDecor, Material.AIR));
                        }
                    } else {
                    	if(world.getBlockAt(x, y, z).getType().getId()!=0)
                        world.getBlockAt(x, y, z).setType(Material.AIR);
                    }
                }
            }
        }
               
        

      
    }

    private Material pickDecor(Random random, Material decor, Material wall) {
        return (random.nextInt(5) == 0) ? decor : wall;
    }

}

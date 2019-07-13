package com.biel.FastSurvival.OverworldStructures;

import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;

public class TreasurePopulator extends BlockPopulator {

	@SuppressWarnings("deprecation")
	@Override
    public void populate(World world, Random random, Chunk chunk) {
		if (!Utils.Possibilitat(1, 10)) {return;}
        int sizeX = 16;
        int sizeY = 12;
        int sizeZ = 16;


        //Editing is over
        int lengthH = sizeX / 2;
        int heightH = sizeY / 2;
        int widthH = sizeZ / 2;
        int centerX = chunk.getX() * 16 + 8;
        int centerY = world.getHighestBlockYAt(chunk.getX() * 16 + 8, chunk.getZ() * 16 + 8) / 2;
        if(centerY<5)
        	return;
        int centerZ = chunk.getZ() * 16 + 8;
        Boolean chained = random.nextBoolean();
        int MaxLength = 3 + random.nextInt(4);
        int offX = random.nextInt(3);
        Location l1 = new Location(world, centerX  + offX, 60, centerZ);
        int ycomp = world.getHighestBlockYAt(l1);
        Biome b = l1.getBlock().getBiome();
        Boolean snow = false;
		if (!(b == Biome.PLAINS || b == Biome.FOREST || b == Biome.JUNGLE || b == Biome.SAVANNA || b == Biome.DESERT || b == Biome.STONE_SHORE || b == Biome.TAIGA) || b == Biome.ICE_SPIKES || b == Biome.FOREST){
        	return;
        }
		if (b == Biome.SNOWY_TAIGA || b == Biome.ICE_SPIKES){
			snow = true;
		}
		Location lp = new Location(world, centerX  + offX, ycomp, centerZ);
		Material type = lp.getBlock().getRelative(BlockFace.DOWN).getType();
		if (type.isTransparent() || !type.isSolid() || type == Material.LEGACY_LEAVES || type == Material.LEGACY_LEAVES_2){
			return;
		}
		int r = 12;
        Vector up = new Vector(0, 1, 0);
		Vector dir1 = new Vector(random.nextDouble(), 0, random.nextDouble()).normalize();
		Vector dir2 = up.clone().multiply(dir1).clone().normalize();
        Vector ini1 = lp.toVector().add(dir1.clone().multiply(r/2));
        Vector ini2 = lp.toVector().add(dir2.clone().multiply(r/2));
        lp.getBlock().setType(Material.GOLD_BLOCK);
        lp.getBlock().getRelative(BlockFace.UP).setType(Material.GOLD_BLOCK);
		for (int i = 0; i < r; i++) {
            ini1.toLocation(world).getBlock().getRelative(BlockFace.UP).setType(Material.COBBLESTONE);
            ini2.toLocation(world).getBlock().setType(Material.COBBLESTONE);
            ini1.add(dir1);
            ini2.add(dir2);
        }
    }

}

package com.biel.FastSurvival.Dimensions.Moon;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

public class MiniMazePopulator extends BlockPopulator {
	@Override
	public void populate(World world, Random random, Chunk source) {
		if (!(random.nextInt(100) <= 1)) {return;}
		int centerX = (source.getX() << 4) + random.nextInt(16);
		int centerZ = (source.getZ() << 4) + random.nextInt(16);
		int centerY = world.getHighestBlockYAt(centerX, centerZ);
		Location center = new Location(world, centerX, centerY, centerZ);
		center.getBlock().setType(Material.EMERALD_BLOCK);
		
	}
}

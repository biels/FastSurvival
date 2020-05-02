package com.biel.FastSurvival.Dimensions.Sky;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class SkyTemplePopulator extends BlockPopulator {
    @Override
    public void populate(@NotNull World world, @NotNull Random random, @NotNull Chunk source) {

    }
    public void generate (Location center) {
        center.getBlock().setType(Material.GOLD_BLOCK);
    }
}

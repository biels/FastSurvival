package com.biel.FastSurvival.Dimensions.Sky.hexgen;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class CellPopulator extends BlockPopulator {
    private final SkyHexChunkGenerator chunkGenerator;

    public CellPopulator(SkyHexChunkGenerator chunkGenerator) {
        this.chunkGenerator = chunkGenerator;
    }

    @Override
    public void populate(@NotNull World world, @NotNull Random random, @NotNull Chunk source) {
        HexCoordinates coordinates = HexCoordinates.fromPosition(new Vector(source.getX() * 16, 0, source.getZ() * 16));

        if (!coordinates.getCorners().stream().allMatch(corner -> world.isChunkLoaded(corner.getBlockX() / 16, corner.getBlockZ() / 16)))
            return;
        try {
            HexCell cell = chunkGenerator.hexGrid.getCell(coordinates);
            if (cell.populated) return;
            cell.populate(source.getWorld());
//            System.out.println("Populated!");
            cell.populated = true;
        } catch (Exception e) {
            System.out.println("Exception @ populate()");
            e.printStackTrace();
        }
    }
}

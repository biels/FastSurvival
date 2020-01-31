package com.biel.FastSurvival.Dimensions.Sky.hexgen;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.Vector;
import org.joml.Vector3i;

import java.util.*;

public class SkyHexChunkGenerator extends ChunkGenerator {
    InfiniteHexGrid hexGrid;

    public SkyHexChunkGenerator() {
    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int cx, int cz, BiomeGrid biome) {
        if(hexGrid == null) hexGrid = new InfiniteHexGrid(world);
        ChunkData chunk = createChunkData(world);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int x1 = cx * 16 + x;
                int z1 = cz * 16 + z;
                HexCell cell = hexGrid.getCell(HexCoordinates.fromPosition(new Vector3i(x1, 0, z1)));
                Material material;
                material = Material.SNOW_BLOCK;
                int type = cell.type;
                if (type == 0) material = Material.DIRT;
                if (type == 1) material = Material.STONE;
                if (type == 2) material = Material.BIRCH_WOOD;
                if (type == 3) material = Material.IRON_ORE;
//                if(cell.coordinates.getCenter().distanceSquared(new Vector(x1, 0, z1)) < 5) material = Material.GOLD_BLOCK;
                for (int y = 60 + 1; y < 70 + type; y++) {
                    chunk.setBlock(x, y, z, material);
                }
            }
        }
        return chunk;
    }


    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Arrays.asList();
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        int x = random.nextInt(200) - 100;
        int z = random.nextInt(200) - 100;
        int y = world.getHighestBlockYAt(x, z);
        return new Location(world, x, y, z);
    }

    @Override
    public boolean isParallelCapable() {
        return false;
    }
}
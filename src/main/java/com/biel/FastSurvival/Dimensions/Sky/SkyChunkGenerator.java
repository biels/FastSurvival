package com.biel.FastSurvival.Dimensions.Sky;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SkyChunkGenerator extends ChunkGenerator {
    private NoiseGenerator generator;

    private NoiseGenerator getGenerator(World world) {
        if (generator == null) {
            generator = new SimplexNoiseGenerator(world);
        }

        return generator;
    }

    private int getHeight(World world, double x, double y, int variance) {
        NoiseGenerator gen = getGenerator(world);

        double result = gen.noise(x / 3, y / 3);
        result *= variance;
        return NoiseGenerator.floor(result);
    }

    //Unused
    public byte[] generate(World world, Random random, int cx, int cz) {
        byte[] result = new byte[32768];

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int height = getHeight(world, cx + x * 0.0625, cz + z * 0.0625, 4) + 50;
                for (int y = 46; y < height; y++) {
                    result[(x * 16 + z) * 128 + y] = (byte) Material.SNOW_BLOCK.getId();
                }
            }
        }

        return result;
    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int cx, int cz, BiomeGrid biome) {
        ChunkData chunk = createChunkData(world);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int height = getHeight(world, cx + x * 0.0625, cz + z * 0.0625, 4) + 50;
                for (int y = 46; y < height; y++) {
                    if (y != 48) {
                        chunk.setBlock(x, y, z, Material.SNOW_BLOCK);
                    } else {
                        chunk.setBlock(x, y, z, Material.BLUE_ICE);
                    }
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
}
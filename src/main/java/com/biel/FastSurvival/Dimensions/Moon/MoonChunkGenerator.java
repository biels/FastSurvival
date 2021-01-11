package com.biel.FastSurvival.Dimensions.Moon;

import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;

import javax.rmi.CORBA.Util;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MoonChunkGenerator extends ChunkGenerator {
    private NoiseGenerator generator;
    private NoiseGenerator slowGenerator;
    private NoiseGenerator ultraSlowGenerator;

    private NoiseGenerator getGenerator(World world) {
        if (generator == null) {
            generator = new SimplexNoiseGenerator(world);
        }
        return generator;
    }

    private NoiseGenerator getSlowGenerator(World world) {
        if (slowGenerator == null) {
            slowGenerator = new SimplexNoiseGenerator(world.getSeed() + 10);
        }
        return slowGenerator;
    }
    private NoiseGenerator getUltraSlowGenerator(World world) {
        if (ultraSlowGenerator == null) {
            ultraSlowGenerator = new SimplexNoiseGenerator(world.getSeed() + 20);
        }
        return ultraSlowGenerator;
    }

    private int getHeight(World world, double x, double y, double variance, int baseline) {
        NoiseGenerator gen = getGenerator(world);
        NoiseGenerator slowGen = getSlowGenerator(world);
        NoiseGenerator varGen = getSlowGenerator(world);

        double varResult = (varGen.noise(x / 20, y / 20) / 1.8) + 0.8;
        variance = variance * varResult;
        int sigmoidHarshness = 35;
        double slowResult = (slowGen.noise(x / 45, y / 45) - 0.5) * sigmoidHarshness;
        double slowAfterSigmoid = Utils.sigmoid(slowResult);
        double result = 0;
        double effectiveVariance = slowAfterSigmoid < 0.6 ? variance : variance / 2.0;
        result = gen.noise(x, y) * effectiveVariance;

        result += (slowAfterSigmoid * 30);
        result += baseline;

        return NoiseGenerator.floor(result);
    }

//
//    public byte[] generate(World world, Random random, int cx, int cz) {
//        byte[] result = new byte[32768];
//
//        for (int x = 0; x < 16; x++) {
//            for (int z = 0; z < 16; z++) {
//                int height = getHeight(world, cx + x * 0.0625, cz + z * 0.0625, 2, 60);
//                for (int y = 0; y < height; y++) {
//                    result[(x * 16 + z) * 128 + y] = (byte) Material.LEGACY_STAINED_CLAY.getId();
//                }
//            }
//        }
//
//        return result;
//    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int cx, int cz, BiomeGrid biome) {
        ChunkData chunk = createChunkData(world);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int height = getHeight(world, cx + x * 0.0625, cz + z * 0.0625, 2, 60);
                int hardenedHeight = height - 15;
                for (int y = 1; y < hardenedHeight; y++) {
                    chunk.setBlock(x, y, z, MoonUtils.getMoonInnerMaterial());
                }
                for (int y = hardenedHeight; y < height; y++) {
                    chunk.setBlock(x, y, z, MoonUtils.getMoonSurfaceMaterial());
                }
                chunk.setBlock(x, 0, z, Material.BEDROCK);
            }
        }
        return chunk;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Arrays.asList(
                new MoonCraterPopulator(),
                new ElectricBossPopulator(),
                new FlagPopulator(),
                new MoonMagicTreePopulator(),
                new ClaySpiralPopulator(),
//                new ClayColorPopulator(),
                new MiniMazePopulator(),
                new RocketPopulator(),
                new MoonBasePopulator()
        );
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
        return true;
    }
}
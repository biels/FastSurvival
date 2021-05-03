package com.biel.FastSurvival.Dimensions.Moon;

import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.Vector;
import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
    int CHUNKS_IN_SC = 3;
    int SC_BLOCK_WIDTH = CHUNKS_IN_SC * 16;

    @Override
    public ChunkData generateChunkData(World world, Random random, int cx, int cz, BiomeGrid biome) {
        ChunkData chunk = createChunkData(world);
        Vector superChunkVec = getSuperChunkFromChunk(cx, cz);
        List<Vector> pointsWithId = getNeighbourPointsWithId(superChunkVec.getBlockX(), superChunkVec.getBlockZ());
       // Bukkit.broadcastMessage(pointsWithId.stream().map(Vector::getBlockY).map(integer -> Integer.toString(integer)).collect(Collectors.joining(", ")));

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                Material mat = MoonUtils.getMoonSurfaceMaterial();
                double height = 60; //getHeight(world, cx + x * 0.0625, cz + z * 0.0625, 2, 60);
                Vector thisBlock = new Vector(cx * 16 + x, 0, cz * 16 + z);
                for (int i = 0; i < pointsWithId.size(); i++) {
                    // For each point, calc Y offset
                    Vector point = pointsWithId.get(i).clone();
                    int id = point.getBlockY();
//                    System.out.println(String.valueOf(point.getY()));

                    point.setY(0);
                    Random random1 = new Random(id);
                    double r = (biasFunction(random1.nextDouble(), 0.4)) * SC_BLOCK_WIDTH * 2;
//                    System.out.println(id + ", " + r);
                    int type = random1.nextInt(2);


                    double distance = thisBlock.distance(point);
                    double maxElevation = 6.0 * random1.nextDouble();
                    double elevation = maxElevation -  maxElevation * biasFunction((distance) / r, 0.6);
                    if (distance < r) {
//                        Bukkit.broadcastMessage(pointsWithId.stream().map(Vector::toString).collect(Collectors.joining(", ")));
                        mat = Material.STONE;
                        height -= (elevation);
                        if (distance <= 2) {
                            mat = type == 0 ? Material.COAL_BLOCK : Material.GOLD_BLOCK;

                        }
                    }

//                    int yOffset = 0;
                }

                int hardenedHeight = (int) (height - 15);
                for (int y = 1; y < hardenedHeight; y++) {
                    chunk.setBlock(x, y, z, MoonUtils.getMoonInnerMaterial());
                }
                for (int y = hardenedHeight; y < height; y++) {
                    chunk.setBlock(x, y, z, mat);
                }
                chunk.setBlock(x, 0, z, Material.BEDROCK);
            }
        }
        return chunk;
    }

    private double biasFunction(double x, double bias) {
        double k = Math.pow(1.0 - bias, 3);
        return (x * k) / (x * k - x + 1);
    }

    private Vector getSuperChunkFromChunk(int cx, int cz) {
        return new Vector(cx / CHUNKS_IN_SC, 0, cz / CHUNKS_IN_SC);
    }

    private Vector getSuperChunkPointOffset(int scx, int scz) {
        Random r1 = new Random(scx * 100L + scz);
        return new Vector(r1.nextInt(SC_BLOCK_WIDTH), 0, r1.nextInt(SC_BLOCK_WIDTH));
    }

    private Vector getSuperChunkVector(int scx, int scz) {
        return new Vector(scx * SC_BLOCK_WIDTH, 0, scz * SC_BLOCK_WIDTH);
    }

    private int getSuperChunkId(int scx, int scz) {
        int id = scx ^ scz;
        return id;
    }

    @NotNull
    private ArrayList<Vector> getNeighbourPointsWithId(int scx, int scz) {
        ArrayList<Vector> points = new ArrayList<>();
        int n = 2;
        for (int i = -n; i <= n; i++) {
            for (int j = -n; j <= n; j++) {
                int scx1 = scx + i;
                int scz1 = scz + j;
                Vector superChunkVector = getSuperChunkVector(scx1, scz1);
                Vector pointOffset = getSuperChunkPointOffset(scx1, scz1);
                Vector res = superChunkVector.clone().add(pointOffset);
                res.setY(getSuperChunkId(scx1, scz1));
                points.add(res);
            }
        }
        return points;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Arrays.asList(
//                new MoonCraterPopulator(),
//                new ElectricBossPopulator(),
//                new FlagPopulator(),
//                new MoonMagicTreePopulator(),
//                new ClaySpiralPopulator(),
////                new ClayColorPopulator(),
//                new MiniMazePopulator(),
//                new RocketPopulator(),
//                new MoonBasePopulator()
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
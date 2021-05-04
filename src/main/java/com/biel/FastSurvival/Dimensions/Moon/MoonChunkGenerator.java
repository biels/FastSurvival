package com.biel.FastSurvival.Dimensions.Moon;

import com.biel.FastSurvival.Utils.Hashing.LongHashFunction;
import com.biel.FastSurvival.Utils.Noise.InfiniteVoronoiNoise;
import com.biel.FastSurvival.Utils.Noise.InfiniteVoronoiNoise.VoronoiPoint;
import com.biel.FastSurvival.Utils.Utils;
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

    InfiniteVoronoiNoise xlIvn;
    List<InfiniteVoronoiNoise> smallIvns;
    private int SMALL_IVN_COUNT = 10;

    private NoiseGenerator getGenerator(World world) {
        if (generator == null) {
            generator = new SimplexNoiseGenerator(world);
        }
        return generator;
    }

    private InfiniteVoronoiNoise getXLIvn(World world, Random r) {
        if (xlIvn == null) {
            LongHashFunction xx = LongHashFunction.xx(r.nextLong());
            xlIvn = new InfiniteVoronoiNoise(r, 40, xx.hashInt(0));
        }
        return xlIvn;
    }

    private List<InfiniteVoronoiNoise> getSmallIvns(World world, Random r) {
        if (smallIvns == null) {
            LongHashFunction xx = LongHashFunction.xx(r.nextLong());
            smallIvns = new ArrayList<>();
            for (int i = 0; i < SMALL_IVN_COUNT; i++) {
                int i1 = r.nextInt(3);
                i1 = 0;
                smallIvns.add(new InfiniteVoronoiNoise(r, i1 + 1, xx.hashInt(i + 1)));
            }
        }
        return smallIvns;
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

    public double craterFunction(double x) {
        double cavityShape = x*x-1;
        double a = (Math.abs(x) - 1 - 0.5);
        double rimShape = 0.5 * a * a;
        double floorShape = x;
        return Math.max(Math.min(cavityShape, rimShape), floorShape);
    }

    double craterFunctionSmooth(double x) {
        x = x * 1.5;
        double cavityShape = x*x-1.0;
        double a = (Math.abs(x) - 1 - 0.5);
        double rimShape = 0.5 * a * a;
        double floorShape = -0.4;
        double s = 0.14;
        return smin(smin(cavityShape, rimShape, s/2), floorShape, -s) / -floorShape;
    }

    double smin(double a,  double b, double  k) {
        double h = clamp(0.5 + 0.5*(a-b)/k, 0.0, 1.0);
        return mix(a, b, h) - k*h*(1.0-h);
    }

    double clamp(double v, double min,  double max) {
        return Math.min(Math.max(v, min), max);
    };

    double mix(double start, double end, double t) {
        return start * (1 - t) + end * t;
    }


    @Override
    public ChunkData generateChunkData(World world, Random random, int cx, int cz, BiomeGrid biome) {
        ChunkData chunk = createChunkData(world);
        InfiniteVoronoiNoise xlIvn = getXLIvn(world, random);
        List<InfiniteVoronoiNoise> allIvns = getInfiniteVoronoiNoises(world, random);

        List<List<VoronoiPoint>> allPointsWithId = allIvns.stream()
                .map(ivn -> {
                    Vector superChunkVec = ivn.getSuperChunkFromChunk(cx, cz);
                    return ivn.getNeighbourPointsWithId(superChunkVec.getBlockX(), superChunkVec.getBlockZ(), 3);
                }).collect(Collectors.toList());
        // Bukkit.broadcastMessage(allPointsWithId.stream().map(Vector::getBlockY).map(integer -> Integer.toString(integer)).collect(Collectors.joining(", ")));

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                double height = getHeight(world, cx + x * 0.0625, cz + z * 0.0625, 2, 60);
                double offset = 0;
                Material mat = MoonUtils.getMoonSurfaceMaterial();
                boolean matLocked = false;
                Vector thisBlock = new Vector(cx * 16 + x, 0, cz * 16 + z);
                for (int ivnIndex = 0; ivnIndex < allIvns.size(); ivnIndex++) {
                    InfiniteVoronoiNoise ivn = allIvns.get(ivnIndex);
                    // For each crater noise
                    List<VoronoiPoint> points = allPointsWithId.get(ivnIndex);
                    for (int pointIndex = 0; pointIndex < points.size(); pointIndex++) {
                        // For each point, calc Y offset
                        boolean isXL = ivn == xlIvn;
                        VoronoiPoint voronoiPoint = points.get(pointIndex);
                        Vector point = voronoiPoint.vector.clone();
                        long id = voronoiPoint.id;
//                    System.out.println(String.valueOf(point.getY()));


                        Random random1 = new Random(id);
                        int chance = 130; // Out of 1000
                        if (isXL) chance = 900; // Out of 1000
                        if (!isXL) chance = chance / SMALL_IVN_COUNT;
                        if (random1.nextInt(1000) >= chance) continue;
                        double size = (random1.nextDouble() + 0.5) / 2;
                        double r = (ivn.SC_BLOCK_WIDTH / 2.0) * (isXL ? 1 : 1) * size; // (biasFunction(random1.nextDouble(), 0.4)) *
//                    System.out.println(id + ", " + r);
                        int type = random1.nextInt(2);


                        double distance = thisBlock.distance(point);
                        double maxElevation = (isXL ? 70 : 9) * size + 1;

//                        double elevation = maxElevation - maxElevation * biasFunction((distance) / r, 0.6);
                        double elevation = (craterFunctionSmooth( (distance / r))) * -1 * maxElevation ;
                        if (distance < r) {
//                        Bukkit.broadcastMessage(allPointsWithId.stream().map(Vector::toString).collect(Collectors.joining(", ")));
                            if (!matLocked) {
                                if (mat == Material.STONE) {
                                    mat = Material.COBBLESTONE;
                                } else {
                                    mat = Material.STONE;
                                }
                                if (isXL) {
                                    mat = Material.WHITE_CONCRETE;
                                }
                            }

                            if (offset >= 0) offset -= (elevation);
                            else offset = offset - (elevation / 2);
                            if (distance <= 2) {
                                mat = type == 0 ? Material.COAL_BLOCK : Material.GOLD_BLOCK;
                                matLocked = true;
                            }
                        }

//                    int yOffset = 0;
                    }
                }
                height += offset;
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

    @NotNull
    public List<InfiniteVoronoiNoise> getInfiniteVoronoiNoises(World world, Random random) {
        InfiniteVoronoiNoise xlIvn = getXLIvn(world, random);
        List<InfiniteVoronoiNoise> sIvn = getSmallIvns(world, random);

        List<InfiniteVoronoiNoise> allIvns = new ArrayList<>();
        allIvns.add(xlIvn);
        allIvns.addAll(sIvn);
        return allIvns;
    }

    private double biasFunction(double x, double bias) {
        double k = Math.pow(1.0 - bias, 3);
        return (x * k) / (x * k - x + 1);
    }

//    private Vector getSuperChunkFromChunk(int cx, int cz) {
//        return new Vector(cx / CHUNKS_IN_SC, 0, cz / CHUNKS_IN_SC);
//    }
//
//    private Vector getSuperChunkPointOffset(int scx, int scz) {
//        Random r1 = new Random(scx * 100L + scz);
//        return new Vector(r1.nextInt(SC_BLOCK_WIDTH), 0, r1.nextInt(SC_BLOCK_WIDTH));
//    }
//
//    private Vector getSuperChunkVector(int scx, int scz) {
//        return new Vector(scx * SC_BLOCK_WIDTH, 0, scz * SC_BLOCK_WIDTH);
//    }
//
//    private int getSuperChunkId(int scx, int scz) {
//        int id = scx ^ scz;
//        return id;
//    }
//
//    @NotNull
//    private ArrayList<Vector> getNeighbourPointsWithId(int scx, int scz) {
//        ArrayList<Vector> points = new ArrayList<>();
//        int n = 2;
//        for (int i = -n; i <= n; i++) {
//            for (int j = -n; j <= n; j++) {
//                int scx1 = scx + i;
//                int scz1 = scz + j;
//                Vector superChunkVector = getSuperChunkVector(scx1, scz1);
//                Vector pointOffset = getSuperChunkPointOffset(scx1, scz1);
//                Vector res = superChunkVector.clone().add(pointOffset);
//                res.setY(getSuperChunkId(scx1, scz1));
//                points.add(res);
//            }
//        }
//        return points;
//    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Arrays.asList(
                new SampleSharedVoronoiPopulator()
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
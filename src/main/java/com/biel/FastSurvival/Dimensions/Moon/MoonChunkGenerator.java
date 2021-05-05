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
import org.spongepowered.noise.Noise;
import org.spongepowered.noise.NoiseQualitySimplex;
import org.spongepowered.noise.model.Cylinder;
import org.spongepowered.noise.module.source.Cylinders;
import org.spongepowered.noise.module.source.Simplex;

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
    static int SMALL_IVN_COUNT = 10;

    private NoiseGenerator getGenerator(World world) {
        if (generator == null) {
            generator = new SimplexNoiseGenerator(world);
        }
        return generator;
    }

    private InfiniteVoronoiNoise getXLIvn(World world, Random r) {
        if (xlIvn == null) {
            LongHashFunction xx = LongHashFunction.xx(r.nextLong());
            xlIvn = new InfiniteVoronoiNoise(r, 65, xx.hashInt(0));
            xlIvn.isXL = true;
        }
        return xlIvn;
    }

    private List<InfiniteVoronoiNoise> getSmallIvns(World world, Random r) {
        if (smallIvns == null) {
            LongHashFunction xx = LongHashFunction.xx(r.nextLong());
            smallIvns = new ArrayList<>();
            for (int i = 0; i < SMALL_IVN_COUNT; i++) {
                int i1 = r.nextInt(3);
//                i1 = 0;
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

    private double getHeightNoiseFactor(World world, double x, double y, double variance, double hillHeightFactor) {
        NoiseGenerator gen = getGenerator(world);
        NoiseGenerator varGen = getSlowGenerator(world);

        double varResult = (varGen.noise(x / 20, y / 20) / 1.8) + 0.8;
        variance = variance * varResult;


        double result = 0;

        double effectiveVariance = hillHeightFactor < 0.6 ? variance : variance / 2.0; // Top of hill less roughness
        result = gen.noise(x, y) * effectiveVariance; // Noise

        //result += (hillHeightFactor * 30); // Hills


        return result;
    }

    private double getHillHeightFactor(World world, double x, double y) {
        NoiseGenerator slowGen = getSlowGenerator(world);

        int sigmoidHarshness = 35;
        double slowResult = (slowGen.noise(x / 45, y / 45) - 0.5) * sigmoidHarshness;
        double slowAfterSigmoid = Utils.sigmoid(slowResult);
        return slowAfterSigmoid;
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
        double cavityShape = x * x - 1;
        double a = (Math.abs(x) - 1 - 0.5);
        double rimShape = 0.5 * a * a;
        double floorShape = x;
        return Math.max(Math.min(cavityShape, rimShape), floorShape);
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
                int baseline = 80;
                double hillHeightFactor = getHillHeightFactor(world, cx + x * 0.0625, cz + z * 0.0625);
                boolean isHill = hillHeightFactor > 0.6;
                double heightNoiseFactor = getHeightNoiseFactor(world, cx + x * 0.0625, cz + z * 0.0625, 1, hillHeightFactor); // Scaled by variance
                double hillOffset = hillHeightFactor * 35;
                double noiseOffset = heightNoiseFactor * 2;
                double height = baseline;
                double offset = 0;
                double xlOffset = 0;
                double cylOffset = 0;
                Material mat = MoonUtils.getMoonSurfaceMaterial();
                boolean matLocked = false;
                Vector thisBlock = new Vector(cx * 16 + x, 0, cz * 16 + z);
                for (int ivnIndex = 0; ivnIndex < allIvns.size(); ivnIndex++) {
                    InfiniteVoronoiNoise ivn = allIvns.get(ivnIndex);
                    // For each ivn
                    List<VoronoiPoint> points = allPointsWithId.get(ivnIndex);
                    for (int pointIndex = 0; pointIndex < points.size(); pointIndex++) {
                        // For each crater, calc Y offset
                        boolean isXL = ivn == xlIvn;
                        VoronoiPoint voronoiPoint = points.get(pointIndex);
                        Vector point = voronoiPoint.vector.clone();
                        long id = voronoiPoint.id;

                        // Info


                        CraterInfo ci = CraterInfo.fromId(id, voronoiPoint.vector, ivn);

                        if (!ci.generated) continue;
                        double size = 1; //(random1.nextDouble() + 0.5) / 2;
                        int type = ci.type;

                        double r = ci.r; //(ivn.SC_BLOCK_WIDTH / 2.0) * (isXL ? 1 : 1) * size; // (biasFunction(random1.nextDouble(), 0.4)) *


                        // Specific to block
                        double distance = thisBlock.distance(point);
                        double maxElevation = (isXL ? 70 : 9) * size + 1;

//                        double craterOffset = maxElevation - maxElevation * biasFunction((distance) / r, 0.6);
                        double relDist = distance / r;

                        if (distance < r) {
                            // Block is inside crater radius

                            Simplex simplex = new Simplex();
                            simplex.setSeed((int) (id));
                            simplex.setNoiseQuality(NoiseQualitySimplex.SMOOTH);
                            simplex.setFrequency(15.0);
                            Cylinder cylinder = new Cylinder(simplex);
                            float angle = new Vector(0, 0, 1).angle(Utils.CrearVector(point, thisBlock));
                            double rawCylNoise = cylinder.getValue(angle * 180 / Math.PI, 0);
                            rawCylNoise = biasFunction(rawCylNoise, 0.16);
                            double cylNoise = Utils.mix(0, (rawCylNoise + 1), CraterInfo.ridgeLerpFn(relDist));

                            double craterOffset = (CraterInfo.craterFunctionSmooth(relDist)) * maxElevation;
//                            craterOffset += (cylNoise - 0.8) * 1.0;

                            // Materials
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

                            if (relDist > CraterInfo.UP_POINT && ci.isXL) {
                                // Outside area
                                mat = Material.WHITE_CONCRETE;
                                double x1 = 1 - biasFunction(1 - relDist, 0.2);
                                double simplexMaxValue = simplex.getMaxValue();
                                if (Utils.sigmoid(rawCylNoise - 1) * 2 + 1 < (1.4 * Utils.map(relDist, CraterInfo.UP_POINT, 1, 1 - (simplexMaxValue - 1), simplexMaxValue) + 0.0)) { // (1.0 / (1 - CraterInfo.UP_POINT) + CraterInfo.UP_POINT)// from 0.8 to 0.0 in UP_POINT to 1
                                    mat = Material.WHITE_CONCRETE_POWDER;
//                                    cylOffset -= 0.5;
                                }

                            }
                            if (xlOffset < 0 || offset < 0) craterOffset /= 2;
                            if (isXL) {
//                                if (xlOffset < 0) craterOffset /= 2;
                                xlOffset += (craterOffset);

                            } else {
                                offset += (craterOffset);
                            }

                            if (distance <= 2) {
                                if (isXL) {
                                    xlOffset += 12;
                                }
                                mat = type == 0 ? Material.COAL_BLOCK : Material.GOLD_BLOCK;
                                matLocked = true;
                            }
                        }


//                    int yOffset = 0;
                    }
                }
                // Add offsets
                height += offset;
                height += xlOffset;
                height += cylOffset;
//                height += hillOffset;
                height += noiseOffset;
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

    static class CraterInfo {
        long id;
        Vector point;
        InfiniteVoronoiNoise ivn;
        boolean generated;
        boolean isXL;
        int type;

        double r;
        double upPoint;
        double innerRUp;
        double downPoint;
        double innerRDown;

        static CraterInfo fromId(long id, Vector point, InfiniteVoronoiNoise ivn) {
            CraterInfo ci = new CraterInfo();
            ci.id = id;
            ci.point = point;
            ci.ivn = ivn;

            ci.isXL = ivn.isXL;

            Random random1 = new Random(id);

            // Chance
            int chance = 130; // Out of 1000
            if (ci.isXL) chance = 900; // Out of 1000
            if (!ci.isXL) chance = chance / SMALL_IVN_COUNT;
            ci.generated = random1.nextInt(1000) < chance;
            if (!ci.generated) return ci;

            double size = 1; //(random1.nextDouble() + 0.5) / 2;
            int type = random1.nextInt(2);
            ci.type = type;

            double r = (ivn.SC_BLOCK_WIDTH / 2.0) * (ci.isXL ? 0.6 : 1) * size; // (biasFunction(random1.nextDouble(), 0.4)) *
//            System.out.println("SC_BLOCK_WIDTH: " + ivn.SC_BLOCK_WIDTH);
            ci.r = r;
            ci.innerRUp = r * UP_POINT; // Got visually from plot
            ci.innerRDown = r * DOWN_POINT; // Got visually from plot

            return ci;
        }

        public OffsetAndMat getOffsetAndMatAt(Vector v) {
            OffsetAndMat res = new OffsetAndMat();
            double distance = v.distance(point);
            double maxElevation = (isXL ? 70 : 9) + 1;

//                        double craterOffset = maxElevation - maxElevation * biasFunction((distance) / r, 0.6);
            double craterOffset = (craterFunctionSmooth((distance / r))) * maxElevation;

            // TODO Add material logic (unwind replacements)
            res.offset = craterOffset;
            return res;
        }

        static double UP_POINT = 0.649;
        static double DOWN_POINT = 0.536;

        public static double craterFunctionSmooth(double x) {
            x = x * 1.5;
            double cavityShape = ((x * 1.5) * (x * 1.5)) - 2.0;
            double a = (Math.abs(x) - 1 - 0.5);
            double rimShape = 0.5 * a * a;
            double floorShape = -0.4;
            double s = 0.14;
            return Utils.smin(Utils.smin(cavityShape, rimShape, s / 3), floorShape, -s) / -floorShape;
        }

        public static double ridgeLerpFn(double x) {
            if (x < CraterInfo.DOWN_POINT) return 0;
            if (x < CraterInfo.UP_POINT) return 9.66*x -5.27;
            return Utils.clamp((-(x * 2.85) + 2.85), 0, 1);
        }

    }

    static class OffsetAndMat {
        double offset;
        Material mat;

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
//                new ClayColorPopulator(),
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
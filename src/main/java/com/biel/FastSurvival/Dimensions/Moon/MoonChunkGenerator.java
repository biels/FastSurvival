package com.biel.FastSurvival.Dimensions.Moon;

import com.biel.FastSurvival.Utils.FontRenderer;
import com.biel.FastSurvival.Utils.Hashing.LongHashFunction;
import com.biel.FastSurvival.Utils.Noise.InfiniteVoronoiNoise;
import com.biel.FastSurvival.Utils.Noise.InfiniteVoronoiNoise.VoronoiPoint;
import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.Vector;
import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.noise.NoiseQualitySimplex;
import org.spongepowered.noise.model.Cylinder;
import org.spongepowered.noise.module.source.Simplex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
        Vector startOfChunk = new Vector(cx * 16, 0, cz * 16);
        List<List<VoronoiPoint>> allPointsWithId = new ArrayList<>();
        for (int i = 0; i < allIvns.size(); i++) {
            InfiniteVoronoiNoise allIvn = allIvns.get(i);
            List<VoronoiPoint> neighbourPointsWithId = allIvn.getNeighbourPointsWithId(startOfChunk.toLocation(world), 3);
            allPointsWithId.add(neighbourPointsWithId);
        }
        // Bukkit.broadcastMessage(allPointsWithId.stream().map(Vector::getBlockY).map(integer -> Integer.toString(integer)).collect(Collectors.joining(", ")));
        List<Material> acidLakeMaterials = CraterInfo.getAcidLakeMaterials();
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
                boolean isAcidLakeBlock = false;
                Cylinder acidCylinder = null;
                Vector xlPoint = null;
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
                        if (isXL) xlPoint = point;
                        long id = voronoiPoint.id;

                        // Info


                        CraterInfo ci = CraterInfo.fromId(id, voronoiPoint.vector, ivn);

                        if (!ci.generated) continue;
                        double size = 1; //(random1.nextDouble() + 0.5) / 2;
                        int type = ci.type;

                        double r = ci.r; //(ivn.SC_BLOCK_WIDTH / 2.0) * (isXL ? 1 : 1) * size; // (biasFunction(random1.nextDouble(), 0.4)) *


                        // Specific to block
                        double distance = thisBlock.distance(point);

                        if (ci.craterKind == CraterInfo.CraterKind.CRATER) {
                            // Crater
                            double maxElevation = (isXL ? 60 : 9) * size + 1;
                            // double craterOffset = maxElevation - maxElevation * biasFunction((distance) / r, 0.6);
                            double relDist = distance / r;

                            if (distance < r) {
                                // Block is inside crater radius

                                Simplex simplex = new Simplex();
                                simplex.setSeed((int) (id));
                                simplex.setNoiseQuality(NoiseQualitySimplex.SMOOTH);
                                simplex.setFrequency(15.0);
                                Cylinder cylinder = new Cylinder(simplex);
                                float angle = new Vector(0, 0, 1).angle(thisBlock.clone().subtract(point.clone()));
                                double rawCylNoise = cylinder.getValue(angle * 180 / Math.PI, 0);
                                rawCylNoise = biasFunction(rawCylNoise, 0.16);
                                double cylNoise = Utils.mix(0, (rawCylNoise + 1), CraterInfo.ridgeLerpFn(relDist));

                                double craterOffset = (CraterInfo.craterFunctionSmooth(relDist)) * maxElevation;
                                if (relDist > 0.5 && ivn.isXL) craterOffset = craterOffset + (cylNoise * 1.5);

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
                                    if (cylNoise < 1) {
                                        mat = Material.WHITE_CONCRETE_POWDER;
                                    }
//                                if (Utils.sigmoid(rawCylNoise - 1) + 1 < (1 * Utils.map(relDist, CraterInfo.UP_POINT, 1, 1 - (simplexMaxValue - 1), simplexMaxValue) + 1.0 ) / 2) { // (1.0 / (1 - CraterInfo.UP_POINT) + CraterInfo.UP_POINT)// from 0.8 to 0.0 in UP_POINT to 1
//                                    mat = Material.WHITE_CONCRETE_POWDER;
////                                    cylOffset -= 0.5;
//                                }

                                }

                                if (relDist < CraterInfo.DOWN_POINT && ci.isXL) {
                                    // Interior of crater

                                    // Generate ecosystems

                                    // LLLDLDLD[LS] // L = Light green, D = Dark green, [LS] = Light source

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
//                                mat = type == 0 ? Material.COAL_BLOCK : Material.GOLD_BLOCK;
//                                matLocked = true;
                                }
                            }
                        } else if (ci.craterKind == CraterInfo.CraterKind.ACID_LAKE) {
                            // Acid Lake LAKE
                            double maxElevation = (isXL ? 60 : 9) * size + 1;
                            // double craterOffset = maxElevation - maxElevation * biasFunction((distance) / r, 0.6);
                            double relDist = distance / r;

                            if (distance < r) {
                                // Block is inside crater radius

                                Simplex shapeSimplex = new Simplex();
                                shapeSimplex.setSeed((int) (id));
                                shapeSimplex.setNoiseQuality(NoiseQualitySimplex.SMOOTH);
                                shapeSimplex.setFrequency(2.0);
                                acidCylinder = new Cylinder(shapeSimplex);

                                Simplex simplex = new Simplex();
                                simplex.setSeed((int) (id));
                                simplex.setNoiseQuality(NoiseQualitySimplex.SMOOTH);
                                simplex.setFrequency(3.0);
                                Cylinder outShapeCyl = new Cylinder(simplex);

                                float angle = new Vector(0, 0, 1).angle(thisBlock.clone().subtract(point.clone()));

                                double outShapeCylNoise = outShapeCyl.getValue(angle * 180 / Math.PI, 0);
                                outShapeCylNoise = biasFunction(outShapeCylNoise, 0.16);
                                double outShapeCylNoiseMix = Utils.mix(0, (outShapeCylNoise + 1), CraterInfo.ridgeLerpFn(relDist));

                                double craterOffset = (CraterInfo.craterFunctionSmooth(relDist)) * maxElevation;
                                if (relDist > 0.5 && ivn.isXL)
                                    craterOffset = craterOffset + (outShapeCylNoiseMix * 1.5);

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
                                    mat = Material.STONE;
                                    double x1 = 1 - biasFunction(1 - relDist, 0.2);
                                    double simplexMaxValue = simplex.getMaxValue();
                                    if (outShapeCylNoiseMix < 1) {
                                        mat = Material.WHITE_CONCRETE_POWDER;
                                    }
//                                if (Utils.sigmoid(outShapeCylNoise - 1) + 1 < (1 * Utils.map(relDist, CraterInfo.UP_POINT, 1, 1 - (simplexMaxValue - 1), simplexMaxValue) + 1.0 ) / 2) { // (1.0 / (1 - CraterInfo.UP_POINT) + CraterInfo.UP_POINT)// from 0.8 to 0.0 in UP_POINT to 1
//                                    mat = Material.WHITE_CONCRETE_POWDER;
////                                    cylOffset -= 0.5;
//                                }

                                }

                                if (relDist < CraterInfo.DOWN_POINT && ci.isXL) {
                                    double acidCylNoise = acidCylinder.getValue(angle * 180 / Math.PI, 0);
                                    double expectedR = acidCylNoise * 30;
                                    isAcidLakeBlock = true;

                                    // LLLDLDLD[LS] // L = Light green, D = Dark green, [LS] = Light source

                                }

                                if (xlOffset < 0 || offset < 0) craterOffset /= 2;
                                xlOffset += (craterOffset);


                                if (distance <= 2) {
                                    if (isXL) {
                                        xlOffset += 12;
                                    }
//                                mat = type == 0 ? Material.COAL_BLOCK : Material.GOLD_BLOCK;
//                                matLocked = true;
                                }
                            }
// END LAKE
                        }


//                    int yOffset = 0;
                    }
                }
                // Add offsets
                height += offset;
                height += xlOffset;
                height += cylOffset;
                height += hillOffset;
                height += noiseOffset;
                int hardenedHeight = (int) (height - 15);
                for (int y = 1; y < hardenedHeight; y++) {
                    chunk.setBlock(x, y, z, MoonUtils.getMoonInnerMaterial());
                }
                if (acidCylinder != null) {
                    float angle = new Vector(0, 0, 1).angle(thisBlock.clone().subtract(xlPoint.clone()));


                    if (isAcidLakeBlock) {
                        int liquidLevel = (int) (height - 1);
                        int liquidHeight = acidLakeMaterials.size();
                        int liquidFloor = liquidLevel - liquidHeight;
                        for (int y = liquidLevel + 1; y <= height; y++) {
                            chunk.setBlock(x, y, z, Material.AIR);
                        }
                        for (int y = liquidFloor; y < liquidLevel; y++) {
                            int acidHeight = liquidHeight - (y - liquidFloor);
                            chunk.setBlock(x, y, z, acidLakeMaterials.get(acidHeight % acidLakeMaterials.size()));
                        }
                    }


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
        CraterKind craterKind = CraterKind.CRATER;
        int type;

        double r;
        double upPoint;
        double innerRUp;
        double downPoint;
        double innerRDown;

        // In the center of the crater there is a structure (with spawners) that determines the kind of meteor
        //


        static enum CraterKind {
            CRATER,
            ACID_LAKE

        }

        static enum CraterMainFeature {
            MOUNTAIN_GRAVEYARD, // Monument of
            MEDALLION,
            CRYSTAL,
            GLASS_FIREPLACE,
        }

        static enum CraterEcosystem {
            POISON_SWAMP, // Jeracraft pattern
            ORE_SPIKES, // Of different ores
            ORE_PATTERNS, // Simulate ore patterns with real ores
            CRYSTALS, // With stained glass and light ik style and jeracraft style
            ROCKET_LAUNCHPAD, // ,
            STONE_LOOPS,
            WATER_POND,
        }

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
            if (ci.isXL && random1.nextInt(100) < 60) ci.craterKind = CraterKind.ACID_LAKE;
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
            if (x < CraterInfo.UP_POINT) return 9.66 * x - 5.27;
            return Utils.clamp((-(x * 2.85) + 2.85), 0, 1);
        }

        public static List<Material> getAcidLakeMaterials() {
            // LLLDLDLD[LS] // L = Light green, D = Dark green, [LS] = Light source
            return Arrays.asList(
                    Material.LIME_STAINED_GLASS, Material.LIME_STAINED_GLASS, Material.LIME_STAINED_GLASS,
                    Material.GREEN_STAINED_GLASS, Material.LIME_STAINED_GLASS,
                    Material.GREEN_STAINED_GLASS, Material.LIME_STAINED_GLASS,
                    Material.GREEN_STAINED_GLASS, Material.LAVA
            );
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

    public void getClosestPoints(Location l) {

        return;
    }


    public void vpCommand(Player p, String[] args) {
        String cmd = args[0];

        Location l = p.getLocation();
        List<InfiniteVoronoiNoise> allIvns = getInfiniteVoronoiNoises(l.getWorld(), null);


        if (cmd.equalsIgnoreCase("draw")) {
            int ivnIndexOnly = -1;
            if (args.length >= 2) ivnIndexOnly = Integer.parseInt(args[1]);
            List<InfiniteVoronoiNoise> usingIvns;
            if (ivnIndexOnly >= 0) {
                usingIvns = new ArrayList<>();
                usingIvns.add(allIvns.get(ivnIndexOnly));
            } else {
                usingIvns = allIvns;
            }

            List<Material> wools = Arrays.asList(
                    Material.BLUE_WOOL,
                    Material.WHITE_WOOL,
                    Material.BLACK_WOOL,
                    Material.GREEN_WOOL,
                    Material.BROWN_WOOL,
                    Material.CYAN_WOOL,
                    Material.GRAY_WOOL
            );

            for (int i = 0, usingIvnsSize = usingIvns.size(); i < usingIvnsSize; i++) {
                InfiniteVoronoiNoise ivn = usingIvns.get(i);
                List<VoronoiPoint> ivnPoints = ivn.getNeighbourPointsWithId(l, ivn.isXL ? 0 : 1);

                int finalIvnIndexOnly = ivnIndexOnly;
                ivnPoints.forEach(vp -> {
                    Vector scv = ivn.getSuperChunkFromLoc(vp.vector);
                    Vector start = ivn.getSuperChunkVector(scv.getBlockX(), scv.getBlockZ());
                    Vector offset = ivn.getSuperChunkPointOffset(scv.getBlockX(), scv.getBlockZ());
                    World w = p.getWorld();
//                    int highestBlockYAt = l.getWorld().getHighestBlockYAt(start.toLocation(w));
                    int y = Math.max(0 + 4, 130) + finalIvnIndexOnly;
                    start.setY(y);
//                    p.teleport(start.toLocation(w));
                    Vector end = start.clone().add(new Vector(ivn.SC_BLOCK_WIDTH, 0, ivn.SC_BLOCK_WIDTH));
                    Utils.getLineBetweenPoints(start, vp.vector.clone().setY(y))
                            .forEach(b -> b.toLocation(w).getBlock().setType(wools.get(finalIvnIndexOnly % wools.size())));
                    Utils.get2dRectangleAround(start.getMidpoint(end), new Vector(0, 1, 0), new Vector(0, 0, 1), ivn.SC_BLOCK_WIDTH, ivn.SC_BLOCK_WIDTH)
                            .forEach(b -> b.toLocation(w).getBlock().setType(wools.get(finalIvnIndexOnly % wools.size())));
                    start.toLocation(w).getBlock().setType(Material.REDSTONE_BLOCK);
                    end.toLocation(w).getBlock().setType(Material.DIAMOND_BLOCK);

                });

            }


//            for (int i = 0, listOfPointsWithIdSize = listOfPointsWithId.size(); i < listOfPointsWithIdSize; i++) {
//                List<VoronoiPoint> list = listOfPointsWithId.get(i);
//                list.stream().forEach(vp -> {
//                    Utils.getLine()
//                });
//            }
        } else if (cmd.equalsIgnoreCase("tp")) {
            // <ivnIdx> <orig | offset>
            int ivnIndexOnly = -1;
            if (args.length >= 2) ivnIndexOnly = Integer.parseInt(args[1]);
            List<InfiniteVoronoiNoise> usingIvns;
            if (ivnIndexOnly >= 0) {
                usingIvns = new ArrayList<>();
                usingIvns.add(allIvns.get(ivnIndexOnly));
            } else {
                usingIvns = allIvns;
            }


            for (int i = 0, usingIvnsSize = usingIvns.size(); i < usingIvnsSize; i++) {
                InfiniteVoronoiNoise ivn = usingIvns.get(i);
                List<VoronoiPoint> ivnPoints = ivn.getNeighbourPointsWithId(l, 0);

                int finalI = i;
                ivnPoints.forEach(vp -> {
                    Vector scv = ivn.getSuperChunkFromLoc(vp.vector);
                    Vector start = ivn.getSuperChunkVector(scv.getBlockX(), scv.getBlockZ());
                    Vector offset = ivn.getSuperChunkPointOffset(scv.getBlockX(), scv.getBlockZ());
                    if (args[2].equalsIgnoreCase("orig")) {
                        Location location = start.toLocation(p.getWorld());
                        location.setY(120);
                        p.teleport(location);
                    } else {
                        Location location = start.clone().add(offset).toLocation(p.getWorld());
                        location.setY(120);
                        p.teleport(location);

                    }
                });
            }
        } else if (cmd.equalsIgnoreCase("d")) {
            ///
            int r = 10;


            List<Material> wools = Arrays.asList(
                    Material.BLUE_WOOL,
                    Material.WHITE_WOOL,
                    Material.BLACK_WOOL,
                    Material.GREEN_WOOL,
                    Material.BROWN_WOOL,
                    Material.CYAN_WOOL,
                    Material.GRAY_WOOL
            );


            if (args.length >= 2) r = Integer.parseInt(args[1]);
            for (int ix = -r; ix < r; ix++) {
                for (int iz = -r; iz < r; iz++) {
                    Chunk source = p.getLocation().add(16 * ix, 0, 16 * iz).getChunk();
//        @NotNull List<VoronoiPoint> neighbourPointsWithId = allIvns.stream()
//                .flatMap(ivn -> ivn.getNeighbourPointsWithId(source.getX(), source.getZ(), 1).stream())
//                .collect(Collectors.toList());
//        List<VoronoiPoint> points = neighbourPointsWithId
//                .stream()
//                .sorted((VoronoiPoint p1, VoronoiPoint p2) -> {
//                    double v = p1.vector.distanceSquared(thisChunk) - p2.vector.distanceSquared(thisChunk);
//                    return (int) v;
//                }).collect(Collectors.toList());
                    Vector thisChunk = source.getBlock(8, 0, 8).getLocation().toVector();

                    // TODO Use hash or additional random seed info for whether this is actually generated
                    for (int i = 0; i < allIvns.size(); i++) {
                        InfiniteVoronoiNoise ivn = allIvns.get(i);

                        Vector superChunkVec = ivn.getSuperChunkFromChunk(source.getX(), source.getZ());

                        List<VoronoiPoint> neighbourPointsWithId = ivn
                                .getNeighbourPointsWithId(superChunkVec.getBlockX(), superChunkVec.getBlockZ(), 0);
                        VoronoiPoint thisPoint = neighbourPointsWithId.get(0);
                        Vector pointVec = thisPoint.vector.clone();
                        double distance = pointVec.distance(thisChunk);

                        boolean inAABB = pointVec.isInAABB(
                                source.getBlock(0, 0, 0).getLocation().toVector(),
                                source.getBlock(15, 1, 15).getLocation().toVector()
                        );
//        Chunk closestPointChunk = pointVec.toLocation(world).getBlock().getChunk();
                        boolean isInChunk = (int) (pointVec.getBlockX() / 16) == source.getX() &&
                                pointVec.getBlockZ() / 16 == source.getZ();
//
                        if (inAABB) {
                            MoonChunkGenerator.CraterInfo ci = MoonChunkGenerator.CraterInfo.fromId(thisPoint.id, thisPoint.vector, ivn);
                            if (!ci.generated) continue;
//                            if (ci.isXL)
//                                System.out.println("In chunk: " + ci.id + " " + ci.r + " xl: " + ci.isXL);
                            World world = p.getWorld();
                            Location l1 = pointVec.toLocation(world);
                            int highestBlockYAt = world.getHighestBlockYAt(l1);
                            // Populate
                            String str = String.valueOf(i % 100);
                            if (ci.isXL) str += "XL";
                            int size = 10;
                            l1.setY(highestBlockYAt);
                            l1.getBlock().setType(i == 0 ? Material.LAPIS_BLOCK : Material.REDSTONE_BLOCK);
                            Vector lateralAxis = new Vector(0, 0, 1);
//                l.add(lateralAxis.clone().multiply(str.length() * size / 2));
                            Vector up = new Vector(0, 1, 0);
                            l1.add(up.clone().multiply(13));
//                if (random.nextInt(100) < 30)
                            int fontSize = 17;
                            if (ci.isXL) fontSize = 20;
                            if (ci.isXL)
                                Utils.getLine(l1.toVector(), up, 80).forEach(v -> v.toLocation(world).getBlock().setType(Material.LAPIS_BLOCK));


                            ///

                            Vector scv = ivn.getSuperChunkFromLoc(thisPoint.vector);
                            Vector start = ivn.getSuperChunkVector(scv.getBlockX(), scv.getBlockZ());
                            Vector offset = ivn.getSuperChunkPointOffset(scv.getBlockX(), scv.getBlockZ());
                            World w = p.getWorld();
//                    int highestBlockYAt = l.getWorld().getHighestBlockYAt(start.toLocation(w));
                            int y = Math.max(0 + 4, 130) + i;
                            start.setY(y);
//                    p.teleport(start.toLocation(w));
                            Vector end = start.clone().add(new Vector(ivn.SC_BLOCK_WIDTH, 0, ivn.SC_BLOCK_WIDTH));
                            int finalI = i;
                            Vector offsetEnd = thisPoint.vector.clone().setY(y);
                            Material wool = wools.get(finalI % wools.size());
                            l1.setY(y - 1);
                            FontRenderer.renderText(str, l1, lateralAxis.multiply(1), up, fontSize, wool);
                            Utils.getLineBetweenPoints(start, offsetEnd)
                                    .forEach(b -> b.toLocation(w).getBlock().setType(wool));
                            Utils.get2dRectangleAround(start.getMidpoint(end), new Vector(0, 1, 0), new Vector(0, 0, 1), ivn.SC_BLOCK_WIDTH, ivn.SC_BLOCK_WIDTH)
                                    .forEach(b -> b.toLocation(w).getBlock().setType(wool));
                            start.toLocation(w).getBlock().setType(Material.DIAMOND_BLOCK);
                            offsetEnd.toLocation(w).getBlock().setType(Material.REDSTONE_BLOCK);


                            ////
//            l.getBlock().setType(Material.LAPIS_BLOCK);
                        }
//            thisChunk.toLocation(world).getBlock().setType(Material.EMERALD_BLOCK);
                    }
                }
            }
        }
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
                new SampleSharedVoronoiPopulator(),
                new MoonCraterPopulator(),
                new ElectricBossPopulator(),
                new FlagPopulator(),
                new MoonMagicTreePopulator(),
                new ClaySpiralPopulator(),
                new ClayColorPopulator(),
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
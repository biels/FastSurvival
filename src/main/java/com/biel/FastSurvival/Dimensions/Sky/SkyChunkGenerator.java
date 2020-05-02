package com.biel.FastSurvival.Dimensions.Sky;

import com.biel.FastSurvival.Utils.Hashing.LongHashFunction;
import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.Vector;
import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;

import java.util.*;

public class SkyChunkGenerator extends ChunkGenerator {
    private NoiseGenerator generator;
    LongHashFunction xx;

    public SkyChunkGenerator() {

    }

    boolean isSourceChunk(int cx, int cz) {
        // TODO Cache result
        return getChunkXXHash(cx, cz) % 43 == 0;
    }

    Map<Long, Long> xxHashCache = new HashMap<>();

    private long getChunkXXHash(int cx, int cz) {
        long key = cx * 10000 + cz;
        xxHashCache.computeIfAbsent(key, (k) -> xx.hashLong(k));
        return xxHashCache.get(key);
    }

    Vector getClosestVoronoiSourceChunk(int cx, int cz) {
        int m = 8;
        int n = 8;
        int[] dr = new int[]{0, -1, 0, 1};
        int[] dc = new int[]{1, 0, -1, 0};
//        char[][] grid = new char[m*2][n*2];


        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{0, 0});
        while (!queue.isEmpty()) {
            Set<Integer> set = new HashSet<>();
            int size = queue.size();
            for (int k = 0; k < size; k++) {
                int[] pos = queue.poll();
//                grid[pos[0]+m][pos[1]+n] = '0';
                int cx1 = cx + pos[0];
                int cz1 = cz + pos[1];
                if (isSourceChunk(cx1, cz1)) {
                    return new Vector(cx1, 0, cz1);
                }
                // Neighbours
                for (int p = 0; p < 4; p++) {
                    int r = pos[0] + dr[p];
                    int c = pos[1] + dc[p];
                    if (r < -m || r >= m || c < -n || c >= n) {
                        continue;
                    }
                    if (!set.contains(r * n + c)) {
                        queue.offer(new int[]{r, c});
                        set.add(r * n + c);
                    }
                }
            }
        }


        return new Vector(cx, 0, cz);
    }

    private NoiseGenerator getGenerator(World world) {
        if (generator == null) {
            generator = new SimplexNoiseGenerator(world);

        }

        return generator;
    }

    private double getHeight(World world, double x, double y) {
        NoiseGenerator gen = getGenerator(world);

        double result = gen.noise(x / 8, y / 8);
        double resultFast = gen.noise(x / 4, y / 4);
        resultFast *= 0.5;
        result *= 1.0;
        return result + resultFast;
    }

    //Unused
//    public byte[] generate(World world, Random random, int cx, int cz) {
//        byte[] result = new byte[32768];
//
//        for (int x = 0; x < 16; x++) {
//            for (int z = 0; z < 16; z++) {
//                int height = getHeight(world, cx + x * 0.0625, cz + z * 0.0625, 4) + 50;
//                for (int y = 46; y < height; y++) {
//                    result[(x * 16 + z) * 128 + y] = (byte) Material.SNOW_BLOCK.getId();
//                }
//            }
//        }
//
//        return result;
//    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int cx, int cz, BiomeGrid biome) {
        initWithWorld(world);
        ChunkData chunk = createChunkData(world);

        boolean isCenter = isSourceChunk(cx, cz);
//        Vector sourceChunk = getClosestVoronoiSourceChunk(cx, cz);
//        long chunkXXHash = getChunkXXHash(sourceChunk.getBlockX(), sourceChunk.getBlockZ());
//        long bucket = Math.abs(chunkXXHash) % 5;
        Material material;


//        if (bucket == 0) material = Material.DIRT;
//        if (bucket == 1) material = Material.STONE;
//        if (bucket == 2) material = Material.BIRCH_WOOD;
//        if (bucket == 3) material = Material.IRON_ORE;
//        System.out.println(bucket);
        int baseHeight = 60;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {

                Vector chunkDist = new Vector(cx, 0, cz);
                chunkDist.multiply(16).add(new Vector(x, 0, z));
                int variance = 5;// (int) (5 + chunkDist.length() / (16 * 3));
                double rawHeight = Utils.sigmoid(getHeight(world, cx + x * 0.0625, cz + z * 0.0625) * 1.4 + 0.10);
                int scaledHeight = NoiseGenerator.floor(rawHeight * variance) + 1;
                int height = scaledHeight + baseHeight;
//                if (rawHeight < -0.9) continue;
                for (int y = baseHeight - scaledHeight + 1 ; y < height; y++) {
                    material = Material.SNOW_BLOCK;
                    if (rawHeight == 0) material = Material.WHITE_STAINED_GLASS;
//                    if (biome1 == Biome.PLAINS) material = Material.GRASS_BLOCK;
//                    if (biome1 == Biome.DARK_FOREST) material = Material.COAL_BLOCK;
//                    if (biome1 == Biome.RIVER) material = Material.LAPIS_BLOCK;
//                    if (biome1 == Biome.BEACH) material = Material.GOLD_BLOCK;
//                    if (biome1 == Biome.COLD_OCEAN) material = Material.BLUE_ICE;
//                    if (isCenter) material = Material.SLIME_BLOCK;
                    chunk.setBlock(x, y, z, material);

//                    if (y != 48) {
//
//                    } else {
//                        chunk.setBlock(x, y, z, Material.BLUE_ICE);
//                    }
                }
            }
        }
        return chunk;
    }

    private void initWithWorld(World world) {
        if (xx == null) {
            xx = LongHashFunction.xx(world.getSeed());
        }
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
        return true;
    }
}
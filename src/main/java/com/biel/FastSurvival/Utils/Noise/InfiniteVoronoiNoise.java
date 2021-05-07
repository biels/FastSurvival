package com.biel.FastSurvival.Utils.Noise;

import com.biel.FastSurvival.Utils.Hashing.LongHashFunction;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class InfiniteVoronoiNoise {
    // Cached local state
    Map<Long, List<Vector>> cachedScPointsWithIds;
    public Long seed;
    Random r;
    public int CHUNKS_IN_SC = 3;
    public int SC_BLOCK_WIDTH;
    private LongHashFunction xx;
    public boolean isXL = false;

    public void setSeed(Long seed) {
        this.seed = seed;
        r.setSeed(seed);
        xx = LongHashFunction.xx(seed);
    }

    public InfiniteVoronoiNoise(Random r, int CHUNKS_IN_SC, long seed) {
        this.r = r;
        this.CHUNKS_IN_SC = CHUNKS_IN_SC;
        this.SC_BLOCK_WIDTH = CHUNKS_IN_SC * 16;
        setSeed(seed);
    }

    public Vector getSuperChunkFromChunk(int cx, int cz) {
        return new Vector(cx / CHUNKS_IN_SC, 0, cz / CHUNKS_IN_SC);
    }
    public Vector getSuperChunkFromLoc(Vector v) {
        return getSuperChunkFromChunk(v.getBlockX() / 16, v.getBlockZ() / 16);
    }

    public Vector getSuperChunkPointOffset(int scx, int scz) {
        Random r1 = new Random(getSuperChunkId(scx, scz) * seed);
        return new Vector(r1.nextInt(SC_BLOCK_WIDTH), 0, r1.nextInt(SC_BLOCK_WIDTH));
    }

    public Vector getSuperChunkVector(int scx, int scz) {
        return new Vector(scx * SC_BLOCK_WIDTH, 0, scz * SC_BLOCK_WIDTH);
    }

    public long getSuperChunkId(int scx, int scz) {
//        int id = new Vector(scx, 0, scz).hashCode();
//        long id = (long) scx << 32 + scz;
        return xx.hashInts(new int[]{scx, scz});
//        return xx.hashLong(id);
    }

    public List<VoronoiPoint> getNeighbourPointsWithId(Location l, int overscan) {
        Vector superChunkVec = this.getSuperChunkFromChunk(l.getBlockX() / 16, l.getBlockZ() / 16);
        return getNeighbourPointsWithId(superChunkVec.getBlockX(), superChunkVec.getBlockZ(), overscan);
    }
    @NotNull
    public List<VoronoiPoint> getNeighbourPointsWithId(int scx, int scz, int overscan) {
        List<VoronoiPoint> points = new ArrayList<>();

        int n = overscan;
        for (int i = -n; i <= n; i++) {
            for (int j = -n; j <= n; j++) {
                int scx1 = scx + i;
                int scz1 = scz + j;
                Vector superChunkVector = getSuperChunkVector(scx1, scz1);
                Vector pointOffset = getSuperChunkPointOffset(scx1, scz1);
                Vector res = superChunkVector.clone().add(pointOffset);
                long superChunkId = getSuperChunkId(scx1, scz1);
                res.setY(0);
                points.add(new VoronoiPoint(superChunkId, res));

            }
        }
        return points;
    }

    public static class VoronoiPoint {
        public long id;
        public Vector vector;

        public VoronoiPoint(long id, Vector vector) {
            this.id = id;
            this.vector = vector;
        }
    }
}

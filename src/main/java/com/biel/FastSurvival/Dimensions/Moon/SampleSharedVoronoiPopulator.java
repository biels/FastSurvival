package com.biel.FastSurvival.Dimensions.Moon;

import com.biel.FastSurvival.Utils.FontRenderer;
import com.biel.FastSurvival.Utils.Noise.InfiniteVoronoiNoise;
import com.biel.FastSurvival.Utils.Noise.InfiniteVoronoiNoise.VoronoiPoint;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class SampleSharedVoronoiPopulator extends BlockPopulator {


    @Override
    public void populate(@NotNull World world, @NotNull Random random, @NotNull Chunk source) {
        MoonChunkGenerator generator = (MoonChunkGenerator) world.getGenerator();
        List<InfiniteVoronoiNoise> allIvns = generator.getInfiniteVoronoiNoises(world, random);
        @NotNull List<VoronoiPoint> neighbourPointsWithId = allIvns.stream()
                .flatMap(ivn -> ivn.getNeighbourPointsWithId(source.getX(), source.getZ(), 0).stream())
                .collect(Collectors.toList());
        Vector thisChunk = source.getBlock(8, 0, 8).getLocation().toVector();
        List<VoronoiPoint> points = neighbourPointsWithId
                .stream()
                .sorted((VoronoiPoint p1, VoronoiPoint p2) -> {
                    double v = p1.vector.distanceSquared(thisChunk) - p2.vector.distanceSquared(thisChunk);
                    return (int) v;
                }).collect(Collectors.toList());

        // TODO Use hash or additional random seed info for whether this is actually generated
        for (int i = 0; i < neighbourPointsWithId.size(); i++) {
            VoronoiPoint thisPoint = neighbourPointsWithId.get(i);
            Vector pointVec = thisPoint.vector;
            double distance = pointVec.distance(thisChunk);

            boolean inAABB = pointVec.isInAABB(
                    source.getBlock(0, 0, 0).getLocation().toVector(),
                    source.getBlock(15, 1, 15).getLocation().toVector()
            );
//        Chunk closestPointChunk = pointVec.toLocation(world).getBlock().getChunk();
            boolean isInChunk = (int) (pointVec.getBlockX() / 16) == source.getX() &&
                    pointVec.getBlockZ() / 16 == source.getZ();
            Location l = pointVec.toLocation(world);
//        int highestBlockYAt = world.getHighestBlockYAt(l);

            if (isInChunk) { // source.getX() % 10 == 0 && source.getZ() % 10 == 0
                System.out.println("In chunk: " + thisPoint.id);
                // Populate
                String str = "A";
                int size = 10;
//            l.setY(highestBlockYAt);
                l.setY(61);
//                l.getBlock().setType(i == 0 ? Material.DIAMOND_BLOCK : Material.REDSTONE_BLOCK);
                Vector lateralAxis = new Vector(0, 1, 0);
                //l.add(lateralAxis.clone().multiply(str.length() * size / 2));
//            if(random.nextInt(100) < 30)
//                FontRenderer.renderText(str, l, lateralAxis.multiply(1), new Vector(1, 0, 0), 14, Material.DIAMOND_BLOCK);
//            l.getBlock().setType(Material.LAPIS_BLOCK);
            }
            thisChunk.toLocation(world).getBlock().setType(Material.EMERALD_BLOCK);
        }


    }
}

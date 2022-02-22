package com.biel.FastSurvival.Dimensions.Moon;

import com.biel.FastSurvival.Utils.FontRenderer;
import com.biel.FastSurvival.Utils.Noise.InfiniteVoronoiNoise;
import com.biel.FastSurvival.Utils.Noise.InfiniteVoronoiNoise.VoronoiPoint;
import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class SampleSharedVoronoiPopulator extends BlockPopulator {


    @Override
    public void populate(@NotNull World world, @NotNull Random random, @NotNull Chunk source) {
        MoonChunkGenerator generator = (MoonChunkGenerator) world.getGenerator();
        List<InfiniteVoronoiNoise> allIvns = generator.getInfiniteVoronoiNoises(world, random);
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

            MoonChunkGenerator.CraterInfo ci = MoonChunkGenerator.CraterInfo.fromId(thisPoint.id, thisPoint.vector, ivn);
            if (!ci.generated) continue;
            if(ci.craterKind == MoonChunkGenerator.CraterInfo.CraterKind.ACID_LAKE) {
                if(distance > ci.r) continue;
                // We are inside the radius
                double r = ci.r;
                if(Utils.Possibilitat(30)) continue;
                // Generate acid lake bubbles using green stained glass panes
                int yLevel = world.getHighestBlockYAt(thisChunk.getBlockX() , thisChunk.getBlockZ());

                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        Vector hVec = source.getBlock(x, 0, z).getLocation().toVector();
                        double d = hVec.distance(ci.point);
                        if(d > ci.r) continue;
                        for (int y = yLevel - 5; y < (12 + (ci.isXL ? 2 : 0)); y++) {
                            if (Math.random() < (0.2 - y/1000.0)) {
                                source.getBlock(x, y, z).setType(Material.GREEN_STAINED_GLASS_PANE);
                            }
                        }
                    }
                }


            };

            boolean inAABB = pointVec.isInAABB(
                    source.getBlock(0, 0, 0).getLocation().toVector(),
                    source.getBlock(15, 1, 15).getLocation().toVector()
            );
//        Chunk closestPointChunk = pointVec.toLocation(world).getBlock().getChunk();
            boolean isInChunk = (int) (pointVec.getBlockX() / 16) == source.getX() &&
                    pointVec.getBlockZ() / 16 == source.getZ();
//
            if (inAABB) {

                if(ci.isXL)
                    System.out.println("In chunk: " + ci.id + " " + ci.r + " xl: " +  ci.isXL);
                Location l = pointVec.toLocation(world);
                int highestBlockYAt = world.getHighestBlockYAt(l);
                // Populate
                String str = String.valueOf(i % 100);
                if(ci.isXL) str += "XL";
                int size = 10;
                l.setY(highestBlockYAt);
//                l.getBlock().setType(i == 0 ? Material.LAPIS_BLOCK : Material.REDSTONE_BLOCK);
                Vector lateralAxis = new Vector(0, 0, 1);
//                l.add(lateralAxis.clone().multiply(str.length() * size / 2));
                Vector up = new Vector(0, 1, 0);
                l.add(up.clone().multiply(13));
//                if (random.nextInt(100) < 30)
                int fontSize = 17;
                if(ci.isXL) fontSize = 20;
//                if(ci.isXL) Utils.getLine(l.toVector(), up, 80).forEach(v -> v.toLocation(world).getBlock().setType(Material.LAPIS_BLOCK));
//                FontRenderer.renderText(str, l, lateralAxis.multiply(1), up, fontSize, Material.DIAMOND_BLOCK);
//            l.getBlock().setType(Material.LAPIS_BLOCK);
            }
//            thisChunk.toLocation(world).getBlock().setType(Material.EMERALD_BLOCK);
        }


    }
}

package com.biel.FastSurvival.Dimensions.Sky.hexgen;

import com.biel.FastSurvival.FastSurvival;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Infinite Hexagonal grid
 */
public class InfiniteHexGrid {
    World world;
    HashMap<HexCoordinates, HexCell> cellMap = new HashMap<>();
    Set<HexCoordinates> generatedCells = new HashSet<>();
    Set<HexCoordinates> scheduledCells = new HashSet<>();
    BukkitTask populationTask;
    boolean spawnGenerated = false;

    public InfiniteHexGrid(World world) {
        this.world = world;
    }

    private void onGenerateCell(HexCoordinates k) {
        generatedCells.add(k);
//        System.out.println("Generated cell " + k + ", Cells: " +  cellMap.size());
        reschedulePopulationTask(25);
    }

    public HexCell getCell(HexCoordinates coordinates) {
        HexCell hexCell = cellMap.computeIfAbsent(coordinates, k -> {
            onGenerateCell(k);
            return new HexCell(k);
        });
        return hexCell;
    }

    public synchronized void populateAllPendingCells() {
        List<HexCoordinates> processed = new ArrayList<>();
//        if (scheduledCells.size() == 0) {
//            Map<Boolean, List<HexCoordinates>> partition = generatedCells.stream()
//                    .collect(Collectors.partitioningBy(coords -> coords.getCorners().stream()
//                            .allMatch(corner -> corner.toLocation(world).getChunk().isLoaded())));
////            unloaded.clear();
//            // Add all unloaded cells to unloaded set
////            unloaded.addAll(partition.get(false));
//            // Set target to loaded cells
//            scheduledCells =  partition.get(true);
//            System.out.println("Scheduled " + scheduledCells.size() + " new cells");
//        }


        // Optimize for unloaded, larger delay when only unloaded chunks remain
        // Maybe use 2 queues: One secondary to store those that were not loaded and should be left for when morte chunks are generated

        int limit = 50;
//        int start = 0;
//        int end = Math.min(start + limit, scheduledCells.size());


        Iterator<HexCoordinates> iterator = scheduledCells.iterator();
        for (int i = 0; i < scheduledCells.size(); i++) {
            HexCoordinates coords = iterator.next();
            if (limit-- == 0) break;
//            boolean b = coords.getCorners().stream()
//                    .allMatch(corner -> world.isChunkLoaded(corner.getBlockX() / 16, corner.getBlockZ() / 16));
//            if (b) {
            try {
                getCell(coords).populate(world);
                processed.add(coords);
            } catch (ConcurrentModificationException e) {
                System.out.println("CME @ " + coords.toString());
                break;
            }
//            }
        }
//        scheduledCells.removeAll(processed);
        System.out.println("processed: " + processed.size() + ", remaining: " + scheduledCells.size());

        scheduledCells.removeAll(processed);
        generatedCells.removeAll(processed);
    }

    public void reschedulePopulationTask(int timeout) {
        if (populationTask != null) {
            populationTask.cancel();
        }
        populationTask = Bukkit.getScheduler().runTaskLater(FastSurvival.getPlugin(), new Runnable() {
            @Override
            public void run() {
                if (!scheduledCells.isEmpty()) {
                    populateAllPendingCells();
                }
                if (scheduledCells.isEmpty()) {
                    generateAreaAroundLocation(world.getSpawnLocation(), 16 * 20);
                    world.getPlayers().forEach(p -> generateAreaAroundLocation(p.getLocation(), 16 * 32));
                    if(!scheduledCells.isEmpty()) System.out.println("Finished batch, adding " + scheduledCells.size() + " more cells");
                }
                reschedulePopulationTask(scheduledCells.isEmpty() ? 60 : 12);
            }
        }, timeout);
    }

    public void generateAreaAroundLocation(Location l, int radius) {
        // .forEach(gc -> getCell(gc).populate(world))
        generatedCells.stream()
                .filter(gc -> !getCell(gc).populated && gc.getCenter().distance(l.toVector()) < radius)
                .forEach(gc -> scheduledCells.add(gc));
    }
}

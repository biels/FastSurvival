package com.biel.FastSurvival.Dimensions.Moon;

import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.*;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class MoonBasePopulator extends BlockPopulator {

    @Override
    public void populate(World world, Random random, Chunk source) {
        if (source.getX() % 15 != 3 || source.getZ() % 15 != 4) return;
        if (random.nextInt(7) > 1) return;

        int centerX = (source.getX() << 4) + random.nextInt(16);
        int centerZ = (source.getZ() << 4) + random.nextInt(16);
        int centerY = world.getHighestBlockYAt(centerX, centerZ) - 1;
        Location start = new Location(world, centerX, centerY, centerZ);
        if(start.getBlock().getType() == Material.LIME_STAINED_GLASS)  return;

        generate(start, world);
    }


    Material bubbleMaterial = Material.WHITE_CONCRETE;
    Material tunnelMaterial = Material.WHITE_CONCRETE;
    Material lineMaterial = Material.YELLOW_CONCRETE;
    double bubbleRadius = 8.0;
    int tunnelRadius = 6; ///
    int minimumDistance = 30;

    public List<Location> generateRandomLocs(Location center, int locationCount, World world) {
        ArrayList<Location> locs = new ArrayList<Location>();
        for (int i = 0; i < locationCount * 10; i++) {
            Vector randVec = Vector.getRandom().add(new Vector(-0.5, 0, -0.5)).normalize().multiply(100);
            randVec.setY(0);
//            Bukkit.broadcastMessage(randVec.toString());
            Location loc = center.clone().add(randVec);
            loc.setY(Objects.requireNonNull(loc.getWorld()).getHighestBlockYAt(loc));
            boolean collision = false;
            for (Location l2 : locs) {
                if (l2.distance(loc) < minimumDistance) {
                    collision = true;
                    break;
                }
            }
            if (!collision) {
                locs.add(loc);
                if (locs.size() >= locationCount) break;
            }
        }
        return locs;
    }

    public Location getFarthestLocation(Location loc, List<Location> locList) {
        Location currentFarthestLoc = loc.clone();
        double currentMaxDistance = 0.0;
        for (int i = 0; i < locList.size(); i++) { // forEach loc in locList
            Location l = locList.get(i);
            if (l.distance(loc) > currentMaxDistance) {
                currentMaxDistance = l.distance(loc);
                currentFarthestLoc = l;
            }
        }
        return currentFarthestLoc;
    }


    public Location getNearestLocation(Location loc, List<Location> locList) {
        List<Location> clonedLocList = new ArrayList<Location>(); // to safely delete its elements
        for (int i = 0; i < locList.size(); i++) {
            if (!locList.get(i).equals(loc)) {
                clonedLocList.add(locList.get(i).clone());
            }
            ;
        }
        Location currentNearestLoc = loc.clone();
        double currentMinDistance = 1000.0;
        for (Location l : clonedLocList) { // forEach loc in locList
            if (!l.equals(loc) && l.distance(loc) < currentMinDistance) {
                currentMinDistance = l.distance(loc);
                currentNearestLoc = l;
            }
        }
        return currentNearestLoc;
    }

    public void connectBubbles(Location b1, Location b2) {
        Vector cylVector = Utils.CrearVector(b1, b2);
        Vector cylNormal = Utils.getClosestAxisVector(Utils.CrearVector(b1, b2), false);
        AtomicInteger i = new AtomicInteger();
        Utils.getLineBetween(b1.clone(), b2.clone())
                .forEach(vector -> {
                    int idx = i.getAndIncrement();
                    Utils.getCylBlocks(vector.toLocation(Objects.requireNonNull(b1.getWorld())), tunnelRadius, 1, false, cylNormal)
                            .forEach(cylBlock -> {
                                if (cylBlock.getY() < vector.toLocation(cylBlock.getLocation().getWorld()).getBlockY()) {
                                    Location cylLoc = cylBlock.getLocation().clone();
                                    int YDifference = vector.getBlockY() - cylLoc.getBlockY();
                                    cylLoc.add(0, YDifference, 0)
                                            .getBlock().setType(tunnelMaterial);
                                    return;
                                }
                                if (cylBlock.getY() == vector.toLocation(cylBlock.getLocation().getWorld()).getBlockY() + 2) {
                                    cylBlock.setType(lineMaterial); // build yellow strip lines in the tunnels
                                    return;
                                }
                                cylBlock.setType(tunnelMaterial);
                            });
                    Utils.getCylBlocks(vector.toLocation(
                            Objects.requireNonNull(b1.getWorld())), tunnelRadius - 2, 1, true, cylNormal)
                            .forEach(cylBlock -> {
                                if (cylBlock.getY() > vector.getBlockY() && cylBlock.getType() != Material.AIR)
                                    cylBlock.setType(Material.AIR);
                            });

                });
    }

    public void buildBubbleSphere(List<Location> bubbles) {
        bubbles.forEach(bubbleLoc -> {
            Utils.getSphereLocations(bubbleLoc, bubbleRadius, true)
                    .forEach(sphereLoc -> {
                        if (sphereLoc.getBlockY() < bubbleLoc.getBlockY()) return;
                        if (sphereLoc.getBlockY() == bubbleLoc.getBlockY() + 2) {
                            sphereLoc.getBlock().setType(Material.YELLOW_CONCRETE); // build yellow strip lines in the bubble
                            return;
                        }
                        if (sphereLoc.getBlockY() >= bubbleLoc.getBlockY() + 4) {
                            sphereLoc.getBlock().setType(Material.GLASS); // build yellow strip lines in the bubble
                            return;
                        }
                        sphereLoc.getBlock().setType(bubbleMaterial);
                    });
        });
    }

    public void buildBubbleFloors(List<Location> bubbles) {
        bubbles.forEach(bubbleLoc -> {
            Utils.getCylBlocks(bubbleLoc, (int) (bubbleRadius - 1.0), 1, true)
                    .forEach(cylBlock -> cylBlock.setType(bubbleMaterial)); //to Build the floor of the bubble;
        });
    }

    public void clearBubbles(List<Location> bubbles) {
        bubbles.forEach(bubbleLoc -> {
            Utils.getSphereLocations(bubbleLoc, 7.0, false)
                    .forEach(sphereLoc -> {
                        if (sphereLoc.getBlock().getType() == Material.AIR || sphereLoc.getBlockY() > bubbleLoc.getBlockY())
                            sphereLoc.getBlock().setType(Material.AIR);
                        ;
                    });
        });
    }

    public void generate(Location start, World world) {
//        start.add(0, 10, 0);
        List<Location> bubbleLocs = generateRandomLocs(start.clone(), 5, world);

        buildBubbleSphere(bubbleLocs);
        List<Location> orderedBubbleLocs = orderListFromFarToNear(bubbleLocs);
        for (Location bubbleLoc : bubbleLocs) {
            connectBubbles(bubbleLoc, getNearestLocation(bubbleLoc, orderedBubbleLocs));
            orderedBubbleLocs.remove(bubbleLoc);
        }
        clearBubbles(bubbleLocs);
        buildBubbleFloors(bubbleLocs);
        start.getBlock().setType(Material.LAPIS_BLOCK);
    }

    public List<Location> orderListFromFarToNear(List<Location> locList) {
        if (locList.size() == 0) return locList;
        Location centerBubble = locList.get(0).clone();
        List<Location> clonedList = new ArrayList<>();
        locList.forEach(l -> clonedList.add(l.clone()));
        List<Location> orderedList = new ArrayList<>();

        for (int i = 0; i < locList.size(); i++) {
            Location farthestLocation = getFarthestLocation(centerBubble, clonedList);
            orderedList.add(farthestLocation);
            clonedList.remove(farthestLocation);
        }
        return orderedList;
    }

}

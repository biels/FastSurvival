package com.biel.FastSurvival.Dimensions.Sky;

import com.biel.FastSurvival.FastSurvival;
import com.biel.FastSurvival.Utils.Cuboid;
import com.biel.FastSurvival.Utils.GestorPropietats;
import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.Axis;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.data.Orientable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SkyNexus {
    int id;
    Location location;
    int beaconLevel;
    int crystalLevel;
    GestorPropietats p;
    static Map<Vector, SkyNexus> allInstances;

    public Location getLocation() {
        return location;
    }

    public int getBeaconLevel() {
        return beaconLevel;
    }

    public void setBeaconLevel(int beaconLevel) {
        this.beaconLevel = beaconLevel;
        p.EstablirPropietat("beaconLevel", beaconLevel);
    }

    public int getCrystalLevel() {
        return crystalLevel;
    }

    public void setCrystalLevel(int crystalLevel, boolean drop) {
        this.crystalLevel = crystalLevel;
        if (crystalLevel < 0) crystalLevel = 0;
        p.EstablirPropietat("crystalLevel", crystalLevel);
        refreshBuildState(drop);
    }

    void loadValuesFromFile(GestorPropietats p) {
        this.p = p;
        location = p.ObtenirLocation("location");
        beaconLevel = p.ObtenirPropietatInt("beaconLevel");
        crystalLevel = p.ObtenirPropietatInt("crystalLevel");
        if (location == null) throw new RuntimeException("SkyNexus file does not contain location");
        getRealBeaconLevel();
        refreshBuildState(false);
    }


    public static void loadAll() {
        File[] listFiles = new File(getSkyNexusFolderPath()).listFiles();
        for (int i = 0; i < listFiles.length; i++) {
            File f = listFiles[i];
//            String name = f.getName();
//            String locationString = name.substring(0, name.length() - 4);
            SkyNexus skyNexus = load(f);
            registerInstance(skyNexus);
        }
    }

    public static SkyNexus load(File f) {
        SkyNexus s = new SkyNexus();
        GestorPropietats p = new GestorPropietats(f.getAbsolutePath());
        s.loadValuesFromFile(p);
        return s;
    }

    public static SkyNexus load(Location l) {
        SkyNexus s = new SkyNexus();
        GestorPropietats p = getSkyNexusFile(l);
        p.EstablirLocation("location", l);
        s.loadValuesFromFile(p);
        registerInstance(s);
        return s;
    }

    public static void registerInstance(SkyNexus s) {
        if (allInstances == null) allInstances = new HashMap<>();
        allInstances.put(s.location.toVector(), s);
    }

    public static Optional<SkyNexus> getNearestSkyNexus(Location location, float distance) {
        return getSkyNexusesNearby(location, distance).stream().findFirst();
    }

    public static List<SkyNexus> getSkyNexusesNearby(Location location, float distance) {
        return allInstances.entrySet().stream()
                .filter(e -> e.getKey().distance(location.toVector()) < distance)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    public static GestorPropietats getSkyNexusFile(Location l) {
        String skyNexusPath = getSkyNexusFolderPath();
        String fileName = Integer.toString((int) l.getX()) + "," + Integer.toString((int) l.getY()) + "," + Integer.toString((int) l.getZ()) + "," + Integer.toString(Bukkit.getWorlds().indexOf(l.getWorld()));
        GestorPropietats g = new GestorPropietats(skyNexusPath + "/" + fileName + ".txt");
        return g;
    }

    public static String getSkyNexusFolderPath() {
        return FastSurvival.getPlugin().getDataFolder() + "/" + "SkyNexus";
    }


    public Cuboid getRingCuboid(int ringIndex) {
        Cuboid c = Utils.getCuboidAround(location.clone().add(0, ringIndex * -1, 0), ringIndex + 1, 0, ringIndex + 1);
        return c;
    }

    static void dropRingItem(Location l) {
        l.getWorld().dropItemNaturally(l, SkyUtils.getSkyCrystal());
    }

    void destroyRing(Cuboid c, boolean drop) {
        List<Cuboid> walls = c.getWalls();
        walls.stream()
                .forEach(w -> w.getBlocks().forEach(b -> b.setType(Material.AIR)));
        // Drop item
        if (drop) {
            walls.stream().findFirst().ifPresent(w -> w.getBlocks().stream().findAny()
                    .ifPresent(block -> dropRingItem(block.getLocation())));
        }

    }

    void buildRing(Cuboid c) {
        AtomicInteger i = new AtomicInteger();
        c.getWalls()
                .forEach(w -> {
                    w.getBlocks().forEach(b -> {
                        if(b.getType() != Material.AIR && !isRingMaterial(b.getType())) b.breakNaturally();
                        b.setType(Material.QUARTZ_PILLAR);
                        Orientable orientable = (Orientable) b.getBlockData();
                        orientable.setAxis((i.get() % 2 != 0) ? Axis.X : Axis.Z);
                        b.setBlockData(orientable);
                    });
                    i.getAndIncrement();
                });
        Arrays.stream(c.corners()).forEach(b -> b.setType(Material.CHISELED_QUARTZ_BLOCK));
    }

    boolean isRingBuilt(Cuboid c) {
        return c.corners()[0].getType() == Material.CHISELED_QUARTZ_BLOCK;
    }

    void ensureRingState(int ringIndex, boolean built, boolean drop) {
        Cuboid c = getRingCuboid(ringIndex);
        boolean isBuilt = isRingBuilt(c);
        if (isBuilt && !built) destroyRing(c, drop);
        if (!isBuilt && built) buildRing(c);
    }

    boolean shouldRingIndexBeBuilt(int ringIndex) {
        return beaconLevel + 1 - ringIndex <= crystalLevel;
    }

    int numberOfRings() {
        return beaconLevel + 1;
    }

    void refreshBuildState(boolean drop) {
        for (int i = 0; i < numberOfRings(); i++) {
            ensureRingState(i, shouldRingIndexBeBuilt(i), drop);
        }
    }

    static Optional<Block> getNearestBeaconBlock(Location l) {
        Cuboid c = Utils.getCuboidAround(l, 5);
        return c.getBlocks().stream().filter(b -> b.getType() == Material.BEACON).findFirst();
    }

    static void handleEntityExplode(EntityExplodeEvent evt) {
//        Bukkit.broadcastMessage("Explosion!!");
        evt.blockList().forEach(block -> {
            if (isRingMaterial(block.getType()) || Utils.getBeaconMaterials().contains(block.getType())) {
                Optional<SkyNexus> nearestSkyNexus = getNearestSkyNexus(block.getLocation(), 10);
                nearestSkyNexus.ifPresent(sn -> {
                    if (isRingMaterial(block.getType()) && Utils.Possibilitat(10))
                        sn.setCrystalLevel(sn.getCrystalLevel() - 1, true);
                    if (Utils.getBeaconMaterials().contains(block.getType()))
                        sn.setCrystalLevel(0, true);
                    sn.refreshBeaconLevel();
                    sn.refreshBuildState(true);
                });

            }
        });
        evt.blockList().removeIf(b -> isRingMaterial(b.getType()));
    }

    static void handleBreakBlock(BlockBreakEvent evt) {
        Block b = evt.getBlock();
        Material m = b.getType();
        boolean ringMaterial = isRingMaterial(m);
        boolean beaconMaterial = Utils.getBeaconMaterials().contains(m);
        if (!ringMaterial && !beaconMaterial) return;
        Optional<SkyNexus> nearestSkyNexus = getNearestSkyNexus(b.getLocation(), 10);
        if (ringMaterial) {
            nearestSkyNexus.ifPresent(sn -> {
                sn.setCrystalLevel(sn.getCrystalLevel() - 1, false);
                dropRingItem(b.getLocation());
                if(sn.getCrystalLevel() > 0) evt.setCancelled(true);
            });
        }
        if (beaconMaterial) {
//            Bukkit.broadcastMessage("Broken beacon material");
            nearestSkyNexus.ifPresent(sn -> {
                Bukkit.getScheduler().runTaskLater(FastSurvival.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        sn.refreshBeaconLevel();
                    }
                }, 20 * 2);
                sn.setCrystalLevel(0, true);
            });
        }
    }

    public static List<Material> getRingMaterials() {
        return Arrays.asList(Material.QUARTZ_PILLAR, Material.CHISELED_QUARTZ_BLOCK);
    }

    private static boolean isRingMaterial(Material m) {
        return getRingMaterials().contains(m);
    }

    int getRealBeaconLevel() {
        Block block = location.getBlock();
        if (block.getType() != Material.BEACON) return 0;
        Beacon beaconState = (Beacon) block.getState();
        return beaconState.getTier();
    }

    void refreshBeaconLevel() {
        int realBeaconLevel = getRealBeaconLevel();
//        Bukkit.broadcastMessage("[R] realBeaconLevel: " + realBeaconLevel + "getBeaconLevel: " + getBeaconLevel());
        setBeaconLevel(realBeaconLevel);
        if (realBeaconLevel != getBeaconLevel()) {
            // If level has changed
//            Bukkit.broadcastMessage("[R] Level has changed!!!");
            setCrystalLevel(0, true);
        }
    }

    boolean isActive() {
        return getBeaconLevel() > 0;
    }

    static PlayerInteractEvent lastPlayerInteractEvent = null;
    static int playerInteractEventIndex = 0;

    static void handleRightClickBlockInEarth(PlayerInteractEvent evt) {
        playerInteractEventIndex++;
        Player p = evt.getPlayer();
        Block b = evt.getClickedBlock();
//        Bukkit.broadcastMessage("handleRightClickBlockInEarth" + playerInteractEventIndex);

        Optional<Block> beaconBlock = Optional.empty();
        if (b.getType() == Material.BEACON) {
            playerInteractEventIndex = 0;
            beaconBlock = Optional.of(b);
        } else if (Utils.getBeaconMaterials().contains(b.getType()) || isRingMaterial(b.getType())) {
            beaconBlock = getNearestBeaconBlock(b.getLocation());
//            getNearestSkyNexus(b.getLocation(), 10).ifPresent(sn -> {
//                sn.setCrystalLevel(sn.getCrystalLevel() - 1);
//                evt.getPlayer().getWorld().dropItemNaturally(b.getLocation(), SkyUtils.getSkyCrystal());
//                evt.setCancelled(true);
//            });
        }
        if (playerInteractEventIndex % 2 != 0) return;
        beaconBlock.ifPresent(beaconBlk -> {
            ItemStack item = p.getInventory().getItemInMainHand();
            Beacon beaconState = (Beacon) beaconBlk.getState();
            if (SkyUtils.isSkyCrystal(item)) {
//                Bukkit.broadcastMessage("SkyUtils.isSkyCrystal(item)");
                SkyNexus skyNexus = SkyNexus.load(beaconBlk.getLocation());
                skyNexus.refreshBeaconLevel();
                int crystalLevel = skyNexus.getCrystalLevel();
                if (skyNexus.getBeaconLevel() != 0 && crystalLevel < skyNexus.getBeaconLevel() + 1) {
                    skyNexus.setCrystalLevel(crystalLevel + 1, true);
                    ItemStack it = item.clone();
                    it.setAmount(1);
                    p.getInventory().removeItem(it);
                }
                evt.setCancelled(true);
                evt.setUseInteractedBlock(Event.Result.DENY);
                evt.setUseItemInHand(Event.Result.ALLOW);
            }
        });
        lastPlayerInteractEvent = evt;
    }

}

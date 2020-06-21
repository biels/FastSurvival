package com.biel.FastSurvival.Dimensions.Sky;

import com.biel.FastSurvival.FastSurvival;
import com.biel.FastSurvival.Utils.Cuboid;
import com.biel.FastSurvival.Utils.GestorPropietats;
import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Orientable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.biel.FastSurvival.Dimensions.Sky.SkyUtils.*;

public class SkyNexus {
    int id;
    Location location;
    int beaconLevel;
    int crystalLevel;
    boolean activated = false;
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
        if (location == null) return;
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
            if (skyNexus.location != null) {
                registerInstance(skyNexus);
            } else {
                skyNexus.delete();
                Bukkit.broadcastMessage("Sky nexus found without location and removed");
            }
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
                .filter(sn -> sn.getValue().getBeaconLevel() > 0)
                .sorted(Comparator.comparingDouble(n -> n.getKey().distanceSquared(location.toVector())))
//                .filter(e -> e.getKey().distance(location.toVector()) < distance)
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

    public Cuboid getBeaconLayerCuboid(int beaconIndex) {
        Cuboid c = Utils.getCuboidAround(location.clone().add(0, (beaconIndex * -1) - 1, 0), beaconIndex + 1, 0, beaconIndex + 1);
        return c;
    }

    public int getRealBeaconLevel() {
        int maxLayers = 4;
        for (int i = 0; i < maxLayers; i++) {
            Cuboid layer = getBeaconLayerCuboid(i);
            boolean allMatch = layer.getBlocks().stream().allMatch(b -> Utils.getBeaconMaterials().contains(b.getType()));
            if (!allMatch) return i;
        }
        return maxLayers;
    }

    //    int getRealBeaconLevel() {
//        Block block = location.getBlock();
//        if (block.getType() != Material.BEACON) return 0;
//        Beacon beaconState = (Beacon) block.getState();
//        return beaconState.getTier();
//    }

    static void dropRingItem(Location l) {
        // FIXME Ensure no accidental drops
        // l.getWorld().dropItemNaturally(l, SkyUtils.getSkyCrystal());
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
                        Material typeBefore = b.getType();
                        if (typeBefore != Material.AIR && !isRingMaterial(typeBefore)) b.breakNaturally();
                        b.setType(Material.QUARTZ_PILLAR);
                        Orientable orientable = (Orientable) b.getBlockData();
                        orientable.setAxis((i.get() % 2 != 0) ? Axis.X : Axis.Z);
                        b.setBlockData(orientable);
                        if (Utils.getBeaconMaterials().contains(typeBefore)) {
                            getNearestSkyNexus(b.getLocation(), 10).ifPresent(sn2 -> sn2.refreshBeaconLevel());
                        }
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
        Block glassBlock = getLocation().clone().add(0, 1, 0).getBlock();
        if (crystalLevel == getRealBeaconLevel() + 1) {
            glassBlock.setType(Material.LIGHT_BLUE_STAINED_GLASS);
        } else glassBlock.setType(Material.AIR);
    }

    void delete() {
        if (location != null) {
            refreshBeaconLevel();
            setCrystalLevel(0, true);
            refreshBuildState(false);
        }
        allInstances.remove(location.toVector());
        p.deleteFile();
        Bukkit.broadcastMessage("Deleted sky nexus at " + location.toVector().toString());
    }

    static Optional<Block> getNearestBeaconBlock(Location l) {
        Cuboid c = Utils.getCuboidAround(l, 5);
        return c.getBlocks().stream()
                .filter(b -> b.getType() == Material.BEACON)
                .min(Comparator.comparingDouble(b -> b.getLocation().distance(l)));
    }

    static List<Block> getBeaconBlocksNearby(Location l) {
        Cuboid c = Utils.getCuboidAround(l, 5);
        return c.getBlocks().stream()
                .filter(b -> b.getType() == Material.BEACON)
                .sorted(Comparator.comparingDouble(b -> b.getLocation().distance(l))).collect(Collectors.toList());
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
        boolean beaconBlock = m == Material.BEACON;
        boolean blueGlassBlock = m == Material.LIGHT_BLUE_STAINED_GLASS;
        if (!ringMaterial && !beaconMaterial && !beaconBlock && !blueGlassBlock) return;
        Optional<SkyNexus> nearestSkyNexus = getNearestSkyNexus(b.getLocation(), 10);
        if (beaconBlock) {
            nearestSkyNexus.ifPresent(sn -> sn.delete());
        }
        if (blueGlassBlock) {
            nearestSkyNexus.ifPresent(sn -> {
                if (evt.getBlock().getRelative(BlockFace.DOWN).getType() != Material.BEACON) return;
                sn.activatePortal();
                evt.setCancelled(true);
            });
        }
        if (ringMaterial) {
            nearestSkyNexus.ifPresent(sn -> {
                int oldCrystalLevel = sn.getCrystalLevel();
                sn.setCrystalLevel(oldCrystalLevel - 1, false);
                if (sn.getCrystalLevel() < oldCrystalLevel) dropRingItem(b.getLocation());
                if (sn.getCrystalLevel() > 0) evt.setCancelled(true);
            });
        }
        if (beaconMaterial) {
//            Bukkit.broadcastMessage("Broken beacon material");
            nearestSkyNexus.ifPresent(sn -> {
//                Bukkit.getScheduler().runTaskLater(FastSurvival.getPlugin(), new Runnable() {
//                    @Override
//                    public void run() {
//                        sn.refreshBeaconLevel();
//                    }
//                }, 20 * 2);
                sn.refreshBeaconLevel();
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

    boolean isReady() {
        return getCrystalLevel() >= getBeaconLevel() + 1;
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
                getBeaconBlocksNearby(beaconBlk.getLocation()).stream()
                        .filter(otherBeacon -> !otherBeacon.equals(beaconBlk))
                        .forEach(otherBeacon -> {
                            if (allInstances.containsKey(otherBeacon.getLocation().toVector())) {
                                getNearestSkyNexus(otherBeacon.getLocation(), 10).ifPresent(sn -> {
                                    sn.delete();
                                });
                                otherBeacon.breakNaturally();

                            }
                        });
                if (skyNexus.getBeaconLevel() != 0 && crystalLevel < skyNexus.getBeaconLevel() + 1) {
                    skyNexus.setCrystalLevel(crystalLevel + 1, true);
                    ItemStack it = item.clone();
                    it.setAmount(1);
                    p.getInventory().removeItem(it);
                    Sound levelUpSound = Sound.BLOCK_END_PORTAL_FRAME_FILL;
                    p.getLocation().getWorld().playSound(p.getLocation(), levelUpSound, SoundCategory.MASTER, 100, 1F + (0.2F * crystalLevel));
                }
                if (skyNexus.getBeaconLevel() != 0 && crystalLevel == skyNexus.getBeaconLevel()) {
                    p.getLocation().getWorld().playSound(p.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, SoundCategory.MASTER, 100, (float) 1.4);
                }

                evt.setCancelled(true);
                evt.setUseInteractedBlock(Event.Result.DENY);
                evt.setUseItemInHand(Event.Result.ALLOW);
            }
        });
        lastPlayerInteractEvent = evt;
    }

    List<Player> getPlayersInRange() {
        return Utils.getNearbyPlayers(location, 16);
    }

    void activatePortal() {
        if (!isActive() || !isReady()) return;
        Block glassBlock = location.clone().add(0, 1, 0).getBlock();
        glassBlock.setType(Material.MAGENTA_STAINED_GLASS);
        List<Player> players = getPlayersInRange();
        players.forEach(p -> {
            Vector playerToBeacon = Utils.CrearVector(
                    p.getLocation().clone(),
                    location.clone().add(0.5, 0.5, 0.5)
            );
            playerToBeacon.setY(0);
            Vector ac = playerToBeacon.multiply(1);
            Vector al = ac.clone().crossProduct(new Vector(0, 1, 0));
            Vector a = ac.clone().add(al).normalize().multiply(0.2);
            p.setVelocity(p.getVelocity().add(al.clone().multiply(1.1)).add(new Vector(0, 1.5, 0)));
            p.setAllowFlight(true);
        });
        repositionPlayersInPortal(players);
        players.forEach(player -> player.setNoDamageTicks(10 * 20));
        if (getSky() == null) {
            glassBlock.setType(Material.GREEN_STAINED_GLASS);
            players.forEach(player -> player.sendMessage(ChatColor.GREEN + "Opening connection to the sky..."));
            Bukkit.getScheduler().scheduleSyncDelayedTask(FastSurvival.getPlugin(), new Runnable() {
                @Override
                public void run() {

                    loadSkyIfNecessary();
                    players.forEach(player -> player.sendMessage(ChatColor.GREEN + "Done!"));
                    glassBlock.setType(Material.MAGENTA_STAINED_GLASS);
                    repositionPlayersInPortal(players);
                }
            }, 10);
        }

        int accelerationTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(FastSurvival.getPlugin(), new Runnable() {
            @Override
            public void run() {
                players.forEach(p -> {
                    Vector playerToBeacon = Utils.CrearVector(
                            p.getLocation().clone(),
                            location.clone().add(0.5, 0.5, 0.5)
                    );
                    playerToBeacon.setY(0);
                    Vector ac = playerToBeacon.multiply(0.1);
                    Vector al = ac.clone().crossProduct(new Vector(0, 1, 0));
                    Vector a = ac.clone().add(al).normalize().multiply(0.2);
//                    System.out.println(a.toString());
                    double x = ((p.getLocation().getY() - (location.getY() - 1)) * 4.1); // wrt  player Y
                    double powerCurve = 0.00015 * x * x - 0.03 * x + 1 + 0.01;
                    if (powerCurve > 1.5) powerCurve = 1.5;
                    if (powerCurve < -0.33) powerCurve = -0.33;
                    Vector nextVelocity = p.getVelocity().add(ac).add(al.clone().multiply(0.295 * powerCurve));
                    Vector velocitySample = nextVelocity.clone();
                    velocitySample.setY(0);
                    if (velocitySample.length() > 1.4) velocitySample.multiply(0.9);
                    nextVelocity.setY(0.15 + 0.0045 * x);
                    nextVelocity.setX(velocitySample.getX());
                    nextVelocity.setZ(velocitySample.getZ());
                    p.setVelocity(nextVelocity);
//                    p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 25, 15, false, false, false));
                });
            }
        }, 1, 1);
        int finishTaskId = Bukkit.getScheduler().scheduleSyncDelayedTask(FastSurvival.getPlugin(), new Runnable() {
            @Override
            public void run() {
                Bukkit.getScheduler().cancelTask(accelerationTaskId);
                players.forEach(p -> {
                    Location skyLocation = getSkyLocation(p.getLocation(), 10);
                    p.teleport(skyLocation);
                    teleportPlayerToSky(p);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 60, 0, false, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 600, 4, false, false, false));
                    p.sendMessage("To the sky!");
                    p.setAllowFlight(false);
                });
                activated = false;
                setCrystalLevel(getCrystalLevel() - Utils.NombreEntre(1, getCrystalLevel()), false);
            }
        }, 20 * 9);
    }

    private void repositionPlayersInPortal(List<Player> players) {
        players.forEach(p -> {
            Vector relativeVec = Utils.CrearVector(SkyNexus.this.location, p.getLocation());
            double relLength = relativeVec.length();
            if (!((!(relLength > 5)) || (!(relLength < 1)))) return;
            relativeVec.normalize().multiply(Utils.NombreEntre(4, 6));
            Location absoluteLoc = SkyNexus.this.location.clone().add(relativeVec);
            Location relocationTarget = SkyNexus.this.location.getWorld().getHighestBlockAt(absoluteLoc).getLocation().add(0, 4, 0);
            p.teleport(relocationTarget);
        });
    }

}

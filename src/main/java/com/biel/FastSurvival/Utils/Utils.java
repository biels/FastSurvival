package com.biel.FastSurvival.Utils;

import com.biel.FastSurvival.FastSurvival;
import com.biel.FastSurvival.Translations.LanguageStrings;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.stream.Collector.Characteristics.UNORDERED;


public class Utils {

    public static double smin(double a, double b, double k) {
        double h = clamp(0.5 + 0.5 * (a - b) / k, 0.0, 1.0);
        return mix(a, b, h) - k * h * (1.0 - h);
    }

    public static double clamp(double v, double min, double max) {
        return Math.min(Math.max(v, min), max);
    }

    ;

    public static double mix(double start, double end, double t) {
        return start * (1 - t) + end * t;
    }

    public static double map(double x, double x1, double x2, double y1, double y2) {
        double m = (y2 - y1) / (x2 - x1);
        double c = y1 - m * x1; // point of interest: c is also equal to y2 - m * x2, though float math might lead to slightly different results.

        return m * x + c;
    }


    public static String L(String code) {
        return LanguageStrings.getString(code);
    }

    public static Location getLocationFromArgs(String[] args, int argsIndex, World world) {
        try {
            return new Location(world, Integer.parseInt(args[argsIndex + 0]), Integer.parseInt(args[argsIndex + 1]), Integer.parseInt(args[argsIndex + 2]));
        } catch (Exception e) {
            return null;
        }
    }

    public static ItemStack setItemNameAndLore(ItemStack item, String name, String... lore) {
        ItemMeta im = item.getItemMeta();
        if (!name.isEmpty()) {
            im.setDisplayName(name);
        }
        im.setLore(Arrays.asList(lore));
        item.setItemMeta(im);
        return item;
    }

    public static ItemStack setItemName(ItemStack item, String name) {
        return setItemNameAndLore(item, name);
    }

    public static ItemStack setItemLore(ItemStack item, String... lore) {
        return setItemNameAndLore(item, "", lore);
    }

    public static void donarItem(Player p, Material mat, int quantitat, String name) {
        Inventory inv = p.getInventory();
        ItemStack item = new ItemStack(mat, quantitat);
        if (!name.isEmpty()) {
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        inv.addItem(item);
    }

    public static void donarItem(Player p, Material mat, String name) {
        donarItem(p, mat, 1, name);
    }

    public static void donarItem(Player p, Material mat, int quantitat) {
        donarItem(p, mat, quantitat, "");
    }

    public static void donarItem(Player p, Material mat) {
        donarItem(p, mat, 1, "");
    }

    public static void donarItemsPlayer(Player ply, ArrayList<ItemStack> items) {
        for (ItemStack item : items) {
            giveItemStack(item, ply);
        }
    }

    @SuppressWarnings("deprecation")
    public static Boolean trySpendItem(ItemStack i, Player p) {
        if (p.getGameMode() == GameMode.SURVIVAL) {
            PlayerInventory inv = p.getInventory();

            if (!inv.removeItem(i).isEmpty()) {
                return false;
            }

            p.updateInventory();
        }
        return true;
    }

    public static void establirItem(Player p, Material mat, int slot, int quantitat) {
        Inventory inv = p.getInventory();
        ItemStack item = new ItemStack(mat, quantitat);
        inv.setItem(slot, item);
    }

    public static void establirItem(Player p, Material mat, int slot) {
        establirItem(p, mat, slot, 1);
    }

    public static int getArmorSlot(ItemStack itemstack) {
        int slot = -1;
        String matstr = itemstack.getType().name();
        if (matstr.contains("HELMET")) {
            slot = 3;
        }
        if (matstr.contains("CHESTPLATE")) {
            slot = 2;
        }
        if (matstr.contains("LEGGINGS")) {
            slot = 1;
        }
        if (matstr.contains("BOOTS")) {
            slot = 0;
        }
        return slot;
    }

    public static ItemStack getItemInHand(LivingEntity l) {
        if (l instanceof Player) {
            return ((Player) l).getInventory().getItemInMainHand();
        }
        EntityEquipment eq = l.getEquipment();
        if (eq != null) {
            return eq.getItemInMainHand();
        } else {
            return null;
        }
    }

    public static boolean isArmor(ItemStack itemstack) {
        int slot = getArmorSlot(itemstack);
        if (slot == -1) {
            return false;
        } else {
            return true;
        }
    }

    public static void giveItemStack(ItemStack itemstack, Player d) {
        boolean isArmor = isArmor(itemstack);
        int slot = getArmorSlot(itemstack);

        if (isArmor) {
            EstablirArmadura(d, itemstack, slot);
        } else {
            d.getInventory().addItem(itemstack);
        }


    }

    public static void EstablirArmadura(Player ply, ItemStack stack, int slot) {
        ItemStack[] armor = ply.getInventory().getArmorContents();
        armor[slot] = stack;
        ply.getInventory().setArmorContents(armor);

    }

    public static void clearPlayers(ArrayList<Player> players) {
        for (Player p : players) {
            clearPlayer(p);
        }
    }

    public static void clearPlayer(Player ply) {
        ply.getInventory().clear();
        clearEffects(ply);
        clearArmor(ply);
        ply.setGameMode(GameMode.SURVIVAL);
        ply.setHealth(ply.getMaxHealth());
        ply.setFireTicks(0);
        ply.setExp(0);
        ply.setLevel(0);
        ply.setFoodLevel(20);
        ply.setExhaustion(20);
        ply.setFallDistance(0);
    }

    public static void clearEffects(Player ply) {
        for (PotionEffect effect : ply.getActivePotionEffects()) {
            ply.removePotionEffect(effect.getType());
        }
    }

    public static void clearArmor(Player player) {
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
    }

    public static ItemStack createColoredArmor(Material part, Color color) {
        ItemStack helm = new ItemStack(part);
        LeatherArmorMeta meta = (LeatherArmorMeta) helm.getItemMeta();
        meta.setColor(color);
        helm.setItemMeta(meta);
        return helm;
        //    	player.getInventory.setHelmet(helm);
    }

    public static int NombreEntre(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    public static boolean Possibilitat(int percentatge, int max) {
        int n = NombreEntre(0, max);
        if (n <= percentatge) {
            return true;
        }
        return false;

    }

    public static boolean Possibilitat(int percentatge) {
        return Possibilitat(percentatge, 100);
    }

    public static void healDamageable(Damageable d, Double amount) {
        Double newAmount = d.getHealth() + amount;
        if (newAmount < 0) {
            newAmount = 0D;
        }
        if (newAmount > d.getMaxHealth()) {
            newAmount = d.getMaxHealth();
        }
        d.setHealth(newAmount);
    }

    public static void setMetadata(Metadatable object, String key, Object value) {
        object.setMetadata(key, new FixedMetadataValue(FastSurvival.getPlugin(), value));
    }

    public static MetadataValue getMetadata(Metadatable object, String key) {
        List<MetadataValue> values = object.getMetadata(key);
        for (MetadataValue value : values) {
            // Plugins are singleton objects, so using == is safe here
            if (value.getOwningPlugin() == FastSurvival.getPlugin()) {
                return value;
            }
        }
        return null;
    }

    public static ArrayList<ItemStack> getInventoryPercent(Inventory inv, float percent) {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        for (ItemStack i : inv.getContents()) {
            if (i == null) {
                continue;
            }
            if (Utils.Possibilitat((int) percent)) {
                Material type = i.getType();
                int amount = (int) Math.floor(i.getAmount() * (percent / 100) * 2);
                ItemStack itemStack = i.clone();
                itemStack.setAmount(amount);
                list.add(itemStack);
            }

        }
        return list;
    }

    public static void BreakBlockLater(final Block block, int delay, final boolean give) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(FastSurvival.getPlugin(), new Runnable() {
            public void run() {
                if (give) {
                    block.breakNaturally();
                } else {
                    block.setType(Material.AIR);
                }

            }
        }, delay);
    }

    public static ArrayList<BlockFace> getFacesNSEW() {
        ArrayList<BlockFace> facesNSEW = new ArrayList<BlockFace>();
        facesNSEW.add(BlockFace.NORTH);
        facesNSEW.add(BlockFace.SOUTH);
        facesNSEW.add(BlockFace.EAST);
        facesNSEW.add(BlockFace.WEST);
        return facesNSEW;
    }

    public static BlockFace getRandomFaceNSEW(Random random) {
        return getRandom(getFacesNSEW(), random);
    }

    public static <T> T getRandom(ArrayList<T> list, Random random) {
        return list.get(random.nextInt(list.size()));
    }

    public static ArrayList<BlockFace> getAdjacentFaces(Block b, ArrayList<BlockFace> fToCheck) {
        return getAdjacentFaces(b, fToCheck, 1);
    }

    public static ArrayList<BlockFace> getAdjacentFaces(Block b, ArrayList<BlockFace> fToCheck, int dist) {
        ArrayList<BlockFace> faces = new ArrayList<BlockFace>();
        for (BlockFace f : fToCheck) {
            Block bl = b.getRelative(f);
            if (bl.getType() == Material.LEGACY_LOG) {
                faces.add(f);
            }
        }
        return faces;
    }

    //	public static BlockFace getOppositeFace(BlockFace f){
    //		ArrayList<BlockFace> f1 = new ArrayList<BlockFace>();
    //		ArrayList<BlockFace> f2 = new ArrayList<BlockFace>();
    //		//--
    //		//--
    //		for (BlockFace f : f1){
    //			if(f.)
    //		}
    //	}
    public static Vector CrearVector(Location inici, Location fi) {
        return fi.toVector().subtract(inici.toVector());
    }

    public static Vector CrearVector(Vector inici, Vector fi) {
        return fi.clone().subtract(inici);
    }

    public static ArrayList<Location> getLocationsCircle(Location center, Double radius, int espai) {
        return getLocationsCircle(center, radius, espai, 0, 360);
    }

    public static ArrayList<Location> getLocationsCircle(Location center, Double radius, int espai, int start, int end) {
        ArrayList<Location> locs = new ArrayList<Location>();
        World world = center.getWorld();
        int i = start; //0

        while (i <= end) { // 360
            float angle = i;
            double toRadians = Math.PI / 180;
            //Location locSpawn = plyr.getLocation().add(0,1,0);
            Location spawnpoint = center.clone().add(new Location(world, Math.cos(angle * toRadians) * radius, 0, Math.sin(angle * toRadians) * radius));

            //Vector dir2 = spawnpoint.toVector().subtract(center.toVector()).normalize().multiply(0.5);
            locs.add(spawnpoint);
            i = i + espai;

        }
        return locs;
    }

    public static ArrayList<Location> getSphereLocations(Location center, Double radius, boolean hollow) {
        ArrayList<Location> locs = new ArrayList<Location>();
        World world = center.getWorld();
        Cuboid c = getSquareCuboid(center, radius);
        for (Block b : c.getBlocks()) {
            if (isInSphere(center, radius, b)) {
                //				if (hollow){
                //					ArrayList<BlockFace> arr = new ArrayList<BlockFace>();
                //					arr.add(BlockFace.NORTH);
                //					arr.add(BlockFace.SOUTH);
                //					arr.add(BlockFace.EAST);
                //					arr.add(BlockFace.WEST);
                //					boolean valid = true;
                //					for (BlockFace face : arr){
                //						Block blk = b.getRelative(face);
                //						if (isInSphere(center, radius, blk)){valid = false;}
                //					}
                //					if (valid == false){continue;}
                //				}
                if (hollow && isInSphere(center, radius - 1, b)) {
                    continue;
                }
                locs.add(b.getLocation());
            }
        }
        return locs;
    }

    public static boolean isInSphere(Location center, Double radius, Block b) {
        return b.getLocation().distance(center) <= radius;
    }

    public static Cuboid getSquareCuboid(Location center, Double radius) {
        return new Cuboid(center.clone().subtract(radius, radius, radius), center.clone().add(radius, radius, radius));
    }

    public static ArrayList<LivingEntity> ordrerEnitiesByProximity(Location loc, ArrayList<LivingEntity> ents) {
        ArrayList<LivingEntity> remaining = ents;
        ArrayList<LivingEntity> ordered = new ArrayList<LivingEntity>();
        while (remaining.size() > 0) {
            LivingEntity nearestEntity = getNearestEntity(loc, ents);
            ordered.add(nearestEntity);
            remaining.remove(nearestEntity);
        }
        return ordered;
    }

    public static LivingEntity getNearestEntity(Location loc, ArrayList<LivingEntity> ents) {
        if (ents.size() == 0) {
            return null;
        }
        LivingEntity nearest = ents.get(0);
        double mindistance = nearest.getLocation().distance(loc);
        for (LivingEntity e : ents) {
            double dist = e.getLocation().distance(loc);
            if (dist < mindistance) {
                mindistance = dist;
                nearest = e;
            }
        }
        return nearest;

    }

    public static ArrayList<LivingEntity> getNearbyEnemies(LivingEntity entity, double dist, boolean lineSight) {
        return getNearbyEnemies(entity, entity.getLocation(), dist, lineSight);
    }

    public static ArrayList<LivingEntity> getNearbyEnemies(LivingEntity entity, Location center, double dist, boolean lineSight) {
        Objects.requireNonNull(entity, "Tried to get nearby enemies of a null entity");
        ArrayList<LivingEntity> ents = new ArrayList<LivingEntity>();
        World world = entity.getWorld();
        //Bukkit.broadcastMessage(Integer.toString(world.getEntities().size()));
        for (Entity e : world.getEntitiesByClass(LivingEntity.class)) {
            if (!(e instanceof LivingEntity)) {
                break;
            }
            LivingEntity le = (LivingEntity) e;
            if (le.getLocation().distance(center) <= dist) {
                if (lineSight) {
                    if (!le.hasLineOfSight(entity)) {
                        continue;
                    }
                }
                if (le.equals(entity)) {
                    continue;
                }
                ents.add(le);
            }
        }
        //Bukkit.broadcastMessage(Integer.toString(ents.size()));
        return ents;
    }

    public static ArrayList<LivingEntity> getNearbyEnemies(Location center, double dist) {
        ArrayList<LivingEntity> ents = new ArrayList<LivingEntity>();
        for (Entity e : center.getWorld().getEntitiesByClass(LivingEntity.class)) {
            if (!(e instanceof LivingEntity)) {
                break;
            }
            LivingEntity le = (LivingEntity) e;
            if (le.getLocation().distance(center) <= dist) {

                ents.add(le);
            }
        }
        //Bukkit.broadcastMessage(Integer.toString(ents.size()));
        return ents;
    }

    public static ArrayList<Player> getNearbyPlayers(Location l, double dist) {
        ArrayList<Player> players = new ArrayList<Player>();
        for (Player ply : l.getWorld().getPlayers()) {
            //if(!ply.getUniqueId().equals(l.get)){
            Location ploc = ply.getLocation();
            if (ploc.distance(l) <= dist) {

                players.add(ply);
            }
            //}
        }
        return players;
    }

    public static ArrayList<Player> getNearbyPlayers(Entity p, double dist) {
        ArrayList<Player> nearbyPlayers = getNearbyPlayers(p.getLocation(), dist);
        if (p instanceof Player) {
            nearbyPlayers.remove(p);
        }
        return nearbyPlayers;
    }

    public static Entity getEntityFromUUID(UUID id, World w) {
        for (Entity e : w.getEntities()) {
            if (e.getUniqueId() == id) {
                return e;
            }
        }
        return null;
    }

    public static Entity getEntityFromUUID(UUID id) {
        for (World w : Bukkit.getWorlds()) {
            Entity entityFromUUID = getEntityFromUUID(id, w);
            if (entityFromUUID != null) {
                return entityFromUUID;
            }
        }
        return null;
    }

    public static Cuboid getCuboidAround(Location loc, int x, int y, int z) {
        return new Cuboid(loc.getWorld(), loc.getBlockX() - x, loc.getBlockY() - y, loc.getBlockZ() - z, loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z);
    }

    public static Cuboid getCuboidAround(Location loc, int r) {
        return getCuboidAround(loc, r, r, r);
    }

    public static ArrayList<Block> getCylBlocks(Location loc, int r, int height, Boolean fill) {
        ArrayList<Block> blks = new ArrayList<Block>();
        int heightDone = 0;
        while (heightDone < height) {
            Location center = loc.clone().add(new Vector(0, heightDone, 0));
            Cuboid c = getCuboidAround(center, r, 0, r);
            for (Block b : c.getBlocks()) {
                double dist = b.getLocation().distance(center);
                Boolean isValid = true;
                if (fill == true) {
                    isValid = (dist <= r);
                }
                if (fill == false) {
                    isValid = (dist >= r - 1 && dist <= r);
                }
                if (isValid) {
                    //b.setType(mat);
                    blks.add(b);
                }
            }
            heightDone++;
        }
        return blks;
    }

    public static List<Block> getCylBlocks(Location loc, int r, int height, Boolean fill, Vector normal) {
        List<Block> blks = new ArrayList<>();
//        Vector axis = normal.getCrossProduct(new Vector(0, 1, 0)).normalize();
//        double angle = new Vector(0,1,0).angle(normal);

        getCylBlocks(new Location(loc.getWorld(), 0, 0, 0), r, height, fill)
                .forEach(block -> {
                    Vector relativeToAxis = block.getLocation().toVector();
//                    Bukkit.broadcastMessage(MessageFormat.format("relativeToAxis: {0} locLength: {1} bloklen: {2}",
//                            relativeToAxis.toString(), loc.length(), block.getLocation().length()));
                    Vector vector = getVectorInPlaneY(normal.clone(), relativeToAxis);
                    blks.add(loc.clone().add(vector).getBlock());
                });
        return blks;
    }

    public static ArrayList<Block> getOuterCylBlocks(Location loc, int r, int height, Boolean fill) {
        ArrayList<Block> cylBlocks = getCylBlocks(loc, r, height, true);
        int newR = r - 1;
        if (newR >= 0) {
            cylBlocks.removeAll(getCylBlocks(loc, newR, height, true));
        }

        return cylBlocks;

    }

    public static ArrayList<Location> drawTriangle(Location point1, Location point2, Location point3) {
        ArrayList<Location> locs = new ArrayList<Location>();
        Vector v1 = Utils.CrearVector(point1, point2);
        Vector v2 = Utils.CrearVector(point1, point3);
        Vector v3 = Utils.CrearVector(point2, point3);
        float angleY = v1.angle(new Vector(0, 1, 0));
        float angleX = v1.angle(new Vector(1, 0, 0));
        float angleZ = v1.angle(new Vector(0, 0, 1));
        double angle = Math.min(Math.min(angleX, Math.min(angleY, angleZ)), 0.78539816 / 2);
        double smallTriangleHeightY = (1.0 / Math.cos(angle));
        double smallTriangleHeightXY = (smallTriangleHeightY / Math.cos(v1.angle(new Vector(1, 0, 0))));
        Vector unitV1 = v1.clone().multiply(smallTriangleHeightY / v1.length());
        Vector unitV2 = v2.clone().multiply(smallTriangleHeightY / v1.length());
        Vector unitV3 = v3.clone().multiply(smallTriangleHeightY / v1.length());
        for (double i = 0.0; i < v1.length() + smallTriangleHeightY; i++) {
            Utils.getLineBetween(unitV1.clone().multiply(i), unitV2.clone().multiply(i))
                    .forEach(vector -> locs.add(vector.toLocation(Objects.requireNonNull(point1.getWorld())).add(point1)));
        }
        return locs;
    }

    public static Vector getClosestAxisVector(Vector v, boolean canBeYAxis) {
        double toRadians = 180 / Math.PI;
        double angleToX = v.angle(new Vector(1, 0, 0));
        if (angleToX > 90.0 / toRadians) angleToX = v.angle(new Vector(-1, 0, 0));
        double angleToY = v.angle(new Vector(0, 1, 0));
        if (angleToY > 90.0 / toRadians) angleToY = v.angle(new Vector(0, -1, 0));
        double angleToZ = v.angle(new Vector(0, 0, 1));
        if (angleToZ > 90.0 / toRadians) angleToZ = v.angle(new Vector(0, 0, -1));

        if (angleToX < angleToY && angleToX < angleToZ) return new Vector(1, 0, 0);
        if (canBeYAxis && angleToY < angleToX && angleToY < angleToZ) return new Vector(0, 1, 0);
        if (angleToZ < angleToX && angleToZ < angleToY) return new Vector(0, 0, 1);
        else {
            if (canBeYAxis) return new Vector(0, 1, 0); // else return Y axis
            return new Vector(1, 0, 0);
        }
    }

    public static Vector getVectorInPlane(Vector normal, Vector relativeToAxis, Vector originalAxis) {
        Vector rotationAxis = originalAxis.getCrossProduct(normal).normalize();
        double x = rotationAxis.getX();
//        Bukkit.broadcastMessage("X" + x);
//            Bukkit.broadcastMessage("rotationAxis len: " + rotationAxis.length());
        if (Double.isNaN(rotationAxis.length())) {
            return relativeToAxis.clone();
        }
//        Bukkit.broadcastMessage("No NaN");
        double angle = normal.angle(originalAxis);
        return relativeToAxis.clone().rotateAroundAxis(rotationAxis, angle);
    }

    public static Vector getVectorInPlaneY(Vector normal, Vector relativeToAxis) {
        return getVectorInPlane(normal, relativeToAxis, new Vector(0, 1, 0));
    }

    public static void fillChestRandomly(Block chest, ArrayList<ItemStack> it) {
        if (chest == null) {
            return;
        }
        if (chest.getType() != Material.CHEST && chest.getType() != Material.TRAPPED_CHEST) {
            return;
        }
        Chest ch = (Chest) chest.getState();
        for (ItemStack i : it) {
            int slot = Utils.NombreEntre(0, ch.getInventory().getSize() - 1);
            ch.getInventory().setItem(slot, i);

        }
    }

    //	public static PotionType getRandomEnumItem(){
    //		return PotionType.values()[Utils.NombreEntre(0, PotionType.values().length - 1)];
    //	}
    public static BlockFace getBlocksSharedFace(Block b1, Block b2) {
        for (BlockFace f : BlockFace.values()) {
            if (b1.getRelative(f).equals(b2)) {
                return f;
            }
        }
        return null;
    }

    public static PotionType getRandomPotionType() {
        return PotionType.values()[Utils.NombreEntre(0, PotionType.values().length - 1)];
    }

    public static ItemStack getRandomPotion() {
        Potion p = new Potion(getRandomPotionType());
        if (Utils.Possibilitat(55)) {
            p.setSplash(true);
        }
        //p.setLevel(1);
        return p.toItemStack(1);
    }

    public static ArrayList<ItemStack> getBrewingItems() {
        ArrayList<ItemStack> i = new ArrayList<ItemStack>();
        i.add(new ItemStack(Material.LEGACY_NETHER_WARTS));
        i.add(new ItemStack(Material.GLOWSTONE));
        i.add(new ItemStack(Material.REDSTONE));
        i.add(new ItemStack(Material.SPIDER_EYE));
        i.add(new ItemStack(Material.MAGMA_CREAM));
        i.add(new ItemStack(Material.BLAZE_POWDER));
        i.add(new ItemStack(Material.SUGAR));
        i.add(new ItemStack(Material.GHAST_TEAR));
        i.add(new ItemStack(Material.GOLDEN_CARROT));
        return i;
    }

    ArrayList<String> freadHumanReadableList(String text) {
        int lastIndex = 0;
        ArrayList<String> parts = new ArrayList<String>();
        while (parts.size() < 3) {
            System.out.println(lastIndex);
            if (lastIndex > text.length()) {
                lastIndex = text.length() - 1;
            }
            if (lastIndex < 0) {
                lastIndex = 0;
            }
            int beginIndex = text.indexOf(":", lastIndex) + 2;
            int endIndex = text.indexOf(",", beginIndex);
            if (endIndex == -1) {
                endIndex = text.length();
            }
            System.out.println("Begin: " + beginIndex);
            System.out.println("End: " + endIndex);

            parts.add(text.substring(beginIndex, endIndex));
            lastIndex = endIndex;
        }
        return parts;
    }

    public static ArrayList<String> readHumanReadableList(String text) {
        ArrayList<String> Tparts = new ArrayList<String>();
        String[] parts = text.split(",");
        for (String cp : parts) {
            String[] cparts = cp.split(":");
            Tparts.add(cparts[1].substring(1));
        }
        return Tparts;
    }

    //"W: 1234, E: 345, R: 33"
    public static String writeHumanReadableList(ArrayList<String> c, ArrayList<String> v) {
        String f = "";
        for (String cap : c) {
            int index = c.indexOf(cap);
            String val = v.get(index);
            if (index != 0) {
                f = f + ", ";
            }
            f = f + cap + ": " + val;
        }
        return f;
    }

    //	ArrayList<String> c = new ArrayList<String>();
    //	ArrayList<String> v = new ArrayList<String>();
    //	c.add("X");v.add(Integer.toString(l.getBlockX()));
    //	return writeHumanReadableList(c, v);
    public static String writeHumanReadableLocation(Location l, boolean world) {
        ArrayList<String> c = new ArrayList<String>();
        ArrayList<String> v = new ArrayList<String>();
        if (world) {
            c.add("W");
            v.add(l.getWorld().getName());
        }
        c.add("X");
        v.add(Integer.toString(l.getBlockX()));
        c.add("Y");
        v.add(Integer.toString(l.getBlockY()));
        c.add("Z");
        v.add(Integer.toString(l.getBlockZ()));
        //Return
        return writeHumanReadableList(c, v);
    }

    public static Location readHumanReadableLocation(String tXYZ, World w) {
        int x, y, z;
        if (w == null) {
            return null;
        }
        ArrayList<String> xyz = readHumanReadableList(tXYZ); //X:10, Y:10, Z: 20
        x = Integer.parseInt(xyz.get(0));
        y = Integer.parseInt(xyz.get(1));
        z = Integer.parseInt(xyz.get(2));
        //		Bukkit.broadcastMessage(Integer.toString(x));
        //		Bukkit.broadcastMessage(Integer.toString(y));
        //		Bukkit.broadcastMessage(Integer.toString(z));
        //Return
        return new Location(w, x, y, z);
    }

    public static Location readHumanReadableLocation(String tWXYZ) {
        int x, y, z;
        World w;
        ArrayList<String> wxyz = readHumanReadableList(tWXYZ); //W: PiloWorld, X:10, Y:10, Z: 20
        w = Bukkit.getWorld(wxyz.get(0));
        x = Integer.parseInt(wxyz.get(1));
        y = Integer.parseInt(wxyz.get(2));
        z = Integer.parseInt(wxyz.get(3));
        //Return
        if (w == null) {
            return null;
        }
        return new Location(w, x, y, z);
    }


    //	public static ArrayList<?> getEnumArrayFromIntArray(ArrayList<Integer> arr){
    //		ArrayList<Enum> list = new ArrayList<Enum>();
    //		for (Integer s : arr){
    //			list.add(AttackGroups.values()[s]);
    //		}
    //		return list;
    //	}
    public static ArrayList<Integer> getIntArrayFromStringArray(ArrayList<String> arr) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (String s : arr) {
            list.add(Integer.parseInt(s));
        }
        return list;
    }

    public static List<Vector> getLine(Vector origin, Vector direction, int length) {
        ArrayList<Vector> l = new ArrayList<>();
        Vector current = origin.clone();
        for (int i = 0; i < length; i++) {
            l.add(current.clone());
            current.add(direction);
        }
        return l;
    }

    public static List<Vector> getLineBetweenPoints(Vector origin, Vector end) {
        ArrayList<Vector> l = new ArrayList<>();
        Vector current = origin.clone();
        Vector currentAtOrig = new Vector(0, 0, 0);
        Vector vector = Utils.CrearVector(origin, end);
        Vector direction = vector.clone().normalize();

        int i = 0;
        while (currentAtOrig.length() <= vector.length()) {
            l.add(current.clone());
            current.add(direction);
            currentAtOrig.add(direction);
            i++;
            if(i > 1000) break;
        }
        return l;
    }


    public static List<Vector> getLineBetween(Vector origin, Vector end) {
        Vector direction = Utils.CrearVector(origin, end);
        double length = Math.round(direction.length());
        if (length < 1) length = 1;
        return getLine(origin, direction.clone().normalize(), (int) length);
    }

    public static List<Vector> getLineBetween(Location origin, Location end) {
        return getLineBetween(origin.toVector(), end.toVector());
    }

    public static List<Vector> getLineBetween(Block origin, Block end) {
        return getLineBetween(origin.getLocation().toVector(), end.getLocation().toVector());
    }

//    public static List<Vector> get2dRectangleAround(Vector a, Vector b) {
//        Vector center = a.getMidpoint(b);
//        Vector up = a.getCrossProduct(b);
//        Vector front = Utils.CrearVector(a, b).angle()
//
//        return get2dRectangleAround(center, up, )
//    }
    public static List<Vector> get2dRectangleAround(Vector center, Vector up, Vector front, int xw, int zw) {
        ArrayList<Vector> l = new ArrayList<>();
        up = up.normalize();
        front = front.normalize();
        //Vector corner = center.clone().add(new Vector(-1 * xw / 2, 0, -1 * zw / 2));
        Vector direction = front.clone().crossProduct(up).normalize().multiply(-1);
        Vector corner = center.clone().add(front.clone().multiply(-1 * zw / 2)).add(direction.clone().multiply(-1 * xw / 2));
        Vector current = corner.clone();
        for (int i = 0; i < 4; i++) {
            int w = i % 2 == 0 ? xw : zw;
            for (int j = 0; j < w; j++) {
                current.add(direction);
                l.add(current.clone());
            }
            direction = direction.crossProduct(up);
        }
        return l.stream().distinct().collect(Collectors.toList());
    }

    public static Vector rotateVectorCC(Vector vec, Vector axis, double theta) {
        double x = vec.getX();
        double y = vec.getY();
        double z = vec.getZ();
        double u = axis.getX();
        double v = axis.getY();
        double w = axis.getZ();
        double xPrime = u * (u * x + v * y + w * z) * (1.0D - Math.cos(theta)) + x * Math.cos(theta) + (-w * y + v * z) * Math.sin(theta);
        double yPrime = v * (u * x + v * y + w * z) * (1.0D - Math.cos(theta)) + y * Math.cos(theta) + (w * x - u * z) * Math.sin(theta);
        double zPrime = w * (u * x + v * y + w * z) * (1.0D - Math.cos(theta)) + z * Math.cos(theta) + (-v * x + u * y) * Math.sin(theta);
        return new Vector(xPrime, yPrime, zPrime);
    }

    public static ItemStack getWitherSkull() {
        return new ItemStack(Material.LEGACY_SKULL_ITEM, 1, (byte) 1);
    }

    private static final Collector<Double, double[], Double> VARIANCE_COLLECTOR = Collector.of( // See https://en.wikipedia.org/wiki/Algorithms_for_calculating_variance
            () -> new double[3], // {count, mean, M2}
            (acu, d) -> { // See chapter about Welford's online algorithm and https://math.stackexchange.com/questions/198336/how-to-calculate-standard-deviation-with-streaming-inputs
                acu[0]++; // Count
                double delta = d - acu[1];
                acu[1] += delta / acu[0]; // Mean
                acu[2] += delta * (d - acu[1]); // M2
            },
            (acuA, acuB) -> { // See chapter about "Parallel algorithm" : only called if stream is parallel ...
                double delta = acuB[1] - acuA[1];
                double count = acuA[0] + acuB[0];
                acuA[2] = acuA[2] + acuB[2] + delta * delta * acuA[0] * acuB[0] / count; // M2
                acuA[1] += delta * acuB[0] / count;  // Mean
                acuA[0] = count; // Count
                return acuA;
            },
            acu -> acu[2] / (acu[0] - 1.0), // Var = M2 / (count - 1)
            UNORDERED);

    public static double getCornerHeightVariance(Cuboid cuboid) {
        Block[] corners = cuboid.corners();
        Double variance = Arrays.stream(corners)
                .mapToDouble(b -> cuboid.getWorld().getHighestBlockYAt(b.getX(), b.getZ())).boxed()
                .collect(VARIANCE_COLLECTOR);
        return variance;
    }

    public static double getCornerHeightAverage(Cuboid cuboid) {
        Block[] corners = cuboid.corners();
        double y = Arrays.stream(corners)
                .mapToInt(b -> cuboid.getWorld().getHighestBlockYAt(b.getX(), b.getZ()))
                .average().getAsDouble();
        return y;
    }

    public static List<Material> getBeaconMaterials() {
        return Arrays.asList(Material.IRON_BLOCK, Material.GOLD_BLOCK, Material.DIAMOND_BLOCK, Material.EMERALD_BLOCK);
    }

    public static double sigmoid(double x) {
        return sigmoid(x, 10);
    }

    public static double sigmoid(double x, double calcTreshold) {
        double y;
        if (x < -1 * calcTreshold)
            y = 0;
        else if (x > calcTreshold)
            y = 1;
        else
            y = 1 / (1 + Math.exp(-x));
        return y;
    }

    public static Location getNearestFloor(Location from) {
        return getNearestWall(from, new Vector(0, -1, 0));
    }

    public static Location getNearestWall(Location from, Vector direction) {
        Location current = from.clone();
        int limit = 50;
        while (current.getBlock().isPassable() && limit > 0) {
            current.add(direction);
            limit--;
        }
        if (limit <= 0) return current.getWorld().getHighestBlockAt(current).getLocation();
        return current;
    }

    public static double distanceFromPointToLine(Vector point, Vector lineStart, Vector lineEnd) {
        Vector line = lineEnd.clone().subtract(lineStart);
        Vector pointToLine = point.clone().subtract(lineStart);
        double lineLength = line.length();
        double lineDot = line.dot(pointToLine);
        double distance = Math.abs(lineDot / lineLength);
        return distance;
    }

    public static int getHighestBlockYAt(WorldInfo worldInfo, LimitedRegion limitedRegion, int x, int z) {
        // while (limitedRegion.getType(X, Y, Z) != Material.AIR) Y++;
        int y = worldInfo.getMinHeight();
        while (limitedRegion.getType(x, y, z) != Material.AIR) {
            y++;
        }
        return y;
    }

}

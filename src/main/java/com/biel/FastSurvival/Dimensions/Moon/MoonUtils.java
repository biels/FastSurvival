package com.biel.FastSurvival.Dimensions.Moon;

import com.biel.FastSurvival.FastSurvival;
import com.biel.FastSurvival.Utils.Cuboid;
import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.util.Vector;

import javax.rmi.CORBA.Util;
import java.util.ArrayList;
import java.util.List;

public class MoonUtils {

    public static World getMoon() {
        return Bukkit.getWorld("moon");//Bukkit.getWorld("Moon");
    }

    public static World getEarth() {
        return Bukkit.getWorlds().get(0);//Bukkit.getWorld("Moon");
    }

    public static void loadMoon() {

        WorldCreator wc = new WorldCreator("moon");
        wc.type(WorldType.NORMAL);
        wc.environment(Environment.NORMAL);
        wc.generator(new MoonChunkGenerator());
        Bukkit.getServer().createWorld(wc);

    }

    public static Boolean IsMoon(World w) {
        if (getMoon() == null) {
            return false;
        }
        return (w.equals(getMoon()));
    }

    public static Boolean IsEarth(World w) {
        if (getEarth() == null) {
            return false;
        }
        return (w.equals(getEarth()));
    }

    public static Boolean IsInMoon(Entity e) {
        if (e == null) {
            return false;
        }
        return IsMoon(e.getWorld());
    }

    public static ItemStack getSpaceGlass() {
        return Utils.setItemNameAndLore(new ItemStack(Material.GLASS), ChatColor.AQUA + "Glass bubble", ChatColor.WHITE + "Allows entities to breath in the moon");
    }

    public static void spaceGlassRecipe() {
        ShapedRecipe r = new ShapedRecipe(FastSurvival.getKey("spaceGlass"), getSpaceGlass());
        r.shape("GGG", "G G", "   ");
        r.setIngredient('G', Material.GLASS);
        Bukkit.getServer().addRecipe(r);
    }

    public static void portalActivateToMoon(ArrayList<Player> p, Block stoneButton) {
        loadMoonIfNecessary();
		Location moonReference = copyPortalToMoon(stoneButton.getLocation());
		deletePortal(stoneButton.getLocation());
        for (Player pl : p) {
			Location targetLocation = getRelativeLocation(pl.getLocation(), stoneButton.getLocation(), moonReference);
			pl.teleport(targetLocation);
        }

    }

    public static void portalActivateToEarth(ArrayList<Player> p, Block stoneButton) {
		Location earthReference = copyPortalToEarth(stoneButton.getLocation());
		deletePortal(stoneButton.getLocation());
        for (Player pl : p) {
			Location targetLocation = getRelativeLocation(pl.getLocation(), stoneButton.getLocation(), earthReference);
			pl.teleport(targetLocation);
		}
    }

    public static void teleportPlayerToMoon(Player p) {
        loadMoonIfNecessary();
        Location moonLocation = getMoonLocation(p.getLocation(), 1);
        int highestBlockYAt = getMoon().getHighestBlockYAt(moonLocation.getBlockX(), moonLocation.getBlockZ());
        moonLocation.setY(highestBlockYAt + 1);
        p.teleport(moonLocation);
        //p.setBedSpawnLocation(moonLocation, true);
    }

    public static void teleportPlayerToEarth(Player p) {
        Location moonLocation = getEarthLocation(p.getLocation(), 1);
        int highestBlockYAt = getEarth().getHighestBlockYAt(moonLocation.getBlockX(), moonLocation.getBlockZ());
        moonLocation.setY(highestBlockYAt + 1);
        p.teleport(moonLocation);
        //p.setBedSpawnLocation(moonLocation, true);
    }

    public static void loadMoonIfNecessary() {
        if (getMoon() == null) {
            loadMoon();
        }
    }

    public static ArrayList<Material> getMoonPortalMaterials() {
        ArrayList<Material> m = new ArrayList<Material>();
        m.add(Material.IRON_BLOCK);
        m.add(Material.IRON_BARS);
        m.add(Material.DIAMOND_BLOCK);
        m.add(Material.GLASS);
        m.add(Material.GLOWSTONE);
        m.add(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
        m.add(Material.NETHER_BRICK_FENCE);
        m.add(Material.FURNACE);
        m.add(Material.REDSTONE_TORCH);
        m.add(Material.REDSTONE_WALL_TORCH);
        m.add(Material.REDSTONE_BLOCK);
        m.addAll(Tag.BUTTONS.getValues());

        return m;
    }

    public static ArrayList<Block> detectMoonPortalBlocks(Location l) {
        ArrayList<Block> blks = new ArrayList<Block>();
        Cuboid detected = Utils.getCuboidAround(l, 5, 6, 5);
        List<Block> blocks = detected.getBlocks();

        for (Block b : blocks) {
            Material t = b.getType();
            if (getMoonPortalMaterials().contains(t)) {
                blks.add(b);
            }
        }
        return blks;

    }

    public static int getButtonElevation(Location l) {
        ArrayList<Block> blks = detectMoonPortalBlocks(l);
        int lowestY = 250;
        for (Block b : blks) {
            if (b.getY() < lowestY) {
                lowestY = b.getY();
            }
        }
        return l.getBlockY() - lowestY;
    }

    public static Location getRelativeLocation(Location origin, Location originReference, Location destinyReference) {
        Vector relative = Utils.CrearVector(originReference, origin);
        return destinyReference.clone().add(relative);
    }

    public static Location getMoonLocation(Location overworldLocation, int offset) {
        //		int highestBlockYAt = getMoon().getHighestBlockYAt(overworldLocation.getBlockX() + offset, overworldLocation.getBlockZ());

        return new Location(getMoon(), overworldLocation.getBlockX(), overworldLocation.getY(), overworldLocation.getBlockZ());
    }

    public static Location getEarthLocation(Location moonLocation, int offset) {
        int highestBlockYAt = getEarth().getHighestBlockYAt(moonLocation.getBlockX(), moonLocation.getBlockZ());

        return new Location(getEarth(), moonLocation.getBlockX(), highestBlockYAt + offset, moonLocation.getBlockZ());
    }

    public static Location getHighestYLoc(Location loc) {
        int highestBlockYAt = loc.getWorld().getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ());
        // TODO Max of 3 samples
        Location newLoc = loc.clone();
        newLoc.setY(highestBlockYAt);
        return newLoc;
    }

    //	public static Block getMoonBlock(Block overworldBlock){
    //
    //	}
    public static Location copyPortalToMoon(Location owBLoc) {
        ArrayList<Block> portalBlocks = detectMoonPortalBlocks(owBLoc);
        Location moonLocation = getHighestYLoc(getMoonLocation(owBLoc, 0));
        Bukkit.broadcastMessage("moonLocation: " + moonLocation);
        Bukkit.broadcastMessage("buttonElevation: " + getButtonElevation(owBLoc));
        moonLocation.add(0, getButtonElevation(owBLoc), 0);
        setPortalCopyBlocks(owBLoc, portalBlocks, moonLocation);
        return moonLocation;
    }

    public static Location copyPortalToEarth(Location mBLoc) {
        ArrayList<Block> portalBlocks = detectMoonPortalBlocks(mBLoc);
        Location earthLocation = getHighestYLoc(getEarthLocation(mBLoc, 0));
        earthLocation.add(0, getButtonElevation(mBLoc), 0);
        setPortalCopyBlocks(mBLoc, portalBlocks, earthLocation);
        return earthLocation;
    }

    public static void setPortalCopyBlocks(Location mBLoc,
                                           ArrayList<Block> portalBlocks, Location earthLocation) {
        for (Block b : portalBlocks) {
            Vector relV = Utils.CrearVector(mBLoc, b.getLocation());
            Location rel = earthLocation.clone().add(relV);
            Block nb = rel.getBlock();
            Material type = nb.getType();
            if (type.isSolid()) {
                nb.setType(b.getType());
                if (type == Material.COAL_BLOCK && Utils.Possibilitat(70)) {
                    nb.setType(Material.GLASS);
                }
                if (type == Material.REDSTONE_BLOCK && Utils.Possibilitat(30)) {
                    nb.setType(Material.GLASS);
                }
                nb.setBlockData(b.getBlockData());
            }
        }
        for (Block b : portalBlocks) {
            Vector relV = Utils.CrearVector(mBLoc, b.getLocation());
            Location rel = earthLocation.clone().add(relV);
            Block nb = rel.getBlock();
            if (!nb.getType().isSolid()) {
                nb.setType(b.getType());
                nb.setBlockData(b.getBlockData());
//				nb.setData(b.getData());
            }
        }
    }

    public static void deletePortal(Location owBLoc) {
        ArrayList<Block> portalBlocks = detectMoonPortalBlocks(owBLoc);
        Location l = owBLoc.clone();
        //TRANSPARENT BLOCKS
        for (Block b : portalBlocks) {
            Vector relV = Utils.CrearVector(owBLoc, b.getLocation());
            Location rel = l.clone().add(relV);
            Block nb = rel.getBlock();
            if (!nb.getType().isSolid()) {
                nb.setType(Material.AIR);
            }
        }
        //SOLID BLOCKS
        for (Block b : portalBlocks) {
            Vector relV = Utils.CrearVector(owBLoc, b.getLocation());
            Location rel = l.clone().add(relV);
            Block nb = rel.getBlock();
            if (nb.getType().isSolid()) {
                nb.setType(Material.AIR);
            }
        }
        Utils.BreakBlockLater(owBLoc.getBlock(), 40, false);
    }

    public static Material getMoonSurfaceMaterial() {
        return Material.WHITE_CONCRETE_POWDER;
    }

    public static Material getMoonInnerMaterial() {
        return Material.WHITE_CONCRETE;
    }
}

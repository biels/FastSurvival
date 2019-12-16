package com.biel.FastSurvival.Utils;

//import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class WGUtils {
//    private static WorldGuardPlugin getWorldGuard() {
//        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
//
//        // WorldGuard may not be loaded
//        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
//            return null; // Maybe you want throw an exception instead
//        }
//
//        return (WorldGuardPlugin) plugin;
//    }

    public static Boolean canBuild(Player p, Location l) {
//        WorldGuardPlugin wg = getWorldGuard();
//        if (wg == null) {
//            return true;
//        }

//        return wg.canBuild(p, l);
        return true;
    }

    public static Boolean canBuild(Player p, Block b) {
        return true;
//        return canBuild(p, b.getLocation());
    }
}

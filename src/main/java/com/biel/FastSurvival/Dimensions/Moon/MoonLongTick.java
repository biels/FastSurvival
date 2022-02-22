package com.biel.FastSurvival.Dimensions.Moon;

import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

public class MoonLongTick implements Runnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if(MoonUtils.IsInMoon(p)) {
                tickPlayer(p);
            }
        }
    }

    public void tickPlayer(Player p) {
        if(p.getLocation().add(0, -1, 0).getBlock().getType() == Material.LIME_STAINED_GLASS) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 6 * 20, 0, false, false));
        }
    }
}

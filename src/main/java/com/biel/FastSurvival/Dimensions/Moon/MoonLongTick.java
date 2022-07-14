package com.biel.FastSurvival.Dimensions.Moon;

import com.biel.FastSurvival.FastSurvival;
import com.biel.FastSurvival.Utils.Noise.InfiniteVoronoiNoise;
import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MoonLongTick implements Runnable {
    @Override
    public void run() {
        List<Location> locs = new ArrayList<>();
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if(MoonUtils.IsInMoon(p)) {
                tickPlayer(p);
                locs.add(p.getLocation());
            }
        }
        locs.stream().map(Location::getChunk).distinct().forEach(this::tickIvns);
    }

    public void tickPlayer(Player p) {
        if(p.getLocation().add(0, -1, 0).getBlock().getType() == Material.LIME_STAINED_GLASS) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 6 * 20, 0, false, false));
        }
    }

    public void tickIvns(Chunk source) {
        // TODO read info from SQLite
    }
}

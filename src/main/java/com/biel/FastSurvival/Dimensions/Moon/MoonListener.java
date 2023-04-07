package com.biel.FastSurvival.Dimensions.Moon;

import com.biel.FastSurvival.FastSurvival;
import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class MoonListener implements Listener {

    public static void handlePlayerInteractEvent(PlayerInteractEvent evt) {
        final Player p = evt.getPlayer();
        Block b = evt.getClickedBlock();
        if (b == null) {
            return;
        }
        Block r = b.getRelative(evt.getBlockFace());
        Action a = evt.getAction();
        if (p.getInventory().getItemInMainHand() == MoonUtils.getSpaceGlass()) {
            p.getEquipment().setHelmet(MoonUtils.getSpaceGlass());
            evt.setCancelled(true);
            return;
        }
        if (MoonUtils.IsInMoon(p)) {

            if (a == Action.RIGHT_CLICK_BLOCK) {
                Material t = p.getInventory().getItemInMainHand().getType();
                if (t == Material.WATER_BUCKET) {
                    r.setType(Material.SNOW_BLOCK);
                    // play a sound
                    p.playSound(p.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 0.5f);
                    evt.setCancelled(true);
                }
                if (t == Material.LAVA_BUCKET) {
                    if (Utils.Possibilitat(30)) {
                        r.setType(Material.STONE);
                    } else {
                        r.setType(Material.OBSIDIAN);
                    }
                    // play a sound
                    p.playSound(p.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 0.7f);

                    evt.setCancelled(true);
                }
                if (t == Material.FLINT_AND_STEEL) {
                    p.playSound(p.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1, 0.5f);
                    evt.setCancelled(true);
                }
                if (t == Material.TNT) {
                    evt.setCancelled(false);
                }
                if (t == Material.GLOWSTONE) {
                    evt.setCancelled(true);
                }
                ///EFFECT
                if (evt.isCancelled()) {
                    evt.getPlayer().getWorld().playEffect(evt.getClickedBlock().getLocation(), Effect.SMOKE, 4);
                }

            }
        }


    }

    @EventHandler
    public void BlockFromToEvent(BlockPlaceEvent event) {
        final Player p = event.getPlayer();
        Block b = event.getBlock();
        if (MoonUtils.IsInMoon(p)) {
            if (b.getType() == Material.TORCH) {
                event.setCancelled(true);
            }
            if (b.getType() == Material.WALL_TORCH) {
                event.setCancelled(true);
            }
            if (b.getType() == Material.CAMPFIRE) {
                event.setCancelled(true);
            }
            if (b.getType() == Material.FIRE) {
                event.setCancelled(true);
            }
        }

        if (p.getInventory().getItemInMainHand() == MoonUtils.getSpaceGlass()) {
            p.getEquipment().setHelmet(MoonUtils.getSpaceGlass());
        }
    }

    @EventHandler
    public void PlayerSleepEvent(PlayerBedEnterEvent event) {
        final Player p = event.getPlayer();
        Location loc = event.getBed().getLocation();
        if (MoonUtils.IsInMoon(p)) {
            event.setCancelled(true);
            loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 1, false, true);
            p.sendMessage("You can't sleep in the moon!");
            // play a sound
            p.playSound(p.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 0.5f);
        }

    }

    @EventHandler
    public void onDmg(EntityDamageEvent evt) {
        if (MoonUtils.IsInMoon(evt.getEntity())) {
            if (evt.getCause() == DamageCause.FALL) {
                Double d = evt.getDamage();
                d = d / 6;
                d = d - 1;
                if (d < 0) {
                    d = 0.0;
                    evt.setCancelled(true);
                }
                evt.setDamage(d);
            }
        }
    }

    @EventHandler
    public void onDecay(LeavesDecayEvent evt) {
        if (MoonUtils.IsMoon(evt.getBlock().getWorld())) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBrak(BlockBreakEvent evt) {
        Block b = evt.getBlock();
        World w = b.getWorld();
        if (MoonUtils.IsMoon(w)) {
            //--
            if (b.getType() == Material.LEGACY_LEAVES && b.getLocation().getBlockY() > 80) {
                evt.setCancelled(true);
            }


        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent evt) {
        World w = evt.getWorld();
        if (MoonUtils.IsMoon(w)) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent evt) {
        World to = evt.getTo().getWorld();
        World from = evt.getFrom().getWorld();
        if (MoonUtils.IsMoon(to)) {
            MoonUtils.giveMoonPotionEffects(evt.getPlayer());
        }
        if (MoonUtils.IsMoon(to)) {
            MoonUtils.clearMoonPotionEffects(evt.getPlayer());
        }
    }

    Integer moonLongTickTaskId = null;
    @EventHandler
    public void onWorldLoad(WorldLoadEvent evt) {
        if (MoonUtils.IsMoon(evt.getWorld())) {
            Bukkit.getScheduler().runTaskTimer(FastSurvival.getPlugin(), new MoonLongTick(), 0, 5 * 20);
        }
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent evt) {
        if (MoonUtils.IsMoon(evt.getWorld())) {
            if(moonLongTickTaskId != null) {
                Bukkit.getScheduler().cancelTask(moonLongTickTaskId);
            }
        }
    }
}


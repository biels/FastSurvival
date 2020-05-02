package com.biel.FastSurvival.Bows;

import com.biel.FastSurvival.Bows.BowUtils.BowType;
import com.biel.FastSurvival.FastSurvival;
import com.biel.FastSurvival.Turrets.TurretUtils;
import com.biel.FastSurvival.Utils.Utils;
import com.biel.FastSurvival.Utils.WGUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.rmi.CORBA.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CustomBowsListener implements Listener {
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent evt) {
        // Skeletons only
        if (evt.isCancelled()) {
            return;
        }
        if (!(evt.getEntity() instanceof Arrow)) {
            return;
        }
        Arrow arr = (Arrow) evt.getEntity();
        if (!(arr.getShooter() instanceof Skeleton)) {
            return;
        }
        Skeleton sk = (Skeleton) arr.getShooter();
        ItemStack item = sk.getEquipment().getItemInMainHand();
        BowType type = BowUtils.getBowType(item);
        if (type == null) {
            return;
        }
        float f = 1F;
        arr.setMetadata("BowType", new FixedMetadataValue(FastSurvival.getPlugin(), type.ordinal()));
        arr.setMetadata("Force", new FixedMetadataValue(FastSurvival.getPlugin(), f));
        switch (type) {
            case BOUNCY:
                break;
            case ELECTRIC:
                break;
            case ENDER:
                break;
            case EXPLOSIVE:
                break;
            case ICY:
                break;
            case MAGNETIC:
                break;
            case MULTI:
                multiShotVector(arr, sk, type, f);
                break;
            case SKY_EXPLOSIVE:
                break;
            case TORCH:
                break;
            case WATER:
                break;
            case SKY_JET:
                makeSnowJet(arr, sk, type, f);
                break;
            case WITHER:
                Utils.healDamageable(sk, 3.2);
                break;
            default:
                break;

        }

    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent evt) {
        // Players (no skeletons)
        if (!(evt.getEntity() instanceof Player)) {
            return;
        }
        if (evt.getBow() == null) {
            return;
        }
        if (!(evt.getProjectile() instanceof Arrow)) {
            return;
        }
        Arrow arr = (Arrow) evt.getProjectile();
        Player p = (Player) evt.getEntity();
        ItemStack item = evt.getBow();
        BowType type = BowUtils.getBowType(item);
        if (type == null) {
            return;
        }
        float f = evt.getForce();
        arr.setMetadata("BowType", new FixedMetadataValue(FastSurvival.getPlugin(), type.ordinal()));
        arr.setMetadata("Force", new FixedMetadataValue(FastSurvival.getPlugin(), f));
        Location pLoc = p.getLocation();
        World world = arr.getWorld();
        switch (type) {
            case ENDER:
                world.playSound(pLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 4 * f, 1);
                break;
            case EXPLOSIVE:
                world.playSound(pLoc, Sound.ENTITY_GENERIC_EXPLODE, 5 * f, 2);
                break;
            case MAGNETIC:
                world.playSound(pLoc, Sound.ENTITY_IRON_GOLEM_HURT, 5 * f, 1.4F);
                break;
            case TORCH:
                world.playSound(pLoc, Sound.ENTITY_ITEM_PICKUP, 7 * f, 1.2F);
                break;
            case BOUNCY:
                world.playSound(pLoc, Sound.ENTITY_SLIME_ATTACK, 7 * f, 1.2F);
                break;
            case ICY:
                break;
            case WATER:
                world.playSound(pLoc, Sound.ENTITY_GENERIC_SPLASH, 7 * f, 1.2F);
                break;
            case WITHER:
                world.playSound(pLoc, Sound.ENTITY_WITHER_SHOOT, 0.8F * f, 1.2F);
                break;
            case ELECTRIC:
                break;
            case SKY_EXPLOSIVE:
                break;
            case SKY_JET:
                makeSnowJet(arr, p, type, f);
                break;
            case MULTI:
                multiShotVector(arr, p, type, f);
                p.getWorld().playSound(p.getLocation(), Sound.ITEM_CROSSBOW_SHOOT, 0.8f, 1.1F);
                break;
            default:
                break;


        }
    }

    public void multiShot(Arrow arr, LivingEntity p, BowType type, float f) {
        int amp = Math.round(1 + 6 * f);
        int i = amp * -1;
        while (i <= amp) {
            float angle = p.getLocation().getYaw() + (5 * i) + 90;
            double toRadians = Math.PI / 180;
            //Location locSpawn = plyr.getLocation().add(0,1,0);
            Location spawnpoint = p.getLocation().add(0, 1.05, 0).add(new Location(p.getWorld(), Math.cos(angle * toRadians), 0, Math.sin(angle * toRadians)));

            Vector dir2 = spawnpoint.toVector().subtract(p.getLocation().add(0, 1, 0).toVector()).normalize().multiply(0.5);
            Arrow arrow = (Arrow) p.getWorld().spawnEntity(spawnpoint, EntityType.ARROW);
            //Bukkit.broadcastMessage(Float.toString(plyr.getLocation().getYaw()));
            arrow.setShooter(p);
            arrow.setFireTicks((int) (100 * f));
            arrow.setVelocity(dir2.normalize().setY(arr.getVelocity().normalize().getY()).normalize().multiply(arr.getVelocity().length()));
            arrow.setMetadata("BowType", new FixedMetadataValue(FastSurvival.getPlugin(), type.ordinal()));
            arrow.setMetadata("Force", new FixedMetadataValue(FastSurvival.getPlugin(), f));
            arrow.setTicksLived(20 * 4 + 10);
            i = i + 1;

        }
    }

    public void multiShotVector(Arrow arr, LivingEntity p, BowType type, float f) {

        Vector center = p.getLocation().add(0, 1.05, 0).toVector();
        double toRadians = Math.PI / 180;
        float radius = (float) (1.1f * Math.sqrt(f));
        Vector direction = p.getLocation().getDirection();
        Vector frontSpawnPoint = direction.clone().multiply(radius);
        float spacingAngle = 5.5f - f * 1.4f;
        if (f >= 1.0f) spacingAngle -= 0.6f;
        int numArrows = (int) (2 * (Math.floor(5 * (f - 0.10) / 0.9f)) + 1);
//		Bukkit.broadcastMessage("numArrows " + numArrows);
//		Bukkit.broadcastMessage("dmg " + arr.getDamage());
        Vector lateralAxis = direction.getCrossProduct(new Vector(0, 1, 0)).normalize();
        Vector headAxis = direction.getCrossProduct(lateralAxis);
//		Bukkit.broadcastMessage("lateralAxis: " + lateralAxis.toString());
//		Bukkit.broadcastMessage("headAxis: " + headAxis.toString());
        for (int j = 0; j < numArrows; j++) {
            Location spawnPoint = direction.clone().multiply(radius)
                    .rotateAroundAxis(headAxis, spacingAngle * toRadians * (j - Math.floor(numArrows / 2.0f))).toLocation(p.getWorld());
            Location spawnLoc = spawnPoint.clone().add(center);
//			spawnLoc.getBlock().setType(Material.GOLD_BLOCK);
//			spawnPointFlat.clone().add(center).toLocation(p.getWorld()).getBlock().setType(Material.REDSTONE_BLOCK);
            Arrow arrow = (Arrow) p.getWorld().spawnEntity(spawnLoc, EntityType.ARROW);
            arrow.setShooter(p);
            arrow.setFireTicks((int) (20));

            arrow.setDamage(2.5f);
            arrow.setVelocity(spawnPoint.toVector().normalize().clone().add(new Vector(0, 0.05, 0)).normalize().multiply(3f * f));
            arrow.setMetadata("BowType", new FixedMetadataValue(FastSurvival.getPlugin(), type.ordinal()));
            arrow.setMetadata("Force", new FixedMetadataValue(FastSurvival.getPlugin(), f));
            arrow.setTicksLived(20 * 4 + 10);
            arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        }
        arr.remove();
    }

    public void makeSkyExplosiveEffect(Arrow arr, LivingEntity p, BowType type, float f, LivingEntity hitEntity) {
        Location center = p.getLocation().toVector().add(new Vector(0, 1, 0)).toLocation(p.getWorld());
        double toRadians = Math.PI / 180;
        float radius = 6.5f;
        Vector direction = p.getLocation().getDirection();
        Vector lateralAxis = direction.getCrossProduct(new Vector(0, 1, 0)).normalize();
        Vector headAxis = lateralAxis.getCrossProduct(direction);
        int tntNum = 7;
        List<Location> tntLocs = new ArrayList<>();
        double lowerAngle = 120.0;
        for (int i = 0; i < tntNum; i++) {
            Location spawnPoint = headAxis.clone().multiply(radius)
                    .rotateAroundAxis(direction, ((i * (360.0 - lowerAngle) / tntNum) - lowerAngle / 2) * toRadians).toLocation(p.getWorld());
            Location spawnLoc = spawnPoint.clone().add(center);
            Block b = spawnLoc.getBlock();
            if (!b.isEmpty() || !b.getRelative(BlockFace.DOWN).isPassable()) continue;
            tntLocs.add(spawnLoc);
            b.setType(Material.TNT);
        }
        Collections.shuffle(tntLocs);
        @NotNull BukkitTask tntRunnable = new BukkitRunnable() {
            TNTPrimed activeTnt;
            int tntIndex = -1;
            int engineOffTicks = 3;

            public void clearTnts() {
                tntLocs.forEach(l -> l.getBlock().setType(Material.AIR));
            }

            @Override
            public void run() {
                if (activeTnt == null) {
//                    Bukkit.broadcastMessage("Create");
                    tntIndex++;
                    if (tntIndex >= tntLocs.size()) {
                        cancel();
                        return;
                    }
                    Location loc = tntLocs.get(tntIndex);
                    if (loc.getBlock().getType() == Material.TNT){
                        loc.getBlock().setType(Material.AIR);
                        activeTnt = (TNTPrimed) p.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
                        activeTnt.setYield(1f);
                        activeTnt.setVelocity(new Vector(0, 0.6, 0));
                        activeTnt.setGravity(false);
                    }
                } else {
                    Location location = hitEntity.getEyeLocation();

                    Vector toPlayer = Utils.CrearVector(activeTnt.getLocation(), location);
                    if (toPlayer.length() > 8) {
                        toPlayer = Utils.CrearVector(activeTnt.getLocation(), location.clone().add(location.getDirection()).add(0, -0.5, 0));
                    }
                    if (toPlayer.length() < 1.4) {
                        activeTnt.setFuseTicks(0);
                    }
                    double gravity = -0.000;
                    if (!activeTnt.getLocation().clone().add(0, -4, 0).getBlock().isPassable()) {
                        gravity = 0.035;
                    }
                    if (toPlayer.length() > 1.4 && !activeTnt.getLocation().clone().add(0, -2, 0).getBlock().isPassable()) {
                        gravity = 0.18;
                    }
                    Vector acceleration = toPlayer.clone().add(new Vector(0, gravity, 0)).normalize().multiply(0.44);
                    if (engineOffTicks == -1) {
                        acceleration.multiply(2.5);

                    }
                    if (engineOffTicks > 0 && engineOffTicks != 1) {
                        acceleration.multiply(-0.3);
                    }
                        engineOffTicks--;
                    if (toPlayer.angle(activeTnt.getVelocity()) / toRadians > 10 && engineOffTicks < -4) {
                        engineOffTicks = 5;
                    }
                    Vector newVelocity = activeTnt.getVelocity().clone().add(acceleration);
                    if (newVelocity.length() > 1.8 && engineOffTicks != -1 && engineOffTicks != 0) {
                        newVelocity.normalize().multiply(1.8);
                    }
                    if (activeTnt.isOnGround()) {
                        activeTnt.setFuseTicks(0);
                    }
                    activeTnt.setVelocity(newVelocity);
                    if (activeTnt.isDead()) {
                        activeTnt = null;
                    }
                    if (hitEntity.isDead()) {
                        clearTnts();
                        cancel();
                        return;
                    }
                }

            }
        }.runTaskTimer(FastSurvival.getPlugin(), 1, 1);
    }

    public void makeSnowJet(Arrow arr, LivingEntity p, BowType type, float f) {
        List<Location> snowLocs = new ArrayList<>();
        List<Vector> snowVels = new ArrayList<>();
        double toRadians = Math.PI / 180;
        @NotNull BukkitTask tntRunnable = new BukkitRunnable() {
            int removingIndex = 0;
            int teleportingIndex = 0;
            int count = 0;
            int step = 0;
            int goToBlockTicks = 10;
            Vector normal = null;
            Vector randomFlat = Vector.getRandom().setY(0);
            @Override
            public void run() {
//                if (hasBeenOnGround || count > 500) cancel();
                switch (step) {
                    case 0:
                        // Create trail
                        Vector loc = arr.getLocation().toVector();
                        double length = arr.getVelocity().length();
                        Location currentLocation = arr.getVelocity().clone().normalize().multiply(-1 * (length + 1))
                                .add(loc).toLocation(p.getWorld());
                        List<Vector> line = Utils.getLine(currentLocation.toVector(), arr.getVelocity().normalize(), (int) Math.round(length));
                        line.forEach(b -> {
                            b.toLocation(p.getWorld()).getBlock().setType(count % 2 == 0 ? Material.SNOW_BLOCK : Material.SNOW_BLOCK);
                            snowLocs.add(b.toLocation(p.getWorld()));
                            snowVels.add(arr.getVelocity().clone());
                        });
                        arr.setVelocity(arr.getVelocity().add(Vector.getRandom().normalize().multiply(0.01)));
                        // Next
                        if (arr.isOnGround() || arr.isDead()) {
                            step = 1;
                            Vector playerToMid = Utils.CrearVector(p.getLocation(), snowLocs.get(snowLocs.size() / 2));
                            Vector playerToLast = Utils.CrearVector(p.getLocation(), snowLocs.get(snowLocs.size() - 1));
                            normal = playerToMid.getCrossProduct(playerToLast);
                        }
                        break;
                    case 1:
                        // Transport player
                        Vector arcNormal = normal.getCrossProduct(arr.getVelocity()).normalize().multiply(-1.8);
                        Location target = snowLocs.get(teleportingIndex).clone().add(arcNormal);

//                        p.setVelocity(vel);

                        Vector toTarget = Utils.CrearVector(p.getLocation(), target);
                        if (toTarget.length() > 0) p.setVelocity(toTarget);
                        if (goToBlockTicks == 0) {
                            p.teleport(target);
                        } else {
//                            target.setDirection(p.getLocation().getDirection());
//                            Vector vel = snowVels.get(teleportingIndex);
//                            p.setVelocity(toTarget);
                        }
                        goToBlockTicks--;
                        if(goToBlockTicks > 0) break;
                        // Next
                        if (teleportingIndex >= snowLocs.size() - 1) {
                            step = 2;
                            // Splash on ground

                            Location location = Utils.getNearestFloor(arr.getLocation());
                            location.getBlock().setType(Material.PACKED_ICE);

                            Utils.getOuterCylBlocks(location.clone().add(0, -1, 0), (int)(f * 5.0), 2, false).forEach(b -> {
                                Block block = b;
                                if(snowLocs.contains(b)) return;
                                if (Utils.Possibilitat(50)) {
                                    if (block.getType() == Material.ICE) block.setType(Material.PACKED_ICE);
                                    if (block.getType() == Material.SNOW_BLOCK) block.setType(Material.ICE);
                                }
                            });
                            int n = Utils.NombreEntre(3, 6);
                            for (int i = 0; i < n; i++) {
                                Vector vector = randomFlat.rotateAroundY((360/n + Utils.NombreEntre(-5, 5)) * toRadians);
                                List<Vector> line1 = Utils.getLine(location.toVector(), vector.normalize(), Utils.NombreEntre(4, 6));
                                line1.forEach(b -> {
                                    Block block = b.toLocation(p.getWorld()).getBlock();
                                    if(snowLocs.contains(b)) return;
                                    if (Utils.Possibilitat(70)) {
                                        if (block.getType() == Material.ICE) block.setType(Material.PACKED_ICE);
                                        if (block.getType() == Material.SNOW_BLOCK) block.setType(Material.ICE);
                                    }
                                });
                            }
                            // Knockback and damage
                            Utils.getNearbyEnemies(p, p.getLocation(), (7 * f) + 4, false).forEach(enemy -> {
                                enemy.damage(6.8f, p);
                                enemy.setVelocity(Utils.CrearVector(p.getLocation(), enemy.getLocation()).normalize().multiply(1.1).add(new Vector(0, 0.6, 0)));
                            });

                            Collections.shuffle(snowLocs);
                        }
                        teleportingIndex++;
                        break;
                    case 2:
                        // Remove trail
                        snowLocs.get(removingIndex).getBlock().setType(Material.AIR);
                        removingIndex++;
                        if (removingIndex >= snowLocs.size()) {
                            cancel();
                            return;
                        }
                        break;
                }
                count++;

            }
        }.runTaskTimer(FastSurvival.getPlugin(), 3, 1);
    }

    @EventHandler
    public void onHit(ProjectileHitEvent evt) {
        //WorldGuard danger
        if (!(evt.getEntity() instanceof Arrow)) {
            return;
        }
        Arrow arr = (Arrow) evt.getEntity();
        if (!(arr.getShooter() instanceof LivingEntity)) {
            return;
        }
        LivingEntity p = (LivingEntity) arr.getShooter();
        Location l = arr.getLocation();
        MetadataValue metadataType = Utils.getMetadata(arr, "BowType");
        if (metadataType == null) {
            return;
        }
        BowType type = BowType.values()[metadataType.asInt()];
        if (type == null) {
            return;
        }
        float f = Utils.getMetadata(arr, "Force").asFloat();
        World world = l.getWorld();
        switch (type) {
            case ENDER:
                p.getWorld().playSound(l, Sound.BLOCK_GLASS_BREAK, 10 * f, 1);
                p.getWorld().playEffect(l, Effect.ENDER_SIGNAL, 4, (int) (28 * f));
                break;
            case EXPLOSIVE:

                //arr.remove();
                break;
            case MAGNETIC:
                p.getWorld().playEffect(l, Effect.SMOKE, 4, (int) (28 * f));
                break;
            case TORCH:
                if (p instanceof Player) {
                    if (!WGUtils.canBuild((Player) p, l)) {
                        break;
                    }
                }
                l.getBlock().setType(Material.TORCH);
                arr.remove();
                break;
            case BOUNCY:
                arr.remove();
                break;
            case ICY:
                arr.remove();
                break;
            case WATER:
                if (true) {
                    if (p instanceof Player) {
                        if (!WGUtils.canBuild((Player) p, l)) {
                            break;
                        }
                    }
                    Block b = l.getBlock();
                    if (b.getPistonMoveReaction() != PistonMoveReaction.BREAK && b.isEmpty() == false) {
                        break;
                    }
                    if (b.isLiquid()) {
                        break;
                    }

                    b.setType(Material.WATER);
                    Utils.BreakBlockLater(b, (int) (20 * 2 * f), false);
                    arr.remove();
                }
                break;
            case WITHER:
                if (true) {
                    if (p instanceof Player) {
                        if (WGUtils.canBuild((Player) p, l)) {
                            break;
                        }
                    }
                    p.getWorld().createExplosion(l, 0.5F * f);
                }
                break;
            case SKY_EXPLOSIVE:
                if (evt.getHitEntity() != null && evt.getHitEntity() instanceof LivingEntity) {
                    makeSkyExplosiveEffect(arr, p, type, f, (LivingEntity) evt.getHitEntity());

                }
                break;
            case ELECTRIC:
                arr.remove();
                ultraStrike(l, f, p, 1);
                final Location lf = l;
                final float ff = f * 2.3F;
                final UUID id = p.getUniqueId();
                Runnable myTask = new Runnable() {
                    @Override
                    public void run() {
                        ultraStrike(lf, ff, p, 0.9F);
                        LivingEntity entityFromUUID = (LivingEntity) Utils.getEntityFromUUID(id, lf.getWorld());
                        if (entityFromUUID == null) return;
                        for (LivingEntity ent : Utils.getNearbyEnemies(entityFromUUID, lf, ff * 16, false)) {
                            if (ent instanceof FallingBlock) return;
                            ;
                            int time = (int) (5 * 20 + ff * 3);
                            ent.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, time, 0));
                            ent.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, time, 0));
                            ent.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, time * 2, 0));
                            ent.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, time / 3, 0));

                            ent.damage(5, entityFromUUID);
                            ;
                            ent.setFallDistance(0);
                            Vector vel = Utils.CrearVector(l, ent.getEyeLocation()).normalize().multiply(1.2);
                            vel.setY(0.5);
                            ent.setVelocity(vel);
                        }
                    }
                };
                Bukkit.getScheduler().runTaskLater(FastSurvival.getPlugin(), myTask, 7);
                for (LivingEntity ent : Utils.getNearbyEnemies(p, l, f * 10, false)) {
                    if (ent instanceof FallingBlock) return;
                    ;
                    int time = (int) (10 * 20 + f * 3);
                    ent.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, time, 0));
                    ent.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, time, 0));
                    ent.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, time * 2, 0));
                    ent.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, time / 3, 0));

                    ent.damage(7, p);
                    ent.setFireTicks(20 * 2);
                    Vector vel = Utils.CrearVector(l, ent.getEyeLocation()).normalize().multiply(2.1);
                    vel.setY(0.5);
                    ent.setVelocity(vel);
                }

                world.strikeLightningEffect(l);
                break;
            case MULTI:
                Entity hitEntity = evt.getHitEntity();
                if (hitEntity != null) hitEntity.setFireTicks(4);
                break;
            default:
                break;


        }
    }

    private void ultraStrike(Location l, float f, LivingEntity p, float height) {
        World world = l.getWorld();
        Location location = world.getHighestBlockAt(l.getBlockX(), l.getBlockZ()).getLocation();
        ArrayList<Block> finalLocs = new ArrayList<Block>();
        try {
            for (Location lo : Utils.getLocationsCircle(location.add(new Vector(0, -1, 0)), 6.8D * f + 1, 4)) {
                Location lob = world.getHighestBlockAt(lo.getBlockX(), lo.getBlockZ()).getLocation();
                Block b = lob.getBlock().getRelative(BlockFace.DOWN);
                if (b.getPistonMoveReaction() == PistonMoveReaction.BREAK || TurretUtils.getTurretAt(b.getLocation()) != null) {
                    b = b.getRelative(BlockFace.DOWN);
                }
                if (b.getPistonMoveReaction() == PistonMoveReaction.BLOCK || b.getPistonMoveReaction() == PistonMoveReaction.IGNORE)
                    continue;
                finalLocs.add(b);
            }
            for (Block b : finalLocs) {
                //b.setType(Material.DIAMOND_BLOCK);
                if (b.getType() == Material.BEDROCK) {
                    continue;
                }
                ;
                if (b.getPistonMoveReaction() == PistonMoveReaction.BLOCK || b.getPistonMoveReaction() == PistonMoveReaction.IGNORE) {
                    continue;
                }
                ;
                if (Tag.PLANKS.isTagged(b.getType())) {
                    b.setType(Material.COBBLESTONE);
                    continue;
                }
                ;
                if (Tag.LOGS.isTagged(b.getType())) {
                    b.setType(Material.STONE);
                    continue;
                }
                ;
                if (b.getType() != Material.AIR) {
                    if (p != null) {
                        if (p instanceof Player) {
                            if (!WGUtils.canBuild((Player) p, l)) {
                                continue;
                            }
                        }
                    }

                    FallingBlock fallingBlock = b.getWorld().spawnFallingBlock(
                            b.getLocation().add(new Vector(0.5, 1.5, 0.5)), b.getBlockData());
                    fallingBlock.setDropItem(true);
                    fallingBlock.setHurtEntities(true);
                    //fallingBlock.setVelocity(Utils.CrearVector(l, location).setY(0).add(vr));
                    fallingBlock.setVelocity(new Vector(0, height, 0));
                    b.setType(Material.AIR);
                }
            }
            //world.strikeLightningEffect(location);
        } catch (Exception e) {

        }

    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent evt) {

        if (evt.isCancelled()) {
            return;
        }
        evt.getEntity().setMetadata("LastDamager", new FixedMetadataValue(FastSurvival.getPlugin(), evt.getDamager().getUniqueId()));
        //---
        if (!(evt.getDamager() instanceof Arrow)) {
            return;
        }
        Arrow arr = (Arrow) evt.getDamager();
        if (!(evt.getEntity() instanceof LivingEntity)) {
            return;
        }
        LivingEntity damaged = (LivingEntity) evt.getEntity();
        if (!(arr.getShooter() instanceof LivingEntity)) {
            return;
        }
        LivingEntity damager = (LivingEntity) arr.getShooter();
        Location l = arr.getLocation();
        World world = l.getWorld();

        MetadataValue metadataType = Utils.getMetadata(arr, "BowType");
        if (metadataType == null) {
            return;
        }
        BowType type = BowType.values()[metadataType.asInt()];
        if (type == null) {
            return;
        }
        float f = Utils.getMetadata(arr, "Force").asFloat();
        Double dmg = evt.getDamage();

        switch (type) {
            case ENDER:
                damager.teleport(damaged.getLocation().clone().add(Vector.getRandom().add(new Vector(0, 0.8, 0))));
                break;
            case EXPLOSIVE:
                if (true) {
                    dmg = 1D;
                    evt.setDamage(1D);
                    MetadataValue metadataAcc = Utils.getMetadata(damaged, "ExplAcc");
                    float ExplAcc;
                    if (metadataAcc != null) {
                        ExplAcc = metadataAcc.asFloat();
                    } else {
                        ExplAcc = 0F;
                    }


                    ExplAcc = (float) (ExplAcc + (1.5 * f));
                    damaged.setMetadata("ExplAcc", new FixedMetadataValue(FastSurvival.getPlugin(), ExplAcc));
                    world.playEffect(l, Effect.SMOKE, 4, (int) (28 * f));
                    world.playSound(damaged.getLocation(), Sound.ENTITY_CREEPER_DEATH, 7 * f, 1.4F);

                    //world.createExplosion(damaged.getLocation(), fExplosion);
                }
                break;
            case MAGNETIC:
                damaged.setVelocity(new Vector(0, 0, 0));
                Vector vM = Utils.CrearVector(damaged.getLocation(), damager.getLocation());
                //vM.normalize();
                vM.multiply(0.8);
                vM.multiply(f);
                Location newLoc = damaged.getLocation().clone().add(vM);
                if (newLoc.getBlock().getType().isSolid()) {
                    break;
                }
                damaged.teleport(newLoc);
                break;
            case TORCH:
                damaged.setFireTicks((int) (20 * 5 * f));
                dmg = dmg / 3;
                break;
            case BOUNCY:
                if (true) {
                    ArrayList<LivingEntity> nearbyEnemies = Utils.getNearbyEnemies(damaged, 15 + (f * 6), true);
                    ArrayList<UUID> bouncedEnemies = new ArrayList<UUID>();
                    nearbyEnemies.remove(damager);
                    int initialtimes = (int) (10 + (f * 3));
                    int times = initialtimes;
                    //Bukkit.broadcastMessage(Integer.toString(times) + " - " + nearbyEnemies.size());
                    MetadataValue metadata = Utils.getMetadata(arr, "Bounced");
                    MetadataValue metadata2 = Utils.getMetadata(arr, "BouncedTimes");
                    if (metadata != null) {
                        ArrayList<UUID> idArr = (ArrayList<UUID>) metadata.value();
                        for (UUID id : idArr) {
                            Entity ent = Utils.getEntityFromUUID(id, world);
                            if (ent != null) {
                                if (ent instanceof LivingEntity) {
                                    LivingEntity le = (LivingEntity) ent;
                                    nearbyEnemies.remove(le);
                                }
                            }
                            bouncedEnemies.add(id);
                        }

                    }
                    if (metadata2 != null) {
                        times = metadata2.asInt();

                    }
                    int actualtimes = initialtimes - times;

                    //First obj
                    dmg = 2.5 + ((actualtimes * 2.4));
//					Bukkit.broadcastMessage("times: " + times + ", dmg: " + dmg);
                    if (damaged instanceof Player && dmg > 7) {
                        dmg = 6.2;
                    }
                    LivingEntity bounced = Utils.getNearestEntity(damaged.getLocation(), nearbyEnemies);
                    if (bounced != null && times >= 0) {

                        Vector v = Utils.CrearVector(damaged.getEyeLocation().clone().add(new Vector(0, damaged.getEyeHeight() / 3.0, 0)), bounced.getEyeLocation());
                        double lv = v.length();
                        double yOffset = 0.6 + (lv / 40);
                        v.add(new Vector(0, yOffset, 0));
                        Location spawnLoc = damaged.getLocation().add(v.clone().normalize().multiply(1.1D));
                        Arrow arrow = (Arrow) world.spawnEntity(spawnLoc, EntityType.ARROW);
                        arrow.setShooter(damager);
                        //arrow.setFireTicks(20000);
                        arrow.setVelocity(v);
                        bouncedEnemies.add(bounced.getUniqueId());
                        arrow.setMetadata("Bounced", new FixedMetadataValue(FastSurvival.getPlugin(), bouncedEnemies));
                        arrow.setMetadata("BouncedTimes", new FixedMetadataValue(FastSurvival.getPlugin(), times - 1));
                        //- normal
                        arrow.setMetadata("BowType", new FixedMetadataValue(FastSurvival.getPlugin(), type.ordinal()));
                        arrow.setMetadata("Force", new FixedMetadataValue(FastSurvival.getPlugin(), f));
                    }
                    break;
                }
            case ICY:
                if (true) {
//				int lvl = 0;
                    //if (damaged.hasPotionEffect(PotionEffectType.SLOW)){
//					for(PotionEffect eff : damaged.getActivePotionEffects()){
//						if (eff.getType() == PotionEffectType.SLOW){
//							lvl = eff.getAmplifier() + 1;
//						}
//					}
                    //}


//				dmg = dmg / 8;
//				if(lvl < 2){break;}
                    //effect
                    if (damaged.hasPotionEffect(PotionEffectType.WEAKNESS)) {
                        return;
                    }
                    int time = (int) (20 * 6 * f);
                    damaged.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, time * 2, 0));
                    damaged.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, time, 0));
                    if (damaged instanceof Player) {
                        ((Player) damaged).playSound(damager.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 0.5F);
                        time = time / 2;
                    }
                    ArrayList<BlockFace> faces = new ArrayList<BlockFace>();
                    faces.add(BlockFace.NORTH);
                    faces.add(BlockFace.SOUTH);
                    faces.add(BlockFace.WEST);
                    faces.add(BlockFace.EAST);
                    for (BlockFace face : faces) {

                        Block block = damaged.getLocation().getBlock().getRelative(face);
                        if (block.getPistonMoveReaction() == PistonMoveReaction.BREAK) {
                            continue;
                        }
                        block.setType(Material.ICE);
                        Utils.BreakBlockLater(block, time, false);

                    }
                    Location tpL = damaged.getLocation().getBlock().getLocation().add(new Vector(0.5, 0, 0.5));
                    tpL.setPitch(damaged.getLocation().getPitch());
                    tpL.setYaw(damaged.getLocation().getYaw());
                    damaged.teleport(tpL);
                    Block gblock = damaged.getLocation().add(0, 2, 0).getBlock();
                    //if (gblock.getPistonMoveReaction() == PistonMoveReaction.BREAK){
                    gblock.setType(Material.PACKED_ICE);
                    Utils.BreakBlockLater(gblock, time, false);
                    //}


                    //----
                    dmg = dmg / 2;
                    break;
                }
            case WATER:
                dmg = dmg / 5;
                break;
            case WITHER:
                dmg = dmg / 6;
                if (true) {
                    int t = (int) (20 * 20 * f);
                    int lvl = 0;
                    if (damaged instanceof Player) {
                        t = t / 3;
                    }
                    if (f >= 0.9 && Utils.Possibilitat(40)) {
                        lvl = 1;
                    }
                    damaged.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, t, lvl, false));
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, t, 0, false));
                }
                break;
            case ELECTRIC:
                Vector line = Utils.CrearVector(damager.getEyeLocation(), damaged.getEyeLocation());
                line.setY(0);
                line.normalize();
                line.multiply(16 + f * 16);
                line.setY(0);
                Location destination = damaged.getLocation().add(line).add(0, 2, 0);
                Block destinationBlock = destination.getBlock();
                List<Block> blocks = Utils.getCuboidAround(destination, 1, 0, 1).getBlocks();
                blocks.add(destinationBlock.getRelative(BlockFace.UP));
                if (!blocks.stream().allMatch(b -> b.getPistonMoveReaction() == PistonMoveReaction.BREAK || b.getType() == Material.AIR))
                    break;
                damaged.teleport(destination);
                Vector newLine = Utils.CrearVector(damager.getEyeLocation(), damaged.getLocation().clone().add(0.5, 0, 0.5));
                List<Vector> lineVects = Utils.getLine(damager.getLocation().clone().add(0.5, 1, 0.5).toVector(), newLine.clone().normalize(), (int) newLine.length());
                lineVects.forEach(v -> {
                    world.playEffect(v.toLocation(world), Effect.SMOKE, 4);
                    world.playEffect(v.toLocation(world), Effect.SMOKE, 3);
                });
                break;
            case MULTI:
                break;
            default:
                break;


        }
        evt.setDamage(dmg);
        //Bukkit.broadcastMessage(Double.toString(evt.getDamage()));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent evt) {
        if (evt.isCancelled()) {
            return;
        }
        //Bukkit.broadcastMessage(Double.toString(evt.getDamage()));
        if (!(evt.getEntity() instanceof LivingEntity)) {
            return;
        }
        LivingEntity damaged = (LivingEntity) evt.getEntity();
        World w = damaged.getWorld();
        EntityDamageEvent lastDamageCause = damaged.getLastDamageCause();
        if (lastDamageCause == null) {
            return;
        }
        if (!(lastDamageCause.getEntity() instanceof LivingEntity)) {
            return;
        }
        LivingEntity damager = (LivingEntity) lastDamageCause.getEntity();
        BowType type = BowUtils.getBowType(Utils.getItemInHand(damager));
        if (type != null) {
            if (type == BowType.EXPLOSIVE) {
                return;
            }
        }

        MetadataValue metadataAcc = Utils.getMetadata(damaged, "ExplAcc");
        if (metadataAcc == null) {
            return;
        }
        //if (evt.getDamage() <= 5){return;}
        if (evt.getCause() == DamageCause.ENTITY_EXPLOSION || evt.getCause() == DamageCause.BLOCK_EXPLOSION) {
            evt.setDamage(evt.getDamage() * 0.6);
            return;
        }
        //Bukkit.broadcastMessage(evt.getCause().name());
        float ExplAcc = metadataAcc.asFloat();
        if (ExplAcc == 0F) {
            return;
        }
        w.createExplosion(damaged.getEyeLocation().toVector().toLocation(w), ExplAcc);
        damaged.setMetadata("ExplAcc", new FixedMetadataValue(FastSurvival.getPlugin(), 0F));

    }

    @EventHandler
    public void onEntityDamageWither(EntityDamageEvent evt) {
        if (evt.isCancelled()) {
            return;
        }
        if (evt.getCause() != DamageCause.WITHER) {
            return;
        }
        if (!(evt.getEntity() instanceof LivingEntity)) {
            return;
        }
        LivingEntity damaged = (LivingEntity) evt.getEntity();
        MetadataValue metadata = Utils.getMetadata(damaged, "LastDamager");
        if (metadata == null) {
            return;
        }
        //Bukkit.broadcastMessage("wither meta0");
        //Bukkit.broadcastMessage(metadata.value().getClass().getName());
        Entity ent = Utils.getEntityFromUUID((UUID) metadata.value(), damaged.getWorld());
        //Bukkit.broadcastMessage(ent.getClass().getName());
        if (ent == null) {
            return;
        }
        //Bukkit.broadcastMessage("wither meta1");
        if (!(ent instanceof LivingEntity)) {
            return;
        }
        //if (ent instanceof Player){Bukkit.broadcastMessage("is a Player!");}
        //Bukkit.broadcastMessage("wither meta2");
        LivingEntity damager = (LivingEntity) ent;
        Utils.healDamageable(damager, evt.getDamage() * 2);
        //damager.setHealth(20.0);
        //damager.getLocation().getBlock().setType(Material.GOLD_BLOCK);
        //Bukkit.broadcastMessage("Curant el wither");
    }

    @EventHandler
    public void onEntityDamageByEntityMark(EntityDamageByEntityEvent evt) {
        if (evt.isCancelled()) {
            return;
        }
        //if (evt.getDamager() == null){return;}
        //Bukkit.broadcastMessage("MarkStart");
        LivingEntity damager;
        if (evt.getDamager() instanceof Projectile) {
            //Bukkit.broadcastMessage("MarkProjectile");
            Projectile arr = (Projectile) evt.getDamager();

            if (!(arr.getShooter() instanceof LivingEntity)) {
                return;
            }
            damager = (LivingEntity) arr.getShooter();
            //Bukkit.broadcastMessage("MarkSetLivingEntity");

        } else {
            //Bukkit.broadcastMessage("MarkElseDirect");
            if (!(evt.getDamager() instanceof LivingEntity)) {
                return;
            }
            //Bukkit.broadcastMessage("MarkElseLivingEntity");
            damager = (LivingEntity) evt.getDamager();
        }

        evt.getEntity().setMetadata("LastDamager", new FixedMetadataValue(FastSurvival.getPlugin(), damager.getUniqueId()));
        //---
    }

    //	@EventHandler
//	public void onEntityDamageByEntityMark(EntityDeathEvent evt) {	
//		
//	}
    @EventHandler
    public void onLightningStrike(LightningStrikeEvent evt) {
        if (evt.isCancelled()) {
            return;
        }
//		LightningStrike light = evt.getLightning();
//		if (!light.isEffect()){
//			int f = Utils.NombreEntre(5, 10);
//			ultraStrike(light.getLocation(), f, null);
//			for(LivingEntity ent : Utils.getNearbyEnemies(light.getLocation(), f * 10)){
//				int time = (int) (10 * 20 + f * 3);
//				ent.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, time, 0));
//				ent.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, time, 0));
//				ent.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, time * 2, 0));
//				ent.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, time / 3, 0));
//
//				ent.damage(6, null);
//				ent.setFireTicks(20 * 2);
//				ent.setVelocity(Vector.getRandom().normalize().multiply(0.86));
//			}
//		}
    }
}
